//
//  DConnectSDK.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief Device Connect SDK for iOS "umbrella" ヘッダ
 @author NTT DOCOMO
 */

/*!
 @mainpage
 
 <p>
 Device Connect SDK for iOS は Device Connect API に対応したiOS向けのデバイスプラグイン及び、UIアプリを開発するためのSDKである。<br/>
 本SDKでは以下の機能を提供する。
 </p>
 @li Device Connect Managerの設定、起動
 @li Device Connect APIへのアクセス機能
 @li デバイスプラグイン開発用クラスの提供
 @li URI生成機能
 @li イベント操作機能
 @li メッセージ操作機能
 
 <h2> Device Connect アーキテクチャ </h2>
 @image html ios_arch.png
 
 */

#import <Foundation/Foundation.h>

#import <DConnectSDK/DConnectUtil.h>
#import <DConnectSDK/DConnectProfileProvider.h>
#import <DConnectSDK/DConnectProfile.h>
#import <DConnectSDK/DConnectMessage.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectManager.h>
#import <DConnectSDK/DConnectDevicePlugin.h>
#import <DConnectSDK/DConnectFileManager.h>
#import <DConnectSDK/DConnectSettings.h>

#import <DConnectSDK/DConnectVibrationProfile.h>
#import <DConnectSDK/DConnectSystemProfile.h>
#import <DConnectSDK/DConnectSettingsProfile.h>
#import <DConnectSDK/DConnectProximityProfile.h>
#import <DConnectSDK/DConnectPhoneProfile.h>
#import <DConnectSDK/DConnectNotificationProfile.h>
#import <DConnectSDK/DConnectNetworkServiceDiscoveryProfile.h>
#import <DConnectSDK/DConnectMediaStreamRecordingProfile.h>
#import <DConnectSDK/DConnectMediaPlayerProfile.h>
#import <DConnectSDK/DConnectFileProfile.h>
#import <DConnectSDK/DConnectFileDescriptorProfile.h>
#import <DConnectSDK/DConnectDeviceOrientationProfile.h>
#import <DConnectSDK/DConnectConnectProfile.h>
#import <DConnectSDK/DConnectBatteryProfile.h>
#import <DConnectSDK/DConnectAuthorizationProfile.h>

#import <DConnectSDK/DConnectEvent.h>
#import <DConnectSDK/DConnectEventCacheController.h>
#import <DConnectSDK/DConnectEventManager.h>
#import <DConnectSDK/DConnectBaseCacheController.h>
#import <DConnectSDK/DConnectDBCacheController.h>
#import <DConnectSDK/DConnectMemoryCacheController.h>
#import <DConnectSDK/DConnectFileCacheController.h>

#import <DConnectSDK/DConnectURIBuilder.h>
#import <DConnectSDK/DConnectMessageFactory.h>
#import <DConnectSDK/DConnectEventHelper.h>