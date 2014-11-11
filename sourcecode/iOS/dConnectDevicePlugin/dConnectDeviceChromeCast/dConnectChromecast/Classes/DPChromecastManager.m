//
//  DPChromecastManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPChromecastManager.h"
#import <GoogleCast/GoogleCast.h>

static NSString *const kReceiverAppID = @"C70CD4D5";
static NSString *const kReceiverAppNamespace = @"urn:x-cast:com.name.space.chromecast.test.receiver";

// セマフォのタイムアウト
static const NSTimeInterval DPSemaphoreTimeout = 20.0;

@interface DPChromecastManagerData : NSObject
@property (nonatomic) GCKDeviceManager *deviceManager;
@property (nonatomic) GCKCastChannel *textChannel;
@property (nonatomic) GCKMediaControlChannel *ctrlChannel;
@property (nonatomic, copy) void (^connectCallback)(BOOL success, NSString *error);
@property (nonatomic, copy) void (^eventCallback)(NSString *mediaID);
@end
@implementation DPChromecastManagerData
@end


@interface DPChromecastManager () <GCKDeviceScannerListener, GCKDeviceManagerDelegate, GCKMediaControlChannelDelegate> {
	GCKDeviceScanner *_deviceScanner;
	NSMutableDictionary *_dataDict;
	dispatch_semaphore_t _semaphore;
}

@end

@implementation DPChromecastManager


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
		_deviceScanner = [[GCKDeviceScanner alloc] init];
		[_deviceScanner addListener:self];
		_dataDict = [NSMutableDictionary dictionary];
		_semaphore = dispatch_semaphore_create(1);
	}
	return self;
}

// スキャン開始
- (void)startScan
{
	[_deviceScanner startScan];
}

// スキャン停止
- (void)stopScan
{
	[_deviceScanner stopScan];
}

// デバイスリスト
- (NSArray*)deviceList
{
	NSMutableArray *array = [NSMutableArray array];
	for (GCKDevice *device in _deviceScanner.devices) {
		[array addObject:@{@"name": device.friendlyName, @"id": device.deviceID}];
	}
	return array;
}

// 接続チェック
- (BOOL)isConnectedWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	if (data) {
		// 既に接続済み
		GCKDeviceManager *deviceManager = data.deviceManager;
		return deviceManager.isConnected;
	}
	return NO;
}

// デバイスに接続
- (void)connectToDeviceWithID:(NSString*)deviceID
				   completion:(void (^)(BOOL success, NSString *error))completion
{
	// デバイス検索
	GCKDevice *device = nil;
	for (device in _deviceScanner.devices) {
		if ([device.deviceID isEqualToString:deviceID]) {
			break;
		}
	}
	
	// デバイスが見つからない
	if (!device) {
		// callback
		completion(NO, @"Device not found.");
		return;
	}
	
	// コマンドが連続して送信されないようにセマフォを立てる
	dispatch_semaphore_wait(_semaphore, dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * DPSemaphoreTimeout));
	
	// 接続確認
	DPChromecastManagerData *data = _dataDict[deviceID];
	if (data) {
		GCKDeviceManager *deviceManager = data.deviceManager;
		// コールバック保持
		data.connectCallback = completion;
		// 既に接続済み
		if (deviceManager.isConnected) {
			// セマフォ解除
			dispatch_semaphore_signal(_semaphore);
			// callback
			completion(YES, nil);
		} else {
			// 再接続
			[deviceManager connect];
		}
		return;
	}
	
	// 接続
	data = [[DPChromecastManagerData alloc] init];
	data.connectCallback = completion;
	_dataDict[deviceID] = data;
	
	NSDictionary *info = [[NSBundle mainBundle] infoDictionary];
	data.deviceManager = [[GCKDeviceManager alloc] initWithDevice:device
												clientPackageName:[info objectForKey:@"CFBundleIdentifier"]];
	data.deviceManager.delegate = self;
	[data.deviceManager connect];
}

// イベントコールバックを設定
- (void)setEventCallbackWithID:(NSString*)deviceID callback:(void (^)(NSString *mediaID))callback
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	if (data) {
		data.eventCallback = callback;
	} else {
		[self connectToDeviceWithID:deviceID completion:^(BOOL success, NSString *error) {
			DPChromecastManagerData *data = _dataDict[deviceID];
			data.eventCallback = callback;
		}];
	}
}

// 接続中のデバイスから切断
- (void)disconnectDeviceWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	if (data) {
		[data.deviceManager stopApplication];
		[data.deviceManager disconnect];
		data.deviceManager = nil;
		data.ctrlChannel = nil;
		data.textChannel = nil;
		[_dataDict removeObjectForKey:deviceID];
	}
}

// テキストの送信
- (void)sendMessageWithID:(NSString*)deviceID message:(NSString*)message type:(int)type
{
	NSDictionary *messageDict = @{@"function": @"write",
								  @"type": @(type),
								  @"message": message};
	[self sendJsonWithID:deviceID json:messageDict];
}

// テキストのクリア
- (void)clearMessageWithID:(NSString*)deviceID
{
	[self sendJsonWithID:deviceID json:@{@"function": @"clear"}];
}

// JSONの送信
- (void)sendJsonWithID:(NSString*)deviceID json:(NSDictionary *)json
{
	NSData *msgData = [NSJSONSerialization dataWithJSONObject:json options:0 error:nil];
	NSString *jsonstr = [[NSString alloc] initWithData:msgData encoding:NSUTF8StringEncoding];
	
	DPChromecastManagerData *data = _dataDict[deviceID];
	[data.textChannel sendTextMessage:jsonstr];
}

