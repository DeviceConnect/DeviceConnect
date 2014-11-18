//
//  TestNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestNetworkServiceDiscoveryProfile.h"
#import "DeviceTestPlugin.h"

NSString *const TestNetworkDeviceIdSpecialCharacters = @"!#$'()-~¥@[;+:*],._/=?&%^|`\"{}<>";
NSString *const TestNetworkDeviceName = @"Test Success Device";
NSString *const TestNetworkDeviceNameSpecialCharacters = @"Test Device ID Special Characters";
NSString *const TestNetworkDeviceType = @"TEST";
const BOOL TestNetworkDeviceOnline = YES;
NSString *const TestNetworkDeviceConfig = @"test config";

@implementation TestNetworkServiceDiscoveryProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark DConnectNetworkServiceDiscoveryProfileDelegate

#pragma mark - Get Methods

- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
{
    
    DConnectArray *services = [DConnectArray array];
    
    // 典型的なサービス
    DConnectMessage *service = [DConnectMessage message];
    [DConnectNetworkServiceDiscoveryProfile setId:TDPDeviceId target:service];
    [DConnectNetworkServiceDiscoveryProfile setName:TestNetworkDeviceName target:service];
    [DConnectNetworkServiceDiscoveryProfile setType:TestNetworkDeviceType target:service];
    [DConnectNetworkServiceDiscoveryProfile setOnline:TestNetworkDeviceOnline target:service];
    [DConnectNetworkServiceDiscoveryProfile setConfig:TestNetworkDeviceConfig target:service];
    [services addMessage:service];
    
    // デバイスIDが特殊なサービス
    service = [DConnectMessage message];
    [DConnectNetworkServiceDiscoveryProfile setId:TestNetworkDeviceIdSpecialCharacters target:service];
    [DConnectNetworkServiceDiscoveryProfile setName:TestNetworkDeviceNameSpecialCharacters target:service];
    [DConnectNetworkServiceDiscoveryProfile setType:TestNetworkDeviceType target:service];
    [DConnectNetworkServiceDiscoveryProfile setOnline:TestNetworkDeviceOnline target:service];
    [DConnectNetworkServiceDiscoveryProfile setConfig:TestNetworkDeviceConfig target:service];
    [services addMessage:service];
    
    response.result = DConnectMessageResultTypeOk;
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    
    return YES;
}

#pragma mark - Put Methods


- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange
                  forKey:DConnectMessageAttribute];
        
        DConnectMessage *service = [DConnectMessage message];
        [DConnectNetworkServiceDiscoveryProfile setId:TDPDeviceId target:service];
        [DConnectNetworkServiceDiscoveryProfile setName:TestNetworkDeviceName target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:TestNetworkDeviceType target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:TestNetworkDeviceOnline target:service];
        [DConnectNetworkServiceDiscoveryProfile setConfig:TestNetworkDeviceConfig target:service];

        [DConnectNetworkServiceDiscoveryProfile setNetworkService:service target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

#pragma mark - Delete Methods


- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

@end
