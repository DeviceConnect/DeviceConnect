//
//  DPPebbleDevicePlugin.h
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectSDK.h>
#import "DPPebbleManager.h"

/*!
 @brief Pebbleデバイスプラグイン。
 */
@interface DPPebbleDevicePlugin : DConnectDevicePlugin

@property (nonatomic) DPPebbleManager *mgr;

@end
