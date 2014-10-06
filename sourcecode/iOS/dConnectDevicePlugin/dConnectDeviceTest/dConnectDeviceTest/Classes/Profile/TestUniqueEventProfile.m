//
//  TestUniqueEventProfile.m
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/09/08.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestUniqueEventProfile.h"
#import "DeviceTestPlugin.h"

NSString *const UniqueEventProfileProfileName = @"event";
NSString *const UniqueEventProfileProfileAttributeUnique = @"unique";

@implementation TestUniqueEventProfile

#pragma mark - init

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - DConnectProfile

- (NSString *) profileName {
    return UniqueEventProfileProfileName;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
{
    response.result = DConnectMessageResultTypeOk;
    
    NSNumber *count = [request objectForKey:@"count"];
    if (!count) {
        count = [NSNumber numberWithInt:1];
    }
    [NSThread sleepForTimeInterval:1.0];
    for (int i = 0; i < [count intValue]; i++) {
        DConnectMessage *event = [DConnectMessage message];
        [event setString:[request sessionKey] forKey:DConnectMessageSessionKey];
        [event setString:[request deviceId] forKey:DConnectMessageDeviceId];
        
        NSString *attribute = [request attribute];
        if ([attribute isEqualToString:UniqueEventProfileProfileAttributeUnique]) {
            [event setString:UniqueEventProfileProfileName forKey:DConnectMessageProfile];
            [event setString:UniqueEventProfileProfileAttributeUnique forKey:DConnectMessageAttribute];
        } else {
            [event setString:DConnectNetworkServiceDiscoveryProfileName forKey:DConnectMessageProfile];
            [event setString:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange forKey:DConnectMessageAttribute];
            
            DConnectMessage *networkService = [DConnectMessage message];
            [DConnectNetworkServiceDiscoveryProfile setId:TDPDeviceId target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setName:@"Test Success Device" target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setType:@"TEST" target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setState:YES target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setConfig:@"test config" target:networkService];
            [DConnectNetworkServiceDiscoveryProfile setNetworkService:networkService target:event];
        }
        
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

@end
