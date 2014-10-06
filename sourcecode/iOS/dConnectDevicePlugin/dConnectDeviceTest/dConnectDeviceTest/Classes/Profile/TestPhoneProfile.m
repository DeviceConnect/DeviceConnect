//
//  TestPhoneProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestPhoneProfile.h"
#import "DeviceTestPlugin.h"

@implementation TestPhoneProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - Post Methods

- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePostCallRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId phoneNumber:(NSString *)phoneNumber
{
    CheckDID(response, deviceId)
    if (phoneNumber == nil || phoneNumber.length == 0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark - Put Methods
- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePutSetRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
            mode:(NSNumber *)mode
{
    CheckDID(response, deviceId)
    if (mode == nil || [mode intValue] == DConnectPhoneProfilePhoneModeUnknown) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark Event Registration

- (BOOL) profile:(DConnectPhoneProfile *)profile didReceivePutOnConnectRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:deviceId forKey:DConnectMessageDeviceId];
        [event setString:DConnectPhoneProfileAttrOnConnect forKey:DConnectMessageAttribute];
        
        DConnectMessage *phoneStatus = [DConnectMessage message];
        [DConnectPhoneProfile setPhoneNumber:@"090xxxxxxxx" target:phoneStatus];
        [DConnectPhoneProfile setState:DConnectPhoneProfileCallStateFinished target:phoneStatus];
        
        [DConnectPhoneProfile setPhoneStatus:phoneStatus target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL) profile:(DConnectPhoneProfile *)profile didReceiveDeleteOnConnectRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }

    return YES;
}


@end
