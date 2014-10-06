//
//  DConnectDevicePluginManager.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectDevicePlugin.h"

/**
 * デバイスプラグインを管理するクラス.
 */
@interface DConnectDevicePluginManager : NSObject

/**
 * デバイスプラグイン一覧を探索する.
 * クラス名一式を取得して、特定の名前が付いているクラスをデバイスプラグインとして認識する。
 */
- (void) searchDevicePlugin;

/**
 * 指定されたデバイスIDのデバイスプラグインを取得する.
 *
 * ここで指定されるdeviceIdは、appendDevicePluginで作成された
 * [device].[deviceplugin].dconnect
 * の形のデバイスIDになる。
 *
 * この関数では、[deviceplugin]の部分を抜き出して、デバイスプラグインを見つけ出す。
 *
 * 指定されたデバイスIDのデバイスプラグインが存在しない場合にはnilを返却する。
 *
 * @param[in] deviceId デバイスID
 * @return DConnectDevicePluginのインスタンス
 */
- (DConnectDevicePlugin *) devicePluginForDeviceId:(NSString *)deviceId;


/**
 * 指定されたプラグインIDのデバイスプラグインを取得する.
 * 指定されたデバイスIDのデバイスプラグインが存在しない場合にはnilを返却する。
 *
 * @param[in] pluginId プラグインID
 * @return DConnectDevicePluginのインスタンス
 */
- (DConnectDevicePlugin *) devicePluginForPluginId:(NSString *)pluginId;

/**
 * 登録されているすべてのデバイスプラグインを取得する.
 * @return 登録されているすべてのデバイスプラグインの配列
 */
- (NSArray *) devicePluginList;

/**
 * デバイスIDにデバイスプラグインのIDを付加する.
 *
 * [device].[deviceplugin].dconnect
 *
 * @param[in] plugin プラグイン
 * @param[in] deviceId オリジナルのデバイスID
 * @return デバイスプラグインのIDが付加されたデバイスID
 */
- (NSString *) deviceIdByAppedingPluginIdWithDevicePlugin:(DConnectDevicePlugin *)plugin deviceId:(NSString *)deviceId;

/**
 * デバイスIDからデバイスプラグインのIDを削除する.
 * @param[in] deviceId デバイスプラグインのIDが付加されたデバイスID
 * @param[in] plugin プラグイン
 * @return オリジナルのデバイスID
 */
- (NSString *) spliteDeviceId:(NSString *)deviceId byDevicePlugin:(DConnectDevicePlugin *)plugin;

@end
