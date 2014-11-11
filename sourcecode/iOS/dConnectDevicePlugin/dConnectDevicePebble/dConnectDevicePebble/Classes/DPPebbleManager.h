//
//  DPPebbleManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface DPPebbleManager : NSObject

// 接続可能なデバイスリスト取得
@property (nonatomic, readonly) NSArray *deviceList;

// 共有インスタンス
+ (instancetype)sharedManager;

// アプリがバックグラウンドに入った時に呼ぶ
- (void)applicationDidEnterBackground;
// アプリがフォアグラウンドに入った時に呼ぶ
- (void)applicationWillEnterForeground;

// バッテリー情報取得
- (void)fetchBatteryInfo:(NSString*)deviceID callback:(void(^)(float level, BOOL isCharging, NSError *error))callback;
// バッテリーレベル取得
- (void)fetchBatteryLevel:(NSString*)deviceID callback:(void(^)(float level, NSError *error))callback;
// バッテリー充電ステータス取得
- (void)fetchBatteryCharging:(NSString*)deviceID callback:(void(^)(BOOL isCharging, NSError *error))callback;

// 充電中のステータス変更イベント登録
- (void)registChargingChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(BOOL isCharging))eventCallback;
// 充電レベル変更イベント登録
- (void)registBatteryLevelChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(float level))eventCallback;

// 充電中のステータス変更イベント削除
- (void)deleteChargingChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback;
// 充電レベル変更イベント削除
- (void)deleteBatteryLevelChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback;

// 傾きイベント登録
- (void)registDeviceOrientationEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(float x, float y, float z, long long t))eventCallback;
// 傾きイベント削除
- (void)deleteDeviceOrientationEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback;

// 日時取得
- (void)fetchDate:(NSString*)deviceID callback:(void(^)(NSString *date, NSError *error))callback;

// バイブレーション開始
- (void)startVibration:(NSString*)deviceID pattern:(NSArray *) pattern callback:(void(^)(NSError *error))callback;
// バイブレーション停止
- (void)stopVibration:(NSString*)deviceID callback:(void(^)(NSError *error))callback;

// 全てのイベント登録を解除
- (void)deleteAllEvents:(void(^)(NSError *error))callback;

// 画像データ送信
- (void)sendImage:(NSString*)deviceID data:(NSData*)data callback:(void(^)(NSError *error))callback;


@end
