//
//  DPIRKitDevicePlugin.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

@class DPIRKitDevice;

@interface DPIRKitDevicePlugin : DConnectDevicePlugin

- (DPIRKitDevice *) deviceForDeviceId:(NSString *)deviceId;

@end
