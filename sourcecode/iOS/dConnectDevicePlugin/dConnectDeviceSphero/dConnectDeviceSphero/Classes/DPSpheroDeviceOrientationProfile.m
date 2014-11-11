//
//  DPSpheroDeviceOrientationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPSpheroDeviceOrientationProfile.h"
#import "DPSpheroDevicePlugin.h"
#import "DPSpheroManager.h"
#import "DPSpheroNetworkServiceDiscoveryProfile.h"

@interface DPSpheroDeviceOrientationProfile() <DPSpheroManagerOrientationDelegate>
@end
@implementation DPSpheroDeviceOrientationProfile

// 初期化
- (id)init {
    self = [super init];
    if (self) {
        self.delegate = self;
        [DPSpheroManager sharedManager].orientationDelegate = self;
    }
    return self;
}

// 共通リクエスト処理
- (void)handleRequest:(DConnectRequestMessage *)request
             response:(DConnectResponseMessage *)response
             isRemove:(BOOL)isRemove
             callback:(void(^)())callback
{
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPSpheroDevicePlugin class]];
    DConnectEventError error;
    if (isRemove) {
        error = [mgr removeEventForRequest:request];
    } else {
        error = [mgr addEventForRequest:request];
    }
    switch (error) {
        case DConnectEventErrorNone:
            callback();
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter:
            [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
            break;
        case DConnectEventErrorFailed:
        case DConnectEventErrorNotFound:
        default:
            [response setErrorToUnknown];
            break;
    }
}


#pragma mark - DPSpheroManagerOrientationDelegate

// Orientationのイベント処理
- (void)spheroManagerStreamingOrientation:(DPAttitude)attitude accel:(DPPoint3D)accel interval:(int)interval
{
    DConnectMessage *message = [DConnectMessage message];
    DConnectMessage *orientmsg = [DConnectMessage message];
    [DConnectDeviceOrientationProfile setAlpha:attitude.yaw target:orientmsg];
    [DConnectDeviceOrientationProfile setBeta:attitude.roll target:orientmsg];
    [DConnectDeviceOrientationProfile setGamma:attitude.pitch target:orientmsg];
    DConnectMessage *accelmsg = [DConnectMessage message];
    [DConnectDeviceOrientationProfile setX:accel.x * 9.81 target:accelmsg];
    [DConnectDeviceOrientationProfile setY:accel.y * 9.81 target:accelmsg];
    [DConnectDeviceOrientationProfile setZ:accel.z * 9.81 target:accelmsg];
    
    [DConnectDeviceOrientationProfile setAcceleration:accelmsg target:message];
    [DConnectDeviceOrientationProfile setRotationRate:orientmsg target:message];
    [DConnectDeviceOrientationProfile setInterval:interval target:message];
    
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPSpheroDevicePlugin class]];
    NSArray *events  = [mgr eventListForDeviceId:[DPSpheroManager sharedManager].currentDeviceID profile:DConnectDeviceOrientationProfileName attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
    if (events == 0) {
        [[DPSpheroManager sharedManager] stopSensorOrientation];
    }
    for (DConnectEvent *msg in events) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:msg];
        [DConnectDeviceOrientationProfile setOrientation:message target:eventMsg];
        DConnectDevicePlugin *plugin = (DConnectDevicePlugin *)self.provider;
        [plugin sendEvent:eventMsg];
    }
}


#pragma mark - DConnectDeviceOrientationProfileDelegate

// Orientationのイベント登録
- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:NO callback:^{
        [[DPSpheroManager sharedManager] startSensorOrientation];
    }];
    return YES;
}

// Orientationのイベント解除
- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:YES callback:^{
        [[DPSpheroManager sharedManager] stopSensorOrientation];
    }];
    return YES;
}

@end
