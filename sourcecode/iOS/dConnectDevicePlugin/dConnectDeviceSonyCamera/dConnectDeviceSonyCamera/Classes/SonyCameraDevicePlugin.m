//
//  SonyCameraDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "SonyCameraDevicePlugin.h"
#import "SonyCameraRemoteApiUtil.h"
#import "SampleRemoteApi.h"
#import "RemoteApiList.h"
#import "DeviceList.h"
#import "SonyCameraViewController.h"
#import "SampleLiveviewManager.h"
#import "SonyCameraCameraProfile.h"

#import <SystemConfiguration/CaptiveNetwork.h>


/*!
 @brief バージョン。
 */
NSString *const SonyDevicePluginVersion = @"1.0";

/*!
 @brief IDのプレフィックス。
 */
NSString *const SonyDeviceId = @"sony_camera_";

/*!
 @brief デバイス名。
 */
NSString *const SonyDeviceName = @"Sony Camera";

/*!
 @brief ファイルのプレフィックス。
 */
NSString *const SonyFilePrefix = @"sony";

/*!
 @define デバイスID.
 */
#define DEVICE_ID @"0"


/*!
 @brief Sony Remote Camera用デバイスプラグイン。
 */
@interface SonyCameraDevicePlugin () <SampleDiscoveryDelegate, DConnectNetworkServiceDiscoveryProfileDelegate, DConnectSystemProfileDelegate, DConnectSystemProfileDataSource, DConnectMediaStreamRecordingProfileDelegate, SonyCameraCameraProfileDelegate, SampleLiveviewDelegate, SonyCameraRemoteApiUtilDelegate, DConnectSettingsProfileDelegate>

/*!
 @brief SonyRemoteApi操作用.
 */
@property (nonatomic) SonyCameraRemoteApiUtil *remoteApi;

/*!
 @brief ファイル管理クラス。
 */
@property (nonatomic, strong) DConnectFileManager *mFileManager;

/*!
 @brief タイムスライス。
 */
@property (nonatomic) UInt64 timeslice;

/*!
 @brief タイムスライス開始時刻。
 */
@property (nonatomic) UInt64 previewStart;

/*!
 @brief プレビューカウント。
 */
@property (nonatomic) int mPreviewCount;

/*!
 @brief サーチフラグ.
 */
@property (nonatomic) BOOL searchFlag;

/*!
 @brief 指定されたURLからデータをダウンロードする。
 
 Sony Cameraのデバイスに対してHTTP通信でデータをダウンロードする。
 @param[in] requestURL データが置いてあるURL
 @return データ
 */
- (NSData *) download:(NSString *)requestURL;

/*!
 @brief ファイルを保存する。
 
 ファイル名は、「sony_201408_011500.png」のようにsonyのプレフィックスに時刻が入る。
 
 @param[in] data 保存するデータ
 
 @retval 保存したファイルへのURL
 @retval nil 保存に失敗した場合
 */
- (NSString *) saveFile:(NSData *)data;

/*!
 @brief 1970/1/1からの時間を取得する。
 @return 時間
 */
- (UInt64) getEpochMilliSeconds;

/*!
 @brief 現在接続されているWifiのSSIDからSony Cameraかチェックする.
 @retval YES Sony Cameraの場合
 @retval NO Sony Camera以外
 */
- (BOOL) checkSSID;

/*!
 @brief 選択されたデバイスIDに対応するカメラを選択する.
 @param deviceId デバイスID
 @param response レスポンス
 @retval YES 選択できた場合
 @retval NO 選択できなかった場合
 */
- (BOOL) selectDeviceId:(NSString *)deviceId response:(DConnectResponseMessage *)response;

/*!
 @brief プレビューイベントを持っているかをチェックする.
 @retval YES 持っている
 @retval NO 持っていない
 */
- (BOOL) hasDataAvaiableEvent;

@end


#pragma mark - SonyCameraDevicePlugin

@implementation SonyCameraDevicePlugin

