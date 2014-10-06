//
//  DPHostNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import <DConnectSDK/DConnectMessage.h>

#import "DPHostNetworkServiceDiscoveryProfile.h"

NSString *const NetworkDiscoveryDeviceId = @"host";

@implementation DPHostNetworkServiceDiscoveryProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate
#pragma mark Get Methods

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    // ハードウェアプラットフォームを取得。
    UIDevice *device = [UIDevice currentDevice];
    NSString *name = [NSString stringWithFormat:@"Host: %@", device.name];
    
    DConnectArray *services = [DConnectArray array];
    
    DConnectMessage *service = [DConnectMessage message];
    [DConnectNetworkServiceDiscoveryProfile setId:NetworkDiscoveryDeviceId target:service];
    [DConnectNetworkServiceDiscoveryProfile setName:name target:service];
    [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
    NSString *config = [NSString stringWithFormat:@"{\"OS\":\"%@ %@\"}",
                        device.systemName, device.systemVersion];
    [DConnectNetworkServiceDiscoveryProfile setConfig:config target:service];
    [services addMessage:service];

    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

#pragma mark - Put Methods

- (BOOL)                    profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
    }
    
    // このデバイスプラグインは常駐；常に接続していて、接続が失われることも無いので、イベントの送信は行わない。
    return YES;
}

#pragma mark - Delete Methods

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
    }
    
    // このデバイスプラグインは常駐；常に接続していて、接続が失われることも無いので、イベントの送信は行わない。
    return YES;
}

#pragma mark - DConnectEventHandling

- (BOOL) unregisterAllEventsWithSessionkey:(NSString *)sessionKey
{
    // このデバイスプラグインは常駐；常に接続していて、接続が失われることも無いので、イベントの送信は行わない。
    return YES;
}

@end
