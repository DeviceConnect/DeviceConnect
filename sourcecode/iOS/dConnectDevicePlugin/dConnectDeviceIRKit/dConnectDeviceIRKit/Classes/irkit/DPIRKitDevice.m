//
//  DPIRKitDevice.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPIRKitDevice.h"

@implementation DPIRKitDevice

- (NSString *) description {
    return [NSString stringWithFormat:@"[name = %@, hostname = %@]", _name, _hostName];
}

@end
