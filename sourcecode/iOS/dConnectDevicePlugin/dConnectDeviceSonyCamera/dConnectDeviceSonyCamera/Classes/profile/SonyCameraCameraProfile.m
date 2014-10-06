//
//  SonyCameraCameraProfile.m
//  dConnectDeviceSonyCamera
//
//  Created by 小林伸郎 on 2014/07/29.
//  Copyright (c) 2014年 小林 伸郎. All rights reserved.
//

#import "SonyCameraCameraProfile.h"

NSString *const SonyCameraCameraProfileName = @"camera";
NSString *const SonyCameraCameraProfileAttrZoom = @"zoom";
NSString *const SonyCameraCameraProfileParamDirection = @"direction";
NSString *const SonyCameraCameraProfileParamMovement = @"movement";
NSString *const SonyCameraCameraProfileParamZoomdiameter = @"zoomdiameter";

@interface SonyCameraCameraProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation SonyCameraCameraProfile

- (NSString *) profileName {
    return SonyCameraCameraProfileName;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    NSString *deviceId = [request deviceId];
    NSString *attribute = [request attribute];
    
    if (attribute) {
        if ([attribute isEqualToString:SonyCameraCameraProfileAttrZoom]) {
            NSString *direction = [request stringForKey:SonyCameraCameraProfileParamDirection];
            NSString *movement = [request stringForKey:SonyCameraCameraProfileParamMovement];
            if ([self hasMethod:@selector(profile:didReceivePutZoomRequest:response:deviceId:direction:movement:) response:response]) {
                send = [_delegate profile:self didReceivePutZoomRequest:request response:response deviceId:deviceId direction:direction movement:movement];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    NSString *deviceId = [request deviceId];
    NSString *attribute = [request attribute];
    
    if (attribute) {
        if ([attribute isEqualToString:SonyCameraCameraProfileAttrZoom]) {
            if ([self hasMethod:@selector(profile:didReceiveGetZoomRequest:response:deviceId:) response:response]) {
                send = [_delegate profile:self didReceiveGetZoomRequest:request response:response deviceId:deviceId];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

@end
