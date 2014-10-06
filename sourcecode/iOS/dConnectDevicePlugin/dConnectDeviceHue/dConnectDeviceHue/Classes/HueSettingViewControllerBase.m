
#import "HueSettingViewControllerBase.h"

@interface HueSettingViewControllerBase ()

@end


@implementation HueSettingViewControllerBase

static ItemBridge *mSelectedItemBridge;

//======================================================================
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    mlog = [[DCLogger alloc]initWithSourceClass:self];

    [mlog entering:@"viewDidLoad" param:nil];

}

//======================================================================
- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    //起動時の位置合わせ
    [self setLayoutConstraint];

}

//======================================================================
//表示後に本体を回転させた場合の位置合わせ
- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation duration:(NSTimeInterval)duration
{
 
    [self setLayoutConstraint];
    
}

//======================================================================
- (void)setLayoutConstraint
{
    
    [mlog entering:@"setLayoutConstraint" param:nil];

    if ([UIApplication sharedApplication].statusBarOrientation == UIInterfaceOrientationPortrait) {
   
        [self setLayoutConstraintPortrait];
    
    } else {
    
        [self setLayoutConstraintLandscape];
        
    }
    
    [mlog exiting:@"setLayoutConstraint" param:nil];
    
}

//======================================================================
//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
}

//======================================================================
//横向き座標調整
- (void)setLayoutConstraintLandscape
{
}

//======================================================================
- (void)viewDidDisappear:(BOOL)animated
{
    //ブリッジとの接続などを切って終わる
    [self disableHeartBeat];
    
    [super viewDidDisappear:animated];
    
}

//======================================================================
- (void)showAleart:(NSString*)msg
{
    UIAlertView *alert = [[UIAlertView alloc]
                          initWithTitle:@"hue"
                          message:msg delegate:self
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil];
    
    [alert show];
}

