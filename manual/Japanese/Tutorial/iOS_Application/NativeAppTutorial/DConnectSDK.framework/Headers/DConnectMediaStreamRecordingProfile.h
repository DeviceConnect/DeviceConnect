//
//  DConnectMediaStreamRecordingProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Media Stream Recordingプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*! 
 @brief プロファイル名: mediastream_recording。 
 */
extern NSString *const DConnectMediaStreamRecordingProfileName;

/*!
 @brief アトリビュート: mediarecorder。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrMediaRecorder;

/*!
 @brief アトリビュート: takephoto。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrTakePhoto;

/*!
 @brief アトリビュート: record。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrRecord;

/*!
 @brief アトリビュート: pause。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrPause;

/*!
 @brief アトリビュート: resume。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrResume;

/*!
 @brief アトリビュート: stop。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrStop;

/*!
 @brief アトリビュート: mutetrack。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrMuteTrack;

/*!
 @brief アトリビュート: unmutetrack。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrUnmuteTrack;

/*!
 @brief アトリビュート: options。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOptions;

/*!
 @brief アトリビュート: onphoto。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOnPhoto;

/*!
 @brief アトリビュート: onrecordingchange。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOnRecordingChange;

/*!
 @brief アトリビュート: ondataavailable。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOnDataAvailable;


/*!
 @brief パラメータ: recorders。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamRecorders;

/*!
 @brief パラメータ: id。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamId;

/*!
 @brief パラメータ: name。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamName;

/*!
 @brief パラメータ: state。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamState;

/*!
 @brief パラメータ: imageWidth。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamImageWidth;

/*!
 @brief パラメータ: imageHeight。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamImageHeight;

/*!
 @brief パラメータ: min。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMin;

/*!
 @brief パラメータ: max。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMax;

/*!
 @brief パラメータ: mimetype。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMIMEType;

/*!
 @brief パラメータ: config。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamConfig;

/*!
 @brief パラメータ: target。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamTarget;

/*!
 @brief パラメータ: path。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamPath;

/*!
 @brief パラメータ: timeslice。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamTimeSlice;

/*!
 @brief パラメータ: settings。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamSettings;

/*!
 @brief パラメータ: photo。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamPhoto;

/*!
 @brief パラメータ: media。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMedia;

/*!
 @brief パラメータ: uri。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamUri;
/*!
 @brief パラメータ: status。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamStatus;
/*!
 @brief パラメータ: errorMessage。
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamErrorMessage;

/*!
 @brief カメラの状態定数: 未定義値。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecorderStateUnknown;
/*!
 @brief カメラの状態定数: 停止中。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecorderStateInactive;
/*!
 @brief カメラの状態定数: レコーディング中。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecorderStateRecording;
/*!
 @brief カメラの状態定数: 一時停止中。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecorderStatePaused;

/*!
 @brief 動画撮影、音声録音の状態定数: 未定義値。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateUnknown;
/*!
 @brief 動画撮影、音声録音の状態定数: 開始。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateRecording;
/*!
 @brief 動画撮影、音声録音の状態定数: 終了。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateStop;
/*!
 @brief 動画撮影、音声録音の状態定数: 一時停止。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStatePause;
/*!
 @brief 動画撮影、音声録音の状態定数: 再開。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateResume;
/*!
 @brief 動画撮影、音声録音の状態定数: ミュート。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateMutetrack;
/*!
 @brief 動画撮影、音声録音の状態定数: ミュート解除。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateUnmutetrack;
/*!
 @brief 動画撮影、音声録音の状態定数: エラー発生。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateError;
/*!
 @brief 動画撮影、音声録音の状態定数: 警告発生。
 */
extern NSString *const DConnectMediaStreamRecordingProfileRecordingStateWarning;

@class DConnectMediaStreamRecordingProfile;

