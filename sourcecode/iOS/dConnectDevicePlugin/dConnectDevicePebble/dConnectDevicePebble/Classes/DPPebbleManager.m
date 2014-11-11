//
//  DPPebbleManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleManager.h"
#import <PebbleKit/PebbleKit.h>
#import "pebble_device_plugin_defines.h"

/** milli G を m/s^2 の値にする係数. */
#define G_TO_MS2_COEFFICIENT 9.81/1000.0

// PebbleWatchAppのUUID
static NSString *const DPPebbleUUID = @"ecfbe3b5-65f4-4532-be4e-3d013058d1f5";

// コマンド送信最大リトライ回数
static const NSInteger DPMaxRetryCount = 3;
// リトライインターバル（秒）
static const NSTimeInterval DPRetryInterval = 1.0;
// セマフォのタイムアウト
static const NSTimeInterval DPSemaphoreTimeout = 10.0;


@interface DPPebbleManager () <PBPebbleCentralDelegate> {
	NSMutableDictionary *_updateHandlerDict;
	NSMutableDictionary *_callbackDict;
	NSMutableDictionary *_eventCallbackDict;
	dispatch_semaphore_t _semaphore;
}

@end

@implementation DPPebbleManager

// 共有インスタンス
+ (instancetype)sharedManager
{
	static id sharedInstance;
	static dispatch_once_t onceSpheroToken;
	dispatch_once(&onceSpheroToken, ^{
		sharedInstance = [[self alloc] init];
	});
	return sharedInstance;
}

// 初期化
- (instancetype)init
{
	self = [super init];
	if (self) {
		_updateHandlerDict = [NSMutableDictionary dictionary];
		_callbackDict = [NSMutableDictionary dictionary];
		_eventCallbackDict = [NSMutableDictionary dictionary];
		_semaphore = dispatch_semaphore_create(1);
		
		[[PBPebbleCentral defaultCentral] setDelegate:self];
		uuid_t myAppUUIDbytes;
		NSUUID *myAppUUID = [[NSUUID alloc] initWithUUIDString:DPPebbleUUID];
		[myAppUUID getUUIDBytes:myAppUUIDbytes];
		[[PBPebbleCentral defaultCentral] setAppUUID:[NSData dataWithBytes:myAppUUIDbytes length:16]];

	}
	return self;
}

// アプリがバックグラウンドに入った時に呼ぶ
- (void)applicationDidEnterBackground
{
}

// アプリがフォアグラウンドに入った時に呼ぶ
- (void)applicationWillEnterForeground
{
	// すぐは復帰できないので。
	dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
		// イベントハンドラの復帰
		for (NSString *deviceID in _updateHandlerDict) {
			PBWatch *watch = [self watchWithDeviceID:deviceID];
			if (watch) {
				[self addHandler:watch deviceID:deviceID];
			}
		}
	});
}


// 接続可能なデバイスリスト取得
- (NSArray*)deviceList
{
	NSMutableArray *array = [NSMutableArray array];
	for (PBWatch *watch in [[PBPebbleCentral defaultCentral] connectedWatches]) {
		//NSLog(@"%@", watch);
		//NSLog(@"%@", watch.name);
		//NSLog(@"%@", watch.serialNumber);
		[array addObject:@{@"name": watch.name, @"id": watch.serialNumber}];
	}
	return array;
}

// デバイスIDからPBWatchを取得
- (PBWatch*)watchWithDeviceID:(NSString*)deviceID
{
	for (PBWatch *watch in [[PBPebbleCentral defaultCentral] connectedWatches]) {
		if ([watch.serialNumber isEqualToString:deviceID]) {
			// 接続済みのもののみ
			if (watch.isConnected) {
				return watch;
			}
			break;
		}
	}
	return nil;
}



#pragma mark - Battery

