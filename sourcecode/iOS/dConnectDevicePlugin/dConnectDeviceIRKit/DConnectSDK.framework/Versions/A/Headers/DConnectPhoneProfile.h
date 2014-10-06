//
//  DConnectPhoneProfile.h
//  DConnectSDK
//
//  Created by 福井 重和 on 2014/05/19.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief Phoneプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.19)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: phone。 */
extern NSString *const DConnectPhoneProfileName;

/*!
 @brief 属性: call。
 */
extern NSString *const DConnectPhoneProfileAttrCall;

/*!
 @brief 属性: set。
 */
extern NSString *const DConnectPhoneProfileAttrSet;

/*!
 @brief 属性: onconnect。
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
 @brief Phone プロファイル。
 <p>
 通話操作機能を提供するAPI。<br/>
 通話操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DConnectPhoneProfileDelegate <NSObject>
@optional

#pragma mark - Post Methods

/*!
 @brief 電話発信を行う。
 
 各デバイスプラグインは、この関数を実装することで電話発信が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId phoneNumber:(NSString *)phoneNumber;

#pragma mark - Put Methods

/*!
 @brief 電話の設定を行う.
 
 各デバイスプラグインは、この関数を実装することで電話の設定（サイレント・マナー・音あり）が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
            mode:(NSNumber *)mode;

#pragma mark Event Registration

/*!
 ファイル更新イベント通知の受領登録をする.
 
 各デバイスプラグインは、この関数を実装することでファイル更新イベント通知の受領ができるようになる。<br>
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
 ファイル更新イベント通知の受領解除をする.
 
 各デバイスプラグインは、この関数を実装することでファイル更新イベント通知の受領を停止できる。<br>
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
 @brief Phoneプロファイル.
 
 以下のメソッドを実装することで、Phoneプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 @li didReceivePostCallRequest: response: deviceId: mediaId:
 @li didReceivePutSetRequest: response: deviceId: mode:
 @li didReceivePutOnConnectRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnConnectRequest: response: deviceId: sessionKey:
 */
@interface DConnectPhoneProfile : DConnectProfile

/*!
 @brief DConnectPhoneProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
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
 @brief dConnectメッセージにイベントオブジェクトを設定する。
 
 @param[in] phoneStatus 電話状態オブジェクト
 @param[in,out] message dConnectメッセージ
 */
+ (void) setPhoneStatus:(DConnectMessage *)phoneStatus target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに通話状態を設定する。
 
 @param[in] state 通話状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setState:(DConnectPhoneProfileCallState)state target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに発信先の電話番号を設定する。
 
 @param[in] phoneNumber 電話番号
 @param[in,out] message dConnectメッセージ
 */
+ (void) setPhoneNumber:(NSString *)phoneNumber target:(DConnectMessage *)message;

@end
