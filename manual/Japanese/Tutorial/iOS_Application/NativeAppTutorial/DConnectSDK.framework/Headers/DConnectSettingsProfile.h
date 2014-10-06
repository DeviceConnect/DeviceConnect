//
//  DConnectSettingsProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Settingsプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: settings。
 */
extern NSString *const DConnectSettingsProfileName;

/*!
 @brief インターフェース: sound。
 */
extern NSString *const DConnectSettingsProfileInterfaceSound;

/*!
 @brief インターフェース: display。
 */
extern NSString *const DConnectSettingsProfileInterfaceDisplay;

/*!
 @brief アトリビュート: volume。
 */
extern NSString *const DConnectSettingsProfileAttrVolume;

/*!
 @brief アトリビュート: date。
 */
extern NSString *const DConnectSettingsProfileAttrDate;

/*!
 @brief アトリビュート: light。
 */
extern NSString *const DConnectSettingsProfileAttrLight;

/*!
 @brief アトリビュート: sleep。
 */
extern NSString *const DConnectSettingsProfileAttrSleep;

/*!
 @brief パラメータ: kind。
 */
extern NSString *const DConnectSettingsProfileParamKind;

/*!
 @brief パラメータ: level。
 */
extern NSString *const DConnectSettingsProfileParamLevel;

/*!
 @brief パラメータ: date。
 */
extern NSString *const DConnectSettingsProfileParamDate;

/*!
 @brief パラメータ: time。
 */
extern NSString *const DConnectSettingsProfileParamTime;

/*!
 @brief ボリュームのレベルの最大値: 1.0。
 */
extern const double DConnectSettingsProfileMaxLevel;

/*!
 @brief ボリュームのレベルの最小値: 0.0。
 */
extern const double DConnectSettingsProfileMinLevel;

/*!
 @enum DConnectSettingsProfileVolumeKind
 @brief 音量の種別定数。
 */
typedef NS_ENUM(NSInteger, DConnectSettingsProfileVolumeKind) {
    DConnectSettingsProfileVolumeKindUnknown = -1,  /*!< 未定義値 */
    DConnectSettingsProfileVolumeKindAlarm = 1,     /*!< アラーム */
    DConnectSettingsProfileVolumeKindCall,          /*!< 通話音 */
    DConnectSettingsProfileVolumeKindRingtone,      /*!< 着信音 */
    DConnectSettingsProfileVolumeKindMail,          /*!< メール着信音 */
    DConnectSettingsProfileVolumeKindOther,         /*!< その他SNS等の着信音 */
    DConnectSettingsProfileVolumeKindMediaPlay,     /*!< メディアプレーヤーの音量 */
};

@class DConnectSettingsProfile;

/*!
 @protocol DConnectSettingsProfileDelegate
 @brief Settings Profileの各APIリクエスト通知用デリゲート。
 
 Settings Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectSettingsProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 
 @brief デバイス音量取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの音量取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Volume Settings API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] kind 種別
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            kind:(DConnectSettingsProfileVolumeKind)kind;

/*!
 
 @brief デバイスの日時取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの日時取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Date Settings API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetDateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 
 @brief デバイスのバックライト明度取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスのバックライト明度取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Display Light Settings API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 
 @brief デバイスの画面消灯設定取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの画面消灯設定取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Display Sleep Settings API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceiveGetSleepRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 
 @brief デバイスの音量設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの音量設定リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Volume Settings API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] kind 種別
 @param[in] level 音量
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            kind:(DConnectSettingsProfileVolumeKind)kind
           level:(NSNumber *)level;

/*!
 
 @brief デバイスの日時設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの日時設定リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Date Settings API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] date 日時
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutDateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            date:(NSString *)date;

/*!
 
 @brief デバイスのバックライト明度設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスのバックライト明度設定リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Display Light Settings API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] level 明度パーセント
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
           level:(NSNumber *)level;

/*!
 
 @brief デバイスの画面消灯設定リクエストを受け取ったことをデリゲートに通知する。
 
 profileがデバイスの画面消灯設定リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Display Sleep Settings API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] time 消灯するまでの時間(ミリ秒)
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSettingsProfile *)profile didReceivePutSleepRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            time:(NSNumber *)time;

@end

/*!
 @class DConnectSettingsProfile
 @brief Settingsプロファイル.
 
 Settings Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectSettingsProfile : DConnectProfile

/*!
 @brief DConnectSettingsProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectSettingsProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectSettingsProfileDelegate> delegate;

#pragma mark - Getter

/*!
 @brief リクエストから音量種別を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval DConnectSettingsProfileVolumeKindUnknown
 @retval DConnectSettingsProfileVolumeKindAlarm
 @retval DConnectSettingsProfileVolumeKindCall
 @retval DConnectSettingsProfileVolumeKindRingtone
 @retval DConnectSettingsProfileVolumeKindMail
 @retval DConnectSettingsProfileVolumeKindOther
 @retval DConnectSettingsProfileVolumeKindMediaPlay
 */
+ (DConnectSettingsProfileVolumeKind) volumeKindFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから音量を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 音量
 @retval nil 音量が指定されていない場合
 */
+ (NSNumber *) levelFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから日時(RFC3339)を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 日時文字列(RFC3339)
 @retval nil 日時が指定されていない場合
 */
+ (NSString *) dateFromRequest:(DConnectMessage *)request;

/*!
 @brief リクエストから消灯するまでの時間(ミリ秒)を取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval 消灯するまでの時間(ミリ秒)。
 @retval nil 消灯時間が指定されていない場合
 */
+ (NSNumber *) timeFromRequest:(DConnectMessage *)request;

#pragma mark - Setter

/*!
 @brief メッセージに音量を設定する。
 
 @param[in] level 音量パーセント(0〜1.0)
 @param[in,out] message 音量を格納するメッセージ
 */
+ (void) setVolumeLevel:(double)level target:(DConnectMessage *)message;

/*!
 @brief メッセージにバックライト明度を設定する。
 
 @param[in] level 明度パーセント(0〜1.0)
 @param[in,out] message バックライト明度を格納するメッセージ
 */
+ (void) setLightLevel:(double)level target:(DConnectMessage *)message;

/*!
 @brief メッセージに日時を設定する。
 
 @param[in] date 日時文字列(RFC3339)
 @param[in,out] message 日時を格納するメッセージ
 */
+ (void) setDate:(NSString *)date target:(DConnectMessage *)message;

/*!
 @brief メッセージに消灯するまでの時間(ミリ秒)を設定する。
 
 @param[in] time 消灯するまでの時間(ミリ秒)
 @param[in,out] message 消灯するまでの時間を格納するメッセージ
 */
+ (void) setTime:(int)time target:(DConnectMessage *)message;

@end
