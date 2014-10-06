//
//  DConnectManagerNetworkServiceDiscoveryProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectNetworkServiceDiscoveryProfile.h"

/**
 * DConnectManager用のNetwork Service Discoveryプロファイル.
 */
@interface DConnectManagerNetworkServiceDiscoveryProfile : DConnectNetworkServiceDiscoveryProfile
<DConnectNetworkServiceDiscoveryProfileDelegate>

- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response;

@end
