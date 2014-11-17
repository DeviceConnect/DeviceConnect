//
//  DPHueNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueNetworkServiceDiscoveryProfile.h"
#import "DPHueManager.h"


@implementation DPHueNetworkServiceDiscoveryProfile


- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

- (BOOL)                           profile:(DConnectNetworkServiceDiscoveryProfile *)profile
    didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                                  response:(DConnectResponseMessage *)response
{
    [response setResult:DConnectMessageResultTypeOk];
    NSDictionary *bridgesFound = [DPHueManager sharedManager].hueBridgeList;
    DConnectArray *services = [DConnectArray array];
    DConnectMessage *service = nil;
    if (bridgesFound.count > 0) {
        NSString * deviceId = @"";
        for (id key in [bridgesFound keyEnumerator]) {
            deviceId = [NSString stringWithFormat:@"%@_%@",[bridgesFound valueForKey:key],key];
            service = [DConnectMessage new];
            [DConnectNetworkServiceDiscoveryProfile
             setId:deviceId
             target:service];
            
            [DConnectNetworkServiceDiscoveryProfile
             setName:[NSString stringWithFormat:@"Hue %@", key]
             target:service];
            
            [DConnectNetworkServiceDiscoveryProfile
             setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
             
             target:service];
            [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
            
            [services addMessage:service];

        }

    }
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    return YES;
}
@end
