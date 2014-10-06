//
//  ViewController.m
//  DeviceConnectUIAppTutorial
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "ViewController.h"
#import <DConnectSDK/DConnectSDK.h>

/*!
 セマフォのタイムアウト時間[秒]
 */
static int const _timeout = 100;


/*!
 セッションキー
 */
static NSString *const SESSION_KEY = @"SESSION_KEY";

@interface ViewController () {
 
    __weak IBOutlet UIButton *createClientBtn;
    __weak IBOutlet UIButton *requestAccessTokenBtn;
    __weak IBOutlet UIButton *sendNotificationBtn;
    __weak IBOutlet UIButton *deviceOrientationBtn;
    
    __weak IBOutlet UILabel *deviceInfo;
    __weak IBOutlet UILabel *clientInfo;
    __weak IBOutlet UILabel *accessTokenInfo;
    __weak IBOutlet UILabel *notificationInfo;
    __weak IBOutlet UILabel *deviceOrientationInfo;
    __weak IBOutlet UILabel *deviceOrientationEventInfo;
    
    DConnectArray *_services;
    NSString *_deviceId;
    NSString *_clientId;
    NSString *_clientSecret;
    NSString *_accessToken;
}

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    _services = nil;
    _deviceId = nil;
    _clientId = nil;
    _clientSecret = nil;
    _accessToken = nil;
    
    [self displayInfo];

	// DConnectManagerの初期化
	DConnectManager *mgr = [DConnectManager sharedManager];
	// DConnectManagerの開始
	[mgr start];
//	// websocketサーバの起動
//	[mgr startWebsocket];
	
	// nativeで作成する場合にはdelegateを設定すること
	mgr.delegate = self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// delegate
- (void)manager:(DConnectManager *)manager didReceiveDConnectMessage:(DConnectMessage *)event {
    
    NSLog(@"receive delegate");
}

- (IBAction)touchUpNetworkServiceDiscovery:(UIButton *)sender {

    // スレッド起動
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

        [self callNetworkServiceDiscoveryAPI];
    });
}
                   
#if 0
- (IBAction)touchUpNetworkServiceDiscovery_:(UIButton *)sender {
    
	// network_service_discoveryを実行する
    DConnectManager *mgr = [DConnectManager sharedManager];
	DConnectRequestMessage *request = [[DConnectRequestMessage alloc]init];
	[request setAction: DConnectMessageActionTypeGet];
	[request setProfile: DConnectNetworkServiceDiscoveryProfileName];
	[request setAttribute: DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices];
    
    NSLog(@"mgr request");
	[mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
        NSLog(@"mgr request(callback)");
        if (response != nil) {
            NSLog(@" - response is not null");
            NSLog(@" - response - result: %d", [response result]);
            if ([response result] == DConnectMessageResultTypeOk) {
                DConnectArray *services = [response arrayForKey: DConnectNetworkServiceDiscoveryProfileParamServices];
                NSLog(@" - response - services: %d", [services count]);
                
                int serviceCount = [services count];
                for (int s = 0; s < serviceCount; s++) {
                    DConnectMessage *service = [services messageAtIndex: s];
                    
                    NSLog(@" - response - service[%d] -----", s);
                    
                    NSLog(@" --- id:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamId]);
                    NSLog(@" --- name:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamName]);
                    NSLog(@" --- type:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamType]);
                    NSLog(@" --- online:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamOnline]);
                    NSLog(@" --- config:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamConfig]);
                    
                    // スレッド起動
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    
                        // デバイスシステムAPIを実行し、デバイスがサポートするプロファイルを取得する
                        NSString *deviceId = [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamId];
                        [self callDeviceSystemAPI: deviceId];
                        
                        // クライアント登録
                        [self callCreateClientAPI: deviceId];
                        
                        // アクセストークン発行
                        [self callCreateAccessTokenAPI: deviceId];

                        // Notificationを送信
                        [self callNotificationAPI: deviceId];
                    });
                }
                
            } else {
                NSLog(@" - response - errorCode: %d", [response errorCode]);
            }
            
            
        } else {
            NSLog(@" - response is null");
        }
        
        
    }];
}
#endif



- (void)callDeviceSystemAPI: (NSString *)deviceId {
    
    /* セマフォ準備 */
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * _timeout);
    
    // device system APIを実行する
    DConnectManager *mgr = [DConnectManager sharedManager];
	DConnectRequestMessage *request = [[DConnectRequestMessage alloc]init];
	[request setAction: DConnectMessageActionTypeGet];
    [request setDeviceId: deviceId];
    [request setApi: DConnectMessageAPI];
    [request setProfile: DConnectSystemProfileName];
	[request setAttribute: DConnectSystemProfileAttrDevice];
    
    NSLog(@"mgr request(device system API)");
	[mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
        NSLog(@"mgr request(callback)");
        if (response != nil) {
            NSLog(@" - response is not null");
            NSLog(@" - response - result: %d", [response result]);
            if ([response result] == DConnectMessageResultTypeOk) {
                DConnectArray *supports = [response arrayForKey: DConnectSystemProfileParamSupports];
                NSLog(@" - response - supports: %d", [supports count]);
                
                int supportCount = [supports count];
                for (int s = 0; s < supportCount; s++) {
                    NSString *support = [supports stringAtIndex: s];
                    
                    NSLog(@" - response - support[%d]: [%@]", s, support);
                }
            } else {
                NSLog(@" - response - errorCode: %d", [response errorCode]);
            }
        }
        
        /* Wait解除 */
        dispatch_semaphore_signal(semaphore);
    }];
    
    /* 応答が返るまでWait */
    dispatch_semaphore_wait(semaphore, timeout);
    NSLog(@"mgr request(device system API) finish");
}

