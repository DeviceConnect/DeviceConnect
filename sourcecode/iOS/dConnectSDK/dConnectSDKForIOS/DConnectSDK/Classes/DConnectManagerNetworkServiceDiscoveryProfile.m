//
//  DConnectManagerNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectManagerNetworkServiceDiscoveryProfile.h"
#import "DConnectManager+Private.h"
#import "DConnectEventManager.h"

/** NetworkDiscoveryのタイムアウト. */
#define DISCOVERY_TIMEOUT 8

@implementation DConnectManagerNetworkServiceDiscoveryProfile

- (id) init {
    
    self = [super init];
    if (self) {
        self.delegate = self;
    }

    return self;
}

#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * DISCOVERY_TIMEOUT);
    DConnectArray *services = [DConnectArray array];
    
    // 各デバイスプラグインからのレスポンスを受け取る
    DConnectManager *mgr = [DConnectManager sharedManager];
    DConnectDevicePluginManager *deviceMgr = mgr.mDeviceManager;
    NSArray *devices = [deviceMgr devicePluginList];
    
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_group_t discoveryGroup = dispatch_group_create();
    
    NSMutableArray *callbackCodes = [NSMutableArray array];
    
    for (DConnectDevicePlugin *plugin in devices) {
        DConnectResponseMessage *resp = [DConnectResponseMessage message];
        [callbackCodes addObject:resp.code];
        
        dispatch_semaphore_t sem = dispatch_semaphore_create(0);
        [mgr addCallback:^(DConnectResponseMessage *response) {
            
            int result = [response integerForKey:DConnectMessageResult];
            if (result == DConnectMessageResultTypeOk) {
                DConnectArray *s = [resp arrayForKey:DConnectNetworkServiceDiscoveryProfileParamServices];
                if (s && [s count] > 0) {
                    for (int i = 0; i < [s count]; i++) {
                        DConnectMessage *msg = [s messageAtIndex:i];
                        NSString *deviceId = [msg stringForKey:DConnectNetworkServiceDiscoveryProfileParamId];
                        if (deviceId) {
                            // デバイスIDにデバイスプラグインのIDを付加する
                            NSString *did = [deviceMgr deviceIdByAppedingPluginIdWithDevicePlugin:plugin
                                                                                         deviceId:deviceId];
                            [msg setString:did forKey:DConnectNetworkServiceDiscoveryProfileParamId];
                            @synchronized (services) {
                                [services addMessage:msg];
                            }
                        }
#ifdef DEBUG
                        else {
                            DCLogW(@"Not found id. %@", NSStringFromClass([plugin class]));
                        }
#endif
                    }
                }
            }
            dispatch_semaphore_signal(sem);
        } forKey:resp.code];

        dispatch_group_async(discoveryGroup, queue, ^{
            BOOL send = [plugin didReceiveRequest:request response:resp];
            if (send) {
                [mgr sendResponse:resp];
            }
            dispatch_semaphore_wait(sem, timeout);
        });
    }

    long result = dispatch_group_wait(discoveryGroup, timeout);
    
    DConnectArray *responseServices = nil;
    if (result != 0) {
        @synchronized (services) {
            // タイムアウトした場合はあとから処理されたものが追加されないようにコピーにしておく。
            responseServices = [services copy];
        }
        
        // ゴミが残ってしまうのでタイムアウトしたらコールバックを解除しておく
        for (NSString *code in callbackCodes) {
            [mgr removeCallbackForKey:code];
        }
    } else {
        responseServices = services;
    }
    
    // レスポンスを作成
    [response setResult:DConnectMessageResultTypeOk];
    [DConnectNetworkServiceDiscoveryProfile setServices:responseServices target:response];
    
    return YES;
}

- (BOOL)                    profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DConnectManager class]];
    
    DConnectEventError error = [mgr addEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorFailed:
        default:
            [response setErrorToUnknown];
            break;
    }
    return YES;
}

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DConnectManager class]];
    
    DConnectEventError error = [mgr removeEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:
            [response setErrorToInvalidRequestParameterWithMessage:@"event does not exist."];
            break;
        case DConnectEventErrorFailed:
        default:
            [response setErrorToUnknown];
            break;
    }
    return YES;
}


@end