/*!
 @protocol DConnectMediaStreamRecordingProfileDelegate
 @brief MediaStream Recording Profileの各APIリクエスト通知用デリゲート。
 
 MediaStream Recording Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectMediaStreamRecordingProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief 使用可能カメラ情報取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileが使用可能なカメラの情報取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording MediaRecorder API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceiveGetMediaRecorderRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 
 @brief サポートオプション一覧取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがサポートオプション一覧取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Options API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceiveGetOptionsRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;

#pragma mark - Post Methods

/*!
 
 @brief 写真撮影リクエストを受け取ったことをデリゲートに通知する。
 
 profileが写真撮影リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Take Photo API [POST]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePostTakePhotoRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;

/*!
 
 @brief 動画撮影、音声録音リクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Record API [POST]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 @param[in] timeslice タイムスライス。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePostRecordRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
          target:(NSString *)target timeslice:(NSNumber *)timeslice;


#pragma mark - Put Methods

/*!
 
 @brief 動画撮影、音声録音の一時停止リクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音の一時停止リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Pause API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutPauseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;

/*!
 
 @brief 動画撮影、音声録音の再開リクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音の再開リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Resume API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutResumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *) target;

/*!
 
 @brief 動画撮影、音声録音の停止リクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音の停止リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Stop API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutStopRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;


/*!
 
 @brief 動画撮影、音声録音のミュートリクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音のミュートリクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording MuteTrack API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutMuteTrackRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;

/*!
 
 @brief 動画撮影、音声録音のミュート解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileが動画撮影、音声録音のミュート解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording UnmuteTrack API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutUnmuteTrackRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target;


/*!
 
 @brief オプション設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileがオプション設定リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Options API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] target ターゲット。省略された場合はnil。
 @param[in] imageWidth 画像の横幅
 @param[in] imageHeight 画像の縦幅
 @param[in] mimeType MIMEタイプ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutOptionsRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          target:(NSString *)target
      imageWidth:(NSNumber *)imageWidth
     imageHeight:(NSNumber *)imageHeight
        mimeType:(NSString *)mimeType;

#pragma mark Event Registration

/*!
 
 @brief onphotoイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonphotoイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Take a Picture Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutOnPhotoRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onrecordingchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonrecordingchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Status Change Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutOnRecordingChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief ondataavailableイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondataavailableイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Data Available Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceivePutOnDataAvailableRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregstration

/*!
 
 @brief onphotoイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonphotoイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Take a Picture Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceiveDeleteOnPhotoRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onrecordingchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonrecordingchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Status Change Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceiveDeleteOnRecordingChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief ondataavailableイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondataavailableイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] MediaStream Recording Data Available Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectMediaStreamRecordingProfile *)profile didReceiveDeleteOnDataAvailableRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectMediaStreamRecordingProfile
 @brief MediaStreamRecordingプロファイル。
 
 Battery Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectMediaStreamRecordingProfile : DConnectProfile

/*!
 @brief DConnectMediaStreamRecordingProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectMediaStreamRecordingProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectMediaStreamRecordingProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージにカメラデータを設定する。
 
 @param[in] recorders カメラデータ
 @param[in,out] message カメラデータを格納するメッセージ
 */
+ (void) setRecorders:(DConnectArray *)recorders target:(DConnectMessage *)message;

/*!
 @brief メッセージにカメラIDを設定する。
 @param[in] cameraId カメラID
 @param[in,out] message カメラIDを格納するメッセージ
 */
+ (void) setRecorderId:(NSString *)cameraId target:(DConnectMessage *)message;

/*!
 @brief メッセージにカメラ名を設定する。
 
 @param[in] name カメラ名
 @param[in,out] message カメラ名を格納するメッセージ
 */
+ (void) setRecorderName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief メッセージにカメラの状態を設定する。
 
 @param[in] state カメラの状態
 @param[in,out] message カメラ状態を格納するメッセージ
 */
+ (void) setRecorderState:(NSString *)state target:(DConnectMessage *)message;

/*!
 @brief メッセージにカメラの横幅を設定する。
 
 @param[in] imageWidth カメラの横幅
 @param[in,out] message カメラの横幅を格納するメッセージ
 */
+ (void) setRecorderImageWidth:(int)imageWidth target:(DConnectMessage *)message;

/*!
 @brief メッセージにカメラの縦幅を設定する。
 
 @param[in] imageHeight カメラの縦幅
 @param[in,out] message カメラの縦幅を格納するメッセージ
 */
+ (void) setRecorderImageHeight:(int)imageHeight target:(DConnectMessage *)message;

/*!
 @brief メッセージにMIMEタイプを設定する。
 
 @param[in] mimeType MIMEタイプ
 @param[in,out] message MIMEタイプを格納するメッセージ
 */
