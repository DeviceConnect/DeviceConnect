//
//  TestNotificationProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestNotificationProfile.h"
#import "DeviceTestPlugin.h"

@implementation TestNotificationProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - Post Methods


- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePostNotifyRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId type:(NSNumber *)type
             dir:(NSString *)dir lang:(NSString *)lang
            body:(NSString *)body tag:(NSString *)tag icon:(NSData *)icon
{
    
    CheckDID(response, deviceId)
    if (type == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        NSString *_id = nil;
        switch ([type intValue]) {
            case DConnectNotificationProfileNotificationTypePhone:
                _id = @"1";
                break;
            case DConnectNotificationProfileNotificationTypeMail:
                _id = @"2";
                break;
            case DConnectNotificationProfileNotificationTypeSMS:
                _id = @"3";
                break;
            case DConnectNotificationProfileNotificationTypeEvent:
                _id = @"4";
                break;
            case DConnectNotificationProfileNotificationTypeUnknown:
                _id = @"5";
                break;
                
            default:
                [response setErrorToInvalidRequestParameter];
                break;
        }
        
        if (_id) {
            response.result = DConnectMessageResultTypeOk;
            [DConnectNotificationProfile setNotificationId:_id target:response];
        }
    }
    
    return YES;
}

#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnClickRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectNotificationProfileAttrOnClick forKey:DConnectMessageAttribute];
        [DConnectNotificationProfile setNotificationId:@"1" target:event];
        [_plugin asyncSendEvent:event];
        
    }
    
    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnShowRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectNotificationProfileAttrOnShow forKey:DConnectMessageAttribute];
        [DConnectNotificationProfile setNotificationId:@"1" target:event];
        [_plugin asyncSendEvent:event];
        
    }

    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectNotificationProfileAttrOnClose forKey:DConnectMessageAttribute];
        [DConnectNotificationProfile setNotificationId:@"1" target:event];
        [_plugin asyncSendEvent:event];
        
    }

    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceivePutOnErrorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectNotificationProfileAttrOnError forKey:DConnectMessageAttribute];
        [DConnectNotificationProfile setNotificationId:@"1" target:event];
        [_plugin asyncSendEvent:event];
        
    }

    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteNotifyRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId notificationId:(NSString *)notificationId
{
    
    CheckDID(response, deviceId)
    if (notificationId == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark Event Unregistration

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnClickRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnShowRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    return YES;
}

- (BOOL) profile:(DConnectNotificationProfile *)profile didReceiveDeleteOnErrorRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    return YES;
}

@end
