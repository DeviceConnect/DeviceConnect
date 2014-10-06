//
//  DConnectProximityProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Proximityプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: proximity。
 */
extern NSString *const DConnectProximityProfileName;

/*!
 @brief アトリビュート: ondeviceproximity。
 */

extern NSString *const DConnectProximityProfileAttrOnDeviceProximity;

/*!
 @brief アトリビュート: onuserproximity。
 */
extern NSString *const DConnectProximityProfileAttrOnUserProximity;

/*!
 @brief パラメータ: value。
 */
extern NSString *const DConnectProximityProfileParamValue;

/*!
 @brief パラメータ: min。
 */
extern NSString *const DConnectProximityProfileParamMin;

/*!
 @brief パラメータ: max。
 */
extern NSString *const DConnectProximityProfileParamMax;

/*!
 @brief パラメータ: threshold。
 */
extern NSString *const DConnectProximityProfileParamThreshold;

/*!
 @brief パラメータ: proximity。
 */
extern NSString *const DConnectProximityProfileParamProximity;

/*!
 @brief パラメータ: near。
 */
extern NSString *const DConnectProximityProfileParamNear;

@class DConnectProximityProfile;

/*!
 @protocol DConnectProximityProfileDelegate
 @brief Proximity Profileの各APIリクエスト通知用デリゲート。
 
 Proximity Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectProximityProfileDelegate <NSObject>
@optional

#pragma mark - Put Methods
#pragma mark Event Registration

/*!
 
 @brief ondeviceproximityイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondeviceproximityイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Proximity Device Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectProximityProfile *)profile didReceivePutOnDeviceProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onuserproximityイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonuserproximityイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Proximity User Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectProximityProfile *)profile didReceivePutOnUserProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregistration

/*!
 
 @brief ondeviceproximityイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondeviceproximityイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Proximity Device Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectProximityProfile *)profile didReceiveDeleteOnDeviceProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 
 @brief onuserproximityイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonuserproximityイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Proximity User Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectProximityProfile *)profile didReceiveDeleteOnUserProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectProximityProfile
 @brief Proximityプロファイル。
 
 Proximity Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectProximityProfile : DConnectProfile


/*!
 @brief DConnectProximityProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectProximityProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectProximityProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージに近接距離を設定する。
 
 @param[in] value 近接距離
 @param[in,out] message 近接距離を格納するメッセージ
 */
+ (void) setValue:(double)value target:(DConnectMessage *)message;

/*!
 @brief メッセージに近接距離の最小値を設定する。
 
 @param[in] min 最小値
 @param[in,out] message 近接距離の最小値を格納するメッセージ
 */
+ (void) setMin:(double)min target:(DConnectMessage *)message;

/*!
 @brief メッセージに近接距離の最大値を設定する。
 
 @param[in] max 最大値
 @param[in,out] message 近接距離の最大値を格納するメッセージ
 */
+ (void) setMax:(double)max target:(DConnectMessage *)message;

/*!
 @brief メッセージに近接距離の閾値を設定する。
 
 @param[in] threshold 閾値
 @param[in,out] message 近接距離の閾値を格納するメッセージ
 */
+ (void) setThreshold:(double)threshold target:(DConnectMessage *)message;

/*!
 @brief メッセージに近接センサー情報を設定する。
 
 @param[in] proximity 近接センサー情報
 @param[in,out] message 近接センサー情報を格納するメッセージ
 */
+ (void) setProximity:(DConnectMessage *)proximity target:(DConnectMessage *)message;

/*!
 @brief メッセージに近接センサー情報を設定する。
 
 @param[in] near 近接の有無。YESの場合近接中、その他はNO
 @param[in,out] message 近接センサー情報を格納するメッセージ
 */
+ (void) setNear:(BOOL)near target:(DConnectMessage *)message;

@end
