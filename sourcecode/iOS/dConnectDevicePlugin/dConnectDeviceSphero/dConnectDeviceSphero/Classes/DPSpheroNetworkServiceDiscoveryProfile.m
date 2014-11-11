//
//  DPSpheroNetworkServiceDiscoveryProfile.m
//  dConnectDeviceSphero
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPSpheroNetworkServiceDiscoveryProfile.h"
#import "DPSpheroDevicePlugin.h"
#import "DPSpheroManager.h"

@implementation DPSpheroNetworkServiceDiscoveryProfile

// 初期化
- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
    
}

//  dConnect Managerに接続されている、デバイスプラグイン対応デバイス一覧を取得する。
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    DConnectArray *services = [DConnectArray array];
    
    NSArray *deviceList = [DPSpheroManager sharedManager].deviceList;
    for (NSDictionary *device in deviceList) {
        DConnectMessage *service = [DConnectMessage new];
        
        [DConnectNetworkServiceDiscoveryProfile setId:device[@"id"] target:service];
        [DConnectNetworkServiceDiscoveryProfile setName:device[@"name"] target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeBluetooth
                                                 target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
        [services addMessage:service];
    }
    [response setResult:DConnectMessageResultTypeOk];
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    return YES;
}

@end
