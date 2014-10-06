//
//  DConnectUtil.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Device Connect のAPIを利用するためのユーティリティ機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectMessage.h>

/*!
 @brief 認証成功通知コールバック。
 
 @param[in] clientId クライアントID
 @param[in] clientSecret クライアントシークレット
 @param[in] accessToken アクセストークン
 */
typedef void (^DConnectAuthorizationSuccessBlock)(NSString *clientId, NSString *clientSecret, NSString *accessToken);

/*!
 @brief 認証失敗通知コールバック。
 
 @param[in] errorCode エラーコード
 */
typedef void (^DConnectAuthorizationFailBlock)(DConnectMessageErrorCodeType errorCode);

/*!
 @class DConnectUtil
 @brief Device Connect のAPIを利用するためのユーティリティ機能を提供するクラス。
 */
@interface DConnectUtil : NSObject

/*!
 @brief 非同期的に認証処理を行う。
 
 <p>
 packageNameにはアプリを一意に識別するための値を格納する。iOSではBundle Identifierなどを格納する。<br/>
 appNameは認証確認ダイアログに表示されるアプリ名を指定する。<br/>
 scopesには、アプリで使用するプロファイルの一覧を指定する。ここに指定の無いプロファイルは使用することができない。<br/>
 </p>
 
 <p>
 既にクライアントが作成されている場合に再度同じpackageNameで作成しようとした場合には、新規でクライアントを作成する。
 以前のクライアントは使用できなくなるので、注意が必要。
 </p>
 @param[in] packageName パッケージ名
 @param[in] appName アプリ名
 @param[in] scopes 使用するプロファイル一覧
 @param[in] success 認証成功通知コールバック
 @param[in] error 認証失敗通知コールバック
 */
+ (void) asyncAuthorizeWithPackageName:(NSString *)packageName
                               appName:(NSString *)appName
                                scopes:(NSArray *)scopes
                               success:(DConnectAuthorizationSuccessBlock)success
                                 error:(DConnectAuthorizationFailBlock)error;

/*!
 @brief 同期的に認証処理を行う。
 
 <p>
 packageNameにはアプリを一意に識別するための値を格納する。iOSではBundle Identifierなどを格納する。<br/>
 appNameは認証確認ダイアログに表示されるアプリ名を指定する。<br/>
 scopesには、アプリで使用するプロファイルの一覧を指定する。ここに指定の無いプロファイルは使用することができない。<br/>
 </p>
 <p>
 既にクライアントが作成されている場合に再度同じpackageNameで作成しようとした場合には、新規でクライアントを作成する。
 以前のクライアントは使用できなくなるので、注意が必要。
 </p>
 @param[in] packageName パッケージ名
 @param[in] appName アプリ名
 @param[in] scopes 使用するプロファイル一覧
 @param[in] success 認証成功通知コールバック
 @param[in] error 認証失敗通知コールバック
 */
+ (void) authorizeWithPackageName:(NSString *)packageName
                          appName:(NSString *)appName
                           scopes:(NSArray *)scopes
                          success:(DConnectAuthorizationSuccessBlock)success
                            error:(DConnectAuthorizationFailBlock)error;

/*!
 @brief 同期的にアクセストークンをリフレッシュする。
 
 @param[in] clientId クライアントID
 @param[in] clientSecret クライアントシークレット
 @param[in] appName アプリ名
 @param[in] scopes 使用するプロファイル一覧
 @param[in] success 認証成功通知コールバック
 @param[in] error 認証失敗通知コールバック
 */
+ (void) refreshAccessTokenWithClientId:(NSString *)clientId
                           clientSecret:(NSString *)clientSecret
                                appName:(NSString *)appName
                                 scopes:(NSArray *)scopes
                                success:(DConnectAuthorizationSuccessBlock)success
                                  error:(DConnectAuthorizationFailBlock)error;
/*!
 @brief 認証用に渡すシグネチャを作成する。
 
 @param[in] clientId クライアントID
 @param[in] grantType グランドタイプ
 @param[in] deviceId デバイスID
 @param[in] scopes スコープ一覧
 @param[in] clientSecret クライアントシークレット
 
 @retval シグネチャ文字列
 @retval nil 作成に失敗
 */
+ (NSString *)generateSignatureWithClientId:(NSString *)clientId
                                  grantType:(NSString *)grantType
                                   deviceId:(NSString *)deviceId
                                     scopes:(NSArray *)scopes
                               clientSecret:(NSString *)clientSecret;

/*!
 @brief 取得したアクセストークンが正しいかをチェックするためのシグネチャを作成する。
 
 @param[in] accessToken アクセストークン
 @param[in] clientSecret クライアントシークレット

 @retval シグネチャ文字列
 @retval nil 作成に失敗
*/
+ (NSString *)generateSignatureWithAccessToken: (NSString *)accessToken
                                  clientSecret: (NSString *)clientSecret;

/*!
 @brief 指定されたNSArrayを連結した文字列に変換する。
 
 @param[in] scopes スコープ一覧が格納された配列
 
 @retval 連結された文字列
 @retval nil 連結に失敗した場合
 */
+ (NSString *) combineScopes:(NSArray *)scopes;

/*!
 @brief DConnectManagerが管理するアクセストークンの一覧を表示する。
 
 現在のトップのViewControllerに対し、モーダルビューでアクセストークンの一覧画面を表示する。
 */
+ (void) showAccessTokenList;

@end