// メディアプレイヤーの状態取得
- (NSString*)mediaPlayerStateWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	switch (data.ctrlChannel.mediaStatus.playerState) {
		case GCKMediaPlayerStateIdle:
			return @"stop";
			break;
		case GCKMediaPlayerStatePlaying:
			return @"play";
			break;
		case GCKMediaPlayerStatePaused:
			return @"pause";
			break;
		case GCKMediaPlayerStateBuffering:
			return @"buffering";
			break;
		default:
			return @"unknown";
	}
}

// 再生位置を取得
- (NSTimeInterval)streamPositionWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return[data.ctrlChannel approximateStreamPosition];
}

// 再生位置を変更
- (void)setStreamPositionWithID:(NSString*)deviceID position:(NSTimeInterval)streamPosition
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	[data.ctrlChannel seekToTimeInterval:streamPosition];
}

// 音量を取得
- (float)volumeWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return data.ctrlChannel.mediaStatus.volume;
}

// 音量を設定
- (void)setVolumeWithID:(NSString*)deviceID volume:(float)volume
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	[data.ctrlChannel setStreamVolume:volume];
}

// ミュート状態取得
- (BOOL)isMutedWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return data.ctrlChannel.mediaStatus.isMuted;
}

// ミュート状態設定
- (void)setIsMutedWithID:(NSString*)deviceID muted:(BOOL)isMuted
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	[data.ctrlChannel setStreamMuted:isMuted];
}

// メディア読み込み
- (NSInteger)loadMediaWithID:(NSString*)deviceID mediaID:(NSString*)mediaID
{
	GCKMediaMetadata *metadata = [[GCKMediaMetadata alloc] init];
	
	GCKMediaInformation *mediaInformation =
	[[GCKMediaInformation alloc] initWithContentID:mediaID
										streamType:GCKMediaStreamTypeNone
									   contentType:@"video/mp4"
										  metadata:metadata
									streamDuration:123
										customData:nil];
	DPChromecastManagerData *data = _dataDict[deviceID];
	return [data.ctrlChannel loadMedia:mediaInformation autoplay:NO];
}

// 再生
- (NSInteger)playWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return [data.ctrlChannel play];
}

// 停止
- (NSInteger)stopWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return [data.ctrlChannel stop];
}

// 一時停止
- (NSInteger)pauseWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return [data.ctrlChannel pause];
}

// 長さ取得
- (NSTimeInterval)durationWithID:(NSString*)deviceID
{
	DPChromecastManagerData *data = _dataDict[deviceID];
	return data.ctrlChannel.mediaStatus.mediaInformation.streamDuration;
}


#pragma mark - GCKDeviceManagerDelegate

// デバイス接続
- (void)deviceManagerDidConnect:(GCKDeviceManager *)deviceManager
{
	// アプリケーションを起動
	[deviceManager launchApplication:kReceiverAppID];
}

// デバイス接続に失敗
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didFailToConnectWithError:(GCKError *)error
{
	// セマフォ解除
	dispatch_semaphore_signal(_semaphore);

	DPChromecastManagerData *data = _dataDict[deviceManager.device.deviceID];
	if (data.connectCallback) {
		data.connectCallback(NO, [GCKError enumDescriptionForCode:error.code]);
	}
	[self disconnectDeviceWithID:deviceManager.device.deviceID];
}

// アプリケーションに接続
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didConnectToCastApplication:(GCKApplicationMetadata *)applicationMetadata
			sessionID:(NSString *)sessionID
  launchedApplication:(BOOL)launchedApplication
{
	DPChromecastManagerData *data = _dataDict[deviceManager.device.deviceID];
	
	data.textChannel = [[GCKCastChannel alloc] initWithNamespace:kReceiverAppNamespace];
	[deviceManager addChannel:data.textChannel];
	
	data.ctrlChannel = [[GCKMediaControlChannel alloc] init];
	data.ctrlChannel.delegate = self;
	[deviceManager addChannel:data.ctrlChannel];
	
	if (data.connectCallback) {
		data.connectCallback(YES, nil);
	}
	
	// セマフォ解除
	dispatch_semaphore_signal(_semaphore);
}

// アプリケーションに接続失敗
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didFailToConnectToApplicationWithError:(NSError *)error
{
	// セマフォ解除
	dispatch_semaphore_signal(_semaphore);
	
	DPChromecastManagerData *data = _dataDict[deviceManager.device.deviceID];
	if (data.connectCallback) {
		data.connectCallback(NO, [GCKError enumDescriptionForCode:error.code]);
	}
	[self disconnectDeviceWithID:deviceManager.device.deviceID];
}

- (void)deviceManager:(GCKDeviceManager *)deviceManager
didReceiveStatusForApplication:(GCKApplicationMetadata *)applicationMetadata;
{
	DPChromecastManagerData *data = _dataDict[deviceManager.device.deviceID];
	if (data.eventCallback) {
		data.eventCallback(data.ctrlChannel.mediaStatus.mediaInformation.contentID);
	}
}

-  (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
didCompleteLoadWithSessionID:(NSInteger)sessionID
{
	[self updateEvent:mediaControlChannel];
}

- (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
   requestDidCompleteWithID:(NSInteger)requestID
{
	[self updateEvent:mediaControlChannel];
}

- (void)updateEvent:(GCKMediaControlChannel *)mediaControlChannel
{
	for (DPChromecastManagerData *data in _dataDict.allValues) {
		if (data.ctrlChannel == mediaControlChannel) {
			if (data.eventCallback) {
				data.eventCallback(data.ctrlChannel.mediaStatus.mediaInformation.contentID);
			}
			break;
		}
	}
}

@end
