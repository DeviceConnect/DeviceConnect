//
//  DConnectAuthorizationProfile.h
//  DConnectSDK
//
//  Created by 小林伸郎 on 2014/07/29.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectAuthorizationProfileName;

/*!
 @brief 属性: create_client。
 */
extern NSString *const DConnectAuthorizationProfileAttrCreateClient;

/*!
 @brief 属性: request_accesstoken。
 */
extern NSString *const DConnectAuthorizationProfileAttrRequestAccessToken;

/*!
 @brief パラメータ: package。
 */
extern NSString *const DConnectAuthorizationProfileParamPackage;

/*!
 @brief パラメータ: clientId。
 */
extern NSString *const DConnectAuthorizationProfileParamClientId;

/*!
 @brief パラメータ: clientSecret。
 */
extern NSString *const DConnectAuthorizationProfileParamClientSecret;

/*!
 @brief パラメータ: grantType。
 */
extern NSString *const DConnectAuthorizationProfileParamGrantType;

/*!
 @brief パラメータ: scope。
 */
extern NSString *const DConnectAuthorizationProfileParamScope;

/*!
 @brief パラメータ: scopes。
 */
extern NSString *const DConnectAuthorizationProfileParamScopes;

/*!
 @brief パラメータ: applicationName。
 */
extern NSString *const DConnectAuthorizationProfileParamApplicationName;

/*!
 @brief パラメータ: signature。
 */
extern NSString *const DConnectAuthorizationProfileParamSignature;

/*!
 @brief パラメータ: expirePeriod。
 */
extern NSString *const DConnectAuthorizationProfileParamExpirePeriod;

/*!
 @brief パラメータ: accessToken。
 */
extern NSString *const DConnectAuthorizationProfileParamAccessToken;

/*!
 @brief authorization_code。
 */
extern NSString *const DConnectAuthorizationProfileGrantTypeAuthorizationCode;

/*!
 @class DConnectAuthorizationProfile
 @brief Authorizationプロファイル。
 */
@interface DConnectAuthorizationProfile : DConnectProfile

#pragma mark - Get Methods

/*!
 @brief Local OAuthで使用するクライアントの作成要求を行う.
 
 @param[in] requst リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] package パッケージ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) didReceiveGetCreateClientRequest:(DConnectRequestMessage *)request
                                 response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId package:(NSString *)package;

/*!
 
 @brief Local OAuthで使用するアクセストークンの作成要求を行う。
 
 @param[in] requst リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] clientId クライアントID
 @param[in] grantType グラントタイプ
 @param[in] scopes スコープ一覧
 @param[in] applicationName アプリケーション名
 @param[in] signature シグネチャー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) didReceiveGetRequestAccessTokenRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId clientId:(NSString *)clientId grantType:(NSString *)grantType scopes:(NSArray *)scopes applicationName:(NSString *)applicationName signature:(NSString *)signature;

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
