//
//  DConnectResponseMessage.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/06/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectMessage.h>

/*! @file
 @brief リクエストに対するレスポンスデータを提供するクラス.
 @author NTT DOCOMO
 @date 作成日(2014.6.9)
 */
@interface DConnectResponseMessage : DConnectMessage

/** 識別コード. */
@property (readonly, nonatomic, strong) NSString *code;

#pragma mark - Result

/*!
 レスポンスに指定された処理結果を設定する.
 
 @param[in] result 処理結果
 */
- (void) setResult:(DConnectMessageResultType)result;

/*!
 レスポンスに設定されている処理結果を取得する.
 */
- (DConnectMessageResultType) result;

#pragma mark - Error

/*!
 レスポンスエラーを指定されたエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] error エラーコード
 @param[in] message エラーメッセージ
 */
- (void) setError:(DConnectMessageErrorCodeType)error message:(NSString *)message;

/*!
 レスポンスエラーを原因不明エラーに設定する.
 */
- (void) setErrorToUnknown;

/*!
 レスポンスエラーを原因不明エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToUnknownWithMessage:(NSString *)message;

/*!
 レスポンスエラーを未サポートプロファイルエラーに設定する.
 */
- (void) setErrorToNotSupportProfile;

/*!
 レスポンスエラーを未サポートプロファイルエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportProfileWithMessage:(NSString *)message;

/*!
 レスポンスエラーを未サポートHTTPメソッドエラーに設定する.
 */
- (void) setErrorToNotSupportAction;

/*!
 レスポンスエラーを未サポートHTTPメソッドエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportActionWithMessage:(NSString *)message;

/*!
 レスポンスエラーを未サポート属性・インターフェースエラーに設定する.
 */
- (void) setErrorToNotSupportAttribute;

/*!
 レスポンスエラーを未サポート属性・インターフェースエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotSupportAttributeWithMessage:(NSString *)message;

/*!
 レスポンスエラーをデバイスID未設定エラーに設定する.
 */
- (void) setErrorToEmptyDeviceId;

/*!
 レスポンスエラーをデバイスID未設定エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToEmptyDeviceIdWithMessage:(NSString *)message;

/*!
 レスポンスエラーをデバイス検知失敗エラーに設定する.
 */
- (void) setErrorToNotFoundDevice;

/*!
 レスポンスエラーをデバイス検知失敗エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToNotFoundDeviceWithMessage:(NSString *)message;

/*!
 レスポンスエラーをタイムアウトエラーに設定する.
 */
- (void) setErrorToTimeout;

/*!
 レスポンスエラーをタイムアウトエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToTimeoutWithMessage:(NSString *)message;

/*!
 レスポンスエラーを不明な属性・インターフェースエラーに設定する.
 */
- (void) setErrorToUnknownAttribute;

/*!
 レスポンスエラーを不明な属性・インターフェースエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToUnknownAttributeWithMessage:(NSString *)message;

/*!
 レスポンスエラーをバッテリー低下エラーに設定する.
 */
- (void) setErrorToLowBattery;

/*!
 レスポンスエラーをバッテリー低下エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToLowBatteryWithMessage:(NSString *)message;

/*!
 レスポンスエラーをリクエストパラメータエラーに設定する.
 */
- (void) setErrorToInvalidRequestParameter;

/*!
 レスポンスエラーをリクエストパラメータエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToInvalidRequestParameterWithMessage:(NSString *)message;

/*!
 レスポンスエラーを認証エラーに設定する.
 */
- (void) setErrorToAuthorization;

/*!
 レスポンスエラーを認証エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToAuthorizationWithMessage:(NSString *)message;

/*!
 レスポンスエラーをアクセストークン有効期限切れエラーに設定する.
 */
- (void) setErrorToExpiredAccessToken;

/*!
 レスポンスエラーをアクセストークン有効期限切れエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToExpiredAccessTokenWithMessage:(NSString *)message;

/*!
 レスポンスエラーをアクセストークン未設定エラーに設定する.
 */
- (void) setErrorToEmptyAccessToken;

/*!
 レスポンスエラーをアクセストークン未設定エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToEmptyAccessTokenWithMessage:(NSString *)message;

/*!
 レスポンスエラーをスコープアクセスエラーに設定する.
 */
- (void) setErrorToScope;

/*!
 レスポンスエラーをスコープアクセスエラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToScopeWithMessage:(NSString *)message;

/*!
 レスポンスエラーをデバイス状態エラーに設定する.
 */
- (void) setErrorToIllegalDeviceState;

/*!
 レスポンスエラーをデバイス状態エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToIllegalDeviceStateWithMessage:(NSString *)message;

/*!
 レスポンスエラーをサーバー状態エラーに設定する.
 */
- (void) setErrorToIllegalServerState;

/*!
 レスポンスエラーをサーバー状態エラーに設定し、指定された文字列をエラーメッセージに設定する.
 
 @param[in] message エラーメッセージ
 */
- (void) setErrorToIllegalServerStateWithMessage:(NSString *)message;

@end
