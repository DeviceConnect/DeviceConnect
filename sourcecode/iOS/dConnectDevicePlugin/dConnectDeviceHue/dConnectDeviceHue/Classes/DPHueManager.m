//
//  DPHueConst.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueManager.h"

@implementation DPHueManager
//見つけたブリッジのリスト
NSString *const DPHueBridgeListName = @"org.deviceconnect.ios.DPHue.ip";

// 共有インスタンス
+ (instancetype)sharedManager
{
    static id sharedInstance;
    static dispatch_once_t onceHueToken;
    dispatch_once(&onceHueToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

// 初期化
- (instancetype)init
{
    self = [super init];
    if (self) {
        [self initHue];
    }
    return self;
}

//HueSDKの初期化
-(void)initHue
{
    if (!phHueSDK) {
        phHueSDK = [[PHHueSDK alloc] init];
        [phHueSDK startUpSDK];
        [phHueSDK enableLogging:NO];
        bridgeSearching = [[PHBridgeSearching alloc] initWithUpnpSearch:YES andPortalSearch:YES andIpAdressSearch:NO];
    }
}

//ブリッジの検索
-(void)searchBridgeWithCompletion:(PHBridgeSearchCompletionHandler)completion
{
    [bridgeSearching startSearchWithCompletionHandler:^(NSDictionary *bridgesFound) {
        _hueBridgeList = bridgesFound;
        if (completion) {
            completion(bridgesFound);
        }
    }];
}

//ブリッジへの認証依頼
-(void)startAuthenticateBridgeWithIpAddress:(NSString*)ipAddress
                                        macAddress:(NSString*)macAddress
                                          receiver:(id)receiver
                    localConnectionSuccessSelector:(SEL)localConnectionSuccessSelector
                                 noLocalConnection:(SEL)noLocalConnection
                                  notAuthenticated:(SEL)notAuthenticated
{
    id registerReceiver = receiver;
    if (!registerReceiver) {
        registerReceiver = self;
    }
    // Register for notifications about pushlinking
    notificationManager = [PHNotificationManager defaultManager];
    if (localConnectionSuccessSelector) {
        //接続成功
        [notificationManager registerObject:registerReceiver withSelector:localConnectionSuccessSelector forNotification:
         LOCAL_CONNECTION_NOTIFICATION];
    }
    if (noLocalConnection) {
        //ブリッジに接続できません
        [notificationManager registerObject:registerReceiver withSelector:noLocalConnection forNotification:
         NO_LOCAL_CONNECTION_NOTIFICATION];
    }
    if (notAuthenticated) {
        //未認証
        [notificationManager registerObject:registerReceiver withSelector:notAuthenticated forNotification:
         NO_LOCAL_AUTHENTICATION_NOTIFICATION];
    }
    if ((ipAddress != nil) && (macAddress != nil)) {
        [phHueSDK setBridgeToUseWithIpAddress:ipAddress macAddress:macAddress];
    }
    [self enableHeartbeat];
}

//Pushlinkの確認開始
-(void)     startPushlinkWithReceiver:(id)receiver
pushlinkAuthenticationSuccessSelector:(SEL)pushlinkAuthenticationSuccessSelector
 pushlinkAuthenticationFailedSelector:(SEL)pushlinkAuthenticationFailedSelector
    pushlinkNoLocalConnectionSelector:(SEL)pushlinkNoLocalConnectionSelector
        pushlinkNoLocalBridgeSelector:(SEL)pushlinkNoLocalBridgeSelector
     pushlinkButtonNotPressedSelector:(SEL)pushlinkButtonNotPressedSelector
{
    
    id registerReceiver = receiver;
    if (!registerReceiver) {
        registerReceiver = self;
    }

    notificationManager = [PHNotificationManager defaultManager];
    if (pushlinkAuthenticationSuccessSelector) {
        //PUSHLINK認証成功
        [notificationManager registerObject:registerReceiver withSelector:pushlinkAuthenticationSuccessSelector forNotification:PUSHLINK_LOCAL_AUTHENTICATION_SUCCESS_NOTIFICATION];
    }
    if (pushlinkAuthenticationFailedSelector) {
        //PUSHLINK認証失敗
        [notificationManager registerObject:registerReceiver withSelector:pushlinkAuthenticationFailedSelector forNotification:PUSHLINK_LOCAL_AUTHENTICATION_FAILED_NOTIFICATION];
    }
    if (pushlinkNoLocalConnectionSelector) {
        //PUSHLINKブリッジに接続できない
        [notificationManager registerObject:registerReceiver withSelector:pushlinkNoLocalConnectionSelector forNotification:PUSHLINK_NO_LOCAL_CONNECTION_NOTIFICATION];
    }
    if (pushlinkNoLocalBridgeSelector) {
        //PUSHLINKブリッジが見つからない
        [notificationManager registerObject:registerReceiver
                               withSelector:pushlinkNoLocalBridgeSelector
                            forNotification:PUSHLINK_NO_LOCAL_BRIDGE_KNOWN_NOTIFICATION];
    }
    if (pushlinkButtonNotPressedSelector) {
        //PUSHLINKブリッジのボタンが押されていない
        [notificationManager registerObject:registerReceiver withSelector:pushlinkButtonNotPressedSelector forNotification:PUSHLINK_BUTTON_NOT_PRESSED_NOTIFICATION];
    }
    [phHueSDK startPushlinkAuthentication];
}

//ライトステータスの取得
-(NSDictionary *)getLightStatus
{
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    return cache.lights;
}

//ライトの点灯
-(BOOL)setLightOnWithResponse:(DConnectResponseMessage*)response
                      lightId:(NSString*)lightId
                   brightness:(double)brightness
                        color:(NSString*)color
{
    
    return YES;//[self changeLightStatus:response lightId:lightId lightState:lightState];
}

//ライトの消灯
-(BOOL)setLightOffWithResponse:(DConnectResponseMessage*)response
                       lightId:(NSString*)lightId
{
    
    //lightState取得
    PHLightState *lightState = [self getLightStateIsOn:NO brightness:0 color:nil];
    if (lightState == nil) {
        return YES;
    }
    
//    return [self changeLightStatus:response lightId:lightId lightState:lightState];
    
    return YES;
}

//ライトの名前変更
-(BOOL)changeLightNamewithResponse:(DConnectResponseMessage*)response
                           lightId:(NSString*)lightId
                              name:(NSString*)name
{
    //nameが指定されてない場合はエラーで返す
    if (![self checkParamRequiredStringItemWithParam:name errorState:STATE_ERROR_NO_NAME]) {
        return YES;
    }
    //LightIdチェック
    if (![self checkParamLightId:lightId]) {
        return YES;
    }
    
    return YES;//[self changeLightName:response lightId:lightId name:name];
   
}


//ライトグループステータスの取得
-(NSDictionary*)getLightGroupStatus
{
    //キャッシュにあるグループの一覧からグループを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    return cache.groups;
}

//ライトグループの点灯
-(BOOL)setLightGroupOnWithResponse:(DConnectResponseMessage*)response
                           groupId:(NSString*)groupId
                        brightness:(double)brightness
                             color:(NSString*)color
{
    //groupIdチェック
    if (![self checkParamGroupId:groupId]) {
        return YES;
    }
    
    PHLightState *lightState = [self getLightStateIsOn:YES brightness:brightness color:color];
    if (lightState == nil) {
        return YES;
    }

    return YES;//[self changeGroupStatus:response groupId:groupId lightState:lightState];
}

//ライトグループの消灯
-(BOOL)setLightGroupOffWithResponse:(DConnectResponseMessage*)response
                            groupId:(NSString*)groupId
{
    //groupIdチェック
    if (![self checkParamGroupId:groupId]) {
        return YES;
    }
    
    //lightState取得
    PHLightState *lightState = [self getLightStateIsOn:NO brightness:0 color:nil];
    
    //lightStateがnilならエラーなので終了
    if (lightState == nil) {
        return YES;
    }
    
    return YES;//[self changeGroupStatus:response groupId:groupId lightState:lightState];
}

//ライトグループ名の変更
-(BOOL)changeLightGroupNameWithResponse:(DConnectResponseMessage*)response
                                groupId:(NSString*)groupId
                                   name:(NSString*)name
{
    //nameが指定されてない場合はエラーで返す
    if (![self checkParamRequiredStringItemWithParam:name errorState:STATE_ERROR_NO_NAME]) {
        return YES;
    }
    
    //groupIdチェック
    if (![self checkParamGroupId:groupId]) {
        return YES;
    }
    
    return YES;//[self changeGroupName:response groupId:groupId name:name];
    
}

//ライトグループの作成
-(BOOL)createLightGroupWithLightIds:(NSArray*)lightIds
                          groupName:(NSString*)groupName
                         completion:(void(^)(NSString* groupId))completion
{
    if (lightIds.count <= 0) {
        _bridgeConnectState = STATE_ERROR_NO_LIGHTID;
        return YES;
    }
    
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    
    //lightIdでArrayを作る
    NSMutableDictionary *lightArray = [NSMutableDictionary dictionary];
    
    for (PHLight *light in cache.lights.allValues) {
        for (NSString *lightId in lightIds) {
            if ([lightId isEqualToString:light.identifier]) {
                [lightArray setObject:light forKey:light.identifier];
            }
        }
    }
    
    //ID Listエラーチェック
    if (lightArray.count <= 0) {
        _bridgeConnectState = STATE_ERROR_INVALID_LIGHTID;
        return YES;
    }
    
    //限界チェック
    if (cache.groups.count >= 16) {
        _bridgeConnectState = STATE_ERROR_LIMIT_GROUP;
        return YES;
    }
    
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{
        
        [bridgeSendAPI createGroupWithName:groupName lightIds:[lightArray allKeys] completionHandler:^(NSString *groupIdentifier, NSArray *errors) {
            if (errors != nil) {
                _bridgeConnectState = STATE_ERROR_CREATE_FAIL_GROUP;
            } else {
                _bridgeConnectState = STATE_CONNECT;
            }
            if (completion) {
                completion(groupIdentifier);
            }
        }];
    });
    return NO;
    
}

//ライトグループの削除
-(BOOL)removeLightGroupWithWithGroupId:(NSString*)groupId
                            completion:(void(^)())completion
{
    //groupIdチェック
    if (![self checkParamGroupId:groupId]) {
        return YES;
    }
    
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    dispatch_sync(dispatch_get_main_queue(), ^{
        [bridgeSendAPI removeGroupWithId:groupId completionHandler:^(NSArray *errors) {
            [self setCompletionWithResponseCompletion:completion errors:errors
                           errorState:STATE_ERROR_DELETE_FAIL_GROUP];
            
        }];
    });
    return NO;
}



//使用できるライトの検索
-(void)searchLightWithCompletion:(PHBridgeSendErrorArrayCompletionHandler)completion {
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    [bridgeSendAPI searchForNewLights:completion];
}

//ハートビートの有効化
-(void)enableHeartbeat {
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    if (cache != nil && cache.bridgeConfiguration != nil && cache.bridgeConfiguration.ipaddress != nil) {
        [phHueSDK enableLocalConnection];
    } else {
        [phHueSDK disableLocalConnection];
        [self searchBridgeWithCompletion:nil];
    }
}

//ハートビートの無効化
-(void)disableHeartbeat {
    [phHueSDK disableLocalConnection];
}

//PHNotificationManagerの解放
-(void)deallocPHNotificationManagerWithReceiver:(id)receiver {
    id registerReceiver = receiver;
    if (!registerReceiver) {
        registerReceiver = self;
    }

    if (notificationManager != nil) {
        [notificationManager deregisterObjectForAllNotifications:registerReceiver];
        notificationManager = nil;
    }
}

//HueSDKの解放
-(void)deallocHueSDK {
    if (phHueSDK != nil) {
        [phHueSDK disableLocalConnection];
        [phHueSDK stopSDK];
        phHueSDK = nil;
    }
}

#pragma mark - private method

//completionHandlerの共通処理
- (void) setCompletionWithResponseCompletion:(void(^)())completion
                        errors:(NSArray*)errors
                  errorState:(BridgeConnectState)errorState
{
    if (errors != nil) {
        _bridgeConnectState = errorState;
    } else {
        _bridgeConnectState = STATE_CONNECT;
    }
    if (completion) {
        completion();
    }
}


//パラメータチェック LightId
- (BOOL)checkParamLightId:(NSString*)lightId
{
    //LightIdが指定されてない場合はエラーで返す
    if (!lightId) {
        _bridgeConnectState = STATE_ERROR_NO_LIGHTID;
        return NO;
    }
    
    //キャッシュにあるライトの一覧からライトを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    for (PHLight *light in cache.lights.allValues) {
        if ([lightId isEqualToString:light.identifier]) {
            return YES;
        }
    }
    _bridgeConnectState = STATE_ERROR_NOT_FOUND_LIGHT;
    return NO;
}

