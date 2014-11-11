//
//  SonyCameraDevicePlugin.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

/*!
 @brief SonyCameraデバイスプラグインのデリゲート。
 */
@protocol SonyCameraDevicePluginDelegate <NSObject>
@optional

/*!
 @brief デバイスの発見通知。
 @param[in] discover デバイスが発見された場合はYES、それ以外はNO
 */
- (void) didReceiveDeviceList:(BOOL)discover;

@end

/**
 * Sony Remote Camera API用デバイスプラグイン.
 */
@interface SonyCameraDevicePlugin : DConnectDevicePlugin

/*!
 @brief デリゲート。
 */
@property (weak, nonatomic) id<SonyCameraDevicePluginDelegate> delegate;

/*!
 @biref Sony Camera Remote APIに対応したデバイスを探索する。
 
 発見通知は、delegateに設定されたSonyCameraDevicePluginDelegateに通知される。
 */
- (void) searchSonyCameraDevice;

/*!
 @brief デバイスプラグインを停止する。
 */
- (void) stop;

/*!
 @brief デバイスプラグインが起動中かチェックする。
 @retval YES 起動中
 @retval NO 停止中
 */
- (BOOL) isStarted;

@end
