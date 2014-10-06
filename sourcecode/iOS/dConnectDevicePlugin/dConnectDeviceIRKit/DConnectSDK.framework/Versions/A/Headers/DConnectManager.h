//
//  DConnectManager.h
//  dConnectManager
//
//  Created by 小林 伸郎 on 2014/05/02.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*!
 @mainpage
 
 dConnectの説明ページ
 */

/*! @file
 @brief dConnect本体。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectProfileProvider.h>


/*!
 @brief アプリケーションがホームボタン押下で一時停止されたことを通知するイベント名。

 イベントを発生させるためには、アプリ側のAppDelegateに以下の実装を行う必要があります。
 @code
 - (void)applicationDidEnterBackground:(UIApplication *)application
 {
    NSNotification* n = [NSNotification notificationWithName:DConnectApplicationDidEnterBackground object:self];
    [[NSNotificationCenter defaultCenter] postNotification:n];
 }
 @endcode
 */
extern NSString *const DConnectApplicationDidEnterBackground;

/*!
 @brief アプリケーションが再開されたことを通知するイベント名。

 イベントを発生させるためには、アプリ側のAppDelegateに以下の実装を行う必要があります。
 @code
 - (void)applicationWillEnterForeground:(UIApplication *)application
 {
    NSNotification* n = [NSNotification notificationWithName:DConnectApplicationWillEnterForeground object:self];
    [[NSNotificationCenter defaultCenter] postNotification:n];
 }
 @endcode
 */
extern NSString *const DConnectApplicationWillEnterForeground;



@class DConnectManager;

/*! @brief DConnectManagerからのイベント受け取るデリゲート.
 */
@protocol DConnectManagerDelegate <NSObject>

/*!
 @brief 各デバイスからのイベントを受領する.
 @param[in] manager マネジャー
 @param[in] event イベントメッセージ
 */
- (void) manager:(DConnectManager *)manager didReceiveDConnectMessage:(DConnectMessage *)event;

@end


/*!
 @class DConnectManager
 @brief DConnectを管理するクラス.
 
 @code
 
 // DConnectManagerの開始
 DConnectManager *mgr = [DConnectManager sharedManager];
 mgr.delegate = delegate;
 
 @endcode
 */
@interface DConnectManager : NSObject <DConnectProfileProvider>

/*!
 @brief 各デバイスからのイベントを受領するデリゲート.
 MARK: d-ConnectブラウザはWebSocket経由でイベントを受け取るので、delegateをnullにしておく事。
 */
@property (nonatomic, weak) id<DConnectManagerDelegate> delegate;

/*!
 @brief DConnectManagerのインスタンスを取得する。
 @return DConnectManagerのインスタンス
 */
+ (DConnectManager *) sharedManager;

/*!
 @brief dConnectManagerに非同期的にリクエストを送信する。
 
 dConnectManagerでは、リクエストを非同期的に実行し、対応するレスポンスをcallbackに通知する。<br/>
 このメソッドを実行する場合には時間がかかる場合がある。<br/>
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
