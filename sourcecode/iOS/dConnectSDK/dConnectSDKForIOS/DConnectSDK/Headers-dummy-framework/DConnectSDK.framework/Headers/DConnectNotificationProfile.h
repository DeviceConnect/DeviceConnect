//
//  NotificationProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Notificationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: notification。
 */
extern NSString *const DConnectNotificationProfileName;

/*!
 @brief アトリビュート: notify。
 */
extern NSString *const DConnectNotificationProfileAttrNotify;

/*!
 @brief アトリビュート: onclick。
 */
extern NSString *const DConnectNotificationProfileAttrOnClick;

/*!
 @brief アトリビュート: onshow。
 */
extern NSString *const DConnectNotificationProfileAttrOnShow;

/*!
 @brief アトリビュート: onclose。
 */
extern NSString *const DConnectNotificationProfileAttrOnClose;

/*!
 @brief アトリビュート: onerror。
 */
extern NSString *const DConnectNotificationProfileAttrOnError;

/*!
 @brief パラメータ: body。
 */
extern NSString *const DConnectNotificationProfileParamBody;

/*!
 @brief パラメータ: type。
 */
extern NSString *const DConnectNotificationProfileParamType;

/*!
 @brief パラメータ: dir。
 */
extern NSString *const DConnectNotificationProfileParamDir;

/*!
 @brief パラメータ: lang。
 */
extern NSString *const DConnectNotificationProfileParamLang;

/*!
 @brief パラメータ: tag。
 */
extern NSString *const DConnectNotificationProfileParamTag;

/*!
 @brief パラメータ: icon。
 */
extern NSString *const DConnectNotificationProfileParamIcon;

/*!
 @brief パラメータ: notificationid。
 */
extern NSString *const DConnectNotificationProfileParamNotificationId;

/*!
 @brief パラメータ: uri。
 */
extern NSString *const DConnectNotificationProfileParamUri;

/*!
 @brief 文字の向き: Unknown。
 */
extern NSString *const DConnectNotificationProfileDirectionUnknown;
/*!
 @brief 文字の向き: auto。
 */
extern NSString *const DConnectNotificationProfileDirectionAuto;
/*!
 @brief 文字の向き: rtl。
 */
extern NSString *const DConnectNotificationProfileDirectionRightToLeft;
/*!
 @brief 文字の向き: ltr。
 */
extern NSString *const DConnectNotificationProfileDirectionLeftToRight;

/*!
 @enum DConnectNotificationProfileNotificationType
 @brief 通知タイプ定数。
 */
typedef NS_ENUM(NSInteger, DConnectNotificationProfileNotificationType) {
    DConnectNotificationProfileNotificationTypeUnknown = -1,    /*!< 未定数値 */
    DConnectNotificationProfileNotificationTypePhone = 0,       /*!< 音声通話着信 */
    DConnectNotificationProfileNotificationTypeMail,            /*!< メール着信 */
    DConnectNotificationProfileNotificationTypeSMS,             /*!< SMS着信 */
    DConnectNotificationProfileNotificationTypeEvent,           /*!< イベント */
};

@class DConnectNotificationProfile;

/*!
 @protocol DConnectNotificationProfileDelegate
 @brief Notification Profileの各APIリクエスト通知用デリゲート。
 
 Notification Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectNotificationProfileDelegate <NSObject>
@optional

#pragma mark - Post Methods

/*!
 
 @brief ノーティフィケーションの表示リクエストを受け取ったことをデリゲートに通知する。
 
 profileがノーティフィケーションの表示リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification API [POST]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] type 通知タイプ
 @param[in] dir メッセージの文字の向き。省略された場合nil。
 @param[in] lang メッセージの言語。省略された場合nil。
 @param[in] body 通知メッセージ。省略された場合nil。
 @param[in] tag 任意タグ文字。省略された場合nil。
 @param[in] icon アイコン画像のバイナリ。省略された場合nil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePostNotifyRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId type:(NSNumber *)type
             dir:(NSString *)dir lang:(NSString *)lang
            body:(NSString *)body tag:(NSString *)tag icon:(NSData *)icon;

#pragma mark - Put Methods
#pragma mark Event Registration

/*!
 
 @brief onclickイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonclickイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Click Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnClickRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onshowイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonshowイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Show Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnShowRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief oncloseイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがoncloseイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Close Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onerrorイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonerrorイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Error Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnErrorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods

/*!
 
 @brief ノーティフィケーションの削除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがノーティフィケーションの削除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] notificationId 通知ID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteNotifyRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId notificationId:(NSString *)notificationId;

#pragma mark Event Unregistration

/*!
 
 @brief onclickイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonclickイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Click Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnClickRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onshowイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonshowイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Show Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnShowRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief oncloseイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがoncloseイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Close Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onerrorイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonerrorイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Notification Error Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnErrorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;


@end

/*!
 @class DConnectNotificationProfile
 @brief Notification プロファイル。
 
 Notification Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectNotificationProfile : DConnectProfile

/*!
 @brief DConnectNotificationProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectNotificationProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectNotificationProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージに通知IDを設定する。
 
 @param[in] notificationId 通知ID
 @param[in,out] message 通知IDを格納するメッセージ
 */
+ (void) setNotificationId:(NSString *)notificationId target:(DConnectMessage *)message;

#pragma mark - Getter

/*!
 @brief リクエストから通知タイプを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 通知タイプ定数
 @retval nil 省略された場合
 */
+ (NSNumber *) typeFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから向きを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval DConnectNotificationProfileDirectionUnknown
 @retval DConnectNotificationProfileDirectionAuto
 @retval DConnectNotificationProfileDirectionRightToLeft
 @retval DConnectNotificationProfileDirectionLeftToRight
 @retval nil 省略された場合
 */
+ (NSString *) dirFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから言語を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 言語(ref. BCP47)
 @retval nil 省略された場合
 */
+ (NSString *) langFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから通知メッセージを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 通知メッセージ
 @retval nil 省略された場合
 */
+ (NSString *) bodyFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストからタグを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval タグ
 @retval nil 省略された場合
 */
+ (NSString *) tagFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストからノーティフィケーションIDを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 通知ID
 @retval nil 省略された場合
 */
+ (NSString *) notificationIdFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストからアイコンデータを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval アイコンデータ
 @retval nil 省略された場合
 */
+ (NSData *) iconFromRequest:(DConnectMessage *)request;

@end
