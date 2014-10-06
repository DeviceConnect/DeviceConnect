//
//  DConnectMemoryCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベントデータをメモリに保持し、キャッシュの操作機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectBaseCacheController.h>

/*!
 @class DConnectMemoryCacheController
 @brief イベントデータをメモリに保持し、キャッシュの操作機能を提供する。
 
 当クラスのインスタンスを @link DConnectEventManager::setController: @endlink で設定することで
 イベントデータをメモリで管理することが出来る。
 @attention インスタンスの破棄と共にイベントデータも破棄されるので注意が必要。
 */
@interface DConnectMemoryCacheController : DConnectBaseCacheController

/*!
 @brief イベントデータの情報を保持するDictionary。
 */
@property (nonatomic, strong) NSMutableDictionary *eventMap;

@end