/*!
 network_service_discoveryを実行する
 @return YES 成功(_servicesに値格納)
 @return NO 失敗
 */
- (BOOL)callNetworkServiceDiscoveryAPI {
    
    _services = nil;
    __block BOOL result = NO;
    
    /* セマフォ準備 */
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * _timeout);
    
	// network_service_discoveryを実行する
    DConnectManager *mgr = [DConnectManager sharedManager];
	DConnectRequestMessage *request = [[DConnectRequestMessage alloc]init];
	[request setAction: DConnectMessageActionTypeGet];
	[request setProfile: DConnectNetworkServiceDiscoveryProfileName];
	[request setAttribute: DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices];
    
    NSLog(@"mgr request(network discovery API)");
	[mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
        if (response != nil) {
            NSLog(@" - response is not null");
            NSLog(@" - response - result: %d", [response result]);
            if ([response result] == DConnectMessageResultTypeOk) {
                DConnectArray *services = [response arrayForKey: DConnectNetworkServiceDiscoveryProfileParamServices];
                NSLog(@" - response - services: %d", [services count]);
                
                _services = services;

                int serviceCount = [services count];
                for (int s = 0; s < serviceCount; s++) {
                    DConnectMessage *service = [services messageAtIndex: s];
                    
                    NSLog(@" - response - service[%d] -----", s);
                    
                    NSLog(@" --- id:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamId]);
                    NSLog(@" --- name:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamName]);
                    NSLog(@" --- type:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamType]);
                    NSLog(@" --- online:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamOnline]);
                    NSLog(@" --- config:%@", [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamConfig]);
                }
                
                result = YES;
                
            } else {
                NSLog(@" - response - errorCode: %d", [response errorCode]);
            }
        }
        
        /* Wait解除 */
        dispatch_semaphore_signal(semaphore);
    }];
    
    /* 応答が返るまでWait */
    dispatch_semaphore_wait(semaphore, timeout);
    NSLog(@"mgr request(request(network discovery API) finish");
    
    return result;
}

// クライアント登録
- (BOOL)callCreateClientAPI: (NSString *)deviceId {
    
    _clientId = nil;
    _clientSecret = nil;
    __block BOOL result = NO;
    
    /* セマフォ準備 */
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * _timeout);
    
    // パッケージ名取得(bundleIdentifierを渡す)
    NSString *package = [self packageName];
    
    // create_clientを実行する
    DConnectManager *mgr = [DConnectManager sharedManager];
    DConnectRequestMessage *request = [[DConnectRequestMessage alloc]init];
    [request setAction: DConnectMessageActionTypeGet];
    [request setApi: DConnectMessageAPI];
    [request setProfile: DConnectAuthorizationProfileName];
    [request setAttribute: DConnectAuthorizationProfileAttrCreateClient];
    [request setDeviceId: deviceId];
    [request setString: package forKey: DConnectAuthorizationProfileParamPackage];
    
    NSLog(@"mgr request(create_client API)");
    NSLog(@" - package: %@", package);
    [mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
        NSLog(@"mgr request(callback)");
        if (response != nil) {
            NSLog(@" - response is not null");
            NSLog(@" - response - result: %d", [response result]);
            if ([response result] == DConnectMessageResultTypeOk) {
                NSString *clientId = [response stringForKey: DConnectAuthorizationProfileParamClientId];
                NSString *clientSecret = [response stringForKey: DConnectAuthorizationProfileParamClientSecret];
                NSLog(@" - response - clientId: %@", clientId);
                NSLog(@" - response - clientSecret: %@", clientSecret);
                
                _clientId = clientId;
                _clientSecret = clientSecret;
                result = YES;
                
            } else {
                NSLog(@" - response - errorCode: %d", [response errorCode]);
            }
        }
        
        /* Wait解除 */
        dispatch_semaphore_signal(semaphore);
    }];
    
    /* 応答が返るまでWait */
    dispatch_semaphore_wait(semaphore, timeout);
    NSLog(@"mgr request(create_client API) finish");
    
    return result;
}

