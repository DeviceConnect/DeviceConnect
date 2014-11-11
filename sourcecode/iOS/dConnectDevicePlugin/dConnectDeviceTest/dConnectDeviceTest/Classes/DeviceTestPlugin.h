//
//  DeviceTestPlugin.h
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/01.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@interface DeviceTestPlugin : DConnectDevicePlugin

- (void) asyncSendEvent:(DConnectMessage *)event;
- (void) asyncSendEvent:(DConnectMessage *)event delay:(NSTimeInterval)delay;

@end
