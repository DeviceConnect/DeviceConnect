//
//  DPChromecastManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>

@interface DPChromecastManager : NSObject

// 接続可能なデバイスリスト取得
@property (nonatomic, readonly) NSArray *deviceList;

// 共有インスタンス
+ (instancetype)sharedManager;

// スキャン開始
- (void)startScan;
// スキャン停止
- (void)stopScan;
// デバイスに接続
- (void)connectToDeviceWithID:(NSString*)deviceid
				   completion:(void (^)(BOOL success, NSString *error))completion;
// 接続中のデバイスから切断
- (void)disconnectDeviceWithID:(NSString*)deviceID;

// イベントコールバックを設定
- (void)setEventCallbackWithID:(NSString*)deviceID callback:(void (^)(NSString *mediaID))callback;

// 接続チェック
- (BOOL)isConnectedWithID:(NSString*)deviceID;

// テキストの送信
- (void)sendMessageWithID:(NSString*)deviceID message:(NSString*)message type:(int)type;
// テキストのクリア
- (void)clearMessageWithID:(NSString*)deviceID;

// メディアプレイヤーの状態取得
- (NSString*)mediaPlayerStateWithID:(NSString*)deviceID;

// 再生位置を取得
- (NSTimeInterval)streamPositionWithID:(NSString*)deviceID;
// 再生位置を変更
- (void)setStreamPositionWithID:(NSString*)deviceID position:(NSTimeInterval)streamPosition;
// 音量を取得
- (float)volumeWithID:(NSString*)deviceID;
// 音量を設定
- (void)setVolumeWithID:(NSString*)deviceID volume:(float)volume;
// ミュート状態取得
- (BOOL)isMutedWithID:(NSString*)deviceID;
// ミュート状態設定
- (void)setIsMutedWithID:(NSString*)deviceID muted:(BOOL)isMuted;


// メディア読み込み
- (NSInteger)loadMediaWithID:(NSString*)deviceID mediaID:(NSString*)mediaID;
// 再生
- (NSInteger)playWithID:(NSString*)deviceID;
// 停止
- (NSInteger)stopWithID:(NSString*)deviceID;
// 一時停止
- (NSInteger)pauseWithID:(NSString*)deviceID;

//長さ取得
- (NSTimeInterval)durationWithID:(NSString*)deviceID;

@end
