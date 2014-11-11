//
//  TestSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestSystemProfile.h"

NSString *const TestSystemVersion = @"1.0";

@implementation TestSystemProfile

- (id) init {
    
    self = [super init];
    
    if (self) {
        self.delegate = self;
        self.dataSource = self;
    }
    
    return self;
}


#pragma mark - DConnectSystemProfileDelegate

#pragma mark - Get Methods

- (BOOL) profile:(DConnectSystemProfile *)profile didReceiveGetDeviceRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        [DConnectSystemProfile setVersion:TestSystemVersion target:response];
        
        DConnectArray *supports = [DConnectArray array];
        [supports addString:DConnectBatteryProfileName];
        [supports addString:DConnectConnectProfileName];
        [supports addString:DConnectDeviceOrientationProfileName];
        [supports addString:DConnectFileDescriptorProfileName];
        [supports addString:DConnectFileProfileName];
        [supports addString:DConnectMediaStreamRecordingProfileName];
        [supports addString:DConnectMediaPlayerProfileName];
        [supports addString:DConnectNetworkServiceDiscoveryProfileName];
        [supports addString:DConnectPhoneProfileName];
        [supports addString:DConnectProximityProfileName];
        [supports addString:DConnectSettingsProfileName];
        [supports addString:DConnectSystemProfileName];
        [supports addString:DConnectVibrationProfileName];
        [DConnectSystemProfile setSupports:supports target:response];
        
        DConnectMessage *connect = [DConnectMessage message];
        [DConnectSystemProfile setWiFiState:DConnectSystemProfileConnectStateOff target:connect];
        [DConnectSystemProfile setBluetoothState:DConnectSystemProfileConnectStateOff target:connect];
        [DConnectSystemProfile setBLEState:DConnectSystemProfileConnectStateOff target:connect];
        [DConnectSystemProfile setNFCState:DConnectSystemProfileConnectStateOff target:connect];
        
        [DConnectSystemProfile setConnect:connect target:response];
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL) profile:(DConnectSystemProfile *)profile didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response sessionKey:(NSString *)sessionKey
{
    response.result = DConnectMessageResultTypeOk;
    return YES;
}

#pragma mark - DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return @"1.0";
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request {
    return [UIViewController new];
}

@end
