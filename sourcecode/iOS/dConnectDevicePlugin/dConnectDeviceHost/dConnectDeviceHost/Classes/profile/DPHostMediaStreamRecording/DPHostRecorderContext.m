//
//  DPHostRecorderContext.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHostMediaStreamRecordingProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostRecorderContext.h"

const char * const AudioCaptureQueueName = "com.nttdocomo.dconnect.host.mediastream_recording.audio_capture";
const char * const VideoCaptureQueueName = "com.nttdocomo.dconnect.host.mediastream_recording.video_capture";

@interface DPHostRecorderDataSource()

@property dispatch_queue_t captureQueue; ///< キャプチャーしたデータの処理を行うキュー

@end

@implementation DPHostRecorderDataSource

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.captureQueue = dispatch_queue_create(nil, DISPATCH_QUEUE_SERIAL);
    }
    return self;
}

+ (instancetype)recorderDataSourceForPhotoWithVideoDevice:(AVCaptureDevice *)videoDevice
{
    if (!videoDevice) {
        NSLog(@"videoDevice must be non-nil.");
        return nil;
    }
    
    DPHostRecorderDataSource *instance = [DPHostRecorderDataSource new];
    instance.type = RecorderDataSourceTypePhoto;
    instance.uniqueId = [videoDevice uniqueID];
    
    return instance;
}

+ (instancetype)recorderDataSourceForAudioWithAudioDevice:(AVCaptureDevice *)audioDevice
{
    if (!audioDevice) {
        NSLog(@"audioDevice must be non-nil.");
        return nil;
    }
    
    DPHostRecorderDataSource *instance = [DPHostRecorderDataSource new];
    instance.type = RecorderDataSourceTypeAudio;
    instance.uniqueId = [audioDevice uniqueID];
    
    return instance;
}

+ (instancetype)recorderDataSourceForVideoWithVideoDevice:(AVCaptureDevice *)videoDevice
{
    if (!videoDevice) {
        NSLog(@"videoDevice must be non-nil.");
        return nil;
    }
    
    DPHostRecorderDataSource *instance = [DPHostRecorderDataSource new];
    instance.type = RecorderDataSourceTypeVideo;
    instance.uniqueId = [videoDevice uniqueID];
    
    return instance;
}

@end

@interface DPHostRecorderContext()

@property dispatch_queue_t queue; /// Read/Writeスキームで用いられる並列キューを返却する。

@end

@implementation DPHostRecorderContext

//- (instancetype)init
//{
//    self = [super init];
//    if (self) {
//        self.isMuted = NO;
//        self.videoOrientation = AVCaptureVideoOrientationPortrait;
//
//        self.session = [AVCaptureSession new];
//
//        self.queue = dispatch_queue_create(NULL, DISPATCH_QUEUE_CONCURRENT);
//    }
//    return self;
//}

- (instancetype)initWithProfile:(DPHostMediaStreamRecordingProfile *)profile
{
    self = [super init];
    if (self) {
        self.profile = profile;
        self.isMuted = NO;
        self.videoOrientation = AVCaptureVideoOrientationPortrait;
        
        self.session = [AVCaptureSession new];
        
        self.queue = dispatch_queue_create(NULL, DISPATCH_QUEUE_CONCURRENT);
        
        // エラーをイベントとして通知する。
        // レコーダー毎のコンテキストが欲しいので、DPHostMediaStreamRecordingProfileをオブザーバーにせず、DPHostRecorderContextの方を
        // オブザーバーにする。
        NSNotificationCenter *notificationCenter = [NSNotificationCenter defaultCenter];
        [notificationCenter addObserver:self selector:@selector(sendOnRecordingChangeEventWithStatus:)
                                   name:AVCaptureSessionRuntimeErrorNotification object:self.session];
    }
    return self;
}

+ (BOOL)containsDevice:(AVCaptureDevice *)device session:(AVCaptureSession *)session
{
    BOOL found = NO;
    for (AVCaptureDeviceInput *input in [session inputs]) {
        if ([[input device].uniqueID isEqualToString:device.uniqueID]) {
            found = YES;
            break;
        }
    }
    return found;
}

