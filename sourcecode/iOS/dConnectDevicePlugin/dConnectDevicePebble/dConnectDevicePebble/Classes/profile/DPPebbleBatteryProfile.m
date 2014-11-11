//
//  DPPebbleBatteryProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleBatteryProfile.h"
#import "DPPebbleDevicePlugin.h"

/** パーセント値にする時の定数. */
#define TO_PERCENT 100.0


@interface DPPebbleBatteryProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;

/*!
 @brief PebbleID保存用変数。
 */
@property (nonatomic) NSString *pDeviceId;

@end


@implementation DPPebbleBatteryProfile

-(NSString*)getDeviceId{
    return self.pDeviceId;
}
-(void)setDeviceId:(DConnectRequestMessage*)request{
    self.pDeviceId = request.deviceId;
}

- (id) initWithDevicePlugin:(DPPebbleDevicePlugin *)plugin {
    self = [super init];
    if (self) {
        self.mgr = plugin.mgr;
        self.delegate = self;
        __unsafe_unretained typeof(self) eventCallbackSelf = self;
        
        [self.mgr addEventCallback:^(NSDictionary *message) {
            if(message != nil) {
                NSNumber *attributeNum = message [@(KEY_ATTRIBUTE)];
                
                switch ([attributeNum intValue]) {
                    case BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE:
                        [eventCallbackSelf sendOnBatteryChange:plugin PebbleDictionary:message];
                        break;
                    case BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE:
                        [eventCallbackSelf sendOnChargingChange:plugin PebbleDictionary:message];
                        break;
                }
            }
            
            
        }
                           profile:@(PROFILE_BATTERY)];
        
        
    }
    
    return self;
}


#pragma mark - DConnectBatteryProfileDelegate

- (BOOL)        profile:(DConnectBatteryProfile *)profile
didReceiveGetAllRequest:(DConnectRequestMessage *)request
               response:(DConnectResponseMessage *)response
               deviceId:(NSString *)deviceId
{
    NSString* mDeviceId = [self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_ALL];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_GET];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                NSNumber *charging = dic[@(KEY_PARAM_BATTERY_CHARGING)];
                NSNumber *level = dic[@(KEY_PARAM_BATTERY_LEVEL)];
                BOOL c = [charging intValue] == BATTERY_CHARGING_ON;
                double l = [level intValue] / (double)TO_PERCENT;
                [response setResult:DConnectMessageResultTypeOk];
                [DConnectBatteryProfile setLevel:l target:response];
                [DConnectBatteryProfile setCharging:c target:response];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

- (BOOL)          profile:(DConnectBatteryProfile *)profile
didReceiveGetLevelRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
    NSString* mDeviceId = [self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_LEVEL];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_GET];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                NSNumber *level = dic[@(KEY_PARAM_BATTERY_LEVEL)];
                double l = [level intValue] / (double)TO_PERCENT;
                [response setResult:DConnectMessageResultTypeOk];
                [DConnectBatteryProfile setLevel:l target:response];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

- (BOOL)             profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_CHARING];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_GET];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                NSNumber *charging = dic[@(KEY_PARAM_BATTERY_CHARGING)];
                BOOL c = [charging intValue] == BATTERY_CHARGING_ON;
                [response setResult:DConnectMessageResultTypeOk];
                [DConnectBatteryProfile setCharging:c target:response];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

#pragma mark - Put Methods

- (BOOL)                     profile:(DConnectBatteryProfile *)profile
didReceivePutOnChargingChangeRequest:(DConnectRequestMessage *)request
                            response:(DConnectResponseMessage *)response
                            deviceId:(NSString *)deviceId
                          sessionKey:(NSString *)sessionKey
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
        
    } else {
        [self setDeviceId:request];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_PUT];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {}];
        Class class = [DPPebbleDevicePlugin class];
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:class];
        DConnectEventError error = [mgr addEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                break;
            case DConnectEventErrorInvalidParameter:
                [response setErrorToInvalidRequestParameter];
                break;
            case DConnectEventErrorNotFound:
            case DConnectEventErrorFailed:
            default:
                [response setErrorToUnknown];
                break;
        }
        return YES;
        
    }
}

- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    
    
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
        
    } else {
        [self setDeviceId:request];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_PUT];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {}];
        Class class = [DPPebbleDevicePlugin class];
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:class];
        DConnectEventError error = [mgr addEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                break;
            case DConnectEventErrorInvalidParameter:
                [response setErrorToInvalidRequestParameter];
                break;
            case DConnectEventErrorNotFound:
            case DConnectEventErrorFailed:
            default:
                [response setErrorToUnknown];
                break;
        }
        return YES;
    }
    
}

#pragma mark - Delete Methods

- (BOOL)                        profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
        
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BATTERY];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_DELETE];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                Class class = [DPPebbleDevicePlugin class];
                DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:class];
                DConnectEventError error = [mgr removeEventForRequest:request];
                switch (error) {
                    case DConnectEventErrorNone:
                        [response setResult:DConnectMessageResultTypeOk];
                        break;
                    case DConnectEventErrorInvalidParameter:
                        [response setErrorToInvalidRequestParameter];
                        break;
                    case DConnectEventErrorNotFound:
                    case DConnectEventErrorFailed:
                    default:
                        [response setErrorToUnknown];
                        break;
                }
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

- (BOOL)                       profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
        
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
        dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE);
        dic[@(KEY_ACTION)] = @(ACTION_DELETE);
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                Class class = [DPPebbleDevicePlugin class];
                DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:class];
                DConnectEventError error = [mgr removeEventForRequest:request];
                switch (error) {
                    case DConnectEventErrorNone:
                        [response setResult:DConnectMessageResultTypeOk];
                        break;
                    case DConnectEventErrorInvalidParameter:
                        [response setErrorToInvalidRequestParameter];
                        break;
                    case DConnectEventErrorNotFound:
                    case DConnectEventErrorFailed:
                    default:
                        [response setErrorToUnknown];
                        break;
                }
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
    
}

/*!
 * バッテリーの状態変更イベントを送信する.
 *
 * @param dic バッテリー状態変更イベント
 */
-(void)sendOnBatteryChange:(DPPebbleDevicePlugin *)plugin PebbleDictionary:(NSDictionary*) dic
{
    NSNumber *level = dic[@(KEY_PARAM_BATTERY_LEVEL)];
    double l = [level intValue] / (double)TO_PERCENT;
    
    DConnectMessage *batteryDatas = [DConnectMessage message];
    [DConnectBatteryProfile setLevel:l target:batteryDatas];
    
    
    
    
    // イベントの取得
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
    NSArray *evts = [mgr eventListForDeviceId:[self getDeviceId]
                                      profile:DConnectBatteryProfileName
                                    attribute:DConnectBatteryProfileAttrOnBatteryChange];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        [DConnectBatteryProfile setBattery:batteryDatas target:eventMsg];
        [plugin sendEvent:eventMsg];
    }
    
    
}




/**
 * バッテリーチャージングイベントを返却する.
 *
 * @param dic チャージングイベント
 */
-(void) sendOnChargingChange:(DPPebbleDevicePlugin *)plugin PebbleDictionary:(NSDictionary*) dic  {
    
    NSNumber *charging = dic[@(KEY_PARAM_BATTERY_CHARGING)];
    BOOL c = [charging intValue] == BATTERY_CHARGING_ON;
    
    DConnectMessage *batteryDatas = [DConnectMessage message];
    [DConnectBatteryProfile setCharging:c target:batteryDatas];
    
    
    
    
    // イベントの取得
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
    NSArray *evts = [mgr eventListForDeviceId:[self getDeviceId]
                                      profile:DConnectBatteryProfileName
                                    attribute:DConnectBatteryProfileAttrOnChargingChange];
    
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        [DConnectBatteryProfile setBattery:batteryDatas target:eventMsg];
        [plugin sendEvent:eventMsg];
    }
    
}
@end
