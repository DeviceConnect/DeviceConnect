//
//  SonyCameraFlashProfile.h
//  dConnectDeviceSonyCamera
//
//  Created by 小林伸郎 on 2014/07/29.
//  Copyright (c) 2014年 小林 伸郎. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@class SonyCameraFlashProfile;

@protocol SonyCameraCameraProfileDelegate<NSObject>

@optional
#pragma mark - Put Methods

/*!
 @brief Zoom
 */
- (BOOL) profile:(SonyCameraFlashProfile *)profile didReceivePutZoomRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId direction:(NSString *)direction movement:(NSString *)movement;
@end


@interface SonyCameraFlashProfile : DConnectProfile

@end