// アクセストークン要求
- (BOOL)callRequestAccessTokenAPI: (NSString *)deviceId {
    
    _accessToken = nil;
    __block BOOL result = NO;
    
    NSString *applicationName = @"テストアプリ";
    NSString *grantType = DConnectAuthorizationProfileGrantTypeAuthorizationCode;
    NSArray *scopes = [@[DConnectDeviceOrientationProfileName, DConnectNotificationProfileName]
                       sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];;
    NSMutableString *scope = [NSMutableString new];
    for (int i = 0; i < scopes.count - 1; ++i) {
        [scope appendString:scopes[i]];
        [scope appendString:@","];
    }
    [scope appendString:[scopes lastObject]];
    
    /* セマフォ準備 */
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * _timeout);
    
    /* signature作成 */
    NSString *signature = [DConnectUtil generateSignatureWithClientId: _clientId
                                                            grantType: grantType
                                                             deviceId: deviceId
                                                               scopes: scopes
                                                         clientSecret: _clientSecret];
    
    // request_accesstokenを実行する
    DConnectManager *mgr = [DConnectManager sharedManager];
    DConnectRequestMessage *request = [[DConnectRequestMessage alloc]init];
    [request setAction: DConnectMessageActionTypeGet];
    [request setApi: DConnectMessageAPI];
    [request setProfile: DConnectAuthorizationProfileName];
    [request setAttribute: DConnectAuthorizationProfileAttrRequestAccessToken];
    [request setDeviceId: deviceId];
    [request setString: _clientId forKey: DConnectAuthorizationProfileParamClientId];
    [request setString: grantType forKey: DConnectAuthorizationProfileParamGrantType];
    [request setString: scope forKey:DConnectAuthorizationProfileParamScope];
    [request setString: applicationName forKey:DConnectAuthorizationProfileParamApplicationName];
    [request setString: signature forKey:DConnectAuthorizationProfileParamSignature];
    
    
    NSLog(@"mgr request(request_accesstoken API)");
    NSLog(@"signature: %@", signature);
    [mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
        NSLog(@"mgr request(callback)");
        if (response != nil) {
            NSLog(@" - response is not null");
            NSLog(@" - response - result: %d", [response result]);
            if ([response result] == DConnectMessageResultTypeOk) {
                NSString *accessToken = [response stringForKey: DConnectAuthorizationProfileParamAccessToken];
                _accessToken = accessToken;
                result = YES;
                NSLog(@" - response - accessToken: %@", accessToken);
            } else {
                NSLog(@" - response - errorCode: %d", [response errorCode]);
            }
        }
        
        /* Wait解除 */
        dispatch_semaphore_signal(semaphore);
    }];
    
    /* 応答が返るまでWait */
    dispatch_semaphore_wait(semaphore, timeout);
    NSLog(@"mgr request(request_accesstoken API) finish");
    return result;
}




