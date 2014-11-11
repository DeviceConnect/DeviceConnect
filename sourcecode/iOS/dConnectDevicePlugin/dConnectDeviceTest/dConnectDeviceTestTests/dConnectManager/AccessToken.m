//
//  AccessToken.m
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/09/02.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
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