// バッテリー情報取得
- (void)fetchBatteryInfo:(NSString*)deviceID callback:(void(^)(float level, BOOL isCharging, NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
	dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_ALL);
	dic[@(KEY_ACTION)] = @(ACTION_GET);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(0, NO, error);
			return;
		}
		// レベルを0~1、充電中かをBOOLで返す
		NSNumber *level = data[@(KEY_PARAM_BATTERY_LEVEL)];
		NSNumber *charging = data[@(KEY_PARAM_BATTERY_CHARGING)];
		callback([level intValue] / 100.0, [charging intValue] == BATTERY_CHARGING_ON, nil);
	}];
}

// バッテリーレベル取得
- (void)fetchBatteryLevel:(NSString*)deviceID callback:(void(^)(float level, NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
	dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_LEVEL);
	dic[@(KEY_ACTION)] = @(ACTION_GET);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(0, error);
			return;
		}
		// レベルを0~1で返す
		NSNumber *level = data[@(KEY_PARAM_BATTERY_LEVEL)];
		callback([level intValue] / 100.0, nil);
	}];
}

// バッテリー充電ステータス取得
- (void)fetchBatteryCharging:(NSString*)deviceID callback:(void(^)(BOOL isCharging, NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
	dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_CHARING);
	dic[@(KEY_ACTION)] = @(ACTION_GET);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(NO, error);
			return;
		}
		// 充電中かをBOOLで返す
		NSNumber *charging = data[@(KEY_PARAM_BATTERY_CHARGING)];
		callback([charging intValue] == BATTERY_CHARGING_ON, nil);
	}];
}

// 充電中のステータス変更イベント登録
- (void)registChargingChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(BOOL isCharging))eventCallback
{
	if (!callback || !eventCallback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
	dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE);
	dic[@(KEY_ACTION)] = @(ACTION_PUT);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(error);
			return;
		}
		// 充電中かをBOOLで返す
		NSNumber *action = data[@(KEY_ACTION)];
		if ([action intValue] == ACTION_EVENT) {
			NSNumber *charging = data[@(KEY_PARAM_BATTERY_CHARGING)];
			eventCallback([charging intValue] == BATTERY_CHARGING_ON);
		} else {
			callback(nil);
		}
	}];
}

// 充電レベル変更イベント登録
- (void)registBatteryLevelChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(float level))eventCallback
{
	if (!callback || !eventCallback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BATTERY);
	dic[@(KEY_ATTRIBUTE)] = @(BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE);
	dic[@(KEY_ACTION)] = @(ACTION_PUT);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(error);
			return;
		}
		// レベルを0~1で返す
		NSNumber *action = data[@(KEY_ACTION)];
		if ([action intValue] == ACTION_EVENT) {
			NSNumber *level = data[@(KEY_PARAM_BATTERY_LEVEL)];
			eventCallback([level intValue] / 100.0);
		} else {
			callback(nil);
		}
	}];
}

// 充電中のステータス変更イベント削除
- (void)deleteChargingChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback
{
	[self deleteEvent:deviceID profile:PROFILE_BATTERY attr:BATTERY_ATTRIBUTE_ON_CHARGING_CHANGE callback:callback];
}

// 充電レベル変更イベント削除
- (void)deleteBatteryLevelChangeEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback
{
	[self deleteEvent:deviceID profile:PROFILE_BATTERY attr:BATTERY_ATTRIBUTE_ON_BATTERY_CHANGE callback:callback];
}


#pragma mark - DeviceOrientation

