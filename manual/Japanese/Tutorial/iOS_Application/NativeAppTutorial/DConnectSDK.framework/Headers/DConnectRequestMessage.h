//
//  DConnectRequestMessage.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief リクエストデータを提供するクラス。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectMessage.h>

/*!
 @class DConnectRequestMessage
 @brief リクエストデータを提供するクラス。
 */
@interface DConnectRequestMessage : DConnectMessage

#pragma mark - Common Parameters
#pragma mark Setter

/*!
 @brief アクションタイプを設定する。
 @param[in] action アクションタイプ
 */
- (void) setAction:(DConnectMessageActionType)action;

/*!
 @brief APIを設定する。
 @param[in] api API
 */
- (void) setApi:(NSString *)api;

/*!
 @brief プロファイルを設定する。
 @param[in] profile プロファイル
 */
- (void) setProfile:(NSString *)profile;

/*!
 @brief アトリビュートを設定する。
 @param[in] attribute アトリビュート
 */
- (void) setAttribute:(NSString *)attribute;

/*!
 @brief インターフェースを設定する。
 @param[in] interface インターフェース
 */
- (void) setInterface:(NSString *)interface;

/*!
 @brief セッションキーを設定する。
 @param[in] sessionKey セッションキー
 */
- (void) setSessionKey:(NSString *)sessionKey;

/*!
 @brief デバイスIDを設定する。
 @param[in] deviceId デバイスID
 */
- (void) setDeviceId:(NSString *)deviceId;

/*!
 @brief プラグインIDを設定する。
 @param[in] pluginId プラグインID
 */
- (void) setPluginId:(NSString *)pluginId;

/*!
 @brief アクセストークンを設定する。
 @param[in] accessToken アクセストークン
 */
- (void) setAccessToken:(NSString *)accessToken;

#pragma mark Getter

/*!
 @brief アクションタイプを取得する。
 @retval アクションタイプ
 */
- (DConnectMessageActionType) action;

/*!
 @brief APIを取得する。
 @retval API
 @retval nil APIが設定されていない場合
 */
- (NSString *) api;

/*!
 @brief プロファイルを取得する。
 @retval プロファイル
 @retval nil プロファイルが設定されていない場合
 */
- (NSString *) profile;

/*!
 @brief アトリビュートを取得する。
 @retval アトリビュート
 @retval nil アトリビュートが設定されていない場合
 */
- (NSString *) attribute;

/*!
 @brief インターフェースを取得する。
 @retval インターフェース
 @retval nil インターフェースが設定されていない場合
 */
- (NSString *) interface;

/*!
 @brief セッションキーを取得する。
 @retval セッションキー
 @retval nil セッションキーが設定されていない場合
 */
- (NSString *) sessionKey;

/*!
 @brief デバイスIDを取得する。
 @retval デバイスID
 @retval nil デバイスIDが設定されていない場合
 */
- (NSString *) deviceId;

/*!
 @brief プラグインIDを取得する。
 @retval プラグインID
 @retval nil プラグインIDが設定されていない場合
 */
- (NSString *) pluginId;

/*!
 @brief アクセストークンを取得する。
 @retval アクセストークン
 @retval nil アクセストークンが設定されていない場合
 */
- (NSString *) accessToken;

@end
