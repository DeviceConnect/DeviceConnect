//
//  TestVibrationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestVibrationProfile.h"

@implementation TestVibrationProfile

- (id) init {
    self = [super init];
    
    if (self) {
        self.delegate = self;
    }
    
    return self;
}

#pragma mark - Put Methods

- (BOOL) profile:(DConnectVibrationProfile *)profile didReceivePutVibrateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId pattern:(NSArray *) pattern
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL) profile:(DConnectVibrationProfile *)profile didReceiveDeleteVibrateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

@end