- (instancetype) init {
    self = [super init];
    if (self) {
        // プラグイン名を設定
        self.pluginName = [NSString stringWithFormat:@"Sony Camera %@", SonyDevicePluginVersion];
        
        // タイムスライスのデフォルト
        self.timeslice = 200;
        
        // タイムスライスカウント開始時刻の初期化
        self.previewStart = 0;
        
        // SonyRemoteApiの初期化
        self.remoteApi = nil;
        
        // サーチフラグ
        self.searchFlag = NO;
        
        // ファイル管理クラスの初期化
        self.mFileManager = [DConnectFileManager fileManagerForPlugin:self];
        
        // EventManagerの初期化
        Class key = [self class];
        [[DConnectEventManager sharedManagerForClass:key] setController:[DConnectMemoryCacheController new]];

        // Network Service Discovery Profileの追加
        DConnectNetworkServiceDiscoveryProfile *networkProfile = [DConnectNetworkServiceDiscoveryProfile new];
        networkProfile.delegate = self;
        
        // System Profileの追加
        DConnectSystemProfile *systemProfile = [DConnectSystemProfile new];
        systemProfile.delegate = self;
        systemProfile.dataSource = self;
        
        // MediaStreamRecording Profileの追加
        DConnectMediaStreamRecordingProfile *mediaProfile = [DConnectMediaStreamRecordingProfile new];
        mediaProfile.delegate = self;
        
        // Settings Profileの追加
        DConnectSettingsProfile *settingsProfile = [DConnectSettingsProfile new];
        settingsProfile.delegate = self;
        
        // Camera Profileの追加
        SonyCameraCameraProfile *cameraProfile = [SonyCameraCameraProfile new];
        cameraProfile.delegate = self;
        
        // 各プロファイルの追加
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:mediaProfile];
        [self addProfile:cameraProfile];
        
        // Settingsプロファイルは、QX10のファームウェアを
        // アップデートしないと使用できない。
        // また、アップデートしてもAPIにバグがあるので、
        // 今回は非サポートとする。
        //[self addProfile:settingsProfile];
        
        // SSIDがSony Cameraの場合には接続確認を行う
        if ([self checkSSID]) {
            [self searchSonyCameraDevice];
        }
        
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
            UIApplication *application = [UIApplication sharedApplication];
            
            [nc addObserver:_self selector:@selector(applicationWillEnterForeground)
                       name:UIApplicationWillEnterForegroundNotification
                     object:application];
            
            [nc addObserver:_self selector:@selector(applicationDidEnterBackground)
                       name:UIApplicationDidEnterBackgroundNotification
                     object:application];
        });
    }
    return self;
}


#pragma mark - Public Methods -

- (void) searchSonyCameraDevice {
    if (self.searchFlag) {
        return;
    }
    self.searchFlag = YES;
    
    // 検索する前にリセットをしておく
    [DeviceList reset];
    [[SampleCameraEventObserver getInstance] destroy];
	// Sony Camera デバイスの探索
	SampleDeviceDiscovery* discovery = [SampleDeviceDiscovery new];
	[discovery performSelectorInBackground:@selector(discover:) withObject:self];
}

- (void) stop {
    [DeviceList reset];
    [[SampleCameraEventObserver getInstance] destroy];
    if ([self.remoteApi isStartedLiveView]) {
        [self.remoteApi actStopLiveView];
    }
    self.remoteApi = nil;
}

- (BOOL) isStarted {
    return self.remoteApi != nil;
}

#pragma mark - Private Methods -

- (void) applicationDidEnterBackground
{
    // バックグラウンドに入ったときの処理
}

- (void) applicationWillEnterForeground
{
    // バックグラウンドから復帰したときの処理
    if ([self checkSSID]) {
        if (self.remoteApi == nil) {
            [self searchSonyCameraDevice];
        } else {
            NSLog(@"TEST: %d",  [[SampleCameraEventObserver getInstance] isStarted]);
        }
    } else {
        [self stop];
    }
}

- (NSData *) download:(NSString *)requestURL {
	NSURL *downoadUrl = [NSURL URLWithString:requestURL];
	NSData *urlData = [NSData dataWithContentsOfURL:downoadUrl];
	return urlData;
}

- (NSString *) saveFile:(NSData *)data
{
    // ファイル名作成
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"JST"]];
    [formatter setDateFormat:@"yyyyMMdd_HHmmss"];
    NSString *dateStr = [formatter stringFromDate:[NSDate date]];
    NSString *fileName = [NSString stringWithFormat:@"%@_%@.png", SonyFilePrefix, dateStr];
    
    // ファイルを保存
    return [self.mFileManager createFileForPath:fileName contents:data];
}