// 傾きイベント登録
- (void)registDeviceOrientationEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback eventCallback:(void(^)(float x, float y, float z, long long t))eventCallback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_DEVICE_ORIENTATION);
	dic[@(KEY_ATTRIBUTE)] = @(DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION);
	dic[@(KEY_ACTION)] = @(ACTION_PUT);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(error);
			return;
		}
		// レベルを0~1で返す
		NSNumber *action = data[@(KEY_ACTION)];
		if ([action intValue] == ACTION_EVENT) {
			NSNumber *xx = data[@(KEY_PARAM_DEVICE_ORIENTATION_X)];
			NSNumber *yy = data[@(KEY_PARAM_DEVICE_ORIENTATION_Y)];
			NSNumber *zz = data[@(KEY_PARAM_DEVICE_ORIENTATION_Z)];
			NSNumber *intervalX = data[@(KEY_PARAM_DEVICE_ORIENTATION_INTERVAL)];
			
			float fx = xx.intValue * G_TO_MS2_COEFFICIENT;
			float fy = yy.intValue * G_TO_MS2_COEFFICIENT;
			float fz = zz.intValue * G_TO_MS2_COEFFICIENT;
			
			eventCallback(fx, fy, fz, intervalX.longLongValue);
		} else {
			callback(nil);
		}
	}];
}

// 傾きイベント削除
- (void)deleteDeviceOrientationEvent:(NSString*)deviceID callback:(void(^)(NSError *error))callback
{
	[self deleteEvent:deviceID profile:PROFILE_DEVICE_ORIENTATION attr:DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION callback:callback];
}


#pragma mark - Setting

// 日時取得
- (void)fetchDate:(NSString*)deviceID callback:(void(^)(NSString *date, NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_SETTING);
	dic[@(KEY_ATTRIBUTE)] = @(SETTING_ATTRIBUTE_DATE);
	dic[@(KEY_ACTION)] = @(ACTION_GET);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		// エラー
		if (!data || error) {
			callback(nil, error);
			return;
		}
		// 日時を返す
		callback(data[@(KEY_PARAM_SETTING_DATE)], nil);
	}];
}


#pragma mark - Vibration

// バイブレーション開始
- (void)startVibration:(NSString*)deviceID pattern:(NSArray *) pattern callback:(void(^)(NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_VIBRATION);
	dic[@(KEY_ATTRIBUTE)] = @(VIBRATION_ATTRIBUTE_VIBRATE);
	dic[@(KEY_ACTION)] = @(ACTION_PUT);
	NSData *pattarnData = [self convertVibrationPattern:pattern];
	if (pattarnData == nil) {
		dic[@(KEY_PARAM_VIBRATION_LEN)] = @(0);
	} else {
		dic[@(KEY_PARAM_VIBRATION_LEN)] = @(pattarnData.length / 2);
		dic[@(KEY_PARAM_VIBRATION_PATTERN)] = pattarnData;
	}
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		callback(error);
	}];
}

// バイブのパターン情報をコンバート
- (NSData*)convertVibrationPattern:(NSArray *)pattern {
	if (pattern == nil || pattern.count == 0) {
		return nil;
	} else {
		NSMutableData *data = [NSMutableData data];
		for (NSNumber *value in pattern) {
			int v = [value intValue];
			char buf[2];
			buf[0] = (char) (v >> 8) & 0xff;
			buf[1] = (char) (v & 0xff);
			[data appendBytes:buf length:2];
		}
		return data;
	}
}

// バイブレーション停止
- (void)stopVibration:(NSString*)deviceID callback:(void(^)(NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_VIBRATION);
	dic[@(KEY_ATTRIBUTE)] = @(VIBRATION_ATTRIBUTE_VIBRATE);
	dic[@(KEY_ACTION)] = @(ACTION_DELETE);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		callback(error);
	}];
}


#pragma mark - System

// 全てのイベント登録を解除
- (void)deleteAllEvents:(void(^)(NSError *error))callback
{
	if (!callback) return;
	
	for (NSArray *data in _eventCallbackDict.allKeys) {
		NSString *deviceID = data[0];
		NSMutableDictionary *dic = [NSMutableDictionary dictionary];
		dic[@(KEY_PROFILE)] = @(PROFILE_SYSTEM);
		dic[@(KEY_ATTRIBUTE)] = @(SYSTEM_ATTRIBUTE_EVENTS);
		dic[@(KEY_ACTION)] = @(ACTION_DELETE);
		[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		}];
	}
	
	// 初期化
	[_eventCallbackDict removeAllObjects];
	
	// コールバック
	callback(nil);
}