//======================================================================
- (BOOL)isIpad
{
    return (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
}

//======================================================================
- (BOOL)isIphone
{
    return (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone);
    
}

- (BOOL)isIPadMini
{
    if (!self.isIpad) {
        return false;
    }
    
    CGRect rect = [[UIScreen mainScreen] bounds];
    
    return ((int)rect.size.height <= 1024);
}

//縦が短いiPhoneの場合
- (BOOL)isIPhoneLong
{
    if (!self.isIphone) {
        return false;
    }
    
    CGRect rect = [[UIScreen mainScreen] bounds];

    return ((int)rect.size.height > 480);
    
}

//======================================================================
//Hue SDKの初期化
- (void)initHueSdk:(NSString*)ipAdr macAdr:(NSString*)macAdr isAuth:(BOOL)isAuth
{
    [mlog entering:@"initHueSdk" param:nil];
    
    //ブリッジとの接続などを一度切る
    [self disableHeartBeat];

    //オブジェクト生成
    phHueSDK = [[PHHueSDK alloc] init];
    [phHueSDK startUpSDK];
    
    [phHueSDK enableLogging:YES];
    
    if ((ipAdr != nil) && (macAdr != nil)) {
        [phHueSDK setBridgeToUseWithIpAddress:ipAdr macAddress:macAdr];
    }
    
    if (isAuth) {
        [self setNotificationManagerAuth];
    }else{
        [self setNotificationManagerConnect];
    }
    
    //ブリッジ検索用オブジェクト生成
    bridgeSearching = [[PHBridgeSearching alloc] initWithUpnpSearch:YES andPortalSearch:YES andIpAdressSearch:NO];
        
    [mlog exiting:@"initHueSdk" param:nil];
    
}

- (void)setNotificationManagerAuth
{

    // Register for notifications about pushlinking
    notificationManager = [PHNotificationManager defaultManager];
    
    //PUSHLINK認証成功
    [notificationManager registerObject:self withSelector:@selector(pushlink_authenticationSuccess) forNotification:PUSHLINK_LOCAL_AUTHENTICATION_SUCCESS_NOTIFICATION];
    
    //PUSHLINK認証失敗
    [notificationManager registerObject:self withSelector:@selector(pushlink_authenticationFailed) forNotification:PUSHLINK_LOCAL_AUTHENTICATION_FAILED_NOTIFICATION];
    
    //PUSHLINKブリッジに接続できません
    [notificationManager registerObject:self withSelector:@selector(pushlink_noLocalConnection) forNotification:PUSHLINK_NO_LOCAL_CONNECTION_NOTIFICATION];
    
    //PUSHLINKブリッジが見つかりません。
    [notificationManager registerObject:self withSelector:@selector
     (pushlink_noLocalBridge) forNotification:
     PUSHLINK_NO_LOCAL_BRIDGE_KNOWN_NOTIFICATION];
    
    //PUSHLINKブリッジのボタンが押されていない
    [notificationManager registerObject:self withSelector:@selector(pushlink_buttonNotPressed) forNotification:PUSHLINK_BUTTON_NOT_PRESSED_NOTIFICATION];

}

- (void)setNotificationManagerConnect
{
  
    // Register for notifications about pushlinking
    notificationManager = [PHNotificationManager defaultManager];
    
    //接続成功
    [notificationManager registerObject:self withSelector:@selector(localConnection) forNotification:
     LOCAL_CONNECTION_NOTIFICATION];
    
    //ブリッジに接続できません
    [notificationManager registerObject:self withSelector:@selector(noLocalConnection) forNotification:
     NO_LOCAL_CONNECTION_NOTIFICATION];
    
    //未認証
    [notificationManager registerObject:self withSelector:@selector(notAuthenticated) forNotification:
     NO_LOCAL_AUTHENTICATION_NOTIFICATION];

}


//======================================================================
/**
 Starts the local heartbeat with a 10 second interval
 */
- (void)enableLocalHeartbeat {
    /***************************************************
     The heartbeat processing collects data from the bridge
     so now try to see if we have a bridge already connected
     *****************************************************/
    
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    if (cache != nil && cache.bridgeConfiguration != nil && cache.bridgeConfiguration.ipaddress != nil) {

        // Some bridge is known
        [phHueSDK enableLocalConnection];

    }
    else {
        /***************************************************
         No bridge connected so start the bridge search process
         *****************************************************/
        
        // No bridge known
        [self searchForBridgeLocal];
    }
}

//======================================================================
/**
 Search for bridges using UPnP and portal discovery, shows results to user or gives error when none found.
 */
- (void)searchForBridgeLocal {
    
    //    Stops the local heartbeat
    [phHueSDK disableLocalConnection];
    
    // Show search screen
    /***************************************************
     A bridge search is started using UPnP to find local bridges
     *****************************************************/
    
    // Start search
    [bridgeSearching startSearchWithCompletionHandler:^(NSDictionary *bridgesFound) {
        
    }];
}

//======================================================================
/**
 Stops the local heartbeat
 */
- (void)disableLocalHeartbeat {
    [phHueSDK disableLocalConnection];
}

//======================================================================
//HueSDKの開放
- (void)disableHeartBeat {
    
    [mlog entering:@"disableHeartBeat" param:nil];
    
    if (phHueSDK != nil) {
        [phHueSDK disableLocalConnection];
        [phHueSDK stopSDK];
    }
    
    if (notificationManager != nil) {
        [notificationManager deregisterObjectForAllNotifications:self];
        notificationManager = nil;
    }
    
    [mlog exiting:@"disableHeartBeat" param:nil];
    
}

//======================================================================
- (void)notificationComman
{

    // Deregister for all notifications
    [[PHNotificationManager defaultManager] deregisterObjectForAllNotifications:self];
    
    //HueSDKの開放
    [self disableHeartBeat];
    
    [self stopIndicator];

}

//======================================================================
/**
 Notification receiver which is called when the pushlinking was successful
 */
- (void)pushlink_authenticationSuccess {
    [mlog entering:@"pushlink_authenticationSuccess" param:nil];
    
    [self notificationComman];
    
    [self after_pushlink_authenticationSuccess];
    
}

- (void)after_pushlink_authenticationSuccess {

    [self showAleart:@"アプリ登録に成功しました。\nライト検索に進みます。"];

}

//======================================================================

/**
 Notification receiver which is called when the pushlinking failed because the time limit was reached
 */
- (void)pushlink_authenticationFailed {
    [mlog entering:@"pushlink_authenticationFailed" param:nil];
    
    [self notificationComman];
    
    [self after_pushlink_authenticationFailed];
    
}

- (void)after_pushlink_authenticationFailed {

    //Authentication failed: time limit reached.
    [self showAleart:@"アプリ登録に失敗しました。"];

}

//======================================================================
/**
 Notification receiver which is called when the pushlinking failed because the local connection to the bridge was lost
 */
- (void)pushlink_noLocalConnection {
    [mlog entering:@"pushlink_noLocalConnection" param:nil];
    
    [self notificationComman];
    
    [self after_pushlink_noLocalConnection];
    
    
}


- (void)after_pushlink_noLocalConnection {
    
    //Authentication failed: No local connection to bridge.
    [self showAleart:@"ブリッジに接続できません。"];
    
}

//======================================================================
/**
 Notification receiver which is called when the pushlinking failed because we do not know the address of the local bridge
 */
- (void)pushlink_noLocalBridge {
    [mlog entering:@"pushlink_noLocalBridge" param:nil];
    
    [self notificationComman];
    
    [self after_pushlink_noLocalBridge];
    
}

//======================================================================
- (void)after_pushlink_noLocalBridge {
    
    //Authentication failed: No local bridge found.
    [self showAleart:@"ブリッジが見つかりません。"];
    
}

//======================================================================
//ブリッジのボタンを押されるまで定期的に送信される
- (void)pushlink_buttonNotPressed {
    
    [mlog entering:@"pushlink_buttonNotPressed" param:nil];
    
    [self after_pushlink_buttonNotPressed];
}

//======================================================================
- (void)after_pushlink_buttonNotPressed
{
    
}

//======================================================================
- (void)localConnection {
    [mlog entering:@"localConnection" param:nil];
    
    [self notificationComman];
    
    [self after_localConnection];

    
}

//======================================================================
- (void)after_localConnection {

}

//======================================================================
- (void)noLocalConnection {
    [mlog entering:@"noLocalConnection" param:nil];
    
    [self notificationComman];
    
    [self after_noLocalConnection];
    
    
}

//======================================================================
- (void)after_noLocalConnection {
    
    //Authentication failed: No local connection to bridge.
    [self showAleart:@"ブリッジに接続できません。"];
    
}

//======================================================================
- (void)notAuthenticated {
    [mlog entering:@"notAuthenticated" param:nil];
    
    [self notificationComman];
    
    [self after_notAuthenticated];
    
}

//======================================================================
- (void)after_notAuthenticated {

    //このメッセージは処理のパターンとして出ないが、例、もしくは念のため書いておく
    [self showAleart:@"アプリ登録されていません。"];

}

//======================================================================
- (void)setSelectedItemBridge:(ItemBridge*)itemBridge{
    
    mSelectedItemBridge = itemBridge.copy;
    
}

//======================================================================
- (void)initSelectedItemBridge{
    
    mSelectedItemBridge = nil;
    mSelectedItemBridge = [[ItemBridge alloc] init];

}

//======================================================================
- (ItemBridge*)getSelectedItemBridge
{
    
    if (mSelectedItemBridge == nil) {
        [self initSelectedItemBridge];
    }
    
    return mSelectedItemBridge;
}

//======================================================================
- (BOOL)isSelectedItemBridge
{
    
    if (mSelectedItemBridge == nil) {
        return NO;
    }
    
    if (mSelectedItemBridge.ipAdress.length < 7) {
        return NO;
    }

    if (mSelectedItemBridge.macAdress.length < 17) {
        return NO;
    }

    return YES;
}

//======================================================================
- (void)showPage:(NSUInteger)jumpIndex
{
    [self.hueViewController showPage:jumpIndex];
}

//======================================================================
//ブリッジ検索ページを開く
- (void)showBridgeListPage
{
    
    [self showPage:0];
    
}

//======================================================================
//アプリ登録ページを開く
- (void)showAuthPage
{
    
    [self showPage:1];
    
}

//======================================================================
//ライト検索ページを開く
- (void)showLightSearchPage
{
    
    [self showPage:2];
    
}

//======================================================================
-(void)startIndicator
{

    //継承先で定義
    
}

//======================================================================
-(void)stopIndicator
{

    //継承先で定義

}

//======================================================================
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
