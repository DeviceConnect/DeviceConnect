//
//  DPIRKitWiFiUtil.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
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
