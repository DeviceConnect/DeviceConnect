//
//  NSString+Hex.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
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
