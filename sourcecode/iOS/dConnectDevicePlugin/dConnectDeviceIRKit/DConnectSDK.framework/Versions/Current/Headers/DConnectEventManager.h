//
//  DConnectEventManager.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectEventCacheController.h>

#pragma mark - DConnectEventManager

@interface DConnectEventManager : NSObject

#pragma mark Static Methods

/*!
 DConnectEventManagerのインスタンスを取得する.
 指定されたクラスのクラス名をキーとして、紐づくマネージャーを取得する。
 指定されたクラスに紐づくマネージャーが無い場合は新規に作成される。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectEventManagerのインスタンス
 */
+ (DConnectEventManager *) sharedManagerForClass:(Class)clazz;

/*!
 DConnectEventManagerのインスタンスを取得する.
 指定された文字列をキーとして、紐づくマネージャーを取得する。
 指定されたキーに紐づくマネージャーが無い場合は新規に作成される。
 
 @param[in] key キー文字列
 @return DConnectEventManagerのインスタンス
 */
+ (DConnectEventManager *) sharedManagerForKey:(NSString *)key;

#pragma mark Instance Methods

/*!
 キャッシュの操作クラスを設定する.
 スレッドセーフではないので、必要な場合は呼び出しもとで同期処理をすること。
 
 @param[in] controller キャッシュ操作オブジェクト
 */
- (void) setController:(id<DConnectEventCacheController>)controller;

/*!
 指定されたイベント登録用のリクエストからイベントデータを登録する.
 
 @param[in] request イベント登録リクエスト
 @return 処理結果
 */
- (DConnectEventError) addEventForRequest:(DConnectRequestMessage *)request;

/*!
 指定されたイベント解除用のリクエストからイベントデータを解除する.
 
 @param[in] request イベント解除リクエスト
 @return 処理結果
 */
- (DConnectEventError) removeEventForRequest:(DConnectRequestMessage *)request;

/*!
 指定されたセッションキーに紐づくイベント情報を解除する.
 
 @param[in] sessionKey セッションキー
 @return 削除に成功した場合はYES、その他はNOを返す。
 */
- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey;

/*!
 イベントを全て削除する.

 @return 成功の場合YES、その他はNOを返す。
 */
- (BOOL) removeAll;

/*!
 指定されたAPIに紐づくイベント情報の一覧を取得する.
 
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForProfile:(NSString *)profile
                        interface:(NSString *)interface
                        attribute:(NSString *)attribute;

/*!
 指定されたAPIに紐づくイベント情報の一覧を取得する.
 
 @param[in] profile プロファイル名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForProfile:(NSString *)profile
                        attribute:(NSString *)attribute;

/*!
 指定されたデバイスIDとAPIに紐づくイベント情報の一覧を取得する.
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForDeviceId:(NSString *)deviceId
                           profile:(NSString *)profile
                         attribute:(NSString *)attribute;

/*!
 指定されたデバイスIDとAPIに紐づくイベント情報の一覧を取得する.
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForDeviceId:(NSString *)deviceId
                           profile:(NSString *)profile
                         interface:(NSString *)interface
                         attribute:(NSString *)attribute;

/*!
 指定されたイベント情報からAPI情報、デバイスID、セッションキーをもつ
 イベントメッセージを生成する.
 
 @param[in] event イベントデータ
 @return イベントメッセージ
 */
+ (DConnectMessage *) createEventMessageWithEvent:(DConnectEvent *)event;

@end
