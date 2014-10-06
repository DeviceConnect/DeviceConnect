//
//  DConnectProximityProfile.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/06/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

/*! @file
 @brief Proximityプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: proximity。 */
extern NSString *const DConnectProximityProfileName;
/*! @brief 属性: ondeviceproximity。 */
extern NSString *const DConnectProximityProfileAttrOnDeviceProximity;
/*! @brief 属性: onuserproximity。 */
extern NSString *const DConnectProximityProfileAttrOnUserProximity;
/*! @brief パラメータ: value。 */
extern NSString *const DConnectProximityProfileParamValue;
/*! @brief パラメータ: min。 */
extern NSString *const DConnectProximityProfileParamMin;
/*! @brief パラメータ: max。 */
extern NSString *const DConnectProximityProfileParamMax;
/*! @brief パラメータ: threshold。 */
extern NSString *const DConnectProximityProfileParamThreshold;
/*! @brief パラメータ: proximity。 */
extern NSString *const DConnectProximityProfileParamProximity;
/*! @brief パラメータ: near。 */
extern NSString *const DConnectProximityProfileParamNear;

@class DConnectProximityProfile;

/*!
 @brief Proximity プロファイル。
 <p>
 スマートデバイスの近接センサーの検知通知を提供するAPI.<br/>
 スマートデバイスの近接センサーの検知通知を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DConnectProximityProfileDelegate <NSObject>
@optional

#pragma mark - Put Methods
#pragma mark Event Registration

/*!
 @brief ondeviceproximityコールバック登録リクエストハンドラー。
 
 ondeviceproximityコールバックを登録し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onuserproximityコールバック登録リクエストハンドラー。
 
 onuserproximityコールバックを登録し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregistration

/*!
 @brief ondeviceproximityコールバック解除リクエストハンドラー。
 
 ondeviceproximityコールバックを解除し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onuserproximityコールバック解除リクエストハンドラー。
 
 onuserproximityコールバックを解除し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectProximityProfile
 @brief Proximityプロファイル.
 
 以下のメソッドを実装することで、Proximityプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 @li didReceivePutOnDeviceProximityRequest: response: deviceId: sessionKey:
 @li didReceivePutOnUserProximityRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnDeviceProximityRequest: response: deviceId: sessionKey:
 @li didReceiveDeleteOnUserProximityRequest: response: deviceId: sessionKey:
 */
@interface DConnectProximityProfile : DConnectProfile

/*!
 @brief DConnectProximityProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectProximityProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief dConnectメッセージに近接距離を設定する。
 
 @param[in] value 近接距離
 @param[in,out] message dConnectメッセージ
 */
+ (void) setValue:(double)value target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに近接距離の最小値を設定する.
 
 @param[in] min 最小値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMin:(double)min target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに近接距離の最大値を設定する.
 
 @param[in] max 最大値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setMax:(double)max target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに近接距離の閾値を設定する.
 
 @param[in] threshold 閾値
 @param[in,out] message dConnectメッセージ
 */
+ (void) setThreshold:(double)threshold target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに近接センサー情報を設定する.
 
 @param[in] proximity 近接センサー情報
 @param[in,out] message dConnectメッセージ
 */
+ (void) setProximity:(DConnectMessage *)proximity target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージに近接センサー情報を設定する.
 
 @param[in] near 近接の有無。YESの場合近接中、その他はNO
 @param[in,out] message dConnectメッセージ
 */
+ (void) setNear:(BOOL)near target:(DConnectMessage *)message;

@end
