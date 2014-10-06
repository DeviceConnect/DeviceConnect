//
//  DConnectDBCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベントデータをDBに永続化し、キャッシュの操作機能を提供する。
 @author NTT DOCOMO
 */

#import <DConnectSDK/DConnectBaseCacheController.h>

/*!
 @class DConnectDBCacheController
 @brief イベントデータをDBに永続化し、キャッシュの操作機能を提供する。
 
 当クラスのインスタンスを @link DConnectEventManager::setController: @endlink で設定することで
 イベントデータをDBで管理することが出来る。
 DBのファイルを分別するため、インスタンスの生成にはユニークなキーが必要となる。
 キーは任意のクラス、もしくは文字列を指定する。
 */
@interface DConnectDBCacheController : DConnectBaseCacheController

/*!
 @brief 指定されたクラスの名前をキーとしてDBCacheControllerのインスタンスを初期化する。
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectDBCacheControllerのインスタンス
 */
- (id) initWithClass:(Class)clazz;

/*!
 @brief 指定された文字列をキーとしてDBCacheControllerのインスタンスを初期化する。
 @param[in] key キー文字列
 @return DConnectDBCacheControllerのインスタンス
 */
- (id) initWithKey:(NSString *)key;

/*!
 @brief 指定されたクラスの名前をキーとしてDBCacheControllerのインスタンスを生成、初期化する。
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectDBCacheControllerのインスタンス
 */
+ (DConnectDBCacheController *) controllerWithClass:(Class)clazz;

/*!
 @brief 指定された文字列をキーとしてDBCacheControllerのインスタンスを生成、初期化する。
 @param[in] key キー文字列
 @return DConnectDBCacheControllerのインスタンス
 */
+ (DConnectDBCacheController *) controllerWithKey:(NSString *)key;

@end