+ (void) setRecorderMIMEType:(NSString *)mimeType target:(DConnectMessage *)message;

/*!
 @brief メッセージに設定情報を設定する。
 
 @param[in] config 設定情報
 @param[in,out] message 設定情報を格納するメッセージ
 */
+ (void) setRecorderConfig:(NSString *)config target:(DConnectMessage *)message;

/*!
 @brief メッセージに縦幅設定の最小値・最小値を設定する。
 
 @param[in] imageHeight 縦幅設定の最小値・最大値
 @param[in,out] message 縦幅設定の最小値・最大値を格納するメッセージ
 */
+ (void) setImageHeight:(DConnectMessage *)imageHeight target:(DConnectMessage *)message;

/*!
 @brief メッセージに横幅設定の最小値・最小値を設定する。
 
 @param[in] imageWidth 横幅設定の最小値・最大値
 @param[in,out] message 横幅設定の最小値・最大値を格納するメッセージ
 */
+ (void) setImageWidth:(DConnectMessage *)imageWidth target:(DConnectMessage *)message;

/*!
 @brief メッセージに最小値を設定する。
 
 @param[in] min 最小値
 @param[in,out] message 最小値を格納するメッセージ
 */
+ (void) setMin:(int)min target:(DConnectMessage *)message;

/*!
 @brief メッセージに最大値を設定する。
 
 @param[in] max 最大値
 @param[in,out] message 最大値を格納するメッセージ
 */
+ (void) setMax:(int)max target:(DConnectMessage *)message;

/*!
 @brief メッセージにパスを設定する。
 
 @param[in] path パス
 @param[in,out] message パスを格納するメッセージ
 */
+ (void) setPath:(NSString *)path target:(DConnectMessage *)message;

/*!
 @brief メッセージに写真データを設定する。
 
 @param[in] photo 写真データ
 @param[in,out] message 写真データを格納するメッセージ
 */
+ (void) setPhoto:(DConnectMessage *)photo target:(DConnectMessage *)message;

/*!
 @brief メッセージにメディアデータを設定する。
 
 @param[in] media メディアデータ
 @param[in,out] message メディアデータを格納するメッセージ
 */
+ (void) setMedia:(DConnectMessage *)media target:(DConnectMessage *)message;

/*!
 @brief メッセージにMIMEタイプを設定する。
 
 @param[in] mimeType MIMEタイプ
 @param[in,out] message MIMEタイプを格納するメッセージ
 */
+ (void) setMIMEType:(NSString *)mimeType target:(DConnectMessage *)message;

/*!
 @brief メッセージにMIMEタイプリストを設定する。
 
 @param[in] mimeTypes MIMEタイプリスト
 @param[in,out] message MIMEタイプリストを格納するメッセージ
 */
+ (void) setMIMETypes:(DConnectArray *)mimeTypes target:(DConnectMessage *)message;

/*!
 @brief メッセージにファイルのURIを設定する。
 
 @param[in] uri ファイルのURI
 @param[in,out] message ファイルのURIを格納するメッセージ
 */
+ (void) setUri:(NSString *)uri target:(DConnectMessage *)message;

/*!
 @brief メッセージに状態を設定する。
 
 @param[in] status 状態
 @param[in,out] message 状態を格納するメッセージ
 */
+ (void) setStatus:(NSString *)status target:(DConnectMessage *)message;

/*!
 @brief メッセージにエラーメッセージを設定する。
 
 @param[in] errorMessage エラーメッセージ
 @param[in,out] message エラーメッセージを格納するメッセージ
 */
+ (void) setErrorMessage:(NSString *)errorMessage target:(DConnectMessage *)message;

#pragma mark + Getter

/*!
 @brief リクエストからカメラの識別IDを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval カメラの識別ID
 @retval nil 省略された場合
 */
+ (NSString *) targetFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストからタイムスライスを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval タイムスライス
 @retval nil 省略された場合
 */
+ (NSNumber *) timesliceFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから横幅を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 横幅
 @retval nil 省略された場合
 */
+ (NSNumber *) imageWidthFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから縦幅を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 縦幅
 @retval nil 省略された場合
 */
+ (NSNumber *) imageHeightFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストからMIMEタイプを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval MIMEタイプ
 @retval nil 省略された場合
 */
+ (NSString *) mimeTypeFromRequest:(DConnectMessage *)request;

@end
