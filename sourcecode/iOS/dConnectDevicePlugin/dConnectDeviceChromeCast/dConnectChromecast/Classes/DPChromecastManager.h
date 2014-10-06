//
//  DPChromecastManager.h
//  dConnectChromecast
//
//  Created by Ryuya Takahashi on 2014/09/08.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <DConnectSDK/DConnectSDK.h>

@interface DPChromecastManager : NSObject

// 接続可能なデバイスリスト取得
@property (nonatomic, readonly) NSArray *deviceList;
// 接続チェック
@property (nonatomic, readonly) BOOL isConnected;
// メディアプレイヤーの状態取得
@property (nonatomic, readonly) NSString *mediaPlayerState;
// 再生位置
@property (nonatomic) NSTimeInterval streamPosition;
// 音量
@property (nonatomic) float volume;
// ミュート状態
@property (nonatomic) BOOL isMuted;


typedef void (^DPChromecastBlock)(DConnectMessage *eventMsg);
@property (nonatomic) id chromecastBlock;
// 共有インスタンス
+ (instancetype)sharedManager;

//イベント登録
- (void)addEvent:(NSString *)deviceId block:(DPChromecastBlock)block;
- (void)removeEvent;
// スキャン開始
- (void)startScan;
// スキャン停止
- (void)stopScan;
// デバイスに接続
- (void)connectToDeviceWithID:(NSString*)deviceid
                   completion:(void (^)(BOOL success, NSString *error))completion;
// 接続中のデバイスから切断
- (void)disconnectDevice;

// テキストの送信
- (void)sendMessage:(NSString*)message type:(int)type;
// テキストのクリア
- (void)clearMessage;

// メディア読み込み
- (NSInteger)loadMediaWithID:(NSString*)mediaID;
// 再生
- (NSInteger)play;
// 停止
- (NSInteger)stop;
// 一時停止
- (NSInteger)pause;

//長さ取得
- (NSTimeInterval)duration;

@end
