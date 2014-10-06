//
//  DConnectEventManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベント管理機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectEventCacheController.h>

#pragma mark - DConnectEventManager

/*!
 @class DConnectEventManager
 @brief イベント管理機能を提供する。
 
 デバイスプラグインに対してイベントの管理機能を提供する。
 イベントデータの追加、削除、検索の機能を提供する。
 イベントデータの操作処理は @link DConnectEventCacheController @endlink の実態に依存する。
 例として、イベントデータをDBに保存し永続化したい場合は @link DConnectDBCacheController @endlink を
 設定する。
 */
@interface DConnectEventManager : NSObject

#pragma mark Static Methods

/*!
 @brief DConnectEventManagerのインスタンスを取得する。
 
 指定されたクラスのクラス名をキーとして、紐づくマネージャーを取得する。
 指定されたクラスに紐づくマネージャーが無い場合は新規に作成される。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectEventManagerのインスタンス
 */
+ (DConnectEventManager *) sharedManagerForClass:(Class)clazz;

/*!
 @brief DConnectEventManagerのインスタンスを取得する。
 指定された文字列をキーとして、紐づくマネージャーを取得する。
 指定されたキーに紐づくマネージャーが無い場合は新規に作成される。
 
 @param[in] key キー文字列
 @return DConnectEventManagerのインスタンス
 */
+ (DConnectEventManager *) sharedManagerForKey:(NSString *)key;

#pragma mark Instance Methods

/*!
 @brief キャッシュの操作クラスを設定する。
 
 EventManagerの処理は当メソッドで設定されたEventCacheControllerの実装に依存する。
 スレッドセーフではないので、必要な場合は呼び出しもとで同期処理をすること。
 
 @param[in] controller キャッシュ操作オブジェクト
 */
- (void) setController:(id<DConnectEventCacheController>)controller;

/*!
 @brief 指定されたイベント登録用のリクエストからイベントデータを登録する。
 
 @param[in] request イベント登録リクエスト
 @return 処理結果
 */
- (DConnectEventError) addEventForRequest:(DConnectRequestMessage *)request;

/*!
 @brief 指定されたイベント解除用のリクエストからイベントデータを解除する。
 
 @param[in] request イベント解除リクエスト
 @return 処理結果
 */
- (DConnectEventError) removeEventForRequest:(DConnectRequestMessage *)request;

/*!
 @brief 指定されたセッションキーに紐づくイベント情報を解除する。
 
 @param[in] sessionKey セッションキー
 @return 削除に成功した場合はYES、その他はNOを返す。
 */
- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey;

/*!
 @brief イベントを全て削除する。

 @return 成功の場合YES、その他はNOを返す。
 */
- (BOOL) removeAll;

/*!
 @brief 指定されたAPIに紐づくイベント情報の一覧を取得する。
 
 @param[in] profile プロファイル名
 @param[in] interface インターフェース名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForProfile:(NSString *)profile
                        interface:(NSString *)interface
                        attribute:(NSString *)attribute;

/*!
 @brief 指定されたAPIに紐づくイベント情報の一覧を取得する。
 
 @param[in] profile プロファイル名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForProfile:(NSString *)profile
                        attribute:(NSString *)attribute;

/*!
 @brief 指定されたデバイスIDとAPIに紐づくイベント情報の一覧を取得する。
 
 @param[in] deviceId デバイスID
 @param[in] profile プロファイル名
 @param[in] attribute アトリビュート名
 @return イベントの一覧
 */
- (NSArray *) eventListForDeviceId:(NSString *)deviceId
                           profile:(NSString *)profile
                         attribute:(NSString *)attribute;

/*!
 @brief 指定されたデバイスIDとAPIに紐づくイベント情報の一覧を取得する。
 
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
 @brief キャッシュデータを書き込む。

 キャッシュデータの書き込み処理は設定したEventCacheControllerの実装に依存する。
 */
- (void) flush;

/*!
 @brief 指定されたイベント情報からAPI情報、デバイスID、セッションキーをもつイベントメッセージを生成する。
 
 @param[in] event イベントデータ
 @return イベントメッセージ
 */
+ (DConnectMessage *) createEventMessageWithEvent:(DConnectEvent *)event;

@end