+ (AVCaptureConnection *)connectionForDevice:(AVCaptureDevice *)device output:(AVCaptureOutput *)output
{
    if (!device || !output) {
        NSLog(@"args must be non-nil.");
        return nil;
    }
    
    for (AVCaptureConnection *connection in [output connections]) {
        BOOL found = NO;
        for (AVCaptureInputPort *inputPort in [connection inputPorts]) {
            AVCaptureInput *input = [inputPort input];
            if ([input isKindOfClass:[AVCaptureDeviceInput class]]) {
                if ([[(AVCaptureDeviceInput *)input device].uniqueID isEqualToString:device.uniqueID]) {
                    found = YES;
                    break;
                }
            }
        }
        if (found) {
            return connection;
        }
    }
    return nil;
}

- (void) performReading:(void(^)(void))callback
{
    dispatch_sync(_queue, callback);
}

- (void) performWriting:(void(^)(void))callback
{
    dispatch_barrier_sync(_queue, callback);
}

- (void) setRecorderDataSource:(DPHostRecorderDataSource *)dataSrc delegate:(id)delegate
{
    NSAssert(dataSrc, @"argument must be non-nil.");
    
    AVCaptureDevice *captureDevice = [AVCaptureDevice deviceWithUniqueID:dataSrc.uniqueId];
    
    if (!captureDevice) {
        NSLog(@"Logic error: failed to obtain a capture device.");
        return;
    }
    
    switch (dataSrc.type) {
        case RecorderDataSourceTypePhoto:
        {
            if (![DPHostRecorderContext containsDevice:captureDevice session:_session]) {
                NSError *error;
                AVCaptureDeviceInput *deviceInput =
                [AVCaptureDeviceInput deviceInputWithDevice:captureDevice error:&error];
                if (error) {
                    NSLog(@"Error encountered while trying to instantiate a video input.");
                    return;
                }
                if ( [_session canAddInput:deviceInput] ) {
                    [_session addInput:deviceInput];
                } else {
                    NSLog(@"Failed to add a video input to the session.");
                    return;
                }
            }
            
            AVCaptureStillImageOutput *stillImageOutput = [AVCaptureStillImageOutput new];
            [stillImageOutput setOutputSettings:
             @{
               // AVVideoCodecKey : AVVideoCodecJPEGと共存してくれない…
               //       (id)kCVPixelBufferPixelFormatTypeKey : [NSNumber numberWithInt:kCVPixelFormatType_32BGRA],
               AVVideoCodecKey : AVVideoCodecJPEG
               }];
            if ( [_session canAddOutput:stillImageOutput] ) {
                [_session addOutput:stillImageOutput];
            } else {
                NSLog(@"Failed to add a still image output to the session.");
                return;
            }
            
            _videoConnection = [DPHostRecorderContext connectionForDevice:captureDevice output:stillImageOutput];
            if (!_videoConnection) {
                NSLog(@"Failed to obtain a video connection.");
                return;
            }
            
            _videoDevice = dataSrc;
            
            break;
        }
            
        case RecorderDataSourceTypeAudio:
        {
            if (![DPHostRecorderContext containsDevice:captureDevice session:_session]) {
                NSError *error;
                AVCaptureDeviceInput *audioIn =
                [AVCaptureDeviceInput deviceInputWithDevice:captureDevice error:&error];
                if (error) {
                    NSLog(@"Error encountered while trying to instantiate an audio input.");
                    return;
                }
                if ( [_session canAddInput:audioIn] ) {
                    [_session addInput:audioIn];
                } else {
                    NSLog(@"Failed to add an audio input to the session.");
                    return;
                }
            }
            
            AVCaptureAudioDataOutput *audioOut = [AVCaptureAudioDataOutput new];
            // delegateがAVCaptureAudioDataOutputSampleBufferDelegateプロトコルに対応しているかチェックすべき？
            [audioOut setSampleBufferDelegate:delegate queue:dataSrc.captureQueue];
            if ([_session canAddOutput:audioOut]) {
                [_session addOutput:audioOut];
            } else {
                NSLog(@"Failed to add an audio output to the session.");
                return;
            }
            
            _audioConnection = [DPHostRecorderContext connectionForDevice:captureDevice output:audioOut];
            if (!_audioConnection) {
                NSLog(@"Failed to obtain an audio connection.");
                return;
            }
            
            _audioDevice = dataSrc;
            break;
        }
            
        case RecorderDataSourceTypeVideo:
        {
            if (![DPHostRecorderContext containsDevice:captureDevice session:_session]) {
                NSError *error;
                AVCaptureDeviceInput *videoIn =
                [[AVCaptureDeviceInput alloc] initWithDevice:captureDevice error:&error];
                if (error) {
                    NSLog(@"Error encountered while trying to instantiate a video input.");
                    return;
                }
                if ([_session canAddInput:videoIn]) {
                    [_session addInput:videoIn];
                } else {
                    NSLog(@"Failed to add a video input to the session.");
                    return;
                }
            }
            
            AVCaptureVideoDataOutput *videoOut = [AVCaptureVideoDataOutput new];
            // MARK: 初回処理が遅い場合は YES が良いらしいが、iOS7以降のiOSデバイスでは気にする必要なし？
            [videoOut setAlwaysDiscardsLateVideoFrames:NO];
            [videoOut setVideoSettings:
             @{
               (id)kCVPixelBufferPixelFormatTypeKey : [NSNumber numberWithInt:kCVPixelFormatType_32BGRA]
               }];
            // delegateがAVCaptureVideoDataOutputSampleBufferDelegateプロトコルに対応しているかチェックすべき？
            [videoOut setSampleBufferDelegate:delegate queue:dataSrc.captureQueue];
            if ([_session canAddOutput:videoOut]) {
                [_session addOutput:videoOut];
            } else {
                NSLog(@"Failed to add a video output to the session.");
                return;
            }
            
            _videoConnection = [DPHostRecorderContext connectionForDevice:captureDevice output:videoOut];
            if (!_videoConnection) {
                NSLog(@"Failed to obtain a video connection.");
                return;
            }
            
            _videoDevice = dataSrc;
            break;
        }
            
        default:
            break;
    }
}

