//
//  Hash.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <CommonCrypto/CommonCrypto.h>
#import "Hash.h"

@implementation Hash

NSString *SHA256Hash(NSString *seed) {
    CC_LONG len;
    if ((len = seed.length) == 0) {
        return nil;
    }
    const char *data = [seed UTF8String];
    unsigned char result[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(data, len, result);
    NSMutableString *ms = @"".mutableCopy;
    for (int i = 0; i < CC_SHA256_DIGEST_LENGTH; ++i) {
        [ms appendFormat:@"%02X", result[i]];
    }
    return ms;
}


@end
