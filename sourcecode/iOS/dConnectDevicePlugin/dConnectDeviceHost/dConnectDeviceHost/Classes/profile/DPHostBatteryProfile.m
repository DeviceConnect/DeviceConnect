//
//  DPHostBatteryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHostDevicePlugin.h"
#import "DPHostBatteryProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"

@interface DPHostBatteryProfile ()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

- (void) sendOnChargingChangeEvent:(NSNotification *)notification;
- (void) sendOnBatteryChangeEvent:(NSNotification *)notification;

@end

@implementation DPHostBatteryProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
        
        // 一度YESにしたらずっとYESにしておく。
        // MARK: イベント配送の有無をみて、動的にYES/NOを切り替える実装の方が良いだろうか？
        [UIDevice currentDevice].batteryMonitoringEnabled = YES;
        
        // UIDeviceBatteryLevelDidChangeNotification通知の配送が開始されていないのなら、開始する。
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(sendOnBatteryChangeEvent:)
                                                     name:UIDeviceBatteryLevelDidChangeNotification
                                                   object:nil];
        // UIDeviceBatteryStateDidChangeNotification通知の配送が開始されていないのなら、開始する。
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(sendOnChargingChangeEvent:)
                                                     name:UIDeviceBatteryStateDidChangeNotification
                                                   object:nil];
        
    }
    return self;
}

- (void)dealloc
{
    // 通知の受領をやめる。
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void) sendOnChargingChangeEvent:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectBatteryProfileName
                                          attribute:DConnectBatteryProfileAttrOnChargingChange];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        DConnectMessage *battery = [DConnectMessage message];
        BOOL charging;
        switch ([notification.object batteryState]) {
            case UIDeviceBatteryStateFull:
            case UIDeviceBatteryStateCharging:
                charging = YES;
                break;
            case UIDeviceBatteryStateUnplugged:
                charging = NO;
                break;
            case UIDeviceBatteryStateUnknown:
            default:
                // 未知のステータス；イベントをそもそも発送しない。
                return;
        }
        [DConnectBatteryProfile setCharging:charging target:battery];
        [DConnectBatteryProfile setBattery:battery target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (void) sendOnBatteryChangeEvent:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectBatteryProfileName
                                          attribute:DConnectBatteryProfileAttrOnBatteryChange];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        DConnectMessage *battery = [DConnectMessage message];
        float level = [notification.object batteryLevel];
        if (level < 0) {
            // 未知のステータス；イベントをそもそも発送しない。
            return;
        }
        [DConnectBatteryProfile setLevel:level target:battery];
        [DConnectBatteryProfile setBattery:battery target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

#pragma mark - Get Methods

- (BOOL)        profile:(DConnectBatteryProfile *)profile
didReceiveGetAllRequest:(DConnectRequestMessage *)request
               response:(DConnectResponseMessage *)response
               deviceId:(NSString *)deviceId
{
    float level = [[UIDevice currentDevice] batteryLevel];
    NSNumber *charging;
    switch ([[UIDevice currentDevice] batteryState]) {
        case UIDeviceBatteryStateFull:
        case UIDeviceBatteryStateCharging:
            charging = @YES;
            break;
        case UIDeviceBatteryStateUnplugged:
            charging = @NO;
            break;
        case UIDeviceBatteryStateUnknown:
        default:
            // 未知のステータス
            charging = nil;
            break;
    }
    if (level >= 0 && level <= 1) {
        [DConnectBatteryProfile setLevel:level target:response];
    }
    if (charging) {
        [DConnectBatteryProfile setCharging:[charging boolValue] target:response];
    }
    if ((level >= 0 && level <= 1) || charging) {
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        // 未知のステータス；エラーレスポンスを返す。
        [response setErrorToUnknownWithMessage:@"Battery status is unknown."];
    }
    return YES;
}

- (BOOL)          profile:(DConnectBatteryProfile *)profile
didReceiveGetLevelRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
    float level = [[UIDevice currentDevice] batteryLevel];
    if (level < 0 || level > 1) {
        // 未知のステータス；エラーレスポンスを返す。
        [response setErrorToUnknownWithMessage:@"Battery status is unknown."];
    } else {
        [DConnectBatteryProfile setLevel:level target:response];
        [response setResult:DConnectMessageResultTypeOk];
    }
    return YES;
}

- (BOOL)             profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
{
    NSNumber *charging;
    switch ([[UIDevice currentDevice] batteryState]) {
        case UIDeviceBatteryStateFull:
        case UIDeviceBatteryStateCharging:
            charging = @YES;
            break;
        case UIDeviceBatteryStateUnplugged:
            charging = @NO;
            break;
        case UIDeviceBatteryStateUnknown:
        default:
            // 未知のステータス；エラーレスポンスを返す。
            [response setErrorToUnknownWithMessage:@"Battery status is unknown."];
            return YES;
    }
    [DConnectBatteryProfile setCharging:[charging boolValue] target:response];
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL)                     profile:(DConnectBatteryProfile *)profile
didReceivePutOnChargingChangeRequest:(DConnectRequestMessage *)request
                            response:(DConnectResponseMessage *)response
                            deviceId:(NSString *)deviceId
                          sessionKey:(NSString *)sessionKey
{
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

- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
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
#pragma mark Event Unregistration

- (BOOL)                        profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
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
    
    return YES;
}

- (BOOL)                       profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
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
    
    return YES;
}

@end
