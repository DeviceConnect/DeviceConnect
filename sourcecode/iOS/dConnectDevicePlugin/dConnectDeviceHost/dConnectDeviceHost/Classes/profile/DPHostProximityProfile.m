//
//  DPHostProximityProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHostDevicePlugin.h"
#import "DPHostProximityProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"

@interface DPHostProximityProfile ()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

- (void) sendOnUserProximityEvent:(NSNotification *)notification;

@end

@implementation DPHostProximityProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        [UIDevice currentDevice].proximityMonitoringEnabled = YES;
        
        if (![UIDevice currentDevice].proximityMonitoringEnabled) {
            // YESを設定したのにNOのまま；近接センサーがサポートされてないので、
            // そもそもプロファイルをインスタンス化させないでDevice System APIにProximity
            // プロファイルが表示されない様にする。
            return nil;
        }
        
        // YESを設定してYES；近接センサーがサポートされている。
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
    }
    return self;
}

- (void)dealloc
{
    // 通知の受領をやめる。
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) sendOnUserProximityEvent:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectProximityProfileName
                                          attribute:DConnectProximityProfileAttrOnUserProximity];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        DConnectMessage *proximity = [DConnectMessage message];
        [DConnectProximityProfile setNear:[notification.object proximityState] target:proximity];
        [DConnectProximityProfile setProximity:proximity target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

#pragma mark - Put Methods
#pragma mark Event Regstration

- (BOOL)                    profile:(DConnectProximityProfile *)profile
didReceivePutOnUserProximityRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    NSArray *evts = [_eventMgr eventListForDeviceId:deviceId
                                            profile:DConnectProximityProfileName
                                          attribute:DConnectProximityProfileAttrOnUserProximity];
    if (evts.count == 0) {
        // UIDeviceProximityStateDidChangeNotification通知の配送が開始されていないのなら、開始する。
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(sendOnUserProximityEvent:)
                                                     name:UIDeviceProximityStateDidChangeNotification
                                                   object:nil];
    }
    
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregstration

- (BOOL)                       profile:(DConnectProximityProfile *)profile
didReceiveDeleteOnUserProximityRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    NSArray *evts = [_eventMgr eventListForDeviceId:deviceId
                                            profile:DConnectBatteryProfileName
                                          attribute:DConnectBatteryProfileAttrOnChargingChange];
    if (evts.count == 0) {
        // イベント受領先が存在しないなら、UIDeviceProximityStateDidChangeNotification通知の配送処理を停止する。
        [[NSNotificationCenter defaultCenter] removeObserver:self name:UIDeviceProximityStateDidChangeNotification object:nil];
    }
    
    return YES;
}

@end
