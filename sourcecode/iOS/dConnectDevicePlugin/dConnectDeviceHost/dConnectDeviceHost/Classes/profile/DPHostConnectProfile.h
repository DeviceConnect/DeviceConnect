//
//  DConnectConnectProfile+DPHostConnectProfile.h
//  dConnectDeviceHost
//
//  Created by 星　貴之 on 2014/11/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
#import <CoreBluetooth/CoreBluetooth.h>

@interface DPHostConnectProfile : DConnectConnectProfile<DConnectConnectProfileDelegate, CBCentralManagerDelegate>

@end
