//
//  NotificationProfile.h
//  DConnectSDK
//
//  Created by 小林 伸郎 on 2014/05/12.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief Notificationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: notification。 */
extern NSString *const DConnectNotificationProfileName;

/*!
 @brief 属性: notify。
 */
extern NSString *const DConnectNotificationProfileAttrNotify;

/*!
 @brief 属性: onclick。
 */
extern NSString *const DConnectNotificationProfileAttrOnClick;

/*!
 @brief 属性: onshow。
 */
extern NSString *const DConnectNotificationProfileAttrOnShow;

/*!
 @brief 属性: onclose。
 */
extern NSString *const DConnectNotificationProfileAttrOnClose;

/*!
 @brief 属性: onerror。
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
 @brief 文字の向き: Unknowns。
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
 @brief Notificationプロファイルのデリゲート。
 <p>
 スマートデバイスのノーティフィケーションの操作機能を提供するAPI。<br/>
 スマートデバイスのノーティフィケーションの操作機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>

 */
@protocol DConnectNotificationProfileDelegate <NSObject>
@optional

#pragma mark - Post Methods

/*!
 @brief ノーティフィケーションの表示要求をする。
 
 各デバイスプラグインは、この関数を実装することでファイルの取得が行える。
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
 onclick属性イベントを登録する.
 
 各デバイスプラグインは、この関数を実装することでonclick属性のイベント通知の受領ができるようになる。<br>
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
 onshow属性イベントを登録する.
 
 各デバイスプラグインは、この関数を実装することでonshow属性のイベント通知の受領ができるようになる。<br>
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
 onclose属性イベントを登録する.
 
 各デバイスプラグインは、この関数を実装することでonclose属性のイベント通知の受領ができるようになる。<br>
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
 onerror属性イベントを登録する.
 
 各デバイスプラグインは、この関数を実装することでonerror属性のイベント通知の受領ができるようになる。<br>
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
 @brief ノーティフィケーションの削除要求をする。
 
 各デバイスプラグインは、この関数を実装することでファイルの取得が行える。
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
 onclick属性イベントを解除する.
 
 各デバイスプラグインは、この関数を実装することでonclick属性のイベント通知の受領を停止できる。<br>
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
 onshow属性イベントを解除する.
 
 各デバイスプラグインは、この関数を実装することでonshow属性のイベント通知の受領を停止できる。<br>
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
 onclose属性イベントを解除する.
 
 各デバイスプラグインは、この関数を実装することでonclose属性のイベント通知の受領を停止できる。<br>
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
 onerror属性イベントを解除する.
 
 各デバイスプラグインは、この関数を実装することでonerror属性のイベント通知の受領を停止できる。<br>
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
 @brief Notificationプロファイル.
 
 以下のメソッドを実装することで、Notificationプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 @li didReceivePostNotifyRequest: response: deviceId: type: dir: lang: body: tag: iconData:
 @li didReceiveDeleteNotifyRequest: response: deviceId: notificationId:
 @li didReceivePutOnClickRequest: response: deviceId: sessionKey:
 @li didReceivePutOnShowRequest: response: deviceId: sessionKey:
 @li didReceivePutOnCloseRequest: response: deviceId: sessionKey:
 @li didReceivePutOnErrorRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnClickRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnShowRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnCloseRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnErrorRequest: response: deviceId: sessionKey:
 */
@interface DConnectNotificationProfile : DConnectProfile

/*!
 @brief DConnectNotificationProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectNotificationProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief dConnectメッセージに通知IDを設定する。
 
 @param[in] notificationId 通知ID
 @param[in,out] message dConnectメッセージ
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
