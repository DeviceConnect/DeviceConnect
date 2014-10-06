//
//  DConnectResponseMessage.h
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
 @class DConnectResponseMessage
 @brief レスポンスデータを提供するクラス。
 */
@interface DConnectResponseMessage : DConnectMessage

/*! 
 @brief レスポンス識別コード。
 */
@property (readonly, nonatomic, strong) NSString *code;

#pragma mark - Result

/*!
 @brief レスポンスに指定された処理結果を設定する。
 
 @param[in] result 処理結果
 */
- (void) setResult:(DConnectMessageResultType)result;

/*!
 @brief レスポンスに設定されている処理結果を取得する。
 
 @return 処理結果
 */
- (DConnectMessageResultType) result;

/*!
 @brief エラーコードを取得する。
 
 @return エラーコード
 */
- (DConnectMessageErrorCodeType) errorCode;

#pragma mark - Error

/*!
 @brief レスポンスエラーを指定されたエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] error エラーコード
 @param[in] message エラーメッセージ
 */
- (void) setError:(DConnectMessageErrorCodeType)error message:(NSString *)message;

/*!
 @brief レスポンスエラーを原因不明エラーに設定する。
 */
- (void) setErrorToUnknown;

/*!
 @brief レスポンスエラーを原因不明エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToUnknownWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーを未サポートプロファイルエラーに設定する。
 */
- (void) setErrorToNotSupportProfile;

/*!
 @brief レスポンスエラーを未サポートプロファイルエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportProfileWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーを未サポートHTTPメソッドエラーに設定する。
 */
- (void) setErrorToNotSupportAction;

/*!
 @brief レスポンスエラーを未サポートHTTPメソッドエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportActionWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーを未サポートアトリビュート・インターフェースエラーに設定する。
 */
- (void) setErrorToNotSupportAttribute;

/*!
 @brief レスポンスエラーを未サポートアトリビュート・インターフェースエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportAttributeWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをデバイスID未設定エラーに設定する。
 */
- (void) setErrorToEmptyDeviceId;

/*!
 @brief レスポンスエラーをデバイスID未設定エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToEmptyDeviceIdWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをデバイス検知失敗エラーに設定する。
 */
- (void) setErrorToNotFoundDevice;

/*!
 @brief レスポンスエラーをデバイス検知失敗エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotFoundDeviceWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをタイムアウトエラーに設定する。
 */
- (void) setErrorToTimeout;

/*!
 @brief レスポンスエラーをタイムアウトエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToTimeoutWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーを不明なアトリビュート・インターフェースエラーに設定する。
 */
- (void) setErrorToUnknownAttribute;

/*!
 @brief レスポンスエラーを不明なアトリビュート・インターフェースエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToUnknownAttributeWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをバッテリー低下エラーに設定する。
 */
- (void) setErrorToLowBattery;

/*!
 @brief レスポンスエラーをバッテリー低下エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToLowBatteryWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをリクエストパラメータエラーに設定する。
 */
- (void) setErrorToInvalidRequestParameter;

/*!
 @brief レスポンスエラーをリクエストパラメータエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToInvalidRequestParameterWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーを認証エラーに設定する。
 */
- (void) setErrorToAuthorization;

/*!
 @brief レスポンスエラーを認証エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToAuthorizationWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをアクセストークン有効期限切れエラーに設定する。
 */
- (void) setErrorToExpiredAccessToken;

/*!
 @brief レスポンスエラーをアクセストークン有効期限切れエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToExpiredAccessTokenWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをアクセストークン未設定エラーに設定する。
 */
- (void) setErrorToEmptyAccessToken;

/*!
 @brief レスポンスエラーをアクセストークン未設定エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToEmptyAccessTokenWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをスコープアクセスエラーに設定する。
 */
- (void) setErrorToScope;

/*!
 @brief レスポンスエラーをスコープアクセスエラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToScopeWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをClinet Idが見つからないエラーに設定する。
 */
- (void) setErrorToNotFoundClientId;

/*!
 @brief レスポンスエラーをClinet Idが見つからないに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotFoundClientIdWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをデバイス状態エラーに設定する。
 */
- (void) setErrorToIllegalDeviceState;

/*!
 @brief レスポンスエラーをデバイス状態エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToIllegalDeviceStateWithMessage:(NSString *)message;

/*!
 @brief レスポンスエラーをサーバー状態エラーに設定する。
 */
- (void) setErrorToIllegalServerState;

/*!
 @brief レスポンスエラーをサーバー状態エラーに設定し、指定された文字列をエラーメッセージに設定する。
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToIllegalServerStateWithMessage:(NSString *)message;

@end
