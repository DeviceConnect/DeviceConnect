//
//  dConnectDeviceHue.m
//  dConnectDeviceHue
//
//  Created by 星　貴之 on 2014/07/07.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "dConnectDeviceHue.h"
#import "DPHueNetworkServiceDiscoveryProfile.h"
#import "DPHueSystemProfile.h"
#import "DPHueLightProfile.h"

@implementation dConnectDeviceHue
- (id) init {
    
    self = [super init];
    
    if (self) {
        
        self.pluginName = [NSString stringWithFormat:@"hue 1.0"];
        
        // Network Service Discovery Profileの追加
        DPHueNetworkServiceDiscoveryProfile *networkProfile = [DPHueNetworkServiceDiscoveryProfile new];
        
        // System Profileの追加
        DPHueSystemProfile *systemProfile = [DPHueSystemProfile new];
        
        // Hue Profileの追加
        DPHueLightProfile *hueProfile = [DPHueLightProfile new];
        
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:hueProfile];
    }
    
    return self;
}
@end
