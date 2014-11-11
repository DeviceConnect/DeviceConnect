//
//  DPPebbleDevicePlugin.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleDevicePlugin.h"
#import "DPPebbleNetworkServiceDiscoveryProfile.h"
#import "DPPebbleSystemProfile.h"
#import "DPPebbleBatteryProfile.h"
#import "DPPebbleFileProfile.h"
#import "DPPebbleVibrationProfile.h"
#import "DPPebbleSettingsProfile.h"
#import "DPPebbleDeviceOrientationProfile.h"
#import "DPPebbleNotificationProfile.h"
#import "PebbleViewController.h"


@interface DPPebbleDevicePlugin ()
@end


@implementation DPPebbleDevicePlugin

- (instancetype) init {
    self = [super init];
    if (self) {
        // プラグイン名を設定
        self.pluginName = [NSString stringWithFormat:@"Pebble 1.0"];
        
        // EventManagerの初期化
        Class key = [self class];
        [[DConnectEventManager sharedManagerForClass:key] setController:[DConnectDBCacheController controllerWithClass:key]];
        
        // Pebble管理クラスの初期化
        self.mgr = [DPPebbleManager new];
        
        // 各プロファイルの初期化
        DPPebbleNetworkServiceDiscoveryProfile *networkProfile =
        [[DPPebbleNetworkServiceDiscoveryProfile alloc] initWithPebbleManager:self.mgr];
        
        DPPebbleSystemProfile *systemProfile =
        [[DPPebbleSystemProfile alloc] initWithPebbleManager:self.mgr];
        
        
        DPPebbleBatteryProfile *batteryProfile =
        [[DPPebbleBatteryProfile alloc] initWithDevicePlugin:self];
        
        DPPebbleFileProfile *fileProfile =
        [[DPPebbleFileProfile alloc] initWithPebbleManager:self.mgr];
        
        DPPebbleSettingsProfile *settingsProfile =
        [[DPPebbleSettingsProfile alloc] initWithPebbleManager:self.mgr];
        
        DPPebbleVibrationProfile *vibrationProfile =
        [[DPPebbleVibrationProfile alloc] initWithPebbleManager:self.mgr];
        
        DPPebbleDeviceOrientationProfile *orientationProfile =
        [[DPPebbleDeviceOrientationProfile alloc] initWithDevicePlugin:self];
        
        DPPebbleNotificationProfile *notificationProfile =
        [[DPPebbleNotificationProfile alloc] initWithPebbleManager:self.mgr];
        
        // 各プロファイルの追加
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:batteryProfile];
        [self addProfile:fileProfile];
        [self addProfile:settingsProfile];
        [self addProfile:vibrationProfile];
        [self addProfile:orientationProfile];
        [self addProfile:notificationProfile];
    }
    return self;
}

@end
