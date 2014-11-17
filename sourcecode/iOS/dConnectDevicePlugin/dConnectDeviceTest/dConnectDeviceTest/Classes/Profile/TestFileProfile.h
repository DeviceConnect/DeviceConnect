//
//  TestFileProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

@class DeviceTestPlugin;

@interface TestFileProfile : DConnectFileProfile<DConnectFileProfileDelegate>

@property (nonatomic, strong) DeviceTestPlugin *plugin;
- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin;

@end
