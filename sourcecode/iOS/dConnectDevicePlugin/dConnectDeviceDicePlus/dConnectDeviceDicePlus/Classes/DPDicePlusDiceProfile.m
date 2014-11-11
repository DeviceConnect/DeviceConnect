//
//  DPDicePlusDiceProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusDiceProfile.h"
#import "DPDicePlusDevicePlugin.h"
#import "DPDicePlusManager.h"

@interface DPDicePlusDiceProfile ()

- (void) addOnDice:(NSString *)deviceId;
- (void) removeOnDice:(NSString *)deviceId;

- (void) addOnMagneto:(NSString *)deviceId;
- (void) removeOnMagneto:(NSString *)deviceId;

- (BOOL) checkDeviceId:(NSString *)deviceId;

@end

@implementation DPDicePlusDiceProfile

- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DPDiceProfileDelegate
#pragma mark - Put Methods

- (BOOL)                        profile:(DPDiceProfile *)profile
     didReceivePutOnMagnetometerRequest:(DConnectRequestMessage *)request
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
                [self addOnMagneto:deviceId];
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

- (BOOL)                        profile:(DPDiceProfile *)profile
     didReceivePutOnDiceRequest:(DConnectRequestMessage *)request
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
                [self addOnDice:deviceId];
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

- (BOOL)                           profile:(DPDiceProfile *)profile
     didReceiveDeleteOnMagnetometerRequest:(DConnectRequestMessage *)request
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
                [self removeOnMagneto:deviceId];
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

- (BOOL)                           profile:(DPDiceProfile *)profile
     didReceiveDeleteOnDiceRequest:(DConnectRequestMessage *)request
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
                [self removeOnDice:deviceId];
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

- (void) addOnDice:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addRollOfDie:die block:^(int roll) {
        DConnectMessage *msg = [DConnectMessage message];
        [msg setInteger:roll forKey:DPDiceProfileParamPip];
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId
                                          profile:DPDiceProfileName
                                        attribute:DPDiceProfileAttrOnDice];
        if ([evts count] == 0) {
            [die stopRollUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DPDiceProfileParamDice];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnDice:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeRollOfDie:die];
}


- (void) addOnMagneto:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addMagnetometerOfDie:die block:^(int x, int y, int z, int interval, int flags) {
        DConnectMessage *msg = [DConnectMessage message];
        [msg setInteger:x forKey:DPDiceProfileParamX];
        [msg setInteger:y forKey:DPDiceProfileParamY];
        [msg setInteger:z forKey:DPDiceProfileParamZ];
        [msg setInteger:interval forKey:DPDiceProfileParamInterval];
        [msg setInteger:flags forKey:DPDiceProfileParamFilter];
        
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId profile:DPDiceProfileName interface:DPDiceProfileInterfaceMagnetometer attribute:DPDiceProfileAttrOnMagnetometer];
        if ([evts count] == 0) {
            [die stopMagnetometerUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DPDiceProfileParamMagnetometer];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnMagneto:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeMagnetometerOfDie:die];
}

- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
