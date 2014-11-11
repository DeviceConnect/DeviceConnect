//
//  DPDicePlusTemperatureProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusTemperatureProfile.h"
#import "DPDicePlusManager.h"

@interface DPDicePlusTemperatureProfile ()
- (BOOL) checkDeviceId:(NSString *)deviceId;
@end

@implementation DPDicePlusTemperatureProfile

- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - Get Methods

- (BOOL)                profile:(DCMTemperatureProfile *)profile
didReceiveGetTemperatureRequest:(DConnectRequestMessage *)request
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
        [mgr getTemperatureOfDie:die block:^(float temperature) {
            [response setResult:DConnectMessageResultTypeOk];
            [response setFloat:temperature forKey:DCMTemperatureProfileParamTemperature];
            [response setInteger:1 forKey:DCMTemperatureProfileParamType];
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        return NO;
    }
}

- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
