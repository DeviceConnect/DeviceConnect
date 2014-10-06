//
//  DConnectEventHelper.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEventHelper.h"

@interface DConnectEventHandlerHolder : NSObject
@property (nonatomic, copy) DConnectEventHandler handler;
@end

@implementation DConnectEventHandlerHolder
@end

@interface DConnectEventHelper()<DConnectManagerDelegate> {
    NSMutableDictionary *_handlers;
}

- (id) initForSingleton;
- (NSString *) keyForMessage:(DConnectMessage *)message;
- (BOOL) isEmptyString:(NSString *)str;

@end

@implementation DConnectEventHelper

#pragma mark - init

- (id) init {
    return nil;
}

- (id) initForSingleton {
    
    self = [super init];
    
    if (self) {
        _handlers = [NSMutableDictionary dictionary];
    }
    
    return self;
}

#pragma mark - DConnectManagerDelegate

- (void) manager:(DConnectManager *)manager didReceiveDConnectMessage:(DConnectMessage *)event {
    DConnectEventHandlerHolder *holder = nil;
    DC_SYNC_START(_handlers)
    holder = [_handlers objectForKey:[self keyForMessage:event]];
    DC_SYNC_END
    if (holder) {
        holder.handler(event);
    }
}

#pragma mark - Public

- (void) registerEventWithRequest:(DConnectRequestMessage *)request
                  responseHandler:(DConnectResponseHandler)responseHandler
                   messageHandler:(DConnectEventHandler)messageHandler
{
    __weak typeof(self) _self = self;
    
    DConnectManager *manager = [DConnectManager sharedManager];
    [manager sendRequest:request callback:^(DConnectResponseMessage *response) {
        
        if (response.result == DConnectMessageResultTypeOk) {
            DC_SYNC_START(_handlers)
            DConnectEventHandlerHolder *holder = [DConnectEventHandlerHolder new];
            holder.handler = messageHandler;
            [_handlers setObject:holder forKey:[_self keyForMessage:request]];
            DC_SYNC_END
        }
        
        responseHandler(response);
    }];
    
}

- (void) unregisterEventWithRequest:(DConnectRequestMessage *)request
                    responseHandler:(DConnectResponseHandler)responseHandler
{
    __weak typeof(self) _self = self;
    
    DConnectManager *manager = [DConnectManager sharedManager];
    [manager sendRequest:request callback:^(DConnectResponseMessage *response) {
        
        if (response.result == DConnectMessageResultTypeOk) {
            
            DC_SYNC_START(_handlers)
            [_handlers removeObjectForKey:[_self keyForMessage:request]];
            DC_SYNC_END
            
        }
        
        responseHandler(response);
    }];
    
    
}

- (void) unregisterAllEventsWithAccessToken:(NSString *)accessToken
{
    
    DConnectManager *dm = [DConnectManager sharedManager];
    
    if (dm.settings.useLocalOAuth && [self isEmptyString:accessToken]) {
        @throw @"AccessToken is needed.";
    }
    
    DC_SYNC_START(_handlers)
    
    NSArray *keys = _handlers.allKeys;
    for (NSString *key in keys) {
        NSArray *params = [key componentsSeparatedByString:@" "];
        if ([params count] == 5) {
            
            NSString *deviceId = [params[0] stringByRemovingPercentEncoding];
            NSString *sessionKey = [params[1] stringByRemovingPercentEncoding];
            NSString *profile = [params[2] stringByRemovingPercentEncoding];
            NSString *interface = [params[3] stringByRemovingPercentEncoding];
            NSString *attribute = [params[4] stringByRemovingPercentEncoding];
            
            DConnectRequestMessage *req = [DConnectRequestMessage message];
            req.action = DConnectMessageActionTypeDelete;
            
            if (![self isEmptyString:deviceId]) {
                req.deviceId = deviceId;
            }
            
            req.profile = profile;
            
            if (![self isEmptyString:interface]) {
                req.interface = interface;
            }

            if (![self isEmptyString:attribute]) {
                req.attribute = attribute;
            }

            req.sessionKey = sessionKey;
            req.accessToken = accessToken;
            
            [dm sendRequest:req callback:nil];
        }
        
        [_handlers removeObjectForKey:key];
    }
    
    DC_SYNC_END
}

#pragma mark - Private

- (NSString *) keyForMessage:(DConnectMessage *)message {
    
    NSMutableString *key = [NSMutableString string];
    
    if (![self isEmptyString:[message stringForKey:DConnectMessageDeviceId]]) {
        [key appendString:[[message stringForKey:DConnectMessageDeviceId]
                           stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    }
    
    [key appendString:@" "];
    
    if (![self isEmptyString:[message stringForKey:DConnectMessageSessionKey]]) {
        [key appendString:[[message stringForKey:DConnectMessageSessionKey]
                           stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    }

    [key appendString:@" "];
    
    if (![self isEmptyString:[message stringForKey:DConnectMessageProfile]]) {
        [key appendString:[[message stringForKey:DConnectMessageProfile]
                           stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    }
    
    [key appendString:@" "];
    
    if (![self isEmptyString:[message stringForKey:DConnectMessageInterface]]) {
        [key appendString:[[message stringForKey:DConnectMessageInterface]
                           stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    }
    
    [key appendString:@" "];
    
    if (![self isEmptyString:[message stringForKey:DConnectMessageAttribute]]) {
        [key appendString:[[message stringForKey:DConnectMessageAttribute]
                           stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
    }
    
    return key;
}

#pragma mark - Private

- (BOOL) isEmptyString:(NSString *)str {
    return !str || (str.length == 0);
}

#pragma mark - Static

+ (DConnectEventHelper *) sharedHelper {
    
    static DConnectEventHelper *helper;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        helper = [[DConnectEventHelper alloc] initForSingleton];
    });
    
    // イベントを受信するため呼ばれたときに常にデリゲートに設定する。
    [DConnectManager sharedManager].delegate = helper;
    
    return helper;
}

@end
