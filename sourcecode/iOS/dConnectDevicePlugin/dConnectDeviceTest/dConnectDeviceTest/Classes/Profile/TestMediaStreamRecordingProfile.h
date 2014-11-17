//
//  TestMediaStreamRecordingProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

@class DeviceTestPlugin;

@interface TestMediaStreamRecordingProfile : DConnectMediaStreamRecordingProfile<DConnectMediaStreamRecordingProfileDelegate>


@property (nonatomic, strong) DeviceTestPlugin *plugin;
- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin;

@end
