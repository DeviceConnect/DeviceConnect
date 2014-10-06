//
//  DPSpheroDevicePlugin.m
//  DPSpheroDevicePlugin
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPSpheroDevicePlugin.h"
#import "DPSpheroNetworkServiceDiscoveryProfile.h"
#import "DPSpheroSensorProfile.h"
#import "DPSpheroSystemProfile.h"
#import "DPSpheroDriveControllerProfile.h"
#import "DPSpheroLightProfile.h"
#import "DPSpheroDeviceOrientationProfile.h"
#import "DPSpheroManager.h"

@interface DPSpheroDevicePlugin()
@end

@implementation DPSpheroDevicePlugin

// 初期化
- (id) init
{
    self = [super init];
    
    if (self) {
        self.pluginName = @"Sphero 1.0";
        //@ToDo リリース時はコメントアウトすること
//        self.useLocalOAuth = NO;

        Class key = [self class];
        [[DConnectEventManager sharedManagerForClass:key] setController:[DConnectDBCacheController controllerWithClass:key]];

        // Network Service Discovery Profileの追加
        DPSpheroNetworkServiceDiscoveryProfile *networkProfile = [DPSpheroNetworkServiceDiscoveryProfile new];
        
        // System Profileの追加
        DPSpheroSystemProfile *systemProfile = [DPSpheroSystemProfile new];
        // Sphero Profileの追加
        DPSpheroSensorProfile *spheroProfile = [DPSpheroSensorProfile new];
        DPSpheroDriveControllerProfile *DCMDriveControllerProfile = [DPSpheroDriveControllerProfile new];
        DPSpheroLightProfile *DCMLightProfile = [DPSpheroLightProfile new];
        DPSpheroDeviceOrientationProfile *deviceorientationProfile = [DPSpheroDeviceOrientationProfile new];
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:spheroProfile];
        [self addProfile:DCMDriveControllerProfile];
        [self addProfile:DCMLightProfile];
        [self addProfile:deviceorientationProfile];
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
            UIApplication *application = [UIApplication sharedApplication];
            
            [nc addObserver:_self selector:@selector(enterForeground)
                       name:UIApplicationWillEnterForegroundNotification
                     object:application];
            
            [nc addObserver:_self selector:@selector(enterBackground)
                       name:UIApplicationDidEnterBackgroundNotification
                     object:application];
        });
    }
    
    return self;
}
- (void)enterBackground {
    [[DPSpheroManager sharedManager] applicationDidEnterBackground];
}

- (void)enterForeground {
    [[DPSpheroManager sharedManager] applicationWillEnterForeground];
}
- (void) dealloc {
    
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    UIApplication *application = [UIApplication sharedApplication];
    
    [nc removeObserver:self name:UIApplicationDidBecomeActiveNotification object:application];
    [nc removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:application];
}
@end
