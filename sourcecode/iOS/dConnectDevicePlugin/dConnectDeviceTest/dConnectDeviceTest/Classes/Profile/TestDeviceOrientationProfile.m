//
//  TestDeviceOrientationProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestDeviceOrientationProfile.h"
#import "DeviceTestPlugin.h"

@implementation TestDeviceOrientationProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}


#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:deviceId forKey:DConnectMessageDeviceId];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectDeviceOrientationProfileName
                  forKey:DConnectMessageAttribute];
        
        DConnectMessage *orientation = [DConnectMessage message];
        
        DConnectMessage *a1 = [DConnectMessage message];
        [DConnectDeviceOrientationProfile setX:0 target:a1];
        [DConnectDeviceOrientationProfile setY:0 target:a1];
        [DConnectDeviceOrientationProfile setZ:0 target:a1];
        
        DConnectMessage *a2 = [DConnectMessage message];
        [DConnectDeviceOrientationProfile setX:0 target:a2];
        [DConnectDeviceOrientationProfile setY:0 target:a2];
        [DConnectDeviceOrientationProfile setZ:0 target:a2];

        DConnectMessage *r = [DConnectMessage message];
        [DConnectDeviceOrientationProfile setAlpha:0 target:r];
        [DConnectDeviceOrientationProfile setBeta:0 target:r];
        [DConnectDeviceOrientationProfile setGamma:0 target:r];
        
        [DConnectDeviceOrientationProfile setAcceleration:a1 target:orientation];
        [DConnectDeviceOrientationProfile setAccelerationIncludingGravity:a2 target:orientation];
        [DConnectDeviceOrientationProfile setRotationRate:r target:orientation];
        [DConnectDeviceOrientationProfile setInterval:0 target:orientation];
        
        [DConnectDeviceOrientationProfile setOrientation:orientation target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL) profile:(DConnectDeviceOrientationProfile *)profile didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}


@end
