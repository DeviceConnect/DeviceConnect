//
//  DConnectMediaStreamRecordingProfile.h
//  DConnectSDK
//
//  Created by 小林 伸郎 on 2014/05/12.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief Media Stream Recordingプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: mediastream_recording。 */
extern NSString *const DConnectMediaStreamRecordingProfileName;

/*!
 @brief 属性: mediarecorder。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrMediaRecorder;

/*!
 @brief 属性: takephoto。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrTakePhoto;

/*!
 @brief 属性: record。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrRecord;

/*!
 @brief 属性: pause。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrPause;

/*!
 @brief 属性: resume。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrResume;

/*!
 @brief 属性: stop。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrStop;

/*!
 @brief 属性: mutetrack。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrMuteTrack;

/*!
 @brief 属性: unmutetrack。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrUnmuteTrack;

/*!
 @brief 属性: options。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOptions;

/*!
 @brief 属性: onphoto。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOnPhoto;

/*!
 @brief 属性: onrecordingchange。
 */
extern NSString *const DConnectMediaStreamRecordingProfileAttrOnRecordingChange;

/*!
 @brief 属性: ondataavailable。
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
 @brief パラメータ: min.
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMin;

/*!
 @brief パラメータ: max.
 */
extern NSString *const DConnectMediaStreamRecordingProfileParamMax;

/*!
 @brief パラメータ: mimetype.
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
 @brief MediaStream Recording プロファイルのデリゲート。
 
 <p>
 スマートデバイスによる写真撮影、動画録画、音声録音などの機能を提供するAPI。<br/>
 スマートデバイスによる写真撮影、動画録画、音声録音などの機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DConnectMediaStreamRecordingProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief 使用可能カメラ情報取得リクエストハンドラー。
 
 使用可能なカメラの情報を提供し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで使用可能なカメラの情報一覧を取得できる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief サポートオプション一覧取得リクエストハンドラー。
 
 サポートしているオプションの一覧を提供し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音のオプション（MIMEタイプ、画像の縦横サイズ…）一覧の取得が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;

#pragma mark - Post Methods

/*!
 @brief 写真撮影依頼リクエストハンドラー。
 
 写真の撮影を実行し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで写真撮影が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;

/*!
 @brief 動画撮影、音声録音依頼リクエストハンドラー.
 
 動画撮影、音声録音を実行し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音が行える。
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
 @brief 動画撮影、音声録音の一時停止依頼リクエストハンドラー。
 
 動画撮影、音声録音を一時停止し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音の一時停止が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;

/*!
 @brief 動画撮影、音声録音の再開依頼リクエストハンドラー.
 
 動画撮影、音声録音を再開し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音の再開が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *) target;

/*!
 @brief 動画撮影、音声録音の停止依頼リクエストハンドラー.
 
 動画撮影、音声録音を停止し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音の停止が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;


/*!
 @brief 動画撮影、音声録音のミュート依頼リクエストハンドラー。
 
 動画撮影、音声録音をミュートし、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音のミュートが行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;

/*!
 @brief 動画撮影、音声録音のミュート解除リクエストハンドラー。
 
 動画撮影、音声録音をミュートを解除し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音のミュート解除が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId target:(NSString *)target;


/*!
 @brief オプション設定リクエストハンドラー.
 
 オプションを設定し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音のオプション（MIMEタイプ、画像の縦横サイズ…）の設定が行える。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
          target:(NSString *)target imageWidth:(NSNumber *)imageWidth
     imageHeight:(NSNumber *)imageHeight mimeType:(NSString *)mimeType;

#pragma mark Event Registration

/*!
 @brief onphotoコールバック登録リクエストハンドラー。
 
 onphotoコールバックを登録し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで写真撮影イベント通知の受領ができるようになる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief onrecordingchangeコールバック登録リクエストハンドラー。
 
 onrecordingchangeコールバックを登録し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音の開始イベント通知の受領ができるようになる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief ondataavailableコールバック登録リクエストハンドラー。
 
 ondataavailableコールバックを登録し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音中に一定時間が経過した事を知らせるイベント通知の受領ができるようになる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregstration

/*!
 @brief onphotoコールバック解除リクエストハンドラー。
 
 onphotoコールバックを解除し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで写真撮影イベント通知の受領を停止できる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief onrecordingchangeコールバック解除リクエストハンドラー。
 
 onPutOnRecordingChangeコールバックを解除し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音のイベント通知の受領を停止できる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief ondataavailableコールバック解除リクエストハンドラー。
 
 ondataavailableコールバックを解除し、その結果をレスポンスパラメータに格納する。
 各デバイスプラグインは、この関数を実装することで動画撮影や音声録音中に一定時間が経過した事を知らせるイベント通知の受領を停止できる。
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectMediaStreamRecordingProfile
 @brief MediaStreamRecordingプロファイル。
 
 以下のメソッドを実装することで、MediaStreamRecordingプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 - didReceiveGetMediaRecorderRequest: response: deviceId:
 - didReceiveGetOptionsRequest: response: deviceId: target:
 - didReceivePostTakePhotoRequest: response: deviceId: target:
 - didReceivePostRecordRequest: response: deviceId: target: timeslice:
 - didReceivePutPauseRequest: response: deviceId: mediaId:
 - didReceivePutResumeRequest: response: deviceId: mediaId:
 - didReceivePutStopRequest: response: deviceId: mediaId:
 - didReceivePutMuteTrackRequest: response: deviceId: mediaId:
 - didReceivePutUnmuteTrackRequest: response: deviceId: mediaId:
 - didReceivePutOptionsRequest: response: deviceId: target: imageWidth: imageHeight: mimeType:
 - didReceivePutOnPhotoRequest: response: deviceId: sessionKey:
 - didReceivePutOnRecordingRequest: response: deviceId: sessionKey:
 - didReceivePutOnPauseRequest: response: deviceId: sessionKey:
 - didReceivePutOnResumeRequest: response: deviceId: sessionKey:
 - didReceivePutOnStopRequest: response: deviceId: sessionKey:
 - didReceivePutOnMuteTrackRequest: response: deviceId: sessionKey:
 - didReceivePutOnUnmuteTrackRequest: response: deviceId: sessionKey:
 - didReceivePutOnErrorRequest: response: deviceId: sessionKey:
 - didReceivePutOnWarningRequest: response: deviceId: sessionKey:
 - didReceivePutOnDataAvailableRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnPhotoRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnRecordingRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnPauseRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnResumeRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnStopRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnMuteTrackRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnUnmuteTrackRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnErrorRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnWarningRequest: response: deviceId: sessionKey:
 - didReceiveDeleteOnDataAvailableRequest: response: deviceId: sessionKey:

 */
