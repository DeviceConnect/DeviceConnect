//
//  DConnectBaseCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief キャッシュコントローラーのベース機能を提供する。
 @author NTT DOCOMO
 */

#import <DConnectSDK/DConnectEventCacheController.h>

/*!
 @class DConnectBaseCacheController
 @brief キャッシュコントローラーのベース機能を提供する。
 */
@interface DConnectBaseCacheController : NSObject<DConnectEventCacheController>

/*!
 @brief イベントデータのパラメータのバリデーションを行う。
 
 イベント登録に必須な項目が指定されているかチェックする。
 
 @param[in] event イベントデータ
 @retval YES プロファイル名、アトリビュート名、セッションキーが指定されている場合。
 @retval NO 上記以外の場合。
 */
- (BOOL) checkParameterOfEvent:(DConnectEvent *)event;

@end
