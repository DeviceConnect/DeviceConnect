//
//  DicePlusNetworkServiceDiscoveryProfile.m
//  dConnectDeviceDicePlus
//
//  Created by 星貴之 on 2014/07/01.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPHueNetworkServiceDiscoveryProfile.h"
#import <HueSDK_iOS/HueSDK.h>
#import "DCLogger.h"

//======================================================================
@interface DPHueNetworkServiceDiscoveryProfile()

@property (nonatomic, weak) DConnectResponseMessage *resHue;
@property (nonatomic, strong) PHBridgeSearching *bridgeSearching;

@end

//======================================================================
@implementation DPHueNetworkServiceDiscoveryProfile

PHHueSDK *phHueSDK;
PHNotificationManager *notificationManager;

NSString *const HueDeviceName = @"hue";
DCLogger *mlog;

//======================================================================
- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    
    mlog = [[DCLogger alloc]initWithSourceClass:self];
    
    return self;
    
}

//======================================================================
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    [mlog entering:@"didReceiveGetGetNetworkServicesRequest" param:nil];
    
    // レスポンスに設定する
    [response setResult:DConnectMessageResultTypeOk];
    
    dispatch_sync(dispatch_get_main_queue(), ^{

        [self initHueSdk];
        [self disableLocalHeartbeat];

        [self.bridgeSearching startSearchWithCompletionHandler:^(NSDictionary *bridgesFound)
        {
            
            [mlog fine:@"didReceiveGetGetNetworkServicesRequest" paramName:@"bridgesFound.count" paramUint:bridgesFound.count];
            
            DConnectArray *services = [DConnectArray array];
            DConnectMessage *service = nil;

            
            // Check for results
            if (bridgesFound.count > 0) {
                NSString * deviceId = @"";
                
                for (id key in [bridgesFound keyEnumerator]) {

                    deviceId = [NSString stringWithFormat:@"%@_%@",[bridgesFound valueForKey:key],key];


                                             
                    [mlog fine:@"didReceiveGetGetNetworkServicesRequest" paramName:@"DeviceID" paramString:deviceId];
                    
                    service = [DConnectMessage new];
                    
                    [DConnectNetworkServiceDiscoveryProfile
                     setId:deviceId
                     target:service];
                    
                    [DConnectNetworkServiceDiscoveryProfile
                     setName:[NSString stringWithFormat:@"%@ %@",HueDeviceName,key]
                     target:service];
                    
                    [DConnectNetworkServiceDiscoveryProfile
                     setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
                     
                     target:service];
                    [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
                    
                    [services addMessage:service];

                }

            }
            [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];

            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        
        [self disableHeartBeat];
        
    });
    
    [mlog exiting:@"didReceiveGetGetNetworkServicesRequest" param:nil];
    
    return NO;
}

//======================================================================
/**
 Stops the local heartbeat
 */
- (void)disableLocalHeartbeat {
    [phHueSDK disableLocalConnection];
}

//======================================================================
//Hue SDKの初期化
- (void)initHueSdk
{
    
    //ブリッジとの接続などを一度切る
    [self disableHeartBeat];
    
    phHueSDK = [[PHHueSDK alloc] init];
    
    [phHueSDK startUpSDK];
    
    //    [mlog fine:@"viewDidLoad" param:@"After startUpSDK"];
    
    [phHueSDK enableLogging:YES];
    
    //    [mlog fine:@"viewDidLoad" param:@"After enableLogging"];
    
    
    notificationManager = [PHNotificationManager defaultManager];
    
    [notificationManager registerObject:self
                                withSelector:@selector(localConnection)
                             forNotification:LOCAL_CONNECTION_NOTIFICATION];
    
    [notificationManager registerObject:self
                                withSelector:@selector(noLocalConnection)
                             forNotification:NO_LOCAL_CONNECTION_NOTIFICATION];
    
    [notificationManager registerObject:self
                                withSelector:@selector(notAuthenticated)
                             forNotification:NO_LOCAL_AUTHENTICATION_NOTIFICATION];
    
    self.bridgeSearching = [[PHBridgeSearching alloc] initWithUpnpSearch:YES andPortalSearch:YES andIpAdressSearch:NO];
    
    [self enableLocalHeartbeat];
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
//接続した時のイベント
- (void)localConnection {
    
    [mlog entering:@"localConnection" param:nil];
    
  
    
    //HueSDKの開放
    [self disableHeartBeat];
}

//======================================================================
//接続が切れた場合のイベント
- (void)noLocalConnection {
    
    [mlog entering:@"noLocalConnection" param:nil];
    
    //HueSDK開放
    [self disableHeartBeat];
    
}

//======================================================================
//アプリ登録されていない場合のイベント
- (void)notAuthenticated {
    
    [mlog entering:@"notAuthenticated" param:nil];
    
    //HueSDK開放
    [self disableHeartBeat];
    
}

@end
