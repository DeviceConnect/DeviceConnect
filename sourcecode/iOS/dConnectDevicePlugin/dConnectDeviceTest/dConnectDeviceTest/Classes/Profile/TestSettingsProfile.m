//
//  TestSettingsProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestSettingsProfile.h"

const double TestSettingsLevel = 0.5;
NSString *const TestSettingsDate = @"2014-01-01T01:01:01+09:00";

@implementation TestSettingsProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - Get Methods

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId kind:(DConnectSettingsProfileVolumeKind)kind
{
    CheckDID(response, deviceId)
    if (kind == DConnectSettingsProfileVolumeKindUnknown) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
        [DConnectSettingsProfile setVolumeLevel:TestSettingsLevel target:response];
    }
    
    return YES;
}
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetDateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectSettingsProfile setDate:TestSettingsDate target:response];
    }

    return YES;
}
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectSettingsProfile setLightLevel:TestSettingsLevel target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetSleepRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectSettingsProfile setTime:1 target:response];
    }
    
    return YES;
}

#pragma mark - Put Methods

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId kind:(DConnectSettingsProfileVolumeKind)kind
           level:(NSNumber *)level
{
    CheckDID(response, deviceId)
    if (kind == DConnectSettingsProfileVolumeKindUnknown
        || level == nil || [level doubleValue] < DConnectSettingsProfileMinLevel
        || [level doubleValue] > DConnectSettingsProfileMaxLevel)
    {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutDateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId date:(NSString *)date
{
    
    CheckDID(response, deviceId)
    if (date == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId level:(NSNumber *)level
{
    
    CheckDID(response, deviceId)
    if (level == nil || [level doubleValue] < DConnectSettingsProfileMinLevel
        || [level doubleValue] > DConnectSettingsProfileMaxLevel)
    {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }

    return YES;
}

- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutSleepRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId time:(NSNumber *)time
{
    CheckDID(response, deviceId)
    if (time == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }

    return YES;
}


@end
