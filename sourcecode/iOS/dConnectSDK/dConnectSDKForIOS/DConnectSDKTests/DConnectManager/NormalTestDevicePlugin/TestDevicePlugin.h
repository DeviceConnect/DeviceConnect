//
//  SuccessTestDevicePlugin.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//


/*! デバイスプラグイン名. */
extern NSString *const TestDevicePluginAppName;


// Network Service Discovery Profileの定数

/*! デバイスプラグイン名. */
extern NSString *const TestDevicePluginName;
/*! デバイスプラグインID. */
extern NSString *const TestDevicePluginId;
/*! デバイスプラグインタイプ. */
extern NSString *const TestDevicePluginType;


// System Profileの定数

/*! デバイスプラグインのバージョンを定義. */
extern NSString *const TestDevicePluginSystemVersion;


// Battery Profileの定数

/** デバイスプラグインのバッテリーチャージフラグを定義. */
extern const BOOL TestDevicePluginBatteryCharging;
/** デバイスプラグインのバッテリーチャージ時間を定義. */
extern const long TestDevicePluginBatteryChargingTime;
/** デバイスプラグインのバッテリー放電時間を定義. */
extern const long TestDevicePluginBatteryDischargingTime;
/** デバイスプラグインのバッテリーレベルを定義. */
extern const float TestDevicePluginBatteryLevel;
