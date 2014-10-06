//
//  DConnectWebSocket.h
//  websocket
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "WebSocket.h"
#import "GCDAsyncSocket.h"

/*! @brief WebSocketの処理を行うクラス.
 */
@interface DConnectWebSocket : NSObject <GCDAsyncSocketDelegate>

/*! @brief ホスト名.
 デフォルトでは、localhost.
 */
@property (nonatomic) NSString *host;

/*! @brief ポート番号.
 デフォルトでは、4035.
 */
@property (nonatomic) int port;

/*! @brief デフォルトの値でWebsobketを初期化する.
 hostは、localhost<br/>
 portは、4035<br/>
 */
- (id) init;

/*! @brief ホスト名、ポート番号を指定してWebsocketを初期化する.
 */
- (id) initWithHost:(NSString *)host port:(int)port;

/*! @brief Websocketの通信を開始する.
 */
- (BOOL) start;

/*! @brief Websocketの通信を停止する.
 */
- (void) stop;

/*! @brief SSL通信フラグを取得する.
 
 SSL通信を行う場合には、このメソッドでYESを返すこと。
 
 @return SSL通信を行う場合はYES、それ以外の場合はNO
 */
- (BOOL) isUseSSL;

/*! @brief SSL通信に必要なコンフィグを作成する.
 
 SSL通信を行う場合には、このメソッドを実装したクラスを作成すること。
 
 @return SSL通信のコンフィグ
 */
- (NSDictionary *) createSSLConfiguration;

/*! @brief イベントを送信する.
 @param[in] event 送信するイベント
 @param[in] sessionKey 送信先のセッションキー
 */
- (void) sendEvent:(NSString *)event forSessionKey:(NSString *)sessionKey;

@end
