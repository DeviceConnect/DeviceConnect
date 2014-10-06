//
//  DConnectSettingsProfile.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/06/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

/*! @file
 @brief Settingsプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */

#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: settings。 */
extern NSString *const DConnectSettingsProfileName;
/*! @brief インターフェース: sound。 */
extern NSString *const DConnectSettingsProfileInterfaceSound;
/*! @brief インターフェース: display。 */
extern NSString *const DConnectSettingsProfileInterfaceDisplay;
/*! @brief 属性: volume。 */
extern NSString *const DConnectSettingsProfileAttrVolume;
/*! @brief 属性: date。 */
extern NSString *const DConnectSettingsProfileAttrDate;
/*! @brief 属性: light。 */
extern NSString *const DConnectSettingsProfileAttrLight;
/*! @brief 属性: sleep。 */
extern NSString *const DConnectSettingsProfileAttrSleep;

/*! @brief パラメータ: kind。 */
extern NSString *const DConnectSettingsProfileParamKind;
/*! @brief パラメータ: level。 */
extern NSString *const DConnectSettingsProfileParamLevel;
/*! @brief パラメータ: date。 */
extern NSString *const DConnectSettingsProfileParamDate;
/*! @brief パラメータ: time。 */
extern NSString *const DConnectSettingsProfileParamTime;

/*! @brief ボリュームのレベルの最大値: 1.0。 */
extern const double DConnectSettingsProfileMaxLevel;
/*! @brief ボリュームのレベルの最小値: 0.0。 */
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
 @brief Settingsプロファイルのデリゲート。
 <p>
 スマートデバイスの各種設定状態の取得および設定機能を提供するAPI。<br/>
 スマートデバイスの各種設定状態の取得および設定機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DConnectSettingsProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief デバイスの音量取得リクエストハンドラー。
 
 デバイスの音量を提供し、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId kind:(DConnectSettingsProfileVolumeKind)kind;

/*!
 @brief デバイスの日時取得リクエストハンドラー。
 
 デバイスの日時を提供し、その結果をレスポンスパラメータに格納する。
 
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
 @brief デバイスのバックライト明度取得リクエストハンドラー。
 
 デバイスのバックライト明度を提供し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief デバイスの画面消灯設定取得リクエストハンドラー。
 
 デバイスの画面消灯設定を提供し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 @brief デバイスの音量設定リクエストハンドラー。
 
 デバイスの音量を設定し、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId kind:(DConnectSettingsProfileVolumeKind)kind
           level:(NSNumber *)level;

/*!
 @brief デバイスの日時設定リクエストハンドラー。
 
 デバイスの日時を設定し、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId date:(NSString *)date;

/*!
 @brief デバイスのバックライト明度設定リクエストハンドラー。
 
 デバイスのバックライト明度を設定し、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId level:(NSNumber *)level;

/*!
 @brief デバイスの画面消灯設定リクエストハンドラー。
 
 デバイスの画面消灯設定を設定し、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId time:(NSNumber *)time;

@end

/*!
 @class DConnectSettingsProfile
 @brief Settingsプロファイル.
 
 以下のメソッドを実装することで、Settingsプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 @li didReceiveGetVolumeRequest: response: deviceId: kind:
 @li didReceiveGetDateRequest: response: deviceId:
 @li didReceiveGetLightRequest: response: deviceId: kind: target:
 @li didReceivePutVolumeRequest: response: deviceId: kind: level:
 @li didReceivePutDateRequest: response: deviceId: date:
 @li didReceivePutLightRequest: response: deviceId: kind: target: level:
 */
@interface DConnectSettingsProfile : DConnectProfile

/*!
 @brief DConnectSettingsProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。

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
 @brief リクエストから日時を取得する。
 
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
 @brief dConnectメッセージに音量を設定する。
 
 @param[in] level 音量パーセント
 @param[in,out] message dConnectメッセージ
 */
+ (void) setVolumeLevel:(double)level target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにバックライト明度を設定する。
 
 @param[in] level 明度パーセント
 @param[in,out] message dConnectメッセージ
 */
+ (void) setLightLevel:(double)level target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに日時を設定する。
 
 @param[in] date 日時文字列(RFC3339)
 @param[in,out] message dConnectメッセージ
 */
+ (void) setDate:(NSString *)date target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに消灯するまでの時間(ミリ秒)を設定する。
 
 @param[in] time 消灯するまでの時間(ミリ秒)
 @param[in,out] message dConnectメッセージ
 */
+ (void) setTime:(int)time target:(DConnectMessage *)message;

@end
