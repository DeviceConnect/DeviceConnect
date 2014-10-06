//
//  DConnectURIBuilder.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectURIBuilder.h"

@implementation DConnectURIBuilder

- (id) init {
    self = [super init];
    if (self) {
        self.params = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void) addParameter:(NSString *)value forKey:(NSString *)key {
    [self.params setObject:value forKey:key];
}

- (NSString *) build {
    NSMutableString *uri = [NSMutableString string];
    [uri appendFormat:@"http://%@:%d", self.host, self.port];
    
    // パスの作成
    if (self.path) {
        [uri appendString:self.path];
    } else {
        if (self.profile) {
            [uri appendFormat:@"/%@", self.profile];
        }
        if (self.interface) {
            [uri appendFormat:@"/%@", self.interface];
        }
        if (self.attribute) {
            [uri appendFormat:@"/%@", self.attribute];
        }
    }
    
    // パラメータの作成
    NSArray *keys = [self.params allKeys];
    for (int i = 0; i < [keys count]; i++) {
        NSString *key = [keys objectAtIndex:i];
        NSString *value = [self.params objectForKey:[keys objectAtIndex:i]];
        if (i == 0) {
            [uri appendFormat:@"?%@=%@", key, value];
        } else {
            [uri appendFormat:@"&%@=%@", key, value];
        }
    }
    
    return uri;
}


@end
