//
//  TestProximityProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestProximityProfile.h"
#import "DeviceTestPlugin.h"

@implementation TestProximityProfile

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


- (BOOL) profile:(DConnectProximityProfile *)profile didReceivePutOnDeviceProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectProximityProfileAttrOnDeviceProximity forKey:DConnectMessageAttribute];
        
        DConnectMessage *proximity = [DConnectMessage message];
        [DConnectProximityProfile setValue:0 target:proximity];
        [DConnectProximityProfile setMax:0 target:proximity];
        [DConnectProximityProfile setMin:0 target:proximity];
        [DConnectProximityProfile setThreshold:0 target:proximity];
        
        [DConnectProximityProfile setProximity:proximity target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectProximityProfile *)profile didReceivePutOnUserProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectProximityProfileAttrOnUserProximity forKey:DConnectMessageAttribute];
        
        DConnectMessage *proximity = [DConnectMessage message];
        [DConnectProximityProfile setNear:true target:proximity];
        
        [DConnectProximityProfile setProximity:proximity target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL) profile:(DConnectProximityProfile *)profile didReceiveDeleteOnDeviceProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }

    return YES;
}
- (BOOL) profile:(DConnectProximityProfile *)profile didReceiveDeleteOnUserProximityRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    return YES;
}

@end