#pragma mark - Image


// 分割サイズ（Pebbleアプリ側でも定義してあるので大きくする場合には、Pebbleアプリ側の定義も修正すること）
#define BUF_SIZE 64

// 画像データ送信
- (void)sendImage:(NSString*)deviceID data:(NSData*)data callback:(void(^)(NSError *error))callback
{
	if (!callback) return;
	
	// 画像サイズ送信
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(PROFILE_BINARY);
	dic[@(KEY_PARAM_BINARY_LENGTH)] = @(data.length);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data2, NSError *error) {
		if (error) {
			callback(error);
		} else {
			dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
				NSUInteger count = data.length / BUF_SIZE + 1;
				dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 15);
				// 画像を分割して送信
				dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
				__block NSError *err = nil;
				for (int i = 0; i < count; i++) {
					// 送信
					[self sendImageBody:deviceID data:data index:i callback:^(NSError *error2) {
						err = error2;
						dispatch_semaphore_signal(semaphore);
					}];
					// 順番に処理
					long result = dispatch_semaphore_wait(semaphore, timeout);
					if (result!=0 || err) {
						// タイムアウトかエラーがあったら終了
						break;
					}
				}
				// コールバック
				callback(err);
			});
		}
	}];
}

// 画像データ送信（中身）
- (void)sendImageBody:(NSString*)deviceID data:(NSData *)data index:(int)index callback:(void(^)(NSError *error))callback
{
	BOOL last = (data.length / BUF_SIZE == index);
	
	NSRange range;
	range.location = index * BUF_SIZE;
	if (last) {
		range.length = data.length - index * BUF_SIZE;
	} else {
		range.length = BUF_SIZE;
	}
	NSData *send = [data subdataWithRange:range];
	
	NSMutableDictionary *request = [NSMutableDictionary dictionary];
	request[@(KEY_PROFILE)] = @(PROFILE_BINARY);
	request[@(KEY_PARAM_BINARY_INDEX)] = @(index);
	request[@(KEY_PARAM_BINARY_BODY)] = send;
	[self sendCommand:deviceID request:request callback:^(NSDictionary *data, NSError *error) {
		callback(error);
	}];
}


#pragma mark - Common

// イベント削除共通
- (void)deleteEvent:(NSString*)deviceID profile:(UInt32)profile attr:(UInt32)attr callback:(void(^)(NSError *error))callback
{
	if (!callback) return;
	
	NSMutableDictionary *dic = [NSMutableDictionary dictionary];
	dic[@(KEY_PROFILE)] = @(profile);
	dic[@(KEY_ATTRIBUTE)] = @(attr);
	dic[@(KEY_ACTION)] = @(ACTION_DELETE);
	[self sendCommand:deviceID request:dic callback:^(NSDictionary *data, NSError *error) {
		callback(error);
		// 保持していたBlockを解放
		[_eventCallbackDict removeObjectForKey:@[deviceID, @(profile), @(attr)]];
	}];
}


#pragma mark - Send Command

// コマンド送信
- (void)sendCommand:(NSString*)deviceID request:(NSMutableDictionary*)request callback:(void(^)(NSDictionary*, NSError*))callback
{
	// 別スレッドで実行
	dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
		// コマンドが連続して送信されないようにセマフォを立てる
		dispatch_semaphore_wait(_semaphore, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * DPSemaphoreTimeout));
		// メインスレッドじゃないとPebbleコマンドが実行されない
		dispatch_async(dispatch_get_main_queue(), ^{
			// Callbackを保持（updateHandler:update:で使用する）
			_callbackDict[deviceID] = callback;
			// Event用のCallbackを保持（Attribute毎にCallbackが変わるのでキーに追加）
			NSNumber *action = request[@(KEY_ACTION)];
			if ([action intValue] == ACTION_PUT) {
				_eventCallbackDict[@[deviceID, request[@(KEY_PROFILE)], request[@(KEY_ATTRIBUTE)]]] = callback;
			}

			// コマンド実行
			[self sendCommand:deviceID request:request retryCount:0];
		});
	});
}

