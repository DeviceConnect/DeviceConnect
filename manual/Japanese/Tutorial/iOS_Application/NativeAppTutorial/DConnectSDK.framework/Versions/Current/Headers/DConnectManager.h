//
//  DConnectManager.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Device Connectの本体。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectProfileProvider.h>
#import <DConnectSDk/DConnectSettings.h>


/*!
 @brief アプリケーションがホームボタン押下で一時停止されたことを通知するイベント名。
 */
extern NSString *const DConnectApplicationDidEnterBackground;

/*!
 @brief アプリケーションが再開されたことを通知するイベント名。
 */
extern NSString *const DConnectApplicationWillEnterForeground;


@class DConnectManager;

/*!
 @protocol DConnectManagerDelegate
 @brief DConnectManagerからのイベント通知を受け取るデリゲート。
 */
@protocol DConnectManagerDelegate <NSObject>

/*!
 @brief 各デバイスからのイベントをデリゲートに通知する。
 @param[in] manager マネジャー
 @param[in] event イベントメッセージ
 */
- (void) manager:(DConnectManager *)manager didReceiveDConnectMessage:(DConnectMessage *)event;

@end


/*!
 @class DConnectManager
 @brief DConnectを管理するクラス。
 
 @code
 
 // DConnectManagerの初期化
 DConnectManager *mgr = [DConnectManager sharedManager];
 // DConnectManagerの開始
 [mgr start];
 // websocketサーバの起動
 [mgr startWebsocket];
 
 // nativeで作成する場合にはdelegateを設定すること
 mgr.delegate = delegate;
 
 @endcode
 */
@interface DConnectManager : NSObject <DConnectProfileProvider>

/*!
 @brief DConnectManagerの設定。
 */
@property (nonatomic, readonly) DConnectSettings *settings;

/*!
 @brief 各デバイスからのイベントを受領するデリゲート。
 @attention WebSocket経由でイベントを受け取る場合はnilを設定する必要がある。
 */
@property (nonatomic, weak) id<DConnectManagerDelegate> delegate;

/*!
 @brief DConnectManagerのインスタンスを取得する。
 @return DConnectManagerのインスタンス
 */
+ (DConnectManager *) sharedManager;

/*!
 @brief DConnectManagerを開始する。
 
 <p>
 この関数が呼び出されることでDConnectManagerが起動する。<br/>
 2回目以降startが呼び出されてもDConnectManagerは何も処理を行わない。
 </p>
 */
- (void) start;

/*!
 @brief Websocketサーバを起動する。
 */
- (void) startWebsocket;

/*!
 @brief DConnectManagerが動作しているかをチェックする。
 @retval YES 動作している
 @retval NO 動作していない
 */
- (BOOL) isStarted;

/*!
 @brief DConnectManagerに非同期的にリクエストを送信する。
 
 DConnectManagerでは、リクエストを非同期的に実行し、対応するレスポンスをcallbackに通知する。<br/>
 このメソッドの実行には時間がかかる場合がある。<br/>
 その場合でも問題が発生しないように注意する必要が有る。<br/>
 
 @param[in] request リクエスト
 @param[in] callback コールバック
 */
- (void) sendRequest:(DConnectRequestMessage *) request callback:(DConnectResponseBlocks)callback;

/*!
 @brief レスポンスデータを送信する。
 
 非同期で処理する場合は当メソッドで明示的にレスポンスを返す必要がある。
 
 @param[in] response レスポンスデータ
 */
- (void) sendResponse:(DConnectResponseMessage *)response;

@end
