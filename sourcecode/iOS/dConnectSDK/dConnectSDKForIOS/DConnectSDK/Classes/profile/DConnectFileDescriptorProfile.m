//
//  DConnectConnectProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectFileDescriptorProfile.h"

NSString *const DConnectFileDescriptorProfileName = @"file_descriptor";
NSString *const DConnectFileDescriptorProfileAttrOpen = @"open";
NSString *const DConnectFileDescriptorProfileAttrClose = @"close";
NSString *const DConnectFileDescriptorProfileAttrRead = @"read";
NSString *const DConnectFileDescriptorProfileAttrWrite = @"write";
NSString *const DConnectFileDescriptorProfileAttrOnWatchFile = @"onwatchfile";
NSString *const DConnectFileDescriptorProfileParamFlag = @"flag";
NSString *const DConnectFileDescriptorProfileParamPosition = @"position";
NSString *const DConnectFileDescriptorProfileParamSize = @"size";
NSString *const DConnectFileDescriptorProfileParamLength = @"length";
NSString *const DConnectFileDescriptorProfileParamFileData = @"fileData";
NSString *const DConnectFileDescriptorProfileParamMedia = @"media";
NSString *const DConnectFileDescriptorProfileParamFile = @"file";
NSString *const DConnectFileDescriptorProfileParamCurr = @"curr";
NSString *const DConnectFileDescriptorProfileParamPrev = @"prev";
NSString *const DConnectFileDescriptorProfileParamUri = @"uri";
NSString *const DConnectFileDescriptorProfileParamPath = @"path";

@interface DConnectFileDescriptorProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectFileDescriptorProfile

#pragma mark - DConnectProfile Methods

- (NSString *) profileName {
    return DConnectFileDescriptorProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    if (attribute) {
        
        NSString *deviceId = [request deviceId];
        NSString *path = [DConnectFileDescriptorProfile pathFromRequest:request];
        
        if ([attribute isEqualToString:DConnectFileDescriptorProfileAttrOpen]) {
            if ([self hasMethod:@selector(profile:didReceiveGetOpenRequest:response:deviceId:path:flag:)
                       response:response])
            {
                NSString *flag = [DConnectFileDescriptorProfile flagFromRequest:request];
                send = [_delegate profile:self didReceiveGetOpenRequest:request response:response
                                 deviceId:deviceId path:path flag:flag];
            }
        } else if ([attribute isEqualToString:DConnectFileDescriptorProfileAttrRead]) {
            if ([self hasMethod:@selector(profile:didReceiveGetReadRequest:response:deviceId:path:length:position:)
                       response:response])
            {
                NSNumber *length = [DConnectFileDescriptorProfile lengthFromRequest:request];
                NSNumber *position = [DConnectFileDescriptorProfile positionFromRequest:request];
                send = [_delegate profile:self didReceiveGetReadRequest:request response:response
                                 deviceId:deviceId path:path length:length position:position];
            }
        } else {
            [response setErrorToUnknownAttribute];
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
    if (attribute) {
        
        NSString *deviceId = [request deviceId];
        NSString *path = [DConnectFileDescriptorProfile pathFromRequest:request];
        
        if ([attribute isEqualToString:DConnectFileDescriptorProfileAttrClose]) {
            
            if ([self hasMethod:@selector(profile:didReceivePutCloseRequest:response:deviceId:path:)
                       response:response])
            {
                send = [_delegate profile:self didReceivePutCloseRequest:request response:response
                                 deviceId:deviceId path:path];
            }
        } else if ([attribute isEqualToString:DConnectFileDescriptorProfileAttrWrite]) {
            if ([self hasMethod:@selector(profile:didReceivePutWriteRequest:response:deviceId:path:media:position:)
                       response:response])
            {
                NSData *media = [DConnectFileDescriptorProfile mediaFromRequest:request];
                NSNumber *position = [DConnectFileDescriptorProfile positionFromRequest:request];
                
                send = [_delegate profile:self didReceivePutWriteRequest:request response:response
                                 deviceId:deviceId path:path media:media position:position];
            }
        } else if ([attribute isEqualToString:DConnectFileDescriptorProfileAttrOnWatchFile]) {
            
            if ([self hasMethod:@selector(profile:didReceivePutOnWatchFileRequest:response:deviceId:sessionKey:)
                       response:response])
            {
                NSString *sessionKey = [request sessionKey];
                send = [_delegate profile:self didReceivePutOnWatchFileRequest:request response:response
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
    
    NSString *attribute = [request attribute];
    
    if ([DConnectFileDescriptorProfileAttrOnWatchFile isEqualToString:attribute]) {
        
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnWatchFileRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            NSString *deviceId = [request deviceId];
            NSString *sessionKey = [request sessionKey];
            send = [_delegate profile:self didReceiveDeleteOnWatchFileRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    return send;
}

#pragma mark - Setter
/*!
 <code>curr</code>パラメータを設定する。
 @param curr
 @param message
 */
+ (void) setCurr:(NSString *)curr target:(DConnectMessage *)message {
    [message setString:curr forKey:DConnectFileDescriptorProfileParamCurr];
}

+ (void) setPrev:(NSString *)prev target:(DConnectMessage *)message {
    [message setString:prev forKey:DConnectFileDescriptorProfileParamPrev];
}

+ (void) setSize:(long long)size target:(DConnectMessage *)message {
    [message setLongLong:size forKey:DConnectFileDescriptorProfileParamSize];
}

+ (void) setFileData:(NSString *)fileData target:(DConnectMessage *)message {
    [message setString:fileData forKey:DConnectFileDescriptorProfileParamFileData];
}

+ (void) setPath:(NSString *)path target:(DConnectMessage *)message {
    [message setString:path forKey:DConnectFileDescriptorProfileParamPath];
}

+ (void) setFile:(DConnectMessage *)file target:(DConnectMessage *)message {
    [message setMessage:file forKey:DConnectFileDescriptorProfileParamFile];
}

#pragma mark - Getter

+ (NSString *) flagFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileDescriptorProfileParamFlag];
}

+ (NSNumber *) lengthFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectFileDescriptorProfileParamLength];
}

+ (NSNumber *) positionFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectFileDescriptorProfileParamPosition];
}

+ (NSData *) mediaFromRequest:(DConnectMessage *)request {
    return [request dataForKey:DConnectFileDescriptorProfileParamMedia];
}

+ (NSString *) pathFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileDescriptorProfileParamPath];
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
