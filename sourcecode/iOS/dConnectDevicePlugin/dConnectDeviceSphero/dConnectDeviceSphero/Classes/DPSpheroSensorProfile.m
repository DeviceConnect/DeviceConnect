//
//  DPSpheroProfile.m
//  dConnectDeviceSphero
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPSpheroSensorProfile.h"
#import "DPSpheroDevicePlugin.h"
#import "DPSpheroManager.h"
#import "DPSpheroNetworkServiceDiscoveryProfile.h"

@interface DPSpheroSensorProfile() <DPSpheroManagerSensorDelegate>
@end

@implementation DPSpheroSensorProfile

// 初期化
- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        [DPSpheroManager sharedManager].sensorDelegate = self;
    }
    return self;
}

// 共通メッセージ送信
- (void)sendMessage:(DConnectMessage*)message
          interface:(NSString *)interface
          attribute:(NSString *)attribute
              param:(NSString*)param
{
    DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPSpheroDevicePlugin class]];
    NSArray *events  = [mgr eventListForDeviceId:[DPSpheroManager sharedManager].currentDeviceID
                                         profile:DPSpheroProfileName
                                       interface:interface
                                       attribute:attribute];
    if (events == 0) {
        if ([interface isEqualToString:DPSpheroProfileInterfaceQuaternion]) {
            [[DPSpheroManager sharedManager] stopSensorQuaternion];
        } else if ([interface isEqualToString:DPSpheroProfileInterfaceLocator]) {
            [[DPSpheroManager sharedManager] stopSensorLocator];
        } else if ([interface isEqualToString:DPSpheroProfileInterfaceCollision]) {
            [[DPSpheroManager sharedManager] stopSensorCollision];
        }
    }
    for (DConnectEvent *event in events) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:event];
        [eventMsg setMessage:message forKey:param];
        DConnectDevicePlugin *plugin = (DConnectDevicePlugin *)self.provider;
        [plugin sendEvent:eventMsg];
    }
   
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


#pragma mark - DPSpheroManagerSensorDelegate

// Quaternionのイベント処理
- (void)spheroManagerStreamingQuaternion:(DPQuaternion)quaternion interval:(int)interval;
{
    DConnectMessage *msg = [DConnectMessage message];
    [msg setDouble:quaternion.q0 forKey:DPSpheroProfileParamQ0];
    [msg setDouble:quaternion.q1 forKey:DPSpheroProfileParamQ1];
    [msg setDouble:quaternion.q2 forKey:DPSpheroProfileParamQ2];
    [msg setDouble:quaternion.q3 forKey:DPSpheroProfileParamQ3];
    [msg setInteger:interval forKey:DPSpheroProfileParamInterval];
    [self sendMessage:msg
            interface:DPSpheroProfileInterfaceQuaternion
            attribute:DPSpheroProfileAttrOnQuaternion
                param:DPSpheroProfileParamQuaternion];
}

// Locatorのイベント処理
- (void)spheroManagerStreamingLocatorPos:(CGPoint)pos velocity:(CGPoint)velocity interval:(int)interval
{
    DConnectMessage *msg = [DConnectMessage message];
    [msg setDouble:pos.x forKey:DPSpheroProfileParamPositionX];
    [msg setDouble:pos.y forKey:DPSpheroProfileParamPositionY];
    [msg setDouble:velocity.x forKey:DPSpheroProfileParamVelocityX];
    [msg setDouble:velocity.y forKey:DPSpheroProfileParamVelocityY];
    [msg setInteger:interval forKey:DPSpheroProfileParamInterval];
    [self sendMessage:msg
            interface:DPSpheroProfileInterfaceLocator
            attribute:DPSpheroProfileAttrOnLocator
                param:DPSpheroProfileParamLocator];
}

// Collisionのイベント処理
- (void)spheroManagerStreamingCollisionImpactAcceleration:(DPPoint3D)accel axis:(CGPoint)axis power:(CGPoint)power speed:(float)speed time:(NSTimeInterval)time
{
    DConnectMessage *msg = [DConnectMessage message];
    DConnectMessage *impactAcceleration = [DConnectMessage message];
    [impactAcceleration setDouble:accel.x forKey:DPSpheroProfileParamX];
    [impactAcceleration setDouble:accel.y forKey:DPSpheroProfileParamY];
    [impactAcceleration setDouble:accel.z forKey:DPSpheroProfileParamZ];
    [msg setMessage:impactAcceleration forKey:DPSpheroProfileParamImpactAcceleration];
    
    DConnectMessage *impactAxis = [DConnectMessage message];
    [impactAxis setBool:axis.x forKey:DPSpheroProfileParamX];
    [impactAxis setBool:axis.y forKey:DPSpheroProfileParamY];
    [msg setMessage:impactAxis forKey:DPSpheroProfileParamImpactAxis];
    
    DConnectMessage *impactPower = [DConnectMessage message];
    [impactPower setDouble:power.x forKey:DPSpheroProfileParamX];
    [impactPower setDouble:power.y forKey:DPSpheroProfileParamY];
    [msg setMessage:impactPower forKey:DPSpheroProfileParamImpactPower];
    [msg setDouble:speed forKey:DPSpheroProfileParamImpactSpeed];
    [msg setDouble:time forKey:DPSpheroProfileParamImpactTimestamp];
    
    [self sendMessage:msg
            interface:DPSpheroProfileInterfaceCollision
            attribute:DPSpheroProfileAttrOnCollision
                param:DPSpheroProfileParamCollision];
}


#pragma mark - Quaternion

// Quaternionのイベントを登録
- (BOOL) profile:(DPSpheroProfile *)profile didReceivePutOnQuaternionRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:NO callback:^{
        [[DPSpheroManager sharedManager] startSensorQuaternion];
    }];
    return YES;
}

// Quaternionのイベント登録を解除
- (BOOL) profile:(DPSpheroProfile *)profile didReceiveDeleteOnQuaternionRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:YES callback:^{
        [[DPSpheroManager sharedManager] stopSensorQuaternion];
    }];
    return YES;
}


#pragma mark - Locator

// Locatorのイベントを登録
- (BOOL) profile:(DPSpheroProfile *)profile didReceivePutOnLocatorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();

    [self handleRequest:request response:response isRemove:NO callback:^{
        [[DPSpheroManager sharedManager] startSensorLocator];
    }];
    return YES;
}

// Locatorのイベント登録を解除
- (BOOL) profile:(DPSpheroProfile *)profile didReceiveDeleteOnLocatorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:YES callback:^{
        [[DPSpheroManager sharedManager] stopSensorLocator];
    }];
    return YES;
}


#pragma mark - Collision

// Collisionのイベントを登録
- (BOOL) profile:(DPSpheroProfile *)profile didReceivePutOnCollisionRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:NO callback:^{
        [[DPSpheroManager sharedManager] startSensorCollision];
    }];
    return YES;
}

// Collisionのイベント登録を解除
- (BOOL) profile:(DPSpheroProfile *)profile didReceiveDeleteOnCollisionRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    // 接続確認
    CONNECT_CHECK();
    
    [self handleRequest:request response:response isRemove:YES callback:^{
        [[DPSpheroManager sharedManager] stopSensorCollision];
    }];
    return YES;
}

@end