//パラメータチェック groupId
- (BOOL)checkParamGroupId:(NSString*)groupId
{
    //groupIdが指定されてない場合はエラーで返す
    if (!groupId) {
        _bridgeConnectState = STATE_ERROR_NO_GROUPID;
        return NO;
    }
    if ([groupId isEqualToString:@"0"]) {
        _bridgeConnectState = STATE_ERROR_NO_GROUPID;
        
        return YES;
    }
    
    //キャッシュにあるグループの一覧からグループを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    for (PHGroup *group in cache.groups.allValues) {
        if ([groupId isEqualToString:group.identifier]) {
            return YES;
        }
    }
    
    _bridgeConnectState = STATE_ERROR_NOT_FOUND_GROUP;
    return NO;
}

//パラメータチェック 必須文字チェック
- (BOOL)checkParamRequiredStringItemWithParam:(NSString*)param
                        errorState:(BridgeConnectState)errorState
{
    
    //valueが指定されてない場合はエラーで返す
    if (param == nil) {
        param = @"";
    }
    if (param.length == 0) {
        _bridgeConnectState = errorState;
        return NO;
    }
    
    return YES;
}


//エラーの場合、エラー情報をresponseに設定しnilをreturn
- (PHLightState*) getLightStateIsOn:(BOOL)isOn
                     brightness:(double)brightness
                          color:(NSString *)color
{
    PHLightState *lightState = [[PHLightState alloc] init];
    
    [lightState setOnBool:isOn];
    
    if (isOn) {
        if (color) {
            if (color.length != 6) {
                _bridgeConnectState = STATE_ERROR_INVALID_COLOR;
                return nil;
            }
            
            CGPoint xyPoint = [self convRgbToXy:color];
            if (xyPoint.x != FLT_MIN && xyPoint.y != FLT_MIN) {
                [lightState setX:[NSNumber numberWithFloat:xyPoint.x]];
                [lightState setY:[NSNumber numberWithFloat:xyPoint.y]];
            } else {
                _bridgeConnectState = STATE_ERROR_INVALID_COLOR;
                return nil;
            }
        }else{
            
            CGPoint xyPoint = [self convRgbToXy:@"FFFFFF"];
            
            [lightState setX:[NSNumber numberWithFloat:xyPoint.x]];
            [lightState setY:[NSNumber numberWithFloat:xyPoint.y]];
            
        }
        
        if (brightness != DBL_MIN) {
            int myBlightness = brightness * 255;
            
            if (myBlightness < 0) {
                myBlightness = 0;
            }
            if (myBlightness > 254) {
                myBlightness = 254;
            }
            [lightState setBrightness:[NSNumber numberWithInt:(int)myBlightness]];
        }
    }
    
    return lightState;
}

