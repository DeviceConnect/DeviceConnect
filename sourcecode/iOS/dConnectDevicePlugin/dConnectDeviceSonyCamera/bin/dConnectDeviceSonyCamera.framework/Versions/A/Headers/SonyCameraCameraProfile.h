//
//  SonyCameraCameraProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const SonyCameraCameraProfileName;

/*!
 @brief 属性: zoom。
 */
extern NSString *const SonyCameraCameraProfileAttrZoom;

/*!
 @brief 属性: direction。
 */
extern NSString *const SonyCameraCameraProfileParamDirection;

/*!
 @brief 属性: movement。
 */
extern NSString *const SonyCameraCameraProfileParamMovement;

/*!
 @brief 属性: zoomdiameter.
 */
extern NSString *const SonyCameraCameraProfileParamZoomdiameter;

/*!
 @class SonyCameraCameraProfile
 @brief カメラプロファイル.
 */
@class SonyCameraCameraProfile;

/*!
 @protocol SonyCameraCameraProfileDelegate
 @brief Sony Camera Camera Profile各APIリクエスト通知用デリゲート。
 */
@protocol SonyCameraCameraProfileDelegate<NSObject>

@optional
#pragma mark - Get Methods

/*!
 @brief ZOOM状態の取得.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 GET http://{dConnectドメイン}/camera/zoom?deviceId=xxxxx
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)         profile:(SonyCameraCameraProfile *)profile
didReceiveGetZoomRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 @brief ZOOMの実行.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{dConnectドメイン}/camera/zoom?deviceId=xxxxx
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] direction ズームインかアウトか
 @param[in] movement ズームをどの程度行うか
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */

- (BOOL)         profile:(SonyCameraCameraProfile *)profile
didReceivePutZoomRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
               direction:(NSString *)direction
                movement:(NSString *)movement;
@end

@interface SonyCameraCameraProfile : DConnectProfile

/*!
 @brief SonyCameraCameraProfileDelegateのデリゲートオブジェクト。
 
 デリゲートは @link SonyCameraCameraProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<SonyCameraCameraProfileDelegate> delegate;

@end
