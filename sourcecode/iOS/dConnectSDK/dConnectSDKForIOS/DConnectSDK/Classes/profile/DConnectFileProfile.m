//
//  DConnectFileProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectFileProfile.h"


NSString *const DConnectFileProfileName = @"file";
NSString *const DConnectFileProfileAttrList = @"list";
NSString *const DConnectFileProfileAttrReceive = @"receive";
NSString *const DConnectFileProfileAttrRemove = @"remove";
NSString *const DConnectFileProfileAttrSend = @"send";
NSString *const DConnectFileProfileAttrMkdir = @"mkdir";
NSString *const DConnectFileProfileAttrRmdir = @"rmdir";
NSString *const DConnectFileProfileParamMIMEType = @"mimeType";
NSString *const DConnectFileProfileParamFiles = @"files";
NSString *const DConnectFileProfileParamFileName = @"fileName";
NSString *const DConnectFileProfileParamFileSize = @"fileSize";
NSString *const DConnectFileProfileParamData = @"data";
NSString *const DConnectFileProfileParamUri = @"uri";
NSString *const DConnectFileProfileParamPath = @"path";
NSString *const DConnectFileProfileParamFileType = @"fileType";
NSString *const DConnectFileProfileParamOrder = @"order";
NSString *const DConnectFileProfileParamOffset = @"offset";
NSString *const DConnectFileProfileParamLimit = @"limit";
NSString *const DConnectFileProfileParamCount = @"count";
NSString *const DConnectFileProfileParamUpdateDate = @"updateDate";
NSString *const DConnectFileProfileParamForce = @"force";

NSString *const DConnectFileProfileOrderASC = @"asc";
NSString *const DConnectFileProfileOrderDESC = @"desc";


@interface DConnectFileProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectFileProfile

#pragma mark - DConnectProfile Methods -

- (NSString *) profileName {
    return DConnectFileProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if ([attribute isEqualToString:DConnectFileProfileAttrReceive]) {
        
        if ([self hasMethod:@selector(profile:didReceiveGetReceiveRequest:response:deviceId:path:)
                   response:response])
        {
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            send = [_delegate profile:self didReceiveGetReceiveRequest:request response:response
                             deviceId:deviceId path:path];
        }
    } else if ([attribute isEqualToString:DConnectFileProfileAttrList]) {
        if ([self hasMethod:@selector(profile:didReceiveGetListRequest:response:deviceId:path:mimeType:order:offset:limit:)
                   response:response])
        {
            
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            NSString *orderStr = [DConnectFileProfile orderFromRequest:request];
            NSNumber *offset = [DConnectFileProfile offsetFromRequest:request];
            NSNumber *limit = [DConnectFileProfile limitFromRequest:request];
            NSString *mimeType = [DConnectFileProfile mimeTypeFromRequest:request];
            NSArray *order = nil;
            
            if (orderStr) {
                order = [orderStr componentsSeparatedByString:@","];
            }
            
            send = [_delegate profile:self didReceiveGetListRequest:request response:response
                             deviceId:deviceId path:path mimeType:mimeType
                                order:order offset:offset limit:limit];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    if ([attribute isEqualToString:DConnectFileProfileAttrSend]) {
        
        if ([self hasMethod:@selector(profile:didReceivePostSendRequest:response:deviceId:path:mimeType:data:)
                   response:response])
        {
            NSData *data = [DConnectFileProfile dataFromRequest:request];
            NSString *deviceId = [request deviceId];
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            NSString *mimeType = [DConnectFileProfile mimeTypeFromRequest:request];
            
            send = [_delegate profile:self didReceivePostSendRequest:request response:response
                             deviceId:deviceId path:path mimeType:mimeType data:data];
        }
    } else if ([attribute isEqualToString:DConnectFileProfileAttrMkdir]) {
        if ([self hasMethod:@selector(profile:didReceivePostMkdirRequest:response:deviceId:path:)
                   response:response])
        {
            NSString *deviceId = [request deviceId];
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            send = [_delegate profile:self didReceivePostMkdirRequest:request
                             response:response
                             deviceId:deviceId
                                 path:path];
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
    if ([attribute isEqualToString:DConnectFileProfileAttrRemove]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteRemoveRequest:response:deviceId:path:)
                   response:response])
        {
            NSString *deviceId = [request deviceId];
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            
            send = [_delegate profile:self didReceiveDeleteRemoveRequest:request response:response
                             deviceId:deviceId path:path];
        }
    } else if ([attribute isEqualToString:DConnectFileProfileAttrRmdir]) {
        
        if ([self hasMethod:@selector(profile:didReceiveDeleteRmdirRequest:response:deviceId:path:force:)
                   response:response])
        {
            NSString *deviceId = [request deviceId];
            NSString *path = [DConnectFileProfile pathFromRequest:request];
            BOOL force = [DConnectFileProfile forceFromRequest:request];
            send = [_delegate profile:self didReceiveDeleteRmdirRequest:request
                             response:response
                             deviceId:deviceId
                                 path:path
                                force:force];
        }
        
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter

+ (void) setURI:(NSString *)uri target:(DConnectMessage *)message {
    [message setString:uri forKey:DConnectFileProfileParamUri];
}

+ (void) setMIMEType:(NSString *)mimeType target:(DConnectMessage *)message {
    [message setString:mimeType forKey:DConnectFileProfileParamMIMEType];
}

+ (void) setFileName:(NSString *)fileName target:(DConnectMessage *)message {
    [message setString:fileName forKey:DConnectFileProfileParamFileName];
}

+ (void) setFileSize:(long long)fileSize target:(DConnectMessage *)message {
    [message setLongLong:fileSize forKey:DConnectFileProfileParamFileSize];
}

+ (void) setFileType:(int)fileType target:(DConnectMessage *)message {
    [message setInteger:fileType forKey:DConnectFileProfileParamFileType];
}

+ (void) setFiles:(DConnectArray *)files target:(DConnectMessage *)message {
    [message setArray:files forKey:DConnectFileProfileParamFiles];
}

+ (void) setPath:(NSString *)path target:(DConnectMessage *)message {
    [message setString:path forKey:DConnectFileProfileParamPath];
}

+ (void) setCount:(int)count target:(DConnectMessage *)message {
    [message setInteger:count forKey:DConnectFileProfileParamCount];
}

+ (void) setUpdateDate:(NSString *)updateDate tareget:(DConnectMessage *)message {
    [message setString:updateDate forKey:DConnectFileProfileParamUpdateDate];
}

#pragma mark - Getter

+ (NSString *) fileNameFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileProfileParamFileName];
}

+ (NSString *) mimeTypeFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileProfileParamMIMEType];
}

+ (NSData *) dataFromRequest:(DConnectMessage *)request {
    return [request dataForKey:DConnectFileProfileParamData];
}

+ (NSString *) pathFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileProfileParamPath];
}

+ (NSString *) orderFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectFileProfileParamOrder];
}

+ (NSNumber *) offsetFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectFileProfileParamOffset];
}

+ (NSNumber *) limitFromRequest:(DConnectMessage *)request {
    return [request numberForKey:DConnectFileProfileParamLimit];
}

+ (BOOL) forceFromRequest:(DConnectMessage *)request {
    return [request boolForKey:DConnectFileProfileParamForce];
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