/*!
 Lightのステータスチェンジ
 */
- (BOOL) changeLightStatusWithLightId:(NSString *)lightId
                lightState:(PHLightState*)lightState
                           completion:(void(^)())completion
{
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{
        [bridgeSendAPI updateLightStateForId:lightId withLightState:lightState completionHandler:^(NSArray *errors) {
            
            [self setCompletionWithResponseCompletion:completion
                                 errors:errors
                           errorState:STATE_ERROR_UPDATE_FAIL_LIGHT_STATE];
            
        }];
    });
    
    return NO;
}

/*
 Lightの名前チェンジ
 */
-(BOOL)changeLightNameWithLightId:(NSString *)lightId
                             name:(NSString *)name
                       completion:(void(^)())completion
{
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{
        
        for (PHLight *light in cache.lights.allValues) {
            if ([light.identifier isEqualToString:lightId]) {
                
                [light setName:name];
                
                [bridgeSendAPI updateLightWithLight:light completionHandler:^(NSArray *errors) {
                    
                    [self setCompletionWithResponseCompletion:completion
                                         errors:errors
                                   errorState:STATE_ERROR_CHANGE_FAIL_LIGHT_NAME];
                    
                }];
                
                break;
            }
        }
    });
    return NO;
}

/*!
 LightGroupのステータスチェンジ
 */
