//
//  TestFileProfile.h
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@class DeviceTestPlugin;

@interface TestFileProfile : DConnectFileProfile<DConnectFileProfileDelegate>

@property (nonatomic, strong) DeviceTestPlugin *plugin;
- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin;

@end
