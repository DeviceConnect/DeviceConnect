//
//  NSString+Hex.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "NSString+Hex.h"

@implementation NSString (Hex)

- (NSString *) stringByHexingWithLength:(size_t)length {
    
    const char *utf8 = [self UTF8String];
    
    NSMutableString *ret = [NSMutableString new];
    
    for (int i = 0; i < strnlen(utf8, length); i++) {
        [ret appendString: [NSString stringWithFormat: @"%02x", utf8[i] & 0xFF]];
    }
    return ret;
}

@end
