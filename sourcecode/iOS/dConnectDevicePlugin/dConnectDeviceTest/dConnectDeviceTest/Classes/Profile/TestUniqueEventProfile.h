//
//  TestUniqueEventProfile.h
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/09/08.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@class DeviceTestPlugin;

@interface TestUniqueEventProfile : DConnectProfile

@property (nonatomic, strong) DeviceTestPlugin *plugin;
- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin;

@end
