//
//  DeviceTestPlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DeviceTestPlugin.h"
#import "TestBatteryProfile.h"
#import "TestConnectProfile.h"
#import "TestNetworkServiceDiscoveryProfile.h"
#import "TestDeviceOrientationProfile.h"
#import "TestFileDescriptorProfile.h"
#import "TestFileProfile.h"
#import "TestMediaPlayerProfile.h"
#import "TestMediaStreamRecordingProfile.h"
#import "TestNotificationProfile.h"
#import "TestPhoneProfile.h"
#import "TestProximityProfile.h"
#import "TestSettingsProfile.h"
#import "TestSystemProfile.h"
#import "TestVibrationProfile.h"
#import "TestUniquePingProfile.h"
#import "TestUniqueTimeoutProfile.h"
#import "TestUniqueEventProfile.h"

@implementation DeviceTestPlugin

- (id) init {
    
    self = [super init];
    
    if (self) {
        self.useLocalOAuth = NO;
        
        [[DConnectEventManager sharedManagerForClass:[self class]]
         setController:[DConnectMemoryCacheController new]];

        [self addProfile:[[TestBatteryProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestConnectProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestDeviceOrientationProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestNetworkServiceDiscoveryProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestFileDescriptorProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestFileProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestMediaPlayerProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestMediaStreamRecordingProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestNotificationProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestPhoneProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestProximityProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[[TestSettingsProfile alloc] initWithDevicePlugin:self]];
        [self addProfile:[TestSystemProfile new]];
        [self addProfile:[TestVibrationProfile new]];
        
        [self addProfile:[TestUniquePingProfile new]];
        [self addProfile:[TestUniqueTimeoutProfile new]];
        [self addProfile:[[TestUniqueEventProfile alloc] initWithDevicePlugin:self]];
    }
    
    return self;
}

- (void) asyncSendEvent:(DConnectMessage *)event {
    [self asyncSendEvent:event delay:DEFAULT_EVENT_DELAY];
}

- (void) asyncSendEvent:(DConnectMessage *)event delay:(NSTimeInterval)delay {
    __block DeviceTestPlugin *_self = self;
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [NSThread sleepForTimeInterval:delay];
        [_self sendEvent:event];
    });

}

@end
