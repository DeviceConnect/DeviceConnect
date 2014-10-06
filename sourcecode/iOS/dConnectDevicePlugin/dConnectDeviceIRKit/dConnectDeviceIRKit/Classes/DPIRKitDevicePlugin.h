//
//  DPIRkitDevicePlugin.h
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/19.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@class DPIRKitDevice;

@interface DPIRKitDevicePlugin : DConnectDevicePlugin

- (DPIRKitDevice *) deviceForDeviceId:(NSString *)deviceId;

@end
