//
//  DConnectDeviceOrientationProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Device Orientationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectDeviceOrientationProfileName;

/*!
 @brief アトリビュート: ondeviceorientation。
 */
extern NSString *const DConnectDeviceOrientationProfileAttrOnDeviceOrientation;

/*!
 @brief パラメータ: orientation。
 */
extern NSString *const DConnectDeviceOrientationProfileParamOrientation;

/*!
 @brief パラメータ: acceleration。
 */
extern NSString *const DConnectDeviceOrientationProfileParamAcceleration;

/*!
 @brief パラメータ: x。
 */
extern NSString *const DConnectDeviceOrientationProfileParamX;

/*!
 @brief パラメータ: y。
 */
extern NSString *const DConnectDeviceOrientationProfileParamY;

/*!
 @brief パラメータ: z。
 */
extern NSString *const DConnectDeviceOrientationProfileParamZ;

/*!
 @brief パラメータ: rotationRate。
 */
extern NSString *const DConnectDeviceOrientationProfileParamRotationRate;

/*!
 @brief パラメータ: alpha。
 */
extern NSString *const DConnectDeviceOrientationProfileParamAlpha;

/*!
 @brief パラメータ: beta。
 */
extern NSString *const DConnectDeviceOrientationProfileParamBeta;

/*!
 @brief パラメータ: gamma。
 */
extern NSString *const DConnectDeviceOrientationProfileParamGamma;

/*!
 @brief パラメータ: interval。
 */
extern NSString *const DConnectDeviceOrientationProfileParamInterval;

/*!
 @brief パラメータ: accelerationIncludingGravity。
 */
extern NSString *const DConnectDeviceOrientationProfileParamAccelerationIncludingGravity;

@class DConnectDeviceOrientationProfile;

/*!
 @protocol DConnectDeviceOrientationProfileDelegate
 @brief Device Orientation Profileの各APIリクエスト通知用デリゲート。
 
 Device Orientation Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectDeviceOrientationProfileDelegate <NSObject>
@optional

#pragma mark - Put Methods
#pragma mark Event Registration

/*!
 @brief ondeviceorientationイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondeviceorientationイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Device Orientation Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectDeviceOrientationProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods
#pragma mark Event Unregistration

/*!
 @brief ondeviceorientationイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがondeviceorientationイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Device Orientation Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectDeviceOrientationProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

@end


/*!
 @class DConnectDeviceOrientationProfile
 @brief Device Orientationプロファイル。
 
 Device Orientation Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectDeviceOrientationProfile : DConnectProfile

/*!
 @brief DConnectDeviceOrientationProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectDeviceOrientationProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectDeviceOrientationProfileDelegate> delegate;

#pragma mark - Setters

/*!
 @brief メッセージに計測のインターバルを設定する。
 @param[in] interval 計測のインターバル(ミリ秒)
 @param[in,out] message インターバルを格納するメッセージ
 */
+ (void) setInterval:(long long)interval target:(DConnectMessage *)message;

/*!
 @brief メッセージにオリエンテーション情報を設定する。
 @param[in] orientation オリエンテーション情報
 @param[in,out] message オリエンテーション情報を格納するメッセージ
 */
+ (void) setOrientation:(DConnectMessage *)orientation target:(DConnectMessage *)message;

/*!
 @brief メッセージに加速度情報を設定する。
 @param[in] acceleration 加速度情報
 @param[in,out] message 加速度情報を格納するメッセージ
 */
+ (void) setAcceleration:(DConnectMessage *)acceleration target:(DConnectMessage *)message;

/*!
 @brief メッセージに重力込み加速度情報を設定する。
 @param[in] accelerationIncludingGravity 重力込み加速度情報
 @param[in,out] message 重力込み加速度情報を格納するメッセージ
 */
+ (void) setAccelerationIncludingGravity:(DConnectMessage *)accelerationIncludingGravity target:(DConnectMessage *)message;

/*!
 @brief メッセージに角速度情報を設定する。
 @param[in] rotationRate 角速度情報
 @param[in,out] message 角速度情報を格納するメッセージ
 */
+ (void) setRotationRate:(DConnectMessage *)rotationRate target:(DConnectMessage *)message;

/*!
 @brief メッセージにx軸方向の加速度、または重力加速度を設定する。
 @param[in] x x軸方向の加速度、または重力加速度
 @param[in,out] message x軸方向の加速度、または重力加速度を格納するメッセージ
 */
+ (void) setX:(double)x target:(DConnectMessage *)message;

/*!
 @brief メッセージにy軸方向の加速度、または重力加速度を設定する。
 @param[in] y y軸方向の加速度、または重力加速度
 @param[in,out] message y軸方向の加速度、または重力加速度を格納するメッセージ
 */
+ (void) setY:(double)y target:(DConnectMessage *)message;

/*!
 @brief メッセージにz軸方向の加速度、または重力加速度を設定する。
 @param[in] z z軸方向の加速度、または重力加速度
 @param[in,out] message z軸方向の加速度、または重力加速度を格納するメッセージ
 */
+ (void) setZ:(double)z target:(DConnectMessage *)message;

/*!
 @brief メッセージにz軸周り角速度を設定する。
 @param[in] alpha z軸周り角速度(degree/s)
 @param[in,out] message z軸周り角速度を格納するメッセージ
 */
+ (void) setAlpha:(double)alpha target:(DConnectMessage *)message;

/*!
 @brief メッセージにx軸周り角速度を設定する。
 @param[in] beta x軸周り角速度(degree/s)
 @param[in,out] message x軸周り角速度を格納するメッセージ
 */
+ (void) setBeta:(double)beta target:(DConnectMessage *)message;

/*!
 @brief メッセージにy軸周り角速度を設定する。
 @param[in] gamma y軸周り角速度(degree/s)
 @param[in,out] message y軸周り角速度を格納するメッセージ
 */
+ (void) setGamma:(double)gamma target:(DConnectMessage *)message;

@end
