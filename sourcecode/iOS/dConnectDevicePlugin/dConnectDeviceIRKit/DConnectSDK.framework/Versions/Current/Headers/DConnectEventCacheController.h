//
//  DConnectEventCacheController.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectEvent.h>

@protocol DConnectEventCacheController <NSObject>
@required

/*!
 イベントデータをキャッシュに追加する.
 
 @param[in] event イベントデータ
 @return 処理結果
 */
- (DConnectEventError) addEvent:(DConnectEvent *)event;

/*!
 イベントデータをキャッシュから削除する.
 
 @param[in] event イベントデータ
 @return 処理結果
 */
- (DConnectEventError) removeEvent:(DConnectEvent *)event;

/*!
 指定されたセッションキーに紐づくイベント情報を全て削除する.
 
 @param[in] sessionKey セッションキー
 @return 成功の場合YES、その他はNOを返す
 */
- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey;

/*!
 キャッシュからデータを全て削除する.
 
 @return 成功の場合YES、その他はNOを返す
 */
- (BOOL) removeAll;

/*!
 キャッシュから指定された条件に合うイベントデータを取得する.
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute 属性名
 @param[in] sessionKey セッションキー
 @return イベントデータ。条件に合うものが無い場合はnilを返す。
 */
- (DConnectEvent *) eventForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                           interface:(NSString *)interface attribute:(NSString *)attribute
                          sessionKey:(NSString *)sessionKey;

/*!
 キャッシュから条件にあうイベントデータの一覧を取得する.
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute 属性名
 @return イベントデータの一覧。無い場合は空のリストを返す。
 */
- (NSArray *) eventsForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                      interface:(NSString *)interface attribute:(NSString *)attribute;

/*!
 キャッシュデータをフラッシュする.
 */
- (void) flush;

@end
