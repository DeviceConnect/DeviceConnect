//
//  DPHostRecorderContext.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <AVFoundation/AVFoundation.h>
#import <CoreMedia/CoreMedia.h>
#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, RecorderDataSourceType) {
    RecorderDataSourceTypePhoto, ///< 写真（静止画）
    RecorderDataSourceTypeAudio, ///< オーディオ（動画における音声）
    RecorderDataSourceTypeVideo, ///< ビデオ（動画における画像）
};

typedef NS_ENUM(NSUInteger, RecorderType) {
    RecorderTypePhoto, ///< 写真（静止画）
    RecorderTypeMovie, ///< 動画（画像 and/or 音声）
};

typedef NS_ENUM(NSUInteger, RecorderState) {
    RecorderStateInactive,  ///< 撮影状態「撮影していない ("inactive"))」
    RecorderStatePaused,    ///< 撮影状態「撮影一時停止中 ("paused")」
    RecorderStateRecording, ///< 撮影状態「撮影中 ("recording")」
};

/*!
 入力デバイス管理クラス
 */
@interface DPHostRecorderDataSource : NSObject

@property (nonatomic) RecorderDataSourceType type;

@property (nonatomic) NSString *uniqueId; ///< 入力デバイスのuniqueId

@property (nonatomic) AVCaptureDevicePosition position; ///< レコーダーの位置
@property (nonatomic) NSNumber *imageWidth;  ///< 現在の撮影画像横幅（写真・動画キャプチャ時以外の場合はnil）
@property (nonatomic) NSNumber *imageHeight; ///< 現在の撮影画像縦幅（写真・動画キャプチャ時以外の場合はnil）
@property (nonatomic) NSNumber *minImageWidth; ///< 撮影画像縦幅の最小値
@property (nonatomic) NSNumber *maxImageWidth; ///< 撮影画像縦幅の最大値
@property (nonatomic) NSNumber *minImageHeight; ///< 撮影画像縦幅の最小値
@property (nonatomic) NSNumber *maxImageHeight; ///< 撮影画像縦幅の最大値
@property UIDeviceOrientation initialDeviceOrientation; ///< 撮影開始時のデバイスの姿勢
@property NSString *config;
@property NSArray *optionArr;

/*!
 静止画用のレコーディング機器を作成する。
 @param[in] videoDevice 写真撮影に用いるビデオ機器
 @return レコーディング機器、もしくは<code>nil</code>
 */
+ (instancetype)recorderDataSourceForPhotoWithVideoDevice:(AVCaptureDevice *)videoDevice;
/*!
 オーディオ撮影用のレコーディング機器を作成する。
 @param[in] audioDevice 録音に用いるオーディオ機器
 @return レコーディング機器、もしくは<code>nil</code>
 */
+ (instancetype)recorderDataSourceForAudioWithAudioDevice:(AVCaptureDevice *)audioDevice;

/*!
 動画用のレコーディング機器を作成する。
 @param videoDevice 録画に用いるビデオ機器
 @return レコーディング機器、もしくは<code>nil</code>
 */
+ (instancetype)recorderDataSourceForVideoWithVideoDevice:(AVCaptureDevice *)videoDevice;

@end

/*!
 レコーダー管理クラス
 */
@interface DPHostRecorderContext : NSObject

@property (nonatomic) RecorderType type;

@property (nonatomic) DPHostMediaStreamRecordingProfile *profile;

@property (nonatomic) NSString *name; ///< レコーダーの名称
@property (nonatomic) RecorderState state; ///< 現在のレコーディング状態
@property (nonatomic) NSURL *mediaURL;
@property (nonatomic) NSString *mimeType; ///< メディアのMIMEタイプ
@property (nonatomic) BOOL isMuted; ///< ミュート状態かどうか
@property (nonatomic) AVCaptureVideoOrientation videoOrientation; ///< 録画開始時のビデオの向き

@property (nonatomic) AVAssetWriter *writer; ///< レコーディング内容の書き出しを行うオブジェクト
@property (nonatomic) DConnectResponseMessage *response; ///< AVAssetWriterの初期化および書き出し成功を確認した際に用いるHTTPレスポンス。

/*!
 キャプチャーセッション。
 <code>nil</code>であればレコーディングはされておらず、
 プロパティ<code>running</code>が<code>YES</code>ならばレコーディング中であり、
 <code>NO</code>ならばレコーディング停止中となる。
 */
