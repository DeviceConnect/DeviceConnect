//
//  TestFileProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestFileProfile.h"
#import "DeviceTestPlugin.h"

NSString *const TestFileMimeType = @"image/png";
const int TestFileFileSize = 64000;
const int TestFileFileType = 0;
NSString *const TestFileFileName = @"test.png";
NSString *const TestFilePath = @"/test.png";

@interface TestFileProfile() {
    DConnectFileManager *_fm;
}

- (NSString *) fileNameFromPath:(NSString *)path;

@end

@implementation TestFileProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
        _fm = [DConnectFileManager fileManagerForPlugin:_plugin];
    }
    
    return self;
}

#pragma mark - Get Methods

- (BOOL) profile:(DConnectFileProfile *)profile didReceiveGetReceiveRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
{
    
    CheckDID(response, deviceId)
    if (path == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    
        NSString *fileName = [self fileNameFromPath:path];
        NSString *uri = [[[_fm URL] URLByAppendingPathComponent:fileName] absoluteString];
    
        [DConnectFileProfile setURI:uri target:response];
        [DConnectFileProfile setMIMEType:TestFileMimeType target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectFileProfile *)profile didReceiveGetListRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
           order:(NSArray *)order
          offset:(NSNumber *)offset
           limit:(NSNumber *)limit
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        DConnectArray *files = [DConnectArray array];
        DConnectMessage *file = [DConnectMessage message];
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        formatter.dateFormat = @"";
        
        [DConnectFileProfile setFileName:TestFileFileName target:file];
        [DConnectFileProfile setPath:TestFilePath target:file];
        [DConnectFileProfile setMIMEType:TestFileMimeType target:file];
        [DConnectFileProfile setUpdateDate:[formatter stringFromDate:[NSDate date]] tareget:file];
        [DConnectFileProfile setFileSize:TestFileFileSize target:file];
        [DConnectFileProfile setFileType:TestFileFileType target:file];
        
        [files addMessage:file];
        [DConnectFileProfile setFiles:files target:response];
        [DConnectFileProfile setCount:files.count target:response];
    }
    
    return YES;
}

#pragma mark - Post Methods

- (BOOL) profile:(DConnectFileProfile *) profile didReceivePostSendRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
            data:(NSData *)data
{
    
    CheckDID(response, deviceId)
    if (path == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        NSString *fileName = [self fileNameFromPath:path];
        NSString *url = [_fm createFileForPath:fileName contents:data];
        
        if (url) {
            response.result = DConnectMessageResultTypeOk;
        } else {
            [response setErrorToUnknown];
        }
    }
    
    return YES;
}


#pragma mark - Put Methods

- (BOOL) profile:(DConnectFileProfile *)profile didReceivePutUpdateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
           media:(NSData *)media
{
    CheckDID(response, deviceId)
    if (path == nil || media == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        NSString *fileName = [self fileNameFromPath:path];
        [_fm removeFileForPath:fileName];
        NSString *url = [_fm createFileForPath:fileName contents:media];
        
        if (url) {
            response.result = DConnectMessageResultTypeOk;
        } else {
            [response setErrorToUnknown];
        }
    }
    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL) profile:(DConnectFileProfile *)profile didReceiveDeleteRemoveRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
{
    
    CheckDID(response, deviceId)
    if (path == nil) {
        [response setErrorToInvalidRequestParameter];
    } else {
        NSString *fileName = [self fileNameFromPath:path];
        if([_fm removeFileForPath:fileName]) {
            response.result = DConnectMessageResultTypeOk;
        } else {
            [response setErrorToUnknownWithMessage:[NSString stringWithFormat:@"Failed to remove file: %@", path]];
        }
    }
    
    return YES;
}

#pragma mark - Private

- (NSString *) fileNameFromPath:(NSString *)path {
    
    NSArray *components = [path componentsSeparatedByString:@"/"];
    if (!components || components.count == 0) {
        return path;
    }
    
    return (NSString *) [components objectAtIndex:components.count - 1];
}


@end
