//
//  DConnectEventHelper.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief イベント処理のヘルパー。
 @author NTT DOCOMO
 */

#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectManager.h>

/*!
 @brief リクエストメッセージに対するレスポンスメッセージを受け取るblocks。
 
 @param[in] response レスポンスメッセージ
 */
typedef void (^DConnectResponseHandler)(DConnectResponseMessage *response);

/*!
 @brief 登録したイベントのメッセージを受け取るblocks。
 
 @param[in] message イベントメッセージ
 */
typedef void (^DConnectEventHandler)(DConnectMessage *message);

/*!
 @class DConnectEventHelper
 @brief イベントメッセージの登録、解除、受信を行うヘルパークラス。
 
 このクラスによってイベント登録される場合は @link DConnectManager::delegate @endlink を占有するため
 他のオブジェクトと競合しないように管理する必要がある。
 */
@interface DConnectEventHelper : NSObject

/*!
 @brief DConnectEventHelperのインスタンスを取得する。
 @return DConnectEventHelperのインスタンス
 */
+ (DConnectEventHelper *) sharedHelper;

/*!
 @brief 指定されたリクエストを実行し、イベントを登録する。
 
 @param[in] request イベント登録用のリクエスト
 @param[in] responseHandler リクエストへのレスポンスを受け取るblocks
 @param[in] messageHandler 登録したイベントのメッセージを受け取るblocks
 */
- (void) registerEventWithRequest:(DConnectRequestMessage *)request
                  responseHandler:(DConnectResponseHandler)responseHandler
                   messageHandler:(DConnectEventHandler)messageHandler;

/*!
 @brief 指定されたリクエストを実行し、イベントを解除する。
 
 @param[in] request イベント解除用のリクエスト
 @param[in] responseHandler リクエストへのレスポンスを受け取るblocks
 */
- (void) unregisterEventWithRequest:(DConnectRequestMessage *)request
                    responseHandler:(DConnectResponseHandler)responseHandler;


/*!
 @brief イベントを全て解除する。
 @param[in] accessToken DConnectManagerのLocalOAuthが有効になっている場合、各イベント解除APIへアクセスするためのアクセストークンが必要となる。LocalOAuthが無効となっている場合は指定する必要はない。
 */
- (void) unregisterAllEventsWithAccessToken:(NSString *)accessToken;

@end