- (UInt64) getEpochMilliSeconds
{
    return (UInt64)floor((CFAbsoluteTimeGetCurrent() + kCFAbsoluteTimeIntervalSince1970) * 1000.0);
}

- (BOOL) checkSSID {
    CFArrayRef interfaces = CNCopySupportedInterfaces();
    if (!interfaces) return NO;
    if (CFArrayGetCount(interfaces)==0) return NO;
    CFDictionaryRef dicRef = CNCopyCurrentNetworkInfo(CFArrayGetValueAtIndex(interfaces, 0));
    if (dicRef) {
        NSString *ssid = CFDictionaryGetValue(dicRef, kCNNetworkInfoKeySSID);
        if ([ssid hasPrefix:@"DIRECT-"]) {
            NSArray *array = @[@"HDR-AS100", @"ILCE-6000", @"DSC-HC60V", @"DSC-HX400", @"ILCE-5000", @"DSC-QX10", @"DSC-QX100", @"HDR-AS15", @"HDR-AS30", @"HDR-MV1", @"NEX-5R", @"NEX-5T", @"NEX-6", @"ILCE-7R/B", @"ILCE-7/B"];
            for (NSString *name in array) {
                NSRange searchResult = [ssid rangeOfString:name];
                if (searchResult.location != NSNotFound) {
                    return YES;
                }
            }
        }
    }
    return NO;
}

- (BOOL) selectDeviceId:(NSString *)deviceId response:(DConnectResponseMessage *)response {
    // デバイスIDの存在チェック
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
        return NO;
    }
    
    // デバイスが存在しない
    if ([DeviceList getSize] <= 0) {
        [response setErrorToNotFoundDevice];
        return NO;
    }
    
    // デバイス選択
    NSInteger idx = [deviceId integerValue];
    [DeviceList selectDeviceAt:idx];

    return YES;
}

- (BOOL) hasDataAvaiableEvent {
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
    NSArray *evts = [mgr eventListForDeviceId:DEVICE_ID
                                      profile:DConnectMediaStreamRecordingProfileName
                                    attribute:DConnectMediaStreamRecordingProfileAttrOnDataAvailable];
    return evts.count > 0;
}

#pragma mark - SampleDiscoveryDelegate

- (void) didReceiveDeviceList:(BOOL) discovery {
    self.searchFlag = NO;

    if (discovery) {
        self.remoteApi = [SonyCameraRemoteApiUtil new];
        self.remoteApi.delegate = self;
        
        // プレビューイベントを持っている場合は、プレビューを再開させる
        if ([self hasDataAvaiableEvent]) {
            if (![self.remoteApi isStartedLiveView]) {
                [self.remoteApi actStartLiveView:self];
            }
        }
    } else {
        // 見つからない
    }
    [self.delegate didReceiveDeviceList:discovery];
}

#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    DConnectArray *services = [DConnectArray array];
    for (int i = 0; i < [DeviceList getSize]; i++) {
        NSString *deviceId = [NSString stringWithFormat:@"%d", i];
        DConnectMessage *service = [DConnectMessage message];
        [DConnectNetworkServiceDiscoveryProfile setId:deviceId target:service];
        [DConnectNetworkServiceDiscoveryProfile setName:SonyDeviceName target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi
                                                 target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
        [services addMessage:service];
    }
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

#pragma mark - DConnectSystemProfileDelegate

- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
    if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil"];
    } else {
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
        if ([mgr removeEventsForSessionKey:sessionKey]) {
            [response setResult:DConnectMessageResultTypeOk];
            
            // 削除した時にイベントが残っていなければ、プレビューを止める
            if (![self hasDataAvaiableEvent]) {
                if ([self.remoteApi isStartedLiveView]) {
                    [self.remoteApi actStopLiveView];
                }
            }
        } else {
            [response setErrorToUnknownWithMessage:@"Cannot delete events."];
        }
    }
    return YES;
}

