//
//  DPSpheroNetworkServiceDiscoveryProfile.h
//  dConnectDeviceSphero
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
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
