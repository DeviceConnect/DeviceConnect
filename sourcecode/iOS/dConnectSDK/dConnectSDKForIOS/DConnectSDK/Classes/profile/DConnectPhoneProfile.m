//
//  DConnectPhoneProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectPhoneProfile.h"

NSString *const DConnectPhoneProfileName = @"phone";
NSString *const DConnectPhoneProfileAttrCall = @"call";
NSString *const DConnectPhoneProfileAttrSet = @"set";
NSString *const DConnectPhoneProfileAttrOnConnect = @"onconnect";
NSString *const DConnectPhoneProfileParamPhoneNumber = @"phoneNumber";
NSString *const DConnectPhoneProfileParamMode = @"mode";
NSString *const DConnectPhoneProfileParamPhoneStatus = @"phoneStatus";
NSString *const DConnectPhoneProfileParamState = @"state";

@interface DConnectPhoneProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectPhoneProfile

#pragma mark - DConnectProfile Methods -

- (NSString *) profileName {
    return DConnectPhoneProfileName;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    
    if ([attribute isEqualToString:DConnectPhoneProfileAttrCall]) {
        if ([self hasMethod:@selector(profile:didReceivePostCallRequest:response:deviceId:phoneNumber:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePostCallRequest:request response:response
                             deviceId:[request deviceId]
                          phoneNumber:[DConnectPhoneProfile phoneNumberFromRequest:request]];
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
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    if ([attribute isEqualToString:DConnectPhoneProfileAttrSet]) {
        if ([self hasMethod:@selector(profile:didReceivePutSetRequest:response:deviceId:mode:)
                   response:response])
        {
            NSNumber *mode = [DConnectPhoneProfile modeFromRequest:request];
            send = [_delegate profile:self didReceivePutSetRequest:request response:response
                             deviceId:deviceId mode:mode];
        }
    } else if ([attribute isEqualToString:DConnectPhoneProfileAttrOnConnect]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnConnectRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            NSString *sessionKey = [request sessionKey];
            send = [_delegate profile:self didReceivePutOnConnectRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
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
    
    NSString *attribute = [request attribute];
    
    if ([attribute isEqualToString:DConnectPhoneProfileAttrOnConnect]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnConnectRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            NSString *deviceId = [request deviceId];
            NSString *sessionKey = [request sessionKey];
            send = [_delegate profile:self didReceiveDeleteOnConnectRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Getter

+ (NSString *) phoneNumberFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectPhoneProfileParamPhoneNumber];
}

+ (NSNumber *) modeFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectPhoneProfileParamMode];
}

#pragma mark - Setter

+ (void) setPhoneStatus:(DConnectMessage *)phoneStatus target:(DConnectMessage *)message {
    [message setMessage:phoneStatus forKey:DConnectPhoneProfileParamPhoneStatus];
}

+ (void) setState:(DConnectPhoneProfileCallState)state target:(DConnectMessage *)message {
    [message setInteger:state forKey:DConnectPhoneProfileParamState];
}

+ (void) setPhoneNumber:(NSString *)phoneNumber target:(DConnectMessage *)message {
    [message setString:phoneNumber forKey:DConnectPhoneProfileParamPhoneNumber];
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