#pragma mark - DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return SonyDevicePluginVersion;
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceSonyCamera_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    
    // iphoneとipadでストーリーボードを切り替える
    UIStoryboard *sb;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        sb = [UIStoryboard storyboardWithName:@"SonyCameraDevicePlugin_iPhone" bundle:bundle];
    } else{
        sb = [UIStoryboard storyboardWithName:@"SonyCameraDevicePlugin_iPad" bundle:bundle];
    }
    UINavigationController *vc = [sb instantiateInitialViewController];
    for (int i = 0; i < vc.viewControllers.count; i++) {
        UIViewController *ctl = vc.viewControllers[i];
        NSString *className = NSStringFromClass([ctl class]);
        if ([className isEqualToString:@"SonyCameraViewController"]) {
            SonyCameraViewController *scvc = (SonyCameraViewController *) ctl;
            scvc.deviceplugin = self;
        }
    }
    return vc;
}

#pragma mark - DConnectMediaStreamRecordingProfileDelegate

- (BOOL)                  profile:(DConnectMediaStreamRecordingProfile *)profile
didReceiveGetMediaRecorderRequest:(DConnectRequestMessage *)request
                         response:(DConnectResponseMessage *)response
                         deviceId:(NSString *)deviceId
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_getStillSize]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    // MEMO: getStillSizeは、QX10は最新のファームウェアでないとサポートしていない
    NSDictionary *dic = [self.remoteApi getStillSize];
    if (dic) {
        NSString *aspect = [dic objectForKey:@"aspect"];
        NSString *size = [dic objectForKey:@"size"];
        
        NSArray *sizes = [aspect componentsSeparatedByString:@":"];
        NSString *w = sizes[0];
        NSString *h = sizes[1];
        int stillSize = 0;
        int width = [w intValue];
        int height = [h intValue];
        
        if ([aspect isEqualToString:@"1:1"]) {
            if ([size isEqualToString:@"3.7M"]) {
                stillSize = (1920 * 1920) / (width * height);
            } else if ([size isEqualToString:@"13M"]) {
                stillSize = (3648 * 3648) / (width * height);
            }
        } else if ([aspect isEqualToString:@"3:2"]) {
            if ([size isEqualToString:@"20M"]) {
                stillSize = (5472 * 3648) / (width * height);
            } else if ([size isEqualToString:@"5M"]) {
                stillSize = (2736 * 1824) / (width * height);
            }
        } else if ([aspect isEqualToString:@"4:3"]) {
            if ([size isEqualToString:@"18M"]) {
                stillSize = (4864 * 3648) / (width * height);
            } else if ([size isEqualToString:@"5M"]) {
                stillSize = (2592 * 1944) / (width * height);
            }
        } else if ([aspect isEqualToString:@"16:9"]) {
            if ([size isEqualToString:@"17M"]) {
                stillSize = (5472 * 3080) / (width * height);
            } else if ([size isEqualToString:@"4.2M"]) {
                stillSize = (2720 * 1528) / (width * height);
            }
        }
        
        if (stillSize == 0) {
            [response setErrorToNotSupportAttribute];
        } else {
            NSString *cameraStatus = self.remoteApi.cameraStatus;
            NSString *status = nil;
            if ([cameraStatus isEqualToString:@"Error"] ||
                [cameraStatus isEqualToString:@"NotReady"] ||
                [cameraStatus isEqualToString:@"MovieSaving"] ||
                [cameraStatus isEqualToString:@"AudioSaving"] ||
                [cameraStatus isEqualToString:@"StillSaving"]) {
                status = DConnectMediaStreamRecordingProfileRecorderStateInactive;
            } else if ([cameraStatus isEqualToString:@"StillCapturing"] ||
                [cameraStatus isEqualToString:@"MediaRecording"] ||
                [cameraStatus isEqualToString:@"AudioRecording"] ||
                [cameraStatus isEqualToString:@"IntervalRecording"]) {
                status = DConnectMediaStreamRecordingProfileRecorderStateRecording;
            } else if ([cameraStatus isEqualToString:@"MovieWaitRecStart"] ||
                [cameraStatus isEqualToString:@"MoviewWaitRecStop"] ||
                [cameraStatus isEqualToString:@"AudioWaitRecStart"] ||
                [cameraStatus isEqualToString:@"AudioRecWaitRecStop"] ||
                [cameraStatus isEqualToString:@"IntervalWaitRecStart"] ||
                [cameraStatus isEqualToString:@"IntervalWaitRecStop"]) {
                status = DConnectMediaStreamRecordingProfileRecorderStatePaused;
            }
            
            width = width * stillSize;
            height = height * stillSize;
            
            DConnectMessage *recorder = [DConnectMessage message];
            [DConnectMediaStreamRecordingProfile setRecorderId:DEVICE_ID target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderName:@"SonyCamera" target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderState:status target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderMIMEType:@"image/png" target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderImageWidth:width target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderImageHeight:height target:recorder];
            [DConnectMediaStreamRecordingProfile setRecorderConfig:@"" target:recorder];
            
            DConnectArray *recorders = [DConnectArray array];
            [recorders addMessage:recorder];
            
            [response setResult:DConnectMessageResultTypeOk];
            [DConnectMediaStreamRecordingProfile setRecorders:recorders target:response];
        }
    } else {
        [response setErrorToNotSupportAttribute];
    }
    return YES;
}

