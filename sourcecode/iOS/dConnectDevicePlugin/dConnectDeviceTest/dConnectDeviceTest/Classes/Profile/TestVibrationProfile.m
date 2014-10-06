//
//  TestVibrationProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
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
