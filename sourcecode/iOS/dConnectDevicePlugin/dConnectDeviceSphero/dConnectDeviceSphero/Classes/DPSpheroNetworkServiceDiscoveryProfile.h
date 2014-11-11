//
//  DPSpheroNetworkServiceDiscoveryProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
/*! @file
 @brief SpheroデバイスプラグインのNetworkServiceDiscoveryProfile機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.6.23)
 */
#import <DConnectSDK/DConnectSDK.h>

/*!
 @class DPSpheroNetworkServiceDiscoveryProfile
 @brief SpheroデバイスプラグインのNetworkServiceDiscoveryProfile機能を提供する
 */
@interface DPSpheroNetworkServiceDiscoveryProfile : DConnectNetworkServiceDiscoveryProfile<DConnectNetworkServiceDiscoveryProfileDelegate>

@end
