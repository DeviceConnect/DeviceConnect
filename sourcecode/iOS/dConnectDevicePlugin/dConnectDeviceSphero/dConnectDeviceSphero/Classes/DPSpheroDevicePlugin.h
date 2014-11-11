//
//  DPSpheroDevicePlugin.h
//  DPSpheroDevicePlugin
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//
/*! @file
 @brief Spheroデバイスプラグインのプロファイルのやりとりを行うためのクラス。
 @author NTT DOCOMO
 @date 作成日(2014.7.22)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectSDK.h>


// 接続確認用マクロ
#define CONNECT_CHECK()  BOOL isConnected = [[DPSpheroManager sharedManager] connectDeviceWithID:deviceId]; \
if (!isConnected) { \
[response setErrorToTimeoutWithMessage:@"Not Connected to Sphero"]; \
return YES; \
} \


/*!
 @brief Sphero用デバイスプラグイン.
 */
@interface DPSpheroDevicePlugin : DConnectDevicePlugin
@end
