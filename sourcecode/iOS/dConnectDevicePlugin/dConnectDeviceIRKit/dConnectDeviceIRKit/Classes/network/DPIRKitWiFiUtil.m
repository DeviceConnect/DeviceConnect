//
//  DPIRKitWiFiUtil.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitWiFiUtil.h"
#import <SystemConfiguration/CaptiveNetwork.h>

@implementation DPIRKitWiFiUtil

+ (NSString *) currentSSID {
    
    CFArrayRef interfaces = CNCopySupportedInterfaces();
    
    if (!interfaces) {
        return nil;
    }
    
    CFIndex count = CFArrayGetCount(interfaces);
    
    if (count < 1) {
        return nil;
    }
    
    NSString *ssid = nil;
    CFDictionaryRef dicRef = CNCopyCurrentNetworkInfo(CFArrayGetValueAtIndex(interfaces, (CFIndex) 0));
    if (dicRef) {
        ssid = CFDictionaryGetValue(dicRef, kCNNetworkInfoKeySSID);
        CFRelease(dicRef);
    }
    
    return ssid;
}

@end
