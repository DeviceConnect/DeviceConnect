//
//  DConnectPhoneProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Phoneプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: phone。
 */
extern NSString *const DConnectPhoneProfileName;

/*!
 @brief アトリビュート: call。
 */
extern NSString *const DConnectPhoneProfileAttrCall;

/*!
 @brief アトリビュート: set。
 */
extern NSString *const DConnectPhoneProfileAttrSet;

/*!
 @brief アトリビュート: onconnect。
 */
extern NSString *const DConnectPhoneProfileAttrOnConnect;

/*!
 @brief パラメータ: phoneNumber。
 */
extern NSString *const DConnectPhoneProfileParamPhoneNumber;

/*!
 @brief パラメータ: mode。
 */
extern NSString *const DConnectPhoneProfileParamMode;

/*!
 @brief パラメータ: phoneStatus。
 */
extern NSString *const DConnectPhoneProfileParamPhoneStatus;

/*!
 @brief パラメータ: state。
 */
extern NSString *const DConnectPhoneProfileParamState;

/*!
 @enum DConnectPhoneProfilePhoneMode
 @brief 電話のモード定数。
 */
typedef NS_ENUM(NSInteger, DConnectPhoneProfilePhoneMode) {
    DConnectPhoneProfilePhoneModeUnknown = -1,  /*!< 未定義値 */
    DConnectPhoneProfilePhoneModeSilent = 0,    /*!< サイレントモード */
    DConnectPhoneProfilePhoneModeManner,        /*!< マナーモード */
    DConnectPhoneProfilePhoneModeSound,         /*!< 音あり */
};

/*!
 @enum DConnectPhoneProfileCallState
 @brief 通話状態定数。
 */
typedef NS_ENUM(NSInteger, DConnectPhoneProfileCallState) {
    DConnectPhoneProfileCallStateUnknown = -1,  /*!< 未定義値 */
    DConnectPhoneProfileCallStateStart = 0,     /*!< 通話開始 */
    DConnectPhoneProfileCallStateFailed,        /*!< 通話失敗 */
    DConnectPhoneProfileCallStateFinished,      /*!< 通話終了 */
};

@class DConnectPhoneProfile;

/*!
 @protocol DConnectPhoneProfileDelegate
 @brief Phone Profileの各APIリクエスト通知用デリゲート。
 
 Phone Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectPhoneProfileDelegate <NSObject>
@optional

#pragma mark - Post Methods

/*!
 
 @brief 電話発信リクエストを受け取ったことをデリゲートに通知する。
 
 profileが電話発信リクエストを受け取ったことをデリゲートに通知する。<br/>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Phone Call API [POST]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] phoneNumber 電話番号
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePostCallRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
     phoneNumber:(NSString *)phoneNumber;

#pragma mark - Put Methods

/*!
 
 @brief 電話の設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileが電話の設定リクエストを受け取ったことをデリゲートに通知する。<br/>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Phone Setting API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] mode 電話のモード
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePutSetRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            mode:(NSNumber *)mode;

#pragma mark Event Registration

/*!
 
 @brief onconnectイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonconnectイベント登録リクエストを受け取ったことをデリゲートに通知する。<br/>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Phone Connect API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePutOnConnectRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregistration

/*!
 
 @brief onconnectイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonconnectイベント解除リクエストを受け取ったことをデリゲートに通知する。<br/>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Phone Connect API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectPhoneProfile *)profile didReceiveDeleteOnConnectRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;


@end

/*!
 @class DConnectPhoneProfile
 @brief Phone プロファイル。
 
 Phone Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectPhoneProfile : DConnectProfile

/*!
 @brief DConnectPhoneProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectPhoneProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectPhoneProfileDelegate> delegate;

#pragma mark - Getter

/*!
 @brief リクエストから発信先の電話番号を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval NSString* 発信先の電話番号
 @retval nil 電話番号の指定が無い場合
 */
+ (NSString *) phoneNumberFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから電話のモードを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 電話のモード定数
 @retval nil 省略された場合
 */
+ (NSNumber *) modeFromRequest:(DConnectMessage *)request;

#pragma mark - Setter

/*!
 @brief メッセージに電話状態を設定する。
 
 @param[in] phoneStatus 電話状態オブジェクト
 @param[in,out] message 電話状態を格納するメッセージ
 */
+ (void) setPhoneStatus:(DConnectMessage *)phoneStatus target:(DConnectMessage *)message;

/*!
 @brief メッセージに通話状態を設定する。
 
 @param[in] state 通話状態
 @param[in,out] message 通話状態を格納するメッセージ
 */
+ (void) setState:(DConnectPhoneProfileCallState)state target:(DConnectMessage *)message;

/*!
 @brief メッセージに発信先の電話番号を設定する。
 
 @param[in] phoneNumber 電話番号
 @param[in,out] message 発信先の電話番号を格納するメッセージ
 */
+ (void) setPhoneNumber:(NSString *)phoneNumber target:(DConnectMessage *)message;

@end
