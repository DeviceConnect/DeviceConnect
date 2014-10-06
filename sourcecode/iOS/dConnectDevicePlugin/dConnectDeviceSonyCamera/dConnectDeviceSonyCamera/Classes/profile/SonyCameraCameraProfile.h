//
//  SonyCameraCameraProfile.h
//  dConnectDeviceSonyCamera
//
//  Created by 小林伸郎 on 2014/07/29.
//  Copyright (c) 2014年 小林 伸郎. All rights reserved.
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
 @brief Zoom
 */
- (BOOL) profile:(SonyCameraCameraProfile *)profile didReceiveGetZoomRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 @brief Zoom
 */
- (BOOL) profile:(SonyCameraCameraProfile *)profile didReceivePutZoomRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId direction:(NSString *)direction movement:(NSString *)movement;
@end

@interface SonyCameraCameraProfile : DConnectProfile

/*!
 @brief SonyCameraCameraProfileDelegateのデリゲートオブジェクト。
 
 デリゲートは @link SonyCameraCameraProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<SonyCameraCameraProfileDelegate> delegate;

@end
