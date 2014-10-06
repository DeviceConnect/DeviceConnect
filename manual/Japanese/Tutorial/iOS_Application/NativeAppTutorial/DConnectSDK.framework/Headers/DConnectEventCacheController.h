//
//  DConnectEventCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベントデータのキャッシュ操作機能を定義する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectEvent.h>

/*!
 @protocol DConnectEventCacheController
 @brief イベントデータのキャッシュ操作機能を定義するプロトコル。
 
 イベントデータのキャッシュ操作を行う機能を定義する。
 データの追加、削除、検索を提供する。
 当クラスの実装クラスのオブジェクトが @link DConnectEventManager @endlink に設定されることで
 抽象化されたイベントデータのキャッシュ操作が実行される。
 
 */
@protocol DConnectEventCacheController <NSObject>
@required

/*!
 @brief イベントデータをキャッシュに追加する。
 
 @param[in] event イベントデータ
 @return 処理結果
 */
- (DConnectEventError) addEvent:(DConnectEvent *)event;

/*!
 @brief イベントデータをキャッシュから削除する。
 
 @param[in] event イベントデータ
 @return 処理結果
 */
- (DConnectEventError) removeEvent:(DConnectEvent *)event;

/*!
 @brief 指定されたセッションキーに紐づくイベント情報を全て削除する。
 
 @param[in] sessionKey セッションキー
 @return 成功の場合YES、その他はNOを返す
 */
- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey;

/*!
 @brief キャッシュからデータを全て削除する。
 
 @return 成功の場合YES、その他はNOを返す
 */
- (BOOL) removeAll;

/*!
 @brief キャッシュから指定された条件に合うイベントデータを取得する。
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute アトリビュート名
 @param[in] sessionKey セッションキー
 @return イベントデータ。条件に合うものが無い場合はnilを返す。
 */
- (DConnectEvent *) eventForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                           interface:(NSString *)interface attribute:(NSString *)attribute
                          sessionKey:(NSString *)sessionKey;

/*!
 @brief キャッシュから条件にあうイベントデータの一覧を取得する。
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute アトリビュート名
 @return イベントデータの一覧。無い場合は空のリストを返す。
 */
- (NSArray *) eventsForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                      interface:(NSString *)interface attribute:(NSString *)attribute;

/*!
 @brief キャッシュデータをフラッシュする。
 */
- (void) flush;

@end
