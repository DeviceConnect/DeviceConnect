//
//  DConnectEvent.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEvent.h"

@implementation DConnectEvent

- (BOOL) isEqual:(id)object {
    
    if (![object isKindOfClass:[DConnectEvent class]]) {
        return NO;
    }
    
    DConnectEvent *other = (DConnectEvent *) object;
    
    BOOL interfaceMatch = NO;
    if ((_interface == nil && other.interface == nil)
        || (_interface != nil && [_interface isEqualToString:other.interface]))
    {
        interfaceMatch = YES;
    }
    
    BOOL deviceIdMatch = NO;
    if ((_deviceId == nil && other.deviceId == nil)
        || (_deviceId != nil && [_deviceId isEqualToString:other.deviceId]))
    {
        deviceIdMatch = YES;
    }
    
    return ([_profile isEqualToString:other.profile] &&
            interfaceMatch &&
            [_attribute isEqualToString:other.attribute] &&
            deviceIdMatch &&
            [_sessionKey isEqualToString:other.sessionKey]);
    
}

#pragma mark - NSCoding
- (void)encodeWithCoder:(NSCoder *)aCoder {
    
    [aCoder encodeObject:_profile forKey:@"profile"];
    [aCoder encodeObject:_interface forKey:@"interface"];
    [aCoder encodeObject:_attribute forKey:@"attribute"];
    [aCoder encodeObject:_deviceId forKey:@"deviceId"];
    [aCoder encodeObject:_accessToken forKey:@"accessToken"];
    [aCoder encodeObject:_sessionKey forKey:@"sessionKey"];
    [aCoder encodeObject:_createDate forKey:@"createDate"];
    [aCoder encodeObject:_updateDate forKey:@"updateDate"];
}

- (id)initWithCoder:(NSCoder *)aDecode {
    
    self = [super init];
    
    if (self) {
        _profile = [aDecode decodeObjectOfClass:[NSString class] forKey:@"profile"];
        _interface = [aDecode decodeObjectOfClass:[NSString class] forKey:@"interface"];
        _attribute = [aDecode decodeObjectOfClass:[NSString class] forKey:@"attribute"];
        _deviceId = [aDecode decodeObjectOfClass:[NSString class] forKey:@"deviceId"];
        _accessToken = [aDecode decodeObjectOfClass:[NSString class] forKey:@"accessToken"];
        _sessionKey = [aDecode decodeObjectOfClass:[NSString class] forKey:@"sessionKey"];
        _createDate = [aDecode decodeObjectOfClass:[NSDate class] forKey:@"createDate"];
        _updateDate = [aDecode decodeObjectOfClass:[NSDate class] forKey:@"updateDate"];
    }
    
    return self;
}

#pragma mark - NSSecureCoding

+ (BOOL) supportsSecureCoding {
    return YES;
}

@end
