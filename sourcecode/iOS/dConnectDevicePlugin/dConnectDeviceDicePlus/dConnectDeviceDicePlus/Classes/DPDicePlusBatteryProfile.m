//
//  DPDicePlusBatteryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusBatteryProfile.h"
#import "DPDicePlusDevicePlugin.h"
#import "DPDicePlusManager.h"

@interface DPDicePlusBatteryProfile ()

- (void) addOnBatteryCharging:(NSString *)deviceId;
- (void) removeOnBatteryCharging:(NSString *)deviceId;

- (void) addOnBatteryState:(NSString *)deviceId;
- (void) removeOnBatteryState:(NSString *)deviceId;

- (BOOL) checkDeviceId:(NSString *)deviceId;

@end

@implementation DPDicePlusBatteryProfile

- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectBatteryProfileDelegate
#pragma mark - Get Methods

- (BOOL)        profile:(DConnectBatteryProfile *)profile
didReceiveGetAllRequest:(DConnectRequestMessage *)request
               response:(DConnectResponseMessage *)response
               deviceId:(NSString *)deviceId
{
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr getDieByUID:deviceId];
        [mgr getBatteryOfDie:die block:^(BOOL charging, double level) {
            [DConnectBatteryProfile setCharging:charging target:response];
            [DConnectBatteryProfile setLevel:level target:response];
            [response setResult:DConnectMessageResultTypeOk];
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        return NO;
    }
}

- (BOOL)          profile:(DConnectBatteryProfile *)profile
didReceiveGetLevelRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId {
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr getDieByUID:deviceId];
        [mgr getBatteryOfDie:die block:^(BOOL charging, double level) {
            [DConnectBatteryProfile setLevel:level target:response];
            [response setResult:DConnectMessageResultTypeOk];
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        return NO;
    }
}

- (BOOL)             profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId {
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else {
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr getDieByUID:deviceId];
        [mgr getBatteryOfDie:die block:^(BOOL charging, double level) {
            [DConnectBatteryProfile setCharging:charging target:response];
            [response setResult:DConnectMessageResultTypeOk];
            [[DConnectManager sharedManager] sendResponse:response];
        }];
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
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil"];
    } else {
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
        DConnectEventError error = [mgr addEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                [self addOnBatteryCharging:deviceId];
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
    }
    return YES;
}

- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil"];
    } else {
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
        DConnectEventError error = [mgr addEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                [self addOnBatteryState:deviceId];
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
    }
    return YES;
}

#pragma mark - Delete Methods

- (BOOL)                        profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil"];
    } else {
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
        DConnectEventError error = [mgr removeEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                [self removeOnBatteryCharging:deviceId];
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
    }
    return YES;
}

- (BOOL)                       profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil"];
    } else {
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
        DConnectEventError error = [mgr removeEventForRequest:request];
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                [self removeOnBatteryState:deviceId];
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
    }
    return YES;
}


#pragma mark - Private Methods

- (void) addOnBatteryCharging:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addBatteryChargingOfDie:die block:^(BOOL charging) {
        DConnectMessage *msg = [DConnectMessage message];
        
        [msg setBool:charging forKey:DConnectBatteryProfileParamCharging];
        
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId
                                             profile:DConnectBatteryProfileName
                                           attribute:DConnectBatteryProfileAttrOnChargingChange];

        if ([evts count] == 0) {
            [die stopBatteryUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DConnectBatteryProfileParamBattery];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnBatteryCharging:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeBatteryChargingOfDie:die];
}

- (void) addOnBatteryState:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addBatteryStateOfDie:die block:^(float level) {
        DConnectMessage *msg = [DConnectMessage message];
        
        [msg setFloat:level forKey:DConnectBatteryProfileParamLevel];
        
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId
                                             profile:DConnectBatteryProfileName
                                           attribute:DConnectBatteryProfileAttrOnBatteryChange];
        if ([evts count] == 0) {
            [die stopBatteryUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DConnectBatteryProfileParamBattery];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnBatteryState:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeBatteryStateOfDie:die];
}


- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