@property (nonatomic) AVCaptureSession *session;
@property (nonatomic) DPHostRecorderDataSource *audioDevice; ///< オーディオ入力デバイス
@property (nonatomic) AVCaptureConnection *audioConnection; ///< オーディオコネクション
@property (nonatomic) AVAssetWriterInput *audioWriterInput; ///< <code>writer</code>へのオーディオ入力
@property (nonatomic) BOOL audioReady; ///< ビデオ入力デバイスのレコーディング準備が整っているかどうか
@property (nonatomic) DPHostRecorderDataSource *videoDevice; ///< ビデオ入力デバイス
@property (nonatomic) AVCaptureConnection *videoConnection; ///< ビデオコネクション
@property (nonatomic) AVAssetWriterInput *videoWriterInput; ///< <code>writer</code>へのビデオ入力
@property (nonatomic) BOOL videoReady; ///< ビデオ入力デバイスのレコーディング準備が整っているかどうか

/*!
 @param[in] profile このコンテキストを所有しているDPHostMediaStreamRecordingProfile
 @return レコーディングコンテキスト
 */
- (instancetype)initWithProfile:(DPHostMediaStreamRecordingProfile *)profile;

/*!
 キャプチャーセッションに指定されたデバイスが既にInputとして追加されているかどうかをチェックする
 @param[in] device 検索したいデバイス
 @param[in] session キャプチャーセッション
 @retval YES <code>device</code>が<code>sesison</code>に含まれる
 @retval NO <code>device</code>が<code>sesison</code>に含まれない
 */
+ (BOOL)containsDevice:(AVCaptureDevice *)device session:(AVCaptureSession *)session;

/*!
 指定された入力デバイスと出力先を持ったキャプチャーコネクションを返却する。
 キャプチャーコネクションが見つからなかった場合は<code>nil</code>を返却する。
 @param device 入力デバイス
 @param output 出力
 @return キャプチャーコネクション、もしくは<code>nil</code>
 */
+ (AVCaptureConnection *)connectionForDevice:(AVCaptureDevice *)device output:(AVCaptureOutput *)output;

/*!
 @brief このコンテキストに対して読み取り処理を行う。処理が終わるまでreturnしない。
 Read/Writeスキームに従い、特定のスレッドで読み取り処理が進行している場合、
 件のスレッド以外のスレッドから読み取り処理を実行できるが、書き込み処理は全ての読み取り処理が
 終了するまで実行されない。
 @param callback 読み取り処理を行うブロック
 */
- (void) performReading:(void(^)(void))callback;
/*!
 @brief このコンテキストに対して書き込み処理を行う。処理が終わるまでreturnしない。
 Read/Writeスキームに従い、特定のスレッドで書き込み処理が進行している場合、
 あらゆるスレッドからの追加の読み取り・書き込み処理は、件の読み取り処理が終了するまで実行されない。
 @param callback 書き込み処理を行うブロック
 */
- (void) performWriting:(void(^)(void))callback;

/*!
 @brief このコンテキストに対してレコーダデータソースを設定する
 @param dataSrc 静止画・ビデオ・音声などのレコーダデータソース
 @param delegate ビデオ・音声などのレコーダデータソースからサンプルを受け取るデリゲート（AVCaptureVideoDataOutputSampleBufferDelegateもしくはAVCaptureAudioDataOutputSampleBufferDelegateプロトコルを実装したクラス）
 */
- (void) setRecorderDataSource:(DPHostRecorderDataSource *)dataSrc delegate:(id)delegate;

/*!
 動画（ビデオ and/or オーディオ）書き出し用のアセットライターを準備
 @param response AVAssetWriterの初期化および書き出し成功を確認した際に用いるHTTPレスポンス
 @retval YES アセットライターのインスタンス化に成功
 @retval NO アセットライターのインスタンス化に失敗
 */
- (BOOL) setupAssetWriterWithResponse:(DConnectResponseMessage *)response;

/*!
 HTTPレスポンスを返却する。
 既にHTTPレスポンスを返却済みの場合は何もおこらない。
 */
- (void) sendResponse;

- (void) sendOnRecordingChangeEventWithStatus:(NSNotification *)notification;

@end
