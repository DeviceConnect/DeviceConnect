//
//  DConnectMessageFactory.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMessageFactory.h"
#import "DConnectURIBuilder.h"
#import "DConnectURLProtocol.h"

#define _DC_S2D(...) [[NSString stringWithFormat:__VA_ARGS__] dataUsingEncoding:NSUTF8StringEncoding]

@interface DConnectMessageFactory()

+ (NSString *) methodForAction:(DConnectMessageActionType)action;
+ (DConnectMessageActionType) actionForMethod:(NSString *)method;
+ (void) convertJSON:(NSDictionary *)json toMessage:(DConnectMessage *)message;
+ (void) convertJSON:(NSArray *)json toArray:(DConnectArray *)array;

@end

@implementation DConnectMessageFactory

+ (NSURLRequest *) requestForMessage:(DConnectRequestMessage *)request {
    
    if (!request.profile) {
        return nil;
    }
    
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    builder.api = request.api;
    builder.profile = request.profile;
    builder.interface = request.interface;
    builder.attribute = request.attribute;
    
    DConnectMessageActionType action = request.action;
    NSMutableURLRequest *urlReq = nil;
    
    BOOL hasBody = NO;
    switch (action) {
        case DConnectMessageActionTypePost:
        case DConnectMessageActionTypePut:
            hasBody = YES;
            break;
        case DConnectMessageActionTypeDelete:
        case DConnectMessageActionTypeGet:
            break;
        default:
            action = DConnectMessageActionTypeGet;
            break;
    }
    
    if (hasBody) {
        // 入れ子になっている部分はHTTPリクエストのBodyとして作れないため無視する。
        // 解析の手間を削減するため全てマルチパートで送る
        urlReq = [NSMutableURLRequest requestWithURL:[builder build]];
        NSString *boundary = [NSString stringWithFormat:@"----%@-dConnect", [[NSUUID UUID] UUIDString]];
        [urlReq addValue:[NSString stringWithFormat:
                          @"multipart/form-data; boundary=%@", boundary] forHTTPHeaderField:@"Content-Type"];
        
        
        NSData *rn = _DC_S2D(@"\r\n");
        NSData *boundaryData = _DC_S2D(@"--%@\r\n", boundary);
        
        NSMutableData *body = [NSMutableData data];
        NSArray *keys = [request allKeys];
        for (NSString *key in keys) {
            
            if ([key isEqualToString:DConnectMessageAction]
                || [key isEqualToString:DConnectMessageAPI]
                || [key isEqualToString:DConnectMessageProfile]
                || [key isEqualToString:DConnectMessageInterface]
                || [key isEqualToString:DConnectMessageAttribute])
            {
                continue;
            }
            
            id value = [request objectForKey:key];
            
            if ([value isKindOfClass:[NSData class]]) {
                [body appendData:boundaryData];
                [body appendData:_DC_S2D(@"Content-Disposition: form-data; name=\"%@\"; filename=\"%@\"\r\n\r\n",
                                         key, @"dammy")];
                [body appendData:(NSData *)value];
            } else {
                
                NSString *data = nil;
                if ([value isKindOfClass:[NSNumber class]]) {
                    data = [NSString stringWithFormat:@"%f", [((NSNumber *) value) doubleValue]];
                } else if ([value isKindOfClass:[NSString class]]) {
                    data = (NSString *) value;
                } else {
                    continue;
                }
                
                [body appendData:boundaryData];
                [body appendData:_DC_S2D(@"Content-Disposition: form-data; name=\"%@\"\r\n\r\n%@",
                                         key, data)];
                
            }
            [body appendData:rn];
        }
        if (body.length > 0) {
            [body appendData:_DC_S2D(@"--%@--\r\n", boundary)];
            urlReq.HTTPBody = body;
            [urlReq addValue:[NSString stringWithFormat:@"%lu",
                              (unsigned long) body.length] forHTTPHeaderField:@"Content-Length"];
        }
    } else {
        NSArray *keys = [request allKeys];
        for (NSString *key in keys) {
            
            if ([key isEqualToString:DConnectMessageAction]
                || [key isEqualToString:DConnectMessageAPI]
                || [key isEqualToString:DConnectMessageProfile]
                || [key isEqualToString:DConnectMessageInterface]
                || [key isEqualToString:DConnectMessageAttribute])
            {
                continue;
            }
            
            
            id value = [request objectForKey:key];
            if ([value isKindOfClass:[NSNumber class]]) {
                [builder addParameter:[NSString stringWithFormat:@"%f", [((NSNumber *) value) doubleValue]]
                              forName:key];
            } else if ([value isKindOfClass:[NSString class]]) {
                [builder addParameter:(NSString *) value forName:key];
            }
        }
        
        urlReq = [NSMutableURLRequest requestWithURL:[builder build]];
    }
    
    urlReq.HTTPMethod = [self methodForAction:action];
    
    return urlReq;
}