- (BOOL)               profile:(DConnectMediaStreamRecordingProfile *)profile
didReceivePostTakePhotoRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
                        target:(NSString *)target
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_actTakePicture]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    // 既に撮影中はエラー
    if ([SonyCameraStatusMovieRecording isEqualToString:self.remoteApi.cameraStatus]) {
        [response setErrorToIllegalDeviceState];
        return YES;
    }
    
    // 動画撮影モード切り替え
    if (![SonyCameraShootModePicture isEqualToString:self.remoteApi.shootMode]) {
        if (![self.remoteApi actSetShootMode:SonyCameraShootModePicture]) {
            [response setErrorToIllegalDeviceState];
            return YES;
        }
    }
    
    __weak typeof(self) _self = self;
    
    // 写真撮影をバックグランドでAPIなどを実行
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSDictionary *dict = [_self.remoteApi actTakePicture];
        if (dict == nil) {
            [response setErrorToTimeout];
        } else {
            NSString *errorMessage = @"";
            NSInteger errorCode = -1;
            NSArray *resultArray = [dict objectForKey:@"result"];
            NSArray *errorArray = [dict objectForKey:@"error"];
            if (errorArray && errorArray.count > 0) {
                errorCode = (NSInteger) [errorArray objectAtIndex:0];
                errorMessage = [errorArray objectAtIndex:1];
            }
            
            // レスポンス作成
            if (resultArray.count <= 0 && errorCode >= 0) {
                [response setErrorToUnknown];
            } else {
                NSArray *arr = resultArray[0];
                NSData *data = [_self download:arr[0]];
                if (data) {
                    // ファイルを保存
                    NSString *uri = [_self saveFile:data];
                    [self didReceivedImage:uri];
                    if (!uri) {
                        // ファイル保存に失敗
                        [response setErrorToUnknown];
                    } else {
                        [response setResult:DConnectMessageResultTypeOk];
                        [DConnectMediaStreamRecordingProfile setPath:[uri lastPathComponent] target:response];
                        [DConnectMediaStreamRecordingProfile setUri:uri target:response];
                    }
                } else {
                    [response setErrorToUnknown];
                }
            }
        }
        
        // レスポンスを返却
        [[DConnectManager sharedManager] sendResponse:response];
    });
    
    return NO;
}

- (BOOL)            profile:(DConnectMediaStreamRecordingProfile *)profile
didReceivePostRecordRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                     target:(NSString *)target
                  timeslice:(NSNumber *)timeslice
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_startRecMode]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }

    // 撮影中は、さらに撮影できないのでエラーを返す
    if ([SonyCameraStatusMovieRecording isEqualToString:self.remoteApi.cameraStatus]) {
        [response setErrorToIllegalDeviceState];
        return YES;
    }
    
    // 動画撮影モード切り替え
    if (![SonyCameraShootModeMovie isEqualToString:self.remoteApi.shootMode]) {
        if (![self.remoteApi actSetShootMode:SonyCameraShootModeMovie]) {
            [response setErrorToIllegalDeviceState];
            return YES;
        }
    }
    
    __weak typeof(self) _self = self;

    // 撮影開始
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSDictionary *dict = [_self.remoteApi startMovieRec];
        if (dict) {
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknown];
        }
        // レスポンスを返却
        [[DConnectManager sharedManager] sendResponse:response];
    });
    
    return NO;
}