- (BOOL)changeGroupStatusWithGroupId:(NSString *)groupId
                          lightState:(PHLightState*)lightState
                          completion:(void(^)())completion
{
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{
        
        // Send lightstate to light
        [bridgeSendAPI setLightStateForGroupWithId:groupId lightState:lightState completionHandler:^(NSArray *errors) {
            
            [self setCompletionWithResponseCompletion:completion
                                 errors:errors
                           errorState:STATE_ERROR_UPDATE_FAIL_GROUP_STATE];
            
        }];
    });
    
    return NO;
}

/*!
 LightGroupのnameチェンジ
 */
- (BOOL) changeGroupNameWithGroupId:(NSString *)groupId
                               name:(NSString *)name
                         completion:(void(^)())completion
{
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{
        
        for (PHGroup *group in cache.groups.allValues) {
            if ([group.identifier isEqualToString:groupId]) {
                [group setName:name];
                [bridgeSendAPI updateGroupWithGroup:group completionHandler:^(NSArray *errors) {
                    
                    [self setCompletionWithResponseCompletion:completion
                                         errors:errors
                                   errorState:STATE_ERROR_CHANGE_FAIL_GROUP_NAME];
                    
                }];
                break;
            }
        }
    });
    return NO;
    
}

/*
 数値判定。
 */