+ (DConnectRequestMessage *) messageForRequest:(NSURLRequest *)request {
    DConnectRequestMessage *message = [DConnectURLProtocol requestMessageWithHTTPReqeust:request];
    return message;
}

+ (DConnectResponseMessage *) messageForResponse:(NSURLResponse *)response data:(NSData *)data {
    
    NSError *error = nil;
    id json = [NSJSONSerialization JSONObjectWithData:data
                                              options:NSJSONReadingMutableContainers error:&error];
    
    if (error || ![json isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    
    DConnectResponseMessage *message = [DConnectResponseMessage message];
    [self convertJSON:(NSDictionary *) json toMessage:message];
    
    return message;
}

#pragma mark - Private Methods

+ (NSString *) methodForAction:(DConnectMessageActionType)action {
    NSString *method;
    switch (action) {
        case DConnectMessageActionTypeGet:
            method = @"GET";
            break;
        case DConnectMessageActionTypePost:
            method = @"POST";
            break;
        case DConnectMessageActionTypePut:
            method = @"PUT";
            break;
        case DConnectMessageActionTypeDelete:
            method = @"DELETE";
            break;
        default:
            method = nil;
            break;
    }
    
    return method;
}

+ (DConnectMessageActionType) actionForMethod:(NSString *)method {
    
    DConnectMessageActionType action;
    method = [method uppercaseString];
    
    if ([@"DELETE" isEqualToString:method]) {
        action = DConnectMessageActionTypeDelete;
    } else if ([@"POST" isEqualToString:method]) {
        action = DConnectMessageActionTypePost;
    } else if ([@"PUT" isEqualToString:method]) {
        action = DConnectMessageActionTypePut;
    } else {
        // 無い場合もデフォルトでGETにしておく。
        action = DConnectMessageActionTypeGet;
    }
    
    return action;
}

+ (void) convertJSON:(NSDictionary *)json toMessage:(DConnectMessage *)message {

    NSArray *keys = [json allKeys];
    for (NSString *key in keys) {
        id obj = [json objectForKey:key];
        if ([obj isKindOfClass:[NSDictionary class]]) {
            DConnectMessage *msg = [DConnectMessage message];
            [self convertJSON:(NSDictionary *)obj toMessage:msg];
            [message setMessage:msg forKey:key];
        } else if ([obj isKindOfClass:[NSArray class]]) {
            DConnectArray *array = [DConnectArray array];
            [self convertJSON:(NSArray *)obj toArray:array];
            [message setArray:array forKey:key];
        } else if ([obj isKindOfClass:[NSNumber class]]) {
            [message setNumber:(NSNumber *)obj forKey:key];
        } else if ([obj isKindOfClass:[NSString class]]) {
            [message setString:(NSString *)obj forKey:key];
        }
    }
    
}

+ (void) convertJSON:(NSArray *)json toArray:(DConnectArray *)array {
    
    for (id obj in json) {
        if ([obj isKindOfClass:[NSDictionary class]]) {
            DConnectMessage *msg = [DConnectMessage message];
            [self convertJSON:(NSDictionary *)obj toMessage:msg];
            [array addMessage:msg];
        } else if ([obj isKindOfClass:[NSArray class]]) {
            DConnectArray *array = [DConnectArray array];
            [self convertJSON:(NSArray *)obj toArray:array];
            [array addArray:array];
        } else if ([obj isKindOfClass:[NSNumber class]]) {
            [array addNumber:(NSNumber *)obj];
        } else if ([obj isKindOfClass:[NSString class]]) {
            [array addString:(NSString *)obj];
        }
    }
    
}

@end
