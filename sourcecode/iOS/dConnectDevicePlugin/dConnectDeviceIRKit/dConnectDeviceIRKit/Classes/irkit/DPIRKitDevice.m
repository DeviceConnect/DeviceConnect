//
//  DPIRKitDevice.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/19.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitDevice.h"

@implementation DPIRKitDevice

- (NSString *) description {
    return [NSString stringWithFormat:@"[name = %@, hostname = %@]", _name, _hostName];
}

@end
