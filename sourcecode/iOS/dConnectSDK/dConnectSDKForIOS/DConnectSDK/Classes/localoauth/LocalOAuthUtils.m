//
//  LocalOAuthUtil.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthUtils.h"

@implementation LocalOAuthUtils

+ (long long)getCurrentTimeInMillis {
    const long long MSEC = 1000;
    return (long long) [[NSDate date] timeIntervalSince1970] * MSEC;
}

@end
