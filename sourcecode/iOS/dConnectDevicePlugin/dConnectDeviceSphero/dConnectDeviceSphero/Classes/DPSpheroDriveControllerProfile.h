//
//  DPSpheroDriveControllerProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
/*! @file
 @brief SpheroデバイスプラグインのDriveControllerProfile機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.6.23)
 */
#import <DCMDevicePluginSDK/DCMDevicePluginSDK.h>
#import <Foundation/Foundation.h>
/*!
 @class DPSpheroDriveControllerProfile
 @brief SpheroデバイスプラグインのDriveControllerProfile機能を提供する
 */
@interface DPSpheroDriveControllerProfile : DCMDriveControllerProfile<DCMDriveControllerProfileDelegate>

@end