@interface DConnectMediaStreamRecordingProfile : DConnectProfile

/*!
 @brief DConnectMediaStreamRecordingProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectMediaStreamRecordingProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief dConnectメッセージにカメラデータを設定する。
 
 @param[in] recorders カメラデータ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorders:(DConnectArray *)recorders target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにカメラIDを設定する。
 @param[in] cameraId カメラID
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderId:(NSString *)cameraId target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにカメラ名を設定する。
 
 @param[in] name カメラ名
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにカメラの状態を設定する。
 
 @param[in] state カメラの状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderState:(NSString *)state target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに横幅を設定する。
 
 @param[in] imageWidth カメラの横幅
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderImageWidth:(int)imageWidth target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに縦幅を設定する。
 
 @param[in] imageHeight カメラの縦幅
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderImageHeight:(int)imageHeight target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにMIMEタイプを設定する。
 
 @param[in] mimeType MIMEタイプ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderMIMEType:(NSString *)mimeType target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに設定情報を設定する。
 
 @param[in] config 設定情報
 @param[in,out] message dConnectメッセージ
 */
+ (void) setRecorderConfig:(NSString *)config target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに縦幅設定の最小値・最小値を設定する。
 
 @param[in] iamgeWidth 縦幅設定の最小値・最大値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setImageHeight:(DConnectMessage *)imageWidth target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに横幅設定の最小値・最小値を設定する。
 
 @param[in] iamgeWidth 横幅設定の最小値・最大値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setImageWidth:(DConnectMessage *)imageWidth target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに最小値を設定する。
 
 @param[in] min 最小値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMin:(int)min target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに最大値を設定する。
 
 @param[in] max 最大値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMax:(int)max target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにパスを設定する。
 
 @param[in] path パス
 @param[in,out] message dConnectメッセージ
 */
+ (void) setPath:(NSString *)path target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに写真データを設定する。
 
 @param[in] photo 写真データ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setPhoto:(DConnectMessage *)photo target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにメディアデータを設定する。
 
 @param[in] media メディアデータ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMedia:(DConnectMessage *)media target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにMIMEタイプを設定する。
 
 @param[in] mimeType MIMEタイプ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMIMEType:(NSString *)mimeType target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにMIMEタイプを設定する。
 
 @param[in] mimeTypes MIMEタイプ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMIMETypes:(DConnectArray *)mimeTypes target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにファイルのURIを設定する。
 
 @param[in] uri ファイルのURI
 @param[in,out] message dConnectメッセージ
 */
+ (void) setUri:(NSString *)uri target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに状態を設定する。
 
 @param[in] status 状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setStatus:(NSString *)status target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにエラーメッセージを設定する。
 
 @param[in] errorMessage エラーメッセージ
 @param[in,out] message dConnectメッセージ
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
