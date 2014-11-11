//
//  TestFileDescriptorProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestFileDescriptorProfile.h"
#import "DeviceTestPlugin.h"

const int TestFileDescriptorFileSize = 64000;
NSString *const TestFileDescriptorPath = @"test.txt";
NSString *const TestFileDescriptorUri = @"test_uri";
NSString *const TestFileDescriptorCurr = @"2014-06-01T00:00:00+0900";
NSString *const TestFileDescriptorPrev = @"2014-06-01T00:00:00+0900";

@implementation TestFileDescriptorProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - Get Methods

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveGetOpenRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
            flag:(NSString *)flag
{
    
    CheckDID(response, deviceId)
    if (path == nil || flag == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveGetReadRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
          length:(NSNumber *)length
        position:(NSNumber *)position
{
    CheckDID(response, deviceId)
    if (path == nil || length == nil || length < 0 || (position != nil && position < 0)) {
        [response setErrorToInvalidRequestParameter];
    } else {
        
        NSData *data = [NSData data];
        NSString *fileData = [data base64EncodedStringWithOptions:kNilOptions];
        response.result = DConnectMessageResultTypeOk;
        [DConnectFileDescriptorProfile setSize:TestFileDescriptorFileSize target:response];
        [DConnectFileDescriptorProfile setFileData:fileData target:response];
    }
    
    return YES;
}

#pragma mark - Put Methods

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutCloseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
{
    
    CheckDID(response, deviceId)
    if (path == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutWriteRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
           media:(NSData *)media
        position:(NSNumber *)position
{
    CheckDID(response, deviceId)
    if (path == nil || media == nil || (position != nil && position < 0)) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark Event Registration

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceivePutOnWatchFileRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionKey forKey:DConnectMessageSessionKey];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectFileDescriptorProfileAttrOnWatchFile forKey:DConnectMessageAttribute];
        
        DConnectMessage *file = [DConnectMessage message];
        [DConnectFileDescriptorProfile setPath:TestFileDescriptorPath target:file];
        [DConnectFileDescriptorProfile setCurr:TestFileDescriptorCurr target:file];
        [DConnectFileDescriptorProfile setPrev:TestFileDescriptorPrev target:file];
        
        [DConnectFileDescriptorProfile setFile:file target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL) profile:(DConnectFileDescriptorProfile *)profile didReceiveDeleteOnWatchFileRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey
{
    
    CheckDIDAndSK(response, deviceId, sessionKey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

@end
