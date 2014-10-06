//
//  DicePlusNetworkServiceDiscoveryProfile.h
//  dConnectDeviceDicePlus
//
//  Created by 星貴之 on 2014/07/01.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <DConnectSDK/DConnectSDK.h>

extern NSString *const HueDeviceId;

@class PHHueSDK;

@interface DPHueNetworkServiceDiscoveryProfile: DConnectNetworkServiceDiscoveryProfile<DConnectNetworkServiceDiscoveryProfileDelegate>

@end