- (BOOL)         profile:(DConnectMediaStreamRecordingProfile *)profile
didReceivePutStopRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                  target:(NSString *)target
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // 撮影が開始されていないので、エラーを返す。
    if ([SonyCameraStatusIdle isEqualToString:self.remoteApi.cameraStatus]) {
        [response setErrorToIllegalDeviceState];
        return YES;
    }

    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_stopRecMode]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    __weak typeof(self) _self = self;
    
    // 撮影停止
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSDictionary *dict = [_self.remoteApi stopMovieRec];
        if (dict) {
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknown];
        }
        // レスポンスを返却
        [[DConnectManager sharedManager] sendResponse:response];
    });
    
    return NO;
}

- (BOOL)            profile:(DConnectMediaStreamRecordingProfile *)profile
didReceivePutOnPhotoRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                 sessionKey:(NSString *)sessionKey
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // セッションキー確認
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil."];
        return YES;
    }

    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
    DConnectEventError error = [mgr addEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:
        case DConnectEventErrorFailed:
        default:
            [response setErrorToUnknown];
            break;
    }
    return YES;
}

- (BOOL)               profile:(DConnectMediaStreamRecordingProfile *)profile
didReceiveDeleteOnPhotoRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
                    sessionKey:(NSString *)sessionKey
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // セッションキー確認
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil."];
        return YES;
    }

    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
    DConnectEventError error = [mgr removeEventForRequest:request];
    switch (error) {
        case DConnectEventErrorNone:
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter:
        case DConnectEventErrorNotFound:
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorFailed:
        default:
            [response setErrorToUnknown];
            break;
    }
    return YES;
}


- (BOOL)                    profile:(DConnectMediaStreamRecordingProfile *)profile
didReceivePutOnDataAvailableRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // セッションキー確認
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil."];
        return YES;
    }

    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_startLiveview]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
    DConnectEventError error = [mgr addEventForRequest:request];
    switch (error) {
    case DConnectEventErrorNone:
        [response setResult:DConnectMessageResultTypeOk];
        // プレビュー開始
        if (![self.remoteApi isStartedLiveView]) {
            [self.remoteApi actStartLiveView:self];
        }
        break;
    case DConnectEventErrorInvalidParameter:
        [response setErrorToInvalidRequestParameter];
        break;
    case DConnectEventErrorNotFound:
    case DConnectEventErrorFailed:
    default:
        [response setErrorToUnknown];
        break;
    }
    return YES;
}


- (BOOL)                       profile:(DConnectMediaStreamRecordingProfile *)profile
didReceiveDeleteOnDataAvailableRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // セッションキー確認
    if (!sessionKey) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil."];
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_startLiveview]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
    DConnectEventError error = [mgr removeEventForRequest:request];
    switch (error) {
    case DConnectEventErrorNone: {
        [response setResult:DConnectMessageResultTypeOk];
            
        // プレビュー停止
        if ([self.remoteApi isStartedLiveView] && ![self hasDataAvaiableEvent]) {
            [self.remoteApi actStopLiveView];
        }
    }   break;
    case DConnectEventErrorInvalidParameter:
    case DConnectEventErrorNotFound:
        [response setErrorToInvalidRequestParameter];
        break;
    case DConnectEventErrorFailed:
    default:
        [response setErrorToUnknown];
        break;
    }
    return YES;
}


#pragma mark - SampleLiveviewDelegate -

- (void) didReceivedData:(NSData *)imageData
{
    // プレビューのタイムスライス時間に満たない場合には無視する
    UInt64 t = [self getEpochMilliSeconds];
    if (t - self.previewStart < self.timeslice) {
        return;
    }
    self.previewStart = t;
    
    self.mPreviewCount++;
    self.mPreviewCount %= 10;
    
    // ファイル名を作成
    NSString *fileName = [NSString stringWithFormat:@"preview%d.jpg", self.mPreviewCount];
    // ファイルを保存
    NSString *uri = [self.mFileManager createFileForPath:fileName contents:imageData];
    if (uri) {
        // イベントの作成
        DConnectMessage *media = [DConnectMessage message];
        [DConnectMediaStreamRecordingProfile setUri:uri target:media];
        [DConnectMediaStreamRecordingProfile setPath:fileName target:media];
        
        // イベントの取得
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
        NSArray *evts = [mgr eventListForDeviceId:DEVICE_ID
                                          profile:DConnectMediaStreamRecordingProfileName
                                        attribute:DConnectMediaStreamRecordingProfileAttrOnDataAvailable];
        // イベント送信
        for (DConnectEvent *evt in evts) {
            DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
            [eventMsg setMessage:media forKey:DConnectMediaStreamRecordingProfileParamMedia];
            [self sendEvent:eventMsg];
        }
    }
}

