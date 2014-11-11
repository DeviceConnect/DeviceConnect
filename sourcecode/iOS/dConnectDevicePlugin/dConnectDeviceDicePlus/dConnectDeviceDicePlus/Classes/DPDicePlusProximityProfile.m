//
//  DPDicePlusProximityProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//


#import "DPDicePlusProximityProfile.h"
#import "DPDicePlusDevicePlugin.h"
#import "DPDicePlusManager.h"

@interface DPDicePlusProximityProfile()

- (void) addOnProximity:(NSString *)deviceId;
- (void) removeOnProximity:(NSString *)deviceId;
- (BOOL) checkDeviceId:(NSString *)deviceId;

@end


@implementation DPDicePlusProximityProfile

- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectProximityProfileDelegate delegate
#pragma mark - Put Methods

- (BOOL)                      profile:(DConnectProximityProfile *)profile
didReceivePutOnDeviceProximityRequest:(DConnectRequestMessage *)request
                             response:(DConnectResponseMessage *)response
                             deviceId:(NSString *)deviceId
                           sessionKey:(NSString *)sessionKey {
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
                [self addOnProximity:deviceId];
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

- (BOOL)                         profile:(DConnectProximityProfile *)profile
didReceiveDeleteOnDeviceProximityRequest:(DConnectRequestMessage *)request
                                response:(DConnectResponseMessage *)response
                                deviceId:(NSString *)deviceId
                              sessionKey:(NSString *)sessionKey {
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
                [self removeOnProximity:deviceId];
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

- (void) addOnProximity:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addProximityOfDie:die block:^(float value, float min, float max, float threshold) {
        DConnectMessage *msg = [DConnectMessage message];
        [msg setFloat:value forKey:DConnectProximityProfileParamValue];
        [msg setFloat:min forKey:DConnectProximityProfileParamMin];
        [msg setFloat:max forKey:DConnectProximityProfileParamMax];
        [msg setFloat:threshold forKey:DConnectProximityProfileParamThreshold];
        
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId
                                             profile:DConnectProximityProfileName
                                           attribute:DConnectProximityProfileAttrOnDeviceProximity];
        if ([evts count] == 0) {
            [die stopProximityUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DConnectProximityProfileParamProximity];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnProximity:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeProximityOfDie:die];
}

- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
