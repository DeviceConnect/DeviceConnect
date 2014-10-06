
//
//  NotificationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectNotificationProfile.h"

NSString *const DConnectNotificationProfileName = @"notification";
NSString *const DConnectNotificationProfileAttrNotify = @"notify";
NSString *const DConnectNotificationProfileAttrOnClick = @"onclick";
NSString *const DConnectNotificationProfileAttrOnShow = @"onshow";
NSString *const DConnectNotificationProfileAttrOnClose = @"onclose";
NSString *const DConnectNotificationProfileAttrOnError = @"onerror";
NSString *const DConnectNotificationProfileParamBody = @"body";
NSString *const DConnectNotificationProfileParamType = @"type";
NSString *const DConnectNotificationProfileParamDir = @"dir";
NSString *const DConnectNotificationProfileParamLang = @"lang";
NSString *const DConnectNotificationProfileParamTag = @"tag";
NSString *const DConnectNotificationProfileParamIcon = @"icon";
NSString *const DConnectNotificationProfileParamNotificationId = @"notificationId";
NSString *const DConnectNotificationProfileParamUri = @"uri";

@interface DConnectNotificationProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectNotificationProfile

#pragma mark - DConnectProfile Methods

- (NSString *) profileName {
    return DConnectNotificationProfileName;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    if ([attribute isEqualToString:DConnectNotificationProfileAttrNotify]) {
        
        if ([self hasMethod:@selector(profile:didReceivePostNotifyRequest:response:deviceId:type:dir:lang:body:tag:icon:)
                   response:response])
        {
            NSData *icon = [DConnectNotificationProfile iconFromRequest:request];
            NSNumber *type = [DConnectNotificationProfile typeFromRequest:request];
            NSString *dir = [DConnectNotificationProfile dirFromRequest:request];
            NSString *lang = [DConnectNotificationProfile langFromRequest:request];
            NSString *body = [DConnectNotificationProfile bodyFromRequest:request];
            NSString *tag = [DConnectNotificationProfile tagFromRequest:request];
            NSString *deviceId = [request deviceId];
            send = [_delegate profile:self didReceivePostNotifyRequest:request response:response
                             deviceId:deviceId type:type dir:dir
                                 lang:lang body:body tag:tag
                                 icon:icon];

        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    NSString *sessionKey = [request sessionKey];
    
    if ([attribute isEqualToString:DConnectNotificationProfileAttrOnClick]) {
        
        if ([self hasMethod:@selector(profile:didReceivePutOnClickRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnClickRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnClose]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnCloseRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnCloseRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnError]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnErrorRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnErrorRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnShow]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnShowRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnShowRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    NSString *sessionKey = [request sessionKey];
    
    if ([attribute isEqualToString:DConnectNotificationProfileAttrOnClick]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnClickRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnClickRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnClose]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnCloseRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnCloseRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnError]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnErrorRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnErrorRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrOnShow]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnShowRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnShowRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectNotificationProfileAttrNotify]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteNotifyRequest:response:deviceId:notificationId:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteNotifyRequest:request response:response
                             deviceId:deviceId
                       notificationId:[DConnectNotificationProfile notificationIdFromRequest:request]];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter

+ (void) setNotificationId:(NSString *)notificationId target:(DConnectMessage *)message {
    [message setString:notificationId forKey:DConnectNotificationProfileParamNotificationId];
}

#pragma mark - Getter

+ (NSNumber *) typeFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectNotificationProfileParamType];
}

+ (NSString *) dirFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectNotificationProfileParamDir];
}

+ (NSString *) langFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectNotificationProfileParamLang];
}

+ (NSString *) bodyFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectNotificationProfileParamBody];
}

+ (NSString *) tagFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectNotificationProfileParamTag];
}

+ (NSString *) notificationIdFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectNotificationProfileParamNotificationId];
}

+ (NSData *) iconFromRequest:(DConnectMessage *)request {
    return [request dataForKey:DConnectNotificationProfileParamIcon];
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