- (BOOL)isDigitWithString:(NSString *)numberString {
    NSRange match = [numberString rangeOfString:@"^[-+]?([0-9]*)?(\\.)?([0-9]*)?$" options:NSRegularExpressionSearch];
    //数値の場合
    if(match.location != NSNotFound) {
        return YES;
    } else {
        _bridgeConnectState = STATE_ERROR_INVALID_BRIGHTNESS;
        return NO;
    }
}


#pragma mark - private method

/*
 Hue方式の色を取得する。
 エラーの場合は、xとyにFLT_MINを返す。
 */
- (CGPoint) convRgbToXy:(NSString *)color
{
    
    NSString *r = [color substringWithRange:NSMakeRange(0, 2)];
    NSString *g = [color substringWithRange:NSMakeRange(2, 2)];
    NSString *b = [color substringWithRange:NSMakeRange(4, 2)];
    
    NSScanner *scan = [NSScanner scannerWithString:r];
    
    unsigned int rr, gg, bb;
    
    if (![scan scanHexInt:&rr]) {
        return CGPointMake(FLT_MIN, FLT_MIN);
    }
    scan = [NSScanner scannerWithString:g];
    if (![scan scanHexInt:&gg]) {
        return CGPointMake(FLT_MIN, FLT_MIN);
    }
    scan = [NSScanner scannerWithString:b];
    if (![scan scanHexInt:&bb]) {
        return CGPointMake(FLT_MIN, FLT_MIN);
    }
    float fRR = (float)(rr/255.0);
    float fGG = (float)(gg/255.0);
    float fBB = (float)(bb/255.0);
    UIColor *uicolor = [UIColor colorWithRed:fRR green:fGG blue:fBB alpha:1.0f];
    
    CGPoint xyPoint = [PHUtilities calculateXY:uicolor forModel:@""];
    
    return xyPoint;
}


-(void)saveBridgeList {
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    [ud setObject:_hueBridgeList forKey:DPHueBridgeListName];
    [ud synchronize];
}


-(void)readBridgeList {
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    _hueBridgeList = [ud dictionaryForKey:DPHueBridgeListName].mutableCopy;
}
@end
