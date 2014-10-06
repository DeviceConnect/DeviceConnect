//
//  LocalOAuthScope.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthScope.h"
#import "LocalOAuth2Settings.h"
#import "LocalOAuthUtils.h"

@implementation LocalOAuthScope

- (id)initWithScope: (NSString *)scope
          timestamp: (long long)timestamp
       expirePeriod: (long long)expirePeriod {
    
    self = [super init];
    
    if (self) {
        self.scope = scope;
        self.timestamp = timestamp;
        self.expirePeriod = expirePeriod;
    }
    
    return self;
}

- (NSString *) getStrExpirePeriod {
    long long e = [self timestamp] + [self expirePeriod] * MSEC;
    
    NSDate *cal = [[NSDate alloc]initWithTimeIntervalSince1970: (e / MSEC)];
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];
    df.dateFormat  = @"y/M/d";
    NSString *displayExpirePeriod = [df stringFromDate: cal];
    
    return displayExpirePeriod;
}


- (BOOL) isExpired {
    
    long long elapsedTime = [LocalOAuthUtils getCurrentTimeInMillis] - self.timestamp;
    long long timeout = self.expirePeriod * MSEC;
    if (elapsedTime > timeout) {
        return YES;
    }
    return NO;
}

+ (NSArray *) toScopeStringArray: (NSArray *)scope {
    if ([scope count] > 0) {
        NSArray *array = [NSArray array];
        return array;
    }
    return nil;
}

+ (LocalOAuthScope *)parse: (NSString *)strScope {
    
    NSArray *div = [strScope componentsSeparatedByString:@","];
    if ([div count] <= 0) {
        return nil;
    }
    
    NSMutableString *scopeName = [NSMutableString string];
    long long timestamp = [LocalOAuthUtils getCurrentTimeInMillis];
    long long expirePeriod = LocalOAuth2Settings_DEFAULT_TOKEN_EXPIRE_PERIOD;
    
    int divCount = [div count];
    if (divCount >= 1) {
        scopeName = [div objectAtIndex:0];
    }
    if (divCount >= 2) {
        NSString *div1 = [div objectAtIndex: 1];
        @try {
            timestamp = [div1 longLongValue];
        } @catch (NSString *e) {
            
        }
    }
    if (divCount >= 3) {
        NSString *div2 = [div objectAtIndex: 2];
        @try {
            expirePeriod = [div2 longLongValue];
        } @catch (NSString *e) {
            
        }
    }
    
    LocalOAuthScope *scope =[[LocalOAuthScope alloc] initWithScope: scopeName
                             timestamp: timestamp
                          expirePeriod: expirePeriod];
    return scope;
}

- (NSString *) toString {
    NSString *strScope = [NSString stringWithFormat: @"%@,%lld,%lld",
                         self.scope, self.timestamp, self.expirePeriod];
    return strScope;
}



@end