// コマンド送信（実装）
- (void)sendCommand:(NSString*)deviceID request:(NSMutableDictionary*)request retryCount:(int)retryCount
{
//	NSLog(@"sendCommand:%@ request:%@ retryCount:%d", deviceID, request, retryCount);

	// リクエストコードを作成して、リクエストに追加
	NSNumber *requestCode = @(rand());
	request[@(KEY_PARAM_REQUEST_CODE)] = requestCode;

	// DeviceIDからWatch取得
	PBWatch *watch = [self watchWithDeviceID:deviceID];
	if (!watch) {
		// セマフォ解除
		dispatch_semaphore_signal(_semaphore);
		// エラーをコールバック
		void (^callback)(NSDictionary*, NSError*)  = _callbackDict[deviceID];
		if (callback) {
			NSError *error = [NSError errorWithDomain:@"DPPebbleManager" code:403 userInfo:nil];
			callback(nil, error);
		}
		return;
	}
	
	// UpdateHandler登録（１つのWatchに１つだけ）
	// フロー的に削除される事は無い（アプリがバックグラウンドに行った時は全てクリアされる）
	if (!_updateHandlerDict[deviceID]) {
		[self addHandler:watch deviceID:deviceID];
	}
	
	// メッセージ送信
	[watch appMessagesPushUpdate:request onSent:^(PBWatch *watch, NSDictionary *update, NSError *error) {
		if (error) {
			// 送信に失敗した場合には、DPMaxRetryCount回までアプリを起動してから再送する
			[watch appMessagesLaunch:^(PBWatch *watch, NSError *error2) {
				if (retryCount < DPMaxRetryCount) {
					// 一定時間後に再度実行
					dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(DPRetryInterval * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
						[self sendCommand:deviceID request:request retryCount:retryCount+1];
					});
				} else {
					// セマフォ解除
					dispatch_semaphore_signal(_semaphore);
					// エラーをコールバック
					void (^callback)(NSDictionary*, NSError*)  = _callbackDict[deviceID];
					if (callback) {
						callback(nil, error);
					}
				}
			}];
		} else {
			// バイナリ送信時は即時返答
			if ([request[@(KEY_PROFILE)] intValue] == PROFILE_BINARY) {
				// セマフォ解除
				dispatch_semaphore_signal(_semaphore);
				// コールバック
				void (^callback)(NSDictionary*, NSError*)  = _callbackDict[deviceID];
				if (callback) {
					callback(nil, nil);
				}
			}
		}
	}];
}

// ハンドラ追加
- (void)addHandler:(PBWatch*)watch deviceID:(NSString*)deviceID
{
	id opaqueHandle = [watch appMessagesAddReceiveUpdateHandler:^BOOL(PBWatch *watch, NSDictionary *update) {
		// ここは一度きりの登録なのでBlockを登録する訳にはいかないのでメソッドで。
		[self updateHandler:watch update:update];
		return YES;
	}];
	_updateHandlerDict[deviceID] = opaqueHandle;
}

// Updateハンドラ
- (void)updateHandler:(PBWatch*)watch update:(NSDictionary*)update
{
	NSNumber *action = update[@(KEY_ACTION)];
	if ([action intValue] == ACTION_EVENT) {
		// Eventのアップデート
		void (^callback)(NSDictionary*, NSError*)  = _eventCallbackDict[@[watch.serialNumber, update[@(KEY_PROFILE)], update[@(KEY_ATTRIBUTE)]]];
		if (callback) {
			callback(update, nil);
		}
	} else {
		// SerialNumber==DeviceIDなので、DeviceIDにキーにコールバックを呼び出す
		void (^callback)(NSDictionary*, NSError*)  = _callbackDict[watch.serialNumber];
		if (callback) {
			callback(update, nil);
		}
		// セマフォ解除
		dispatch_semaphore_signal(_semaphore);
	}
}

@end
