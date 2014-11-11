//
//  DPDicePlusDeviceOrientationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusDeviceOrientationProfile.h"
#import "DPDicePlusDevicePlugin.h"
#import "DPDicePlusManager.h"

@interface DPDicePlusDeviceOrientationProfile ()
- (void) addOnDeviceOrientation:(NSString *)deviceId;
- (void) removeOnDeviceOrientation:(NSString *)deviceId;
- (BOOL) checkDeviceId:(NSString *)deviceId;
@end


@implementation DPDicePlusDeviceOrientationProfile

- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectBatteryProfileDelegate
#pragma mark - Put Methods

- (BOOL)                        profile:(DConnectDeviceOrientationProfile *)profile
didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
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
                [self addOnDeviceOrientation:deviceId];
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

- (BOOL)                           profile:(DConnectDeviceOrientationProfile *)profile
didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
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
                [self removeOnDeviceOrientation:deviceId];
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

- (void) addOnDeviceOrientation:(NSString *)deviceId {
    __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
    
    DConnectEventManager *evtMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr addOrientationOfDie:die block:^(double x, double y, double z, int roll, int pitch, int yaw, int interval) {
        DConnectMessage *msg = [DConnectMessage message];

        DConnectMessage *accel = [DConnectMessage message];
        [accel setFloat:x forKey:DConnectDeviceOrientationProfileParamX];
        [accel setFloat:y forKey:DConnectDeviceOrientationProfileParamY];
        [accel setFloat:z forKey:DConnectDeviceOrientationProfileParamZ];
        
        DConnectMessage *rotate = [DConnectMessage message];
        [rotate setFloat:yaw forKey:DConnectDeviceOrientationProfileParamAlpha];
        [rotate setFloat:roll forKey:DConnectDeviceOrientationProfileParamBeta];
        [rotate setFloat:pitch forKey:DConnectDeviceOrientationProfileParamGamma];
        
        [msg setMessage:accel forKey:DConnectDeviceOrientationProfileParamAccelerationIncludingGravity];
        [msg setMessage:rotate forKey:DConnectDeviceOrientationProfileParamRotationRate];
        [msg setInteger:interval forKey:DConnectDeviceOrientationProfileParamInterval];
        
        NSArray *evts = [evtMgr eventListForDeviceId:deviceId
                                             profile:DConnectDeviceOrientationProfileName
                                           attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
        if ([evts count] == 0) {
            [die stopOrientationUpdates];
            [die stopAccelerometerUpdates];
        }
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:msg forKey:DConnectDeviceOrientationProfileParamOrientation];
            [_self sendEvent:eventMsg];
        }
    }];
}

- (void) removeOnDeviceOrientation:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    [mgr removeOrientationOfDie:die];
}

- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
