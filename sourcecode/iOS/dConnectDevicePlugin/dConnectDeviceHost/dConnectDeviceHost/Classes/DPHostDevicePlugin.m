//
//  DPHostDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectFileManager.h>

#import "DPHostDevicePlugin.h"

#import "DPHostBatteryProfile.h"
#import "DPHostDeviceOrientationProfile.h"
#import "DPHostFileDescriptorProfile.h"
#import "DPHostFileProfile.h"
#import "DPHostMediaPlayerProfile.h"
#import "DPHostMediaStreamRecordingProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostNotificationProfile.h"
#import "DPHostPhoneProfile.h"
#import "DPHostProximityProfile.h"
#import "DPHostSettingsProfile.h"
#import "DPHostSystemProfile.h"
#import "DPHostVibrationProfile.h"
#import "DPHostConnectProfile.h"

@implementation DPHostDevicePlugin

+ (void) initialize {
    // イベントマネージャの準備
    Class clazz = [DPHostDevicePlugin class];
    DConnectEventManager *eventMgr =
    [DConnectEventManager sharedManagerForClass:clazz];
    [eventMgr setController:[DConnectMemoryCacheController new]];
}

- (id) init {
    self = [super init];
    if (self) {
        self.fileMgr = [DConnectFileManager fileManagerForPlugin:self];
        
        self.pluginName = @"Host 1.0.1";
        
        // プロファイルを追加
        [self addProfile:[DPHostBatteryProfile new]];
        [self addProfile:[DPHostDeviceOrientationProfile new]];
        [self addProfile:[[DPHostFileDescriptorProfile alloc] initWithFileManager:self.fileMgr]];
        [self addProfile:[DPHostFileProfile new]];
        [self addProfile:[DPHostMediaPlayerProfile new]];
        [self addProfile:[DPHostMediaStreamRecordingProfile new]];
        [self addProfile:[DPHostNetworkServiceDiscoveryProfile new]];
        [self addProfile:[DPHostNotificationProfile new]];
        [self addProfile:[DPHostPhoneProfile new]];
        [self addProfile:[DPHostProximityProfile new]];
        [self addProfile:[DPHostSettingsProfile new]];
        [self addProfile:[DPHostSystemProfile new]];
        [self addProfile:[DPHostVibrationProfile new]];
        [self addProfile:[DPHostConnectProfile new]];

    }
    return self;
}

- (NSString *) pathByAppendingPathComponent:(NSString *)pathComponent
{
    return [self.fileMgr.URL URLByAppendingPathComponent:pathComponent].standardizedURL.path;
}

@end
