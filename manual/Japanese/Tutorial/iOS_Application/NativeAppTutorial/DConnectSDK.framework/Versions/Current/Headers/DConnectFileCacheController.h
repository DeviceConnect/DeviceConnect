//
//  DConnectFileCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベントデータをファイルに保持し、キャッシュの操作機能を提供する。
 @author NTT DOCOMO
 */

#import <DConnectSDK/DConnectMemoryCacheController.h>

/*!
 @class DConnectFileCacheController
 @brief イベントデータをファイルに保持し、キャッシュの操作機能を提供する。
 
 当クラスのインスタンスを @link DConnectEventManager::setController: @endlink で設定することで
 イベントデータをファイルで管理することが出来る。
 常時はメモリに保持され、@link DConnectEventManager::flush @endlink を実行することでファイルに保存される。
 データを永続化する場合は必ずフラッシュすること。
 また、初期化時にフラグを設定することでデータ操作時に自動的にフラッシュすることも出来る。
 
 */
@interface DConnectFileCacheController : DConnectMemoryCacheController

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectFileCacheControllerのインスタンス
 */
- (id) initWithClass:(Class)clazz;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @param[in] autoFlush 自動フラッシュフラグ。YESにすると追加、削除時に自動的にファイルに反映される。
 @return DConnectFileCacheControllerのインスタンス
 */
- (id) initWithClass:(Class)clazz autoFlush:(BOOL)autoFlush;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] key キー文字列
 @return DConnectFileCacheControllerのインスタンス
 */
- (id) initWithKey:(NSString *)key;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] key キー文字列
 @param[in] autoFlush 自動フラッシュフラグ。YESにすると追加、削除時に自動的にファイルに反映される。
 @return DConnectFileCacheControllerのインスタンス
 */
- (id) initWithKey:(NSString *)key autoFlush:(BOOL)autoFlush;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを生成、初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @return DConnectFileCacheControllerのインスタンス
 */
+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを生成、初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] key キー文字列
 @return DConnectFileCacheControllerのインスタンス
 */
+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを生成、初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] clazz クラスオブジェクト。このクラスのクラス名をキーとする。
 @param[in] autoFlush 自動フラッシュフラグ。YESにすると追加、削除時に自動的にファイルに反映される。
 @return DConnectFileCacheControllerのインスタンス
 */
+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz autoFlush:(BOOL)autoFlush;

/*!
 @brief 指定されたクラスの名前をキーとしてFileCacheControllerのインスタンスを生成、初期化する。
 
 ファイル名を分別するため、キーを指定して初期化する必要がある。
 
 @param[in] key キー文字列
 @param[in] autoFlush 自動フラッシュフラグ。YESにすると追加、削除時に自動的にファイルに反映される。
 @return DConnectFileCacheControllerのインスタンス
 */
+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key autoFlush:(BOOL)autoFlush;

@end
