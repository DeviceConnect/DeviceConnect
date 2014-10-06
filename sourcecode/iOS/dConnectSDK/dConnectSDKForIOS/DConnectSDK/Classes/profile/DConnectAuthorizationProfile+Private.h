//
//  DConnectAuthorizationProfile+Private.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectAuthorizationProfile.h"

/*!
 @class DConnectAuthorizationProfile
 @brief Authorizationプロファイル。
 */
@interface DConnectAuthorizationProfile : DConnectProfile

/*!
 @brief 任意のオブジェクトを指定してAuthorizationProfileを初期化する。
 オブジェクトはDConnectDevicePluginもしくはDConnectManagerのインスタンスでなければならない。
 
 @param[in] object DevicePluginかManagerかを判別するためのオブジェクト
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (id) initWithObject:(id)object;

#pragma mark - Get Methods

/*!
 @brief Local OAuthで使用するクライアントの作成要求の受信を通知する。
 
 @param[in] requst リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] package パッケージ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) didReceiveGetCreateClientRequest:(DConnectRequestMessage *)request
                                 response:(DConnectResponseMessage *)response
                                 deviceId:(NSString *)deviceId
                                  package:(NSString *)package;

/*!
 
 @brief Local OAuthで使用するアクセストークンの作成要求の受信を通知する。
 
 @param[in] requst リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] clientId クライアントID
 @param[in] grantType グラントタイプ
 @param[in] scopes 要求されるプロファイルの一覧
 @param[in] applicationName アプリケーション名
 @param[in] signature シグネチャ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) didReceiveGetRequestAccessTokenRequest:(DConnectRequestMessage *)request
                                       response:(DConnectResponseMessage *)response
                                       deviceId:(NSString *)deviceId
                                       clientId:(NSString *)clientId
                                      grantType:(NSString *)grantType
                                         scopes:(NSArray *)scopes
                                applicationName:(NSString *)applicationName
                                      signature:(NSString *)signature;

#pragma mark - Setter

/*!
 @brief メッセージにクライアントIDを設定する。
 @param[in] clientId クライアントID
 */
+ (void) setClientId:(NSString *)clientId target:(DConnectMessage *)message;

/*!
 @brief メッセージにクライアントシークレットを設定する。
 @param[in] clientSecret クライアントシークレット
 */
+ (void) setClientSceret:(NSString *)clientSceret target:(DConnectMessage *)message;

/*!
 @brief メッセージにアクセストークンを設定する。
 @param[in] accessToken アクセストークン
 */
+ (void) setAccessToken:(NSString *)accessToken target:(DConnectMessage *)message;

/*!
 @brief メッセージにシグネチャーを設定する。
 @param[in] signature シグネチャー
 */
+ (void) setSignature:(NSString *)signature target:(DConnectMessage *)message;

/*!
 @brief メッセージにスコープ一覧を設定する。
 @param[in] scopes スコープ一覧
 */
+ (void) setScopes:(DConnectArray *)scopes target:(DConnectMessage *)message;

/*!
 @brief メッセージにスコープを設定する。
 @param[in] scope スコープ
 */
+ (void) setScope:(NSString *)scope target:(DConnectMessage *)message;

/*!
 @brief メッセージにスコープの有効期限を設定する。
 @param[in] priod スコープの有効期限
 */
+ (void) setExpirePriod:(long long)priod target:(DConnectMessage *)message;

#pragma mark - Getter

/*!
 @brief リクエストからパッケージを取得する。
 @retval パッケージ
 @retval nil リクエストにパッケージが指定されていない場合
 */
+ (NSString *) packageFromRequest:(DConnectRequestMessage *)request;

/*!
 @brief リクエストからクライアントIDを取得する。
 @retval クライアントID
 @retval nil リクエストにクライアントIDが指定されていない場合
 */
+ (NSString *) clientIdFromRequest:(DConnectRequestMessage *)request;

/*!
 @brief リクエストからグラントタイプを取得する。
 @retval グラントタイプ
 @retval nil リクエストにグラントタイプが指定されていない場合
 */
+ (NSString *) grantTypeFromRequest:(DConnectRequestMessage *)request;

/*!
 @brief リクエストからスコープを取得する。
 @retval スコープ
 @retval nil リクエストにスコープが指定されていない場合
 */
+ (NSString *) scopeFromeFromRequest:(DConnectRequestMessage *)request;

/*!
 @brief スコープを文字列から解析し、スコープ一覧の配列に変換する。
 @retval スコープ一覧
 @retval nil 解析に失敗した場合
 */
+ (NSArray *) parsePattern:(NSString *)scope;

/*!
 @brief リクエストからアプリケーション名を取得する。
 @retval アプリケーション名
 @retval nil リクエストにアプリケーション名が指定されていない場合
 */
+ (NSString *) applicationNameFromRequest:(DConnectRequestMessage *)request;

/*!
 @brief リクエストからシグネチャーを取得する。
 @retval シグネチャー
 @retval nil リクエストにシグネチャーが指定されていない場合
 */
+ (NSString *) signatureFromRequest:(DConnectRequestMessage *)request;

@end