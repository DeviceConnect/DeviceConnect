//
//  DConnectDevicePlugin+Private.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectDevicePlugin+Private.h"

@implementation DConnectDevicePlugin (Private)

- (NSString *) pluginId {
    return [NSString stringWithFormat:@"%@.dconnect", NSStringFromClass([self class])];
}

@end
