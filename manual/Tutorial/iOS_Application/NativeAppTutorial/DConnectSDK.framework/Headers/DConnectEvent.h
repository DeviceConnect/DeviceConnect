//
//  DConnectEvent.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベントの情報を保持するクラス。
 @author NTT DOCOMO
 */

#pragma mark - Constants

/*!
 @enum DConnectEventError
 @brief Eventのエラー定義。
 */
typedef NS_ENUM(NSUInteger, DConnectEventError) {
    DConnectEventErrorNone,                 /*!< エラー無し */
    DConnectEventErrorInvalidParameter,		/*!< 不正なパラメータ */
    DConnectEventErrorNotFound,       		/*!< マッチするイベント無し */
    DConnectEventErrorFailed,            	/*!< 処理失敗 */
};

#pragma mark - DConnectEvent

/*!
 @class DConnectEvent
 @brief イベントの情報を保持するクラス。
 */
@interface DConnectEvent : NSObject<NSCoding, NSSecureCoding>

/*!
 @brief 登録されたイベントのプロファイル名。
 */
@property (nonatomic, strong) NSString *profile;

/*!
 @brief 登録されたイベントのインターフェース名。
 */
@property (nonatomic, strong) NSString *interface;

/*!
 @brief 登録されたイベントのアトリビュート名。
 */
@property (nonatomic, strong) NSString *attribute;

/*!
 @brief イベント登録時に指定されたデバイスのID。
 */
@property (nonatomic, strong) NSString *deviceId;

/*!
 @brief イベント登録時に指定されたアクセストークン。
 */
@property (nonatomic, strong) NSString *accessToken;

/*!
 @brief イベント登録時に指定されたセッションキー。
 */
@property (nonatomic, strong) NSString *sessionKey;

/*!
 @brief イベント登録日。
 */
@property (nonatomic, strong) NSDate *createDate;

/*!
 @brief イベント更新日。
 */
@property (nonatomic, strong) NSDate *updateDate;

@end