- (void) didReceivedError {
    [self stop];
    [self searchSonyCameraDevice];
}


#pragma mark - SonyCameraRemoteApiUtilDelegate

- (void) didReceivedImage:(NSString *)imageUrl
{
    __weak typeof(self) _self = self;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSData *picture = [_self download:imageUrl];
        if (picture) {
            NSString *uri = [_self saveFile:picture];
            if (!uri) {
                return;
            }
            
            // イベント作成
            DConnectMessage *photo = [DConnectMessage message];
            [DConnectMediaStreamRecordingProfile setUri:uri target:photo];
            [DConnectMediaStreamRecordingProfile setPath:[uri lastPathComponent] target:photo];
            [DConnectMediaStreamRecordingProfile setMIMEType:@"image/png" target:photo];
            
            // イベントの取得
            DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[self class]];
            NSArray *evts = [mgr eventListForDeviceId:DEVICE_ID
                                              profile:DConnectMediaStreamRecordingProfileName
                                            attribute:DConnectMediaStreamRecordingProfileAttrOnPhoto];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                [eventMsg setMessage:photo forKey:DConnectMediaStreamRecordingProfileParamPhoto];
                [_self sendEvent:eventMsg];
            }
        }
    });
}

#pragma mark - DConnectSettingsProfileDelegate

- (BOOL)         profile:(DConnectSettingsProfile *)profile
didReceivePutDateRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                    date:(NSString *)date
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }

    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_setCurrentTime]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }

    __weak typeof(self) _self = self;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        BOOL result = [_self.remoteApi setDate:date];
        if (result) {
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknown];
        }
        // レスポンスを返却
        [[DConnectManager sharedManager] sendResponse:response];
    });
    // レスポンスは非同期で返却するので
    return NO;
}

#pragma mark - SonyCameraCameraProfileDelegate
#pragma mark - Get Methods

/*!
 @brief Zoom
 */
- (BOOL) profile:(SonyCameraCameraProfile *)profile didReceiveGetZoomRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_actZoom]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }
    
    if (self.remoteApi.zoomPosition < 0) {
        [response setErrorToIllegalDeviceState];
    } else {
        // ズームのデータ
        [response setResult:DConnectMessageResultTypeOk];
        [response setDouble:self.remoteApi.zoomPosition
                     forKey:SonyCameraCameraProfileParamZoomdiameter];
    }
    return YES;
}

#pragma mark - Put Methods

- (BOOL)         profile:(SonyCameraCameraProfile *)profile
didReceivePutZoomRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
               direction:(NSString *)direction
                movement:(NSString *)movement
{
    // デバイスIDのチェック
    if (![self selectDeviceId:deviceId response:response]) {
        return YES;
    }
    
    // サポートしていない
    if (![self.remoteApi isApiAvailable:API_actZoom]) {
        [response setErrorToNotSupportAttribute];
        return YES;
    }

    __weak typeof(self) _self = self;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSDictionary *dict = [_self.remoteApi actZoom:direction movement:movement];
        if (dict == nil) {
            [response setErrorToTimeout];
        } else {
            NSString *errorMessage = @"";
            NSInteger errorCode = -1;
            NSArray *resultArray = [dict objectForKey:@"result"];
            NSArray *errorArray = [dict objectForKey:@"error"];
            if (errorArray && errorArray.count > 0) {
                errorCode = (NSInteger) [errorArray objectAtIndex:0];
                errorMessage = [errorArray objectAtIndex:1];
            }
            
            // レスポンス作成
            if (resultArray.count <= 0 && errorCode >= 0) {
                [response setErrorToUnknown];
            } else {
                [response setResult:DConnectMessageResultTypeOk];
            }
        }
        
        // レスポンスを返却
        [[DConnectManager sharedManager] sendResponse:response];
    });
    
    // レスポンスは非同期で返却するので
    return NO;
}

@end
