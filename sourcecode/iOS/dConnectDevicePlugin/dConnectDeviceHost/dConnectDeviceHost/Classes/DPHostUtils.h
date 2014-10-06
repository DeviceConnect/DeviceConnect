//
//  DPHostUtils.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

#define SELF_PLUGIN ((DPHostDevicePlugin *)self.provider)

@interface DPHostUtils : NSObject

+ (NSString *) randomStringWithLength:(NSUInteger)len;
+ (NSString *) percentEncodeString:(NSString *)string withEncoding:(NSStringEncoding)encoding;

@end
