//
//  DConnectBatteryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectBatteryProfile.h"

// Profile Name
NSString *const DConnectBatteryProfileName = @"battery";

// Atttribute
NSString *const DConnectBatteryProfileAttrCharging         = @"charging";
NSString *const DConnectBatteryProfileAttrChargingTime     = @"chargingTime";
NSString *const DConnectBatteryProfileAttrDischargingTime  = @"dischargingTime";
NSString *const DConnectBatteryProfileAttrLevel            = @"level";
NSString *const DConnectBatteryProfileAttrOnChargingChange = @"onchargingchange";
NSString *const DConnectBatteryProfileAttrOnBatteryChange  = @"onbatterychange";

// Parameter
NSString *const DConnectBatteryProfileParamCharging        = @"charging";
NSString *const DConnectBatteryProfileParamChargingTime    = @"chargingTime";
NSString *const DConnectBatteryProfileParamDischargingTime = @"dischargingTime";
NSString *const DConnectBatteryProfileParamLevel           = @"level";
NSString *const DConnectBatteryProfileParamBattery         = @"battery";


@interface DConnectBatteryProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectBatteryProfile

#pragma mark - DConnectProfile Methods

- (NSString *) profileName {
    return DConnectBatteryProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *attribute = [request attribute];
    
    if (attribute) {
        if ([attribute isEqualToString:DConnectBatteryProfileAttrLevel]) {
            if ([self hasMethod:@selector(profile:didReceiveGetLevelRequest:response:deviceId:) response:response])
            {
                send = [_delegate profile:self didReceiveGetLevelRequest:request
                                 response:response deviceId:deviceId];
            }
        } else if ([attribute isEqualToString:DConnectBatteryProfileAttrCharging]) {
            if ([self hasMethod:@selector(profile:didReceiveGetChargingRequest:response:deviceId:) response:response])
            {
                send = [_delegate profile:self didReceiveGetChargingRequest:request
                                 response:response deviceId:deviceId];
            }
        } else if ([attribute isEqualToString:DConnectBatteryProfileAttrChargingTime]) {
            if ([self hasMethod:@selector(profile:didReceiveGetChargingTimeRequest:response:deviceId:) response:response])
            {
                send = [_delegate profile:self didReceiveGetChargingTimeRequest:request
                                 response:response deviceId:deviceId];
            }
        } else if ([attribute isEqualToString:DConnectBatteryProfileAttrDischargingTime]) {
            if ([self hasMethod:@selector(profile:didReceiveGetDischargingTimeRequest:response:deviceId:) response:response])
            {
                send = [_delegate profile:self didReceiveGetDischargingTimeRequest:request
                                 response:response deviceId:deviceId];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else if ([self hasMethod:@selector(profile:didReceiveGetAllRequest:response:deviceId:) response:response])
    {
        // attributeが存在しない場合には全属性を取得する
        send = [_delegate profile:self didReceiveGetAllRequest:request response:response deviceId:deviceId];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *sessionKey = [request sessionKey];
    NSString *attribute = [request attribute];
    
    if (attribute) {
        if ([attribute isEqualToString:DConnectBatteryProfileAttrOnChargingChange]) {
            
            if ([self hasMethod:@selector(profile:didReceivePutOnChargingChangeRequest:response:deviceId:sessionKey:)
                       response:response])
            {
                send = [_delegate profile:self didReceivePutOnChargingChangeRequest:request response:response
                                 deviceId:deviceId sessionKey:sessionKey];
            }
            
        } else if ([attribute isEqualToString:DConnectBatteryProfileAttrOnBatteryChange]) {
            
            if ([self hasMethod:@selector(profile:didReceivePutOnBatteryChangeRequest:response:deviceId:sessionKey:)
                       response:response])
            {
                send = [_delegate profile:self didReceivePutOnBatteryChangeRequest:request response:response
                                 deviceId:deviceId sessionKey:sessionKey];
            }
            
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *sessionKey = [request sessionKey];
    NSString *attribute = [request attribute];
    
    if ([DConnectBatteryProfileAttrOnChargingChange isEqualToString:attribute]) {
        
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnChargingChangeRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnChargingChangeRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
        
    } else if ([DConnectBatteryProfileAttrOnBatteryChange isEqualToString:attribute]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnBatteryChangeRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnBatteryChangeRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    return send;
}

#pragma mark - Setter

+ (void) setLevel:(double)level target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Response must not be nil.";
    } else if (level < 0 || level > 1.0f) {
        @throw @"Level must be between 0 and 1.0.";
    } else {
        [message setFloat:level forKey:DConnectBatteryProfileParamLevel];
    }
    
}

+ (void) setCharging:(BOOL)charging target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Response must not be nil.";
    } else {
        [message setBool:charging forKey:DConnectBatteryProfileParamCharging];
    }
}

+ (void) setChargingTime:(double)chargingTime target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Response must not be nil.";
    } else {
        [message setInteger:chargingTime forKey:DConnectBatteryProfileParamChargingTime];
    }
}

+ (void) setDischargingTime:(double)dischargingTime target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Response must not be nil.";
    } else {
        [message setInteger:dischargingTime forKey:DConnectBatteryProfileParamDischargingTime];
    }
}

+ (void) setBattery:(DConnectMessage *)battery target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Message must not be nil.";
    } else if (!battery) {
        @throw @"Battery must not be nil.";
    } else {
        [message setMessage:battery forKey:DConnectBatteryProfileParamBattery];
    }
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
