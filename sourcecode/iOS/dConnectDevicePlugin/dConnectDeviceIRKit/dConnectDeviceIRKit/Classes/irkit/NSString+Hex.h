//
//  NSString+Hex.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface NSString (Hex)

- (NSString *) stringByHexingWithLength:(size_t)length;

@end
