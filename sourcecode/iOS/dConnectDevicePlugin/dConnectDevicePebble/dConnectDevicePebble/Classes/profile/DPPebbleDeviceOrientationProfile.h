//
//  DPPebbleDeviceOrientationProfile.h
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
#import "DPPebbleManager.h"

@class DPPebbleDevicePlugin;

/*!
 @brief Pebble用 DeviceOrientationプロファイル。
 */
@interface DPPebbleDeviceOrientationProfile : DConnectDeviceOrientationProfile <DConnectDeviceOrientationProfileDelegate>

/*!
 @brief PebbleManager付きで初期化する。
 @param[in] plugin デバイスプラグイン
 */
- (id) initWithDevicePlugin:(DPPebbleDevicePlugin *)plugin;

@end
