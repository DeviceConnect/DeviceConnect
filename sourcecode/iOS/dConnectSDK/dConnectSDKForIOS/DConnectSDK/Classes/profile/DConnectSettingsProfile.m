//
//  DConnectSettingsProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSettingsProfile.h"

NSString *const DConnectSettingsProfileName = @"settings";
NSString *const DConnectSettingsProfileInterfaceSound = @"sound";
NSString *const DConnectSettingsProfileInterfaceDisplay = @"display";
NSString *const DConnectSettingsProfileAttrVolume = @"volume";
NSString *const DConnectSettingsProfileAttrDate = @"date";
NSString *const DConnectSettingsProfileAttrLight = @"light";
NSString *const DConnectSettingsProfileAttrSleep = @"sleep";
NSString *const DConnectSettingsProfileParamKind = @"kind";
NSString *const DConnectSettingsProfileParamLevel = @"level";
NSString *const DConnectSettingsProfileParamDate = @"date";
NSString *const DConnectSettingsProfileParamTime = @"time";
const double DConnectSettingsProfileMaxLevel = 1.0;
const double DConnectSettingsProfileMinLevel = 0.0;

@interface DConnectSettingsProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;
+ (void) setLevel:(double)level target:(DConnectMessage *)message;

@end

@implementation DConnectSettingsProfile

- (NSString *) profileName {
    return DConnectSettingsProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if (interface) {
        if ([interface isEqualToString:DConnectSettingsProfileInterfaceSound] &&
            [attribute isEqualToString:DConnectSettingsProfileAttrVolume])
        {
            if ([self hasMethod:@selector(profile:didReceiveGetVolumeRequest:response:deviceId:kind:)
                       response:response])
            {
                send = [_delegate profile:self didReceiveGetVolumeRequest:request response:response
                                 deviceId:deviceId
                                     kind:[DConnectSettingsProfile volumeKindFromRequest:request]];
            }
        } else if ([interface isEqualToString:DConnectSettingsProfileInterfaceDisplay]) {
            if ([attribute isEqualToString:DConnectSettingsProfileAttrLight]) {
                if ([self hasMethod:@selector(profile:didReceiveGetLightRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveGetLightRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectSettingsProfileAttrSleep]) {
                if ([self hasMethod:@selector(profile:didReceiveGetSleepRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveGetSleepRequest:request
                                     response:response deviceId:deviceId];
                }
            } else {
                [response setErrorToUnknownAttribute];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else if ([attribute isEqualToString:DConnectSettingsProfileAttrDate]) {
        if ([self hasMethod:@selector(profile:didReceiveGetDateRequest:response:deviceId:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveGetDateRequest:request
                             response:response deviceId:deviceId];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if (interface) {
        if ([interface isEqualToString:DConnectSettingsProfileInterfaceSound] &&
            [attribute isEqualToString:DConnectSettingsProfileAttrVolume]) {
            
            if ([self hasMethod:@selector(profile:didReceivePutVolumeRequest:response:deviceId:kind:level:)
                       response:response])
            {
                send = [_delegate profile:self didReceivePutVolumeRequest:request response:response deviceId:deviceId
                                     kind:[DConnectSettingsProfile volumeKindFromRequest:request]
                                    level:[DConnectSettingsProfile levelFromRequest:request]];
            }
        } else if ([interface isEqualToString:DConnectSettingsProfileInterfaceDisplay]) {
            if ([attribute isEqualToString:DConnectSettingsProfileAttrLight]) {
                if ([self hasMethod:@selector(profile:didReceivePutLightRequest:response:deviceId:level:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutLightRequest:request response:response deviceId:deviceId
                                        level:[DConnectSettingsProfile levelFromRequest:request]];
                }
            } else if ([attribute isEqualToString:DConnectSettingsProfileAttrSleep]) {
                if ([self hasMethod:@selector(profile:didReceivePutSleepRequest:response:deviceId:time:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutSleepRequest:request response:response deviceId:deviceId
                                         time:[DConnectSettingsProfile timeFromRequest:request]];
                }
            } else {
                [response setErrorToUnknownAttribute];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else if ([attribute isEqualToString:DConnectSettingsProfileAttrDate]) {
        if ([self hasMethod:@selector(profile:didReceivePutDateRequest:response:deviceId:date:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutDateRequest:request response:response deviceId:deviceId
                                 date:[DConnectSettingsProfile dateFromRequest:request]];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Getter

+ (DConnectSettingsProfileVolumeKind) volumeKindFromRequest:(DConnectMessage *)request {
    int code = [request integerForKey:DConnectSettingsProfileParamKind];
    switch (code) {
        case DConnectSettingsProfileVolumeKindUnknown:
        case DConnectSettingsProfileVolumeKindAlarm:
        case DConnectSettingsProfileVolumeKindCall:
        case DConnectSettingsProfileVolumeKindMail:
        case DConnectSettingsProfileVolumeKindRingtone:
        case DConnectSettingsProfileVolumeKindOther:
            return code;
        default:
            return DConnectSettingsProfileVolumeKindUnknown;
    }
}

+ (NSString *) dateFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectSettingsProfileParamDate];
}

+ (NSNumber *) levelFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectSettingsProfileParamLevel];
}

+ (NSNumber *) timeFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectSettingsProfileParamTime];
}


#pragma mark - Setter

+ (void) setLevel:(double)level target:(DConnectMessage *)message {
    if (level < DConnectSettingsProfileMinLevel ||
        level > DConnectSettingsProfileMaxLevel) {
        @throw [NSString stringWithFormat:@"level must be between %f and %f.",
                DConnectSettingsProfileMinLevel,
                DConnectSettingsProfileMaxLevel];
    }
    [message setDouble:level forKey:DConnectSettingsProfileParamLevel];
}

+ (void) setVolumeLevel:(double)level target:(DConnectMessage *)message {
    [self setLevel:level target:message];
}

+ (void) setLightLevel:(double)level target:(DConnectMessage *)message {
    [self setLevel:level target:message];
}

+ (void) setDate:(NSString *)date target:(DConnectMessage *)message {
    [message setString:date forKey:DConnectSettingsProfileParamDate];
}

+ (void) setTime:(int)time target:(DConnectMessage *)message {
    [message setInteger:time forKey:DConnectSettingsProfileParamTime];
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