- (BOOL) setupAssetWriterWithResponse:(DConnectResponseMessage *)response;
{
    // AVAssetWriterの初期化および書き出し成功を確認した際に用いるHTTPレスポンス。
    _response = response;
    NSString *fileName = [NSString stringWithFormat:@"%@_%@", [[NSProcessInfo processInfo] globallyUniqueString], @"movie.mp4"];
    NSURL *fileURL = [NSURL fileURLWithPath:[NSTemporaryDirectory() stringByAppendingPathComponent:fileName]];
    _writer = [AVAssetWriter assetWriterWithURL:fileURL fileType:AVFileTypeQuickTimeMovie error:nil];
    
    // エラードメイン「AVFoundationErrorDomain」コード「-11823」メッセージ「Cannot Save」（ファイルが既に存在する）が出る問題への対処
    [[NSFileManager defaultManager] removeItemAtURL:_writer.outputURL error:nil];
    
    return _writer != nil;
}

- (void) sendResponse
{
    if (_response) {
        [[DConnectManager sharedManager] sendResponse:_response];
        _response = nil;
    }
}

- (void) sendOnRecordingChangeEventWithStatus:(NSNotification *)notification
{
    // イベントの取得
    NSArray *evts = [_profile.eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                                    profile:DConnectMediaStreamRecordingProfileName
                                                  attribute:DConnectMediaStreamRecordingProfileAttrOnPhoto];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        DConnectMessage *media = [DConnectMessage message];
        [DConnectMediaStreamRecordingProfile setStatus:DConnectMediaStreamRecordingProfileRecordingStateError
                                                target:media];
        NSError *error = notification.userInfo[AVCaptureSessionErrorKey];
        [DConnectMediaStreamRecordingProfile setErrorMessage:error.localizedDescription target:media];
        [DConnectMediaStreamRecordingProfile setPhoto:media target:eventMsg];
        
        [(DConnectDevicePlugin *)_profile.provider sendEvent:eventMsg];
    }
}

@end
