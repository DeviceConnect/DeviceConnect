//
//  AccessToken.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "AccessToken.h"

@implementation AccessToken

- (id) initWithResponse:(NSDictionary *)response
{
    self.token = [response objectForKey:@"accessToken"];
    self.signature = [response objectForKey:@"signature"];
    NSArray *array = [response objectForKey:@"scopes"];
    if (array) {
        self.expirePeriods = [NSMutableDictionary new];
        for (NSDictionary *scope in array) {
            NSString *scopeName = [scope objectForKey:@"scope"];
            if (scopeName) {
                [self.expirePeriods setValue:scopeName forKey:[scope objectForKey:@"expirePeriod"]];
            }
        }
    }
    return self;
}

@end
