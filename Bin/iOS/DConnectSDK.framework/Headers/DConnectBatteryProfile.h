//
//  DConnectBatteryProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Batteryプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectBatteryProfileName;

/*!
 @brief アトリビュート: charging。
 */
extern NSString *const DConnectBatteryProfileAttrCharging;

/*!
 @brief アトリビュート: chargingTime。
 */
extern NSString *const DConnectBatteryProfileAttrChargingTime;

/*!
 @brief アトリビュート: dischargingTime。
 */
extern NSString *const DConnectBatteryProfileAttrDischargingTime;

/*!
 @brief アトリビュート: level。
 */
extern NSString *const DConnectBatteryProfileAttrLevel;

/*!
 @brief アトリビュート: onchargingchange。
 */
extern NSString *const DConnectBatteryProfileAttrOnChargingChange;

/*!
 @brief アトリビュート: onbatterychange。
 */
extern NSString *const DConnectBatteryProfileAttrOnBatteryChange;

/*!
 @brief パラメータ: charing。
 */
extern NSString *const DConnectBatteryProfileParamCharging;

/*!
 @brief パラメータ: chargingTime。
 */
extern NSString *const DConnectBatteryProfileParamChargingTime;

/*!
 @brief パラメータ: dischargingTime。
 */
extern NSString *const DConnectBatteryProfileParamDischargingTime;

/*!
 @brief パラメータ: level。
 */
extern NSString *const DConnectBatteryProfileParamLevel;

/*!
 @brief パラメータ: battery。
 */
extern NSString *const DConnectBatteryProfileParamBattery;

@class DConnectBatteryProfile;

/*!
 @protocol DConnectBatteryProfileDelegate
 @brief Battery Profile各のAPIリクエスト通知用デリゲート。
 
 Battery Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectBatteryProfileDelegate<NSObject>

@optional
#pragma mark - Get Methods

/*!
 @brief 全アトリビュート取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileが全アトリビュート取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveGetAllRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 @brief level取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがlevel取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Level API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveGetLevelRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 @brief charging取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがcharging取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Charging API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveGetChargingRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 @brief chargingTime取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがchargingTime取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Charging Time API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveGetChargingTimeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 @brief dischargingTime取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがdischargingTime取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Discharging Time API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveGetDischargingTimeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

#pragma mark - Put Methods
#pragma mark Event Registration

/*!
 @brief onchargingchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonchargingchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Charging Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceivePutOnChargingChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onbatterychangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonbatterychangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods

/*!
 @brief onchargingchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonchargingchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Charging Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onbatterychangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonbatterychangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Battery Status Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectBatteryProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectBatteryProfile *)profile didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectBatteryProfile
 @brief Batteryプロファイル。
 
 Battery Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectBatteryProfile : DConnectProfile

/*!
 @brief BatteryProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectBatteryProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectBatteryProfileDelegate> delegate;

#pragma mark - Setters

/*!
 @brief メッセージにバッテリーレベルを設定する。
 
 バッテリーレベルは、0.0〜1.0の範囲になる。
 @par
 - 0.0の場合は残量なし。
 - 1.0の場合はフル充電。
 
 @param[in] level バッテリーレベル(0〜1.0)
 @param[in,out] message バッテリーレベルを格納するメッセージ
 */
+ (void) setLevel:(double)level target:(DConnectMessage *)message;

/*!
 @brief メッセージにバッテリー充電中フラグを設定する。
 @param[in] charging 充電中はYES、それ以外はNO
 @param[in,out] message バッテリー充電中フラグを格納するメッセージ
 */
+ (void) setCharging:(BOOL)charging target:(DConnectMessage *)message;

/*!
 @brief メッセージにバッテリーの充電時間を設定する。
 @param[in] chargingTime 充電時間(秒)
 @param[in,out] message バッテリー充電時間を格納するメッセージ
 */
+ (void) setChargingTime:(double)chargingTime target:(DConnectMessage *)message;

/*!
 @brief メッセージにバッテリーの放電時間を設定する。
 @param[in] dischargingTime 放電時間(秒)
 @param[in,out] message バッテリー法電磁間を格納するメッセージ
 */
+ (void) setDischargingTime:(double)dischargingTime target:(DConnectMessage *)message;

/*!
 @brief メッセージにバッテリー情報を設定する。
 @param[in] battery バッテリー情報
 @param[in,out] message バッテリー情報を格納するメッセージ
 */
+ (void) setBattery:(DConnectMessage *)battery target:(DConnectMessage *)message;

@end
