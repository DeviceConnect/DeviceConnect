//
//  DPChromecastManager.m
//  dConnectChromecast
//
//  Created by Ryuya Takahashi on 2014/09/08.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPChromecastManager.h"
#import <GoogleCast/GoogleCast.h>
#import "DPChromecastDevicePlugin.h"
#import <DConnectSDK/DConnectSDK.h>

static NSString *const kReceiverAppID = @"C70CD4D5";
static NSString *const kReceiverAppNamespace = @"urn:x-cast:com.name.space.chromecast.test.receiver";

//static NSString *const kReceiverAppID = @"794B7BBF";
//static NSString *const kReceiverAppID = kGCKMediaDefaultReceiverApplicationID;
//static NSString *const kReceiverAppNamespace = @"urn:x-cast:com.google.cast.sample.helloworld"

@interface DPChromecastManager () <GCKDeviceScannerListener, GCKDeviceManagerDelegate, GCKMediaControlChannelDelegate> {
    GCKDeviceScanner *_deviceScanner;
    GCKDeviceManager *_deviceManager;
    GCKCastChannel *_textChannel;
    GCKMediaControlChannel *_ctrlChannel;
    NSString *chromecastMediaId;
    NSString *chromecastDeviceId;
    void (^_connectCallback)(BOOL success, NSString *error);
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
        chromecastMediaId = nil;
        chromecastDeviceId = nil;
        _chromecastBlock = nil;
        [_deviceScanner addListener:self];
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
- (BOOL)isConnected
{
    return _deviceManager.isConnected;
}

// デバイスに接続
- (void)connectToDeviceWithID:(NSString*)deviceid
                   completion:(void (^)(BOOL success, NSString *error))completion
{
    // FIXME: 短いスパンの接続に耐えられない、ロックするかスタックさせるか。。。
    _connectCallback  = completion;
    
    GCKDevice *device = nil;
    for (device in _deviceScanner.devices) {
        if ([device.deviceID isEqualToString:deviceid]) {
            break;
        }
    }
    
    // デバイスが見つからない
    if (!device) {
        // callback
        _connectCallback(NO, @"Device not found.");
        return;
    }

    NSDictionary *info = [[NSBundle mainBundle] infoDictionary];
    if (_deviceManager) {
        if ([_deviceManager.device.deviceID isEqualToString:deviceid]) {
            chromecastDeviceId = deviceid;
            // 既に接続済み
            if (_deviceManager.isConnected) {
                // callback
                _connectCallback(YES, nil);
            } else {
                // 再接続
                [_deviceManager connect];
            }
            return;
        } else {
            chromecastDeviceId = nil;
            // 切断
            [self disconnectDevice];
        }
    }
    _deviceManager = [[GCKDeviceManager alloc] initWithDevice:device
                                            clientPackageName:[info objectForKey:@"CFBundleIdentifier"]];
    _deviceManager.delegate = self;
    [_deviceManager connect];
}

// 接続中のデバイスから切断
- (void)disconnectDevice
{
    [_deviceManager leaveApplication];
    [_deviceManager disconnect];
    _deviceManager = nil;
    _ctrlChannel = nil;
    _textChannel = nil;
}

// テキストの送信
- (void)sendMessage:(NSString*)message type:(int)type
{
    NSDictionary *messageDict = @{@"function": @"write",
                                  @"type": @(type),
                                  @"message": message};
    [self sendJson:messageDict];
}

// テキストのクリア
- (void)clearMessage
{
    [self sendJson:@{@"function": @"clear"}];
}

// JSONの送信
- (void)sendJson:(NSDictionary *)message
{
    NSData *msgData = [NSJSONSerialization dataWithJSONObject:message options:0 error:nil];
    NSString *jsonstr = [[NSString alloc] initWithData:msgData encoding:NSUTF8StringEncoding];
    
    //NSLog(@"sendTextMessage:%@", jsonstr);
    [_textChannel sendTextMessage:jsonstr];
}

// メディアプレイヤーの状態取得
- (NSString*)mediaPlayerState
{
    switch (_ctrlChannel.mediaStatus.playerState) {
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
- (NSTimeInterval)streamPosition
{
    return[_ctrlChannel approximateStreamPosition];
    //return _ctrlChannel.mediaStatus.streamPosition;
}

// 再生位置を変更
- (void)setStreamPosition:(NSTimeInterval)streamPosition
{
    [_ctrlChannel seekToTimeInterval:streamPosition];
}

// 音量を取得
- (float)volume
{
    return _ctrlChannel.mediaStatus.volume;
}

// 音量を設定
- (void)setVolume:(float)volume
{
    [_ctrlChannel setStreamVolume:volume];
}

// ミュート状態取得
- (BOOL)isMuted
{
    return _ctrlChannel.mediaStatus.isMuted;
}

// ミュート状態設定
- (void)setIsMuted:(BOOL)isMuted
{
    [_ctrlChannel setStreamMuted:isMuted];
}

// メディア読み込み
- (NSInteger)loadMediaWithID:(NSString*)mediaID
{
    //NSLog(@"loadMediaWithID:%@", mediaID);
    
    GCKMediaMetadata *metadata = [[GCKMediaMetadata alloc] init];

    GCKMediaInformation *mediaInformation =
    [[GCKMediaInformation alloc] initWithContentID:mediaID
                                        streamType:GCKMediaStreamTypeNone
                                       contentType:@"video/mp4"
                                          metadata:metadata
                                    streamDuration:123
                                        customData:nil];
    chromecastMediaId = mediaID;
    return [_ctrlChannel loadMedia:mediaInformation];
}

// 再生
- (NSInteger)play
{
    //NSLog(@"play");
    return [_ctrlChannel play];
}

// 停止
- (NSInteger)stop
{
    return [_ctrlChannel stop];
}

// 一時停止
- (NSInteger)pause
{
    return [_ctrlChannel pause];
}

//長さ取得
- (NSTimeInterval)duration
{
    return _ctrlChannel.mediaStatus.mediaInformation.streamDuration;
}


#pragma mark - GCKDeviceScannerListener

// デバイスを発見
- (void)deviceDidComeOnline:(GCKDevice *)device
{
    //NSLog(@"Chromecast found!! %@[%@]", device.friendlyName, device.deviceID);
}

// デバイスをロスト
- (void)deviceDidGoOffline:(GCKDevice *)device
{
    //NSLog(@"Chromecast lost... %@[%@]", device.friendlyName, device.deviceID);
}


#pragma mark - GCKDeviceManagerDelegate

// デバイス接続
- (void)deviceManagerDidConnect:(GCKDeviceManager *)deviceManager
{
    //NSLog(@"connected!!");
    
    // アプリケーションを起動
    [_deviceManager launchApplication:kReceiverAppID];
}

// デバイス接続に失敗
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didFailToConnectWithError:(GCKError *)error
{
    _connectCallback(NO, [GCKError enumDescriptionForCode:error.code]);
    [self disconnectDevice];
}

// アプリケーションに接続
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didConnectToCastApplication:(GCKApplicationMetadata *)applicationMetadata
            sessionID:(NSString *)sessionID
  launchedApplication:(BOOL)launchedApplication
{
    //NSLog(@"application has launched %d", launchedApplication);
    
    _textChannel = [[GCKCastChannel alloc] initWithNamespace:kReceiverAppNamespace];
    [_deviceManager addChannel:_textChannel];
    
    
    _ctrlChannel = [[GCKMediaControlChannel alloc] init];
    _ctrlChannel.delegate = self;
    [_deviceManager addChannel:_ctrlChannel];
    
    _connectCallback(YES, nil);
}

// アプリケーションに接続失敗
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didFailToConnectToApplicationWithError:(NSError *)error
{
    _connectCallback(NO, [GCKError enumDescriptionForCode:error.code]);
    [self disconnectDevice];
}

// デバイス切断時
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didDisconnectWithError:(GCKError *)error
{
    //NSLog(@"disconnected:%@", [GCKError enumDescriptionForCode:error.code]);
}

// 状態変化時
- (void)deviceManager:(GCKDeviceManager *)deviceManager
didReceiveStatusForApplication:(GCKApplicationMetadata *)applicationMetadata
{
    //NSLog(@"Received device status: %@", applicationMetadata);
    DConnectMessage *message = [DConnectMessage message];
    [DConnectMediaPlayerProfile setMediaId:chromecastMediaId target:message];
    [DConnectMediaPlayerProfile setMIMEType:@"video/mp4" target:message];
    [DConnectMediaPlayerProfile setStatus:[self mediaPlayerState] target:message];
    [DConnectMediaPlayerProfile setPos:[self streamPosition] target:message];
    [DConnectMediaPlayerProfile setVolume:[self volume] target:message];
    DPChromecastBlock block = (DPChromecastBlock)self.chromecastBlock;
    if (block) {
        block(message);
    }
}


#pragma mark - GCKMediaControlChannelDelegate

// メディアロード完了
-  (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
didCompleteLoadWithSessionID:(NSInteger)sessionID
{
    DConnectMessage *message = [DConnectMessage message];
    [DConnectMediaPlayerProfile setMediaId:chromecastMediaId target:message];
    [DConnectMediaPlayerProfile setMIMEType:@"video/mp4" target:message];
    [DConnectMediaPlayerProfile setStatus:[self mediaPlayerState] target:message];
    [DConnectMediaPlayerProfile setPos:[self streamPosition] target:message];
    [DConnectMediaPlayerProfile setVolume:[self volume] target:message];
    DPChromecastBlock block = (DPChromecastBlock)self.chromecastBlock;
    if (block) {
        block(message);
    }
    //NSLog(@"didCompleteLoadWithSessionID:%d", (int)sessionID);
}

// メディアロード失敗
- (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
didFailToLoadMediaWithError:(NSError *)error
{
    //NSLog(@"didFailToLoadMediaWithError:%@", [GCKError enumDescriptionForCode:error.code]);
}

- (void)addEvent:(NSString *)deviceId block:(DPChromecastBlock)block {
    self.chromecastBlock = block;
}
- (void)removeEvent {
    self.chromecastBlock = nil;
}

// リクエスト成功
- (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
   requestDidCompleteWithID:(NSInteger)requestID
{
    DConnectMessage *message = [DConnectMessage message];
    [DConnectMediaPlayerProfile setMediaId:chromecastMediaId target:message];
    [DConnectMediaPlayerProfile setMIMEType:@"video/mp4" target:message];
    [DConnectMediaPlayerProfile setStatus:[self mediaPlayerState] target:message];
    [DConnectMediaPlayerProfile setPos:[self streamPosition] target:message];
    [DConnectMediaPlayerProfile setVolume:[self volume] target:message];
    DPChromecastBlock block = (DPChromecastBlock)self.chromecastBlock;
    if (block) {
        block(message);
    }
    //NSLog(@"requestDidCompleteWithID:%d", (int)requestID);
}

// リクエスト失敗
- (void)mediaControlChannel:(GCKMediaControlChannel *)mediaControlChannel
       requestDidFailWithID:(NSInteger)requestID
                      error:(NSError *)error
{
    //NSLog(@"requestDidFailWithID:%d[%@]", (int)requestID, [GCKError enumDescriptionForCode:error.code]);
}
@end
