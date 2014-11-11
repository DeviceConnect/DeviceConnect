//
//  DPChromecastNetworkServiceDiscoveryProfile.m
//  dConnectChromecast
//
//  Created by Ryuya Takahashi on 2014/09/12.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPChromecastNetworkServiceDiscoveryProfile.h"
#import "DPChromecastManager.h"

@implementation DPChromecastNetworkServiceDiscoveryProfile

// 初期化
- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
    
}


#pragma mark Get Methods

//  dConnect Managerに接続されている、デバイスプラグイン対応デバイス一覧を取得する。
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    DConnectArray *services = [DConnectArray array];
    
    NSArray *deviceList = [DPChromecastManager sharedManager].deviceList;
    for (NSDictionary *device in deviceList) {
        DConnectMessage *service = [DConnectMessage new];
        
        [DConnectNetworkServiceDiscoveryProfile setId:device[@"id"] target:service];
        [DConnectNetworkServiceDiscoveryProfile setName:device[@"name"] target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
                                                 target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
        [services addMessage:service];
    }
    [response setResult:DConnectMessageResultTypeOk];
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    return YES;
}


#pragma mark - Put Methods

- (BOOL)                    profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    [response setErrorToNotSupportProfile];
    return YES;
}


#pragma mark - Delete Methods

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    [response setErrorToNotSupportProfile];
    return YES;
}


@end
