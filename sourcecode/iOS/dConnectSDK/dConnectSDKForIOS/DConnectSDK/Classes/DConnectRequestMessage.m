//
//  DConnectRequestMessage.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectRequestMessage.h"

@implementation DConnectRequestMessage

- (id) init {
    
    self = [super init];
    
    if (self) {
        [self setString:DConnectMessageDefaultAPI forKey:DConnectMessageAPI];
    }
    
    return self;
}

#pragma mark - Common Parameters
#pragma mark Setter

- (void) setAction:(DConnectMessageActionType)action {
    [self setInteger:action forKey:DConnectMessageAction];
}

- (void) setApi:(NSString *)api {
    [self setString:api forKey:DConnectMessageAPI];
}

- (void) setProfile:(NSString *)profile {
    [self setString:profile forKey:DConnectMessageProfile];
}

- (void) setAttribute:(NSString *)attribute {
    [self setString:attribute forKey:DConnectMessageAttribute];
}

- (void) setInterface:(NSString *)interface {
    [self setString:interface forKey:DConnectMessageInterface];
}

- (void) setSessionKey:(NSString *)sessionKey {
    [self setString:sessionKey forKey:DConnectMessageSessionKey];
}

- (void) setDeviceId:(NSString *)deviceId {
    [self setString:deviceId forKey:DConnectMessageDeviceId];
}

- (void) setPluginId:(NSString *)pluginId {
    [self setString:pluginId forKey:DConnectMessagePluginId];
}

- (void) setAccessToken:(NSString *)accessToken {
    [self setString:accessToken forKey:DConnectMessageAccessToken];
}

#pragma mark Getter

- (NSString *) api {
    return [self stringForKey:DConnectMessageAPI];
}

- (NSString *) profile {
    return [self stringForKey:DConnectMessageProfile];
}

- (NSString *) attribute {
    return [self stringForKey:DConnectMessageAttribute];
}

- (NSString *) interface {
    return [self stringForKey:DConnectMessageInterface];
}

- (NSString *) sessionKey {
    return [self stringForKey:DConnectMessageSessionKey];
}

- (NSString *) deviceId {
    return [self stringForKey:DConnectMessageDeviceId];
}

- (NSString *) pluginId {
    return [self stringForKey:DConnectMessagePluginId];
}

- (DConnectMessageActionType) action {
    return [self integerForKey:DConnectMessageAction];
}

- (NSString *) accessToken {
    return [self stringForKey:DConnectMessageAccessToken];
}

@end
