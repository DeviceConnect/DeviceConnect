//
//  NSString+Extension.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "NSString+Extension.h"

@implementation NSString (Extension)

+ (instancetype)stringWithUTF8StringAddingNullTermination:(const char *const) bytes length:(NSUInteger)length
{
    char *tmp = calloc(length + 1, sizeof(char));
    strncpy(tmp, bytes, length);
    tmp[length] = '\0';
    ++length;
    NSString *str = [self stringWithUTF8String:tmp];
    free(tmp);
    return str;
}

@end
