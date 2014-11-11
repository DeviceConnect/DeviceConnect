//
//  DPPebbleNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleNetworkServiceDiscoveryProfile.h"
#import "DPPebbleManager.h"

@interface DPPebbleNetworkServiceDiscoveryProfile ()
@end


@implementation DPPebbleNetworkServiceDiscoveryProfile

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
	
	NSArray *deviceList = [DPPebbleManager sharedManager].deviceList;
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