// Notificationを送信
- (BOOL)callNotificationAPI: (NSString *)deviceId {
    
    __block BOOL result = NO;
    
    /* セマフォ準備 */
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * _timeout);
	
    // notifyを実行する
    DConnectRequestMessage *request = [DConnectRequestMessage new];
    [request setAction: DConnectMessageActionTypePost];
    [request setApi: @"gotapi"];
    [request setProfile: DConnectNotificationProfileName];
    [request setAttribute: DConnectNotificationProfileAttrNotify];
    [request setDeviceId: deviceId];
    [request setAccessToken: _accessToken];
    
    [request setInteger: DConnectNotificationProfileNotificationTypeMail forKey: DConnectNotificationProfileParamType];
    [request setString: @"notification message" forKey: DConnectNotificationProfileParamBody];

    NSLog(@"mgr request(notification API)");
DConnectManager *mgr = [DConnectManager sharedManager];
[mgr sendRequest: request callback:^(DConnectResponseMessage *response) {
    NSLog(@"mgr request(callback)");
    if (response != nil) {
        NSLog(@" - response is not null");
        NSLog(@" - response - result: %d", [response result]);
        if ([response result] == DConnectMessageResultTypeOk) {
            NSString *notificationId = [response stringForKey: DConnectNotificationProfileParamNotificationId];
            NSLog(@" - response - OK notificationId: %@", notificationId);
            result = YES;
        } else {
            NSLog(@" - response - errorCode: %d", [response errorCode]);
        }
    }
    
    /* Wait解除 */
    dispatch_semaphore_signal(semaphore);
}];
    
    /* 応答が返るまでWait */
    dispatch_semaphore_wait(semaphore, timeout);
    NSLog(@"mgr request(notification API) finish");
    return result;
}

// パッケージ名取得(bundleIdentifierを渡す)
- (NSString *)packageName {
    NSBundle *bundle = [NSBundle mainBundle];
    NSString *package = [bundle bundleIdentifier];
//    NSString *package = @"packagename";
    return package;
}

/*!
 デバイス検索
 */
- (IBAction)onDeviceSearch:(id)sender {
    
    // スレッド起動
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

        /* network_service_discoveryを実行しデバイスを1件選択する */
        BOOL result = [self callNetworkServiceDiscoveryAPI];
        
        NSString *message = nil;
        if (result == YES) {
            if (_services != nil && [_services count] > 0) {
                DConnectMessage *service = [_services messageAtIndex: 0];
                _deviceId = [service stringForKey: DConnectNetworkServiceDiscoveryProfileParamId];
                message = @"処理成功しました。";
                [createClientBtn setEnabled:YES];
            } else {
                message = @"serviceが見つかりませんでした。";
            }
        } else {
            message = @"タイムアウトしました。";
        }
        
        // 通知
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_deviceId != nil) {
                [deviceInfo setText: [NSString stringWithFormat: @"deviceId: %@", _deviceId]];
            } else {
                [deviceInfo setText: @""];
            }
            UIAlertView *alert =
                [[UIAlertView alloc]
                 initWithTitle: @"デバイス検索"
                 message: message
                 delegate:nil
                 cancelButtonTitle:nil
                 otherButtonTitles:@"OK", nil
                 ];
                [alert show];
        });
    });
}

/*!
 クライアント登録
 */
- (IBAction)onCreateClient:(id)sender {
    
    // DeviceSystemを実行する
    [self callDeviceSystemAPI: _deviceId];
    
    // スレッド起動
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        // クライアント登録
        BOOL result = [self callCreateClientAPI: _deviceId];
        
        NSString *message = nil;
        if (result == YES) {
            message = @"処理成功しました。";
            [requestAccessTokenBtn setEnabled:YES];
        } else {
            message = @"タイムアウトしました。";
        }
        
        // 通知
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_clientId != nil) {
                [clientInfo setText: [NSString stringWithFormat: @"clientId: %@", _clientId]];
            } else {
                [clientInfo setText: @""];
            }
            
            UIAlertView *alert =
            [[UIAlertView alloc]
             initWithTitle: @"クライアント登録"
             message: message
             delegate:nil
             cancelButtonTitle:nil
             otherButtonTitles:@"OK", nil
             ];
            [alert show];
        });
    });
}


/*!
 アクセストークン要求
 */
- (IBAction)onRequestAccessToken:(id)sender {
    
    // スレッド起動
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        // アクセストークン要求
        BOOL result = [self callRequestAccessTokenAPI: _deviceId];
        
        NSString *message = nil;
        if (result == YES) {
            message = @"処理成功しました。";
            [sendNotificationBtn setEnabled:YES];
        } else {
            message = @"タイムアウトしました。";
        }
        
        // 通知
        dispatch_async(dispatch_get_main_queue(), ^{
            if (_accessToken != nil) {
                [accessTokenInfo setText: [NSString stringWithFormat: @"accessToken: %@", _accessToken]];
            } else {
                [accessTokenInfo setText: @""];
            }
            
            UIAlertView *alert =
            [[UIAlertView alloc]
             initWithTitle: @"アクセストークン要求"
             message: message
             delegate:nil
             cancelButtonTitle:nil
             otherButtonTitles:@"OK", nil
             ];
            [alert show];
        });
    });
}

/*!
 Notification送信
 */
- (IBAction)onSendNotification:(id)sender {
    
    // スレッド起動
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        // Notification送信
        BOOL result = [self callNotificationAPI:_deviceId];
        
        NSString *message = nil;
        if (result == YES) {
            message = @"処理成功しました。";
            [deviceOrientationBtn setEnabled:YES];
        } else {
            message = @"タイムアウトしました。";
        }
        
        // 通知
        dispatch_async(dispatch_get_main_queue(), ^{
            [notificationInfo setText: [NSString stringWithFormat: @"notification: %@", message]];
            
            UIAlertView *alert =
            [[UIAlertView alloc]
             initWithTitle: @"Notification送信"
             message: message
             delegate:nil
             cancelButtonTitle:nil
             otherButtonTitles:@"OK", nil
             ];
            [alert show];
        });
    });
}

static DConnectEventHelper *_helper = nil;

/*!
 DeviceOrientationイベント
 */
- (IBAction)onDeviceOrientaionEvent:(id)sender {
   
    // DeviceOrientationイベント登録リクエスト
    DConnectRequestMessage *request = [DConnectRequestMessage new];
    [request setAction: DConnectMessageActionTypePut];
    [request setDeviceId: _deviceId];
    [request setProfile: DConnectDeviceOrientationProfileName];
    [request setAttribute: DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
    [request setSessionKey: SESSION_KEY];
    [request setAccessToken:_accessToken];
    
    
    [[DConnectEventHelper sharedHelper] registerEventWithRequest: request
                     responseHandler: ^(DConnectResponseMessage *response)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            NSString *message = nil;
            if ([response result] == DConnectMessageResultTypeOk) {
                message = @"処理成功しました。";
            } else {
                message = @"処理失敗しました。";
            }
            [deviceOrientationInfo setText: [NSString stringWithFormat: @"event: %@", message]];
        });
    }
                      messageHandler:^(DConnectMessage *message)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            
            DConnectMessage *orientation = [message messageForKey: DConnectDeviceOrientationProfileParamOrientation];
            
            DConnectMessage *acceleration = [orientation  messageForKey: DConnectDeviceOrientationProfileParamAcceleration];

            double x = [acceleration doubleForKey: DConnectDeviceOrientationProfileParamX];
            double y = [acceleration doubleForKey: DConnectDeviceOrientationProfileParamY];
            double z = [acceleration doubleForKey: DConnectDeviceOrientationProfileParamZ];
            
            [deviceOrientationEventInfo setText: [NSString stringWithFormat: @"acceleration: x=%.1lf, y=%.1lf, z=%.1lf", x, y, z]];
            
        });
    }];
}

/*!
 情報表示
 */
- (void) displayInfo {
    
    if (_deviceId != nil) {
        [deviceInfo setText: _deviceId];
    } else {
        [deviceInfo setText: @""];
    }
    
    if (_clientId != nil) {
        [clientInfo setText: _clientId];
    } else {
        [clientInfo setText: @""];
    }
    
    if (_accessToken != nil) {
        [accessTokenInfo setText: _accessToken];
    } else {
        [accessTokenInfo setText: @""];
    }
    
    [notificationInfo setText: @""];
}


@end
