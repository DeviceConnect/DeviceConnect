//
//  DPHostFileDescriptorProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <sys/stat.h>
#import <sys/event.h>
#import "DPHostFileDescriptorProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostDevicePlugin.h"
#import "DPHostUtils.h"

@interface DPHostFileDescriptorProfile ()

@property NSMutableDictionary *fileHandleDict;
@property NSString *openFileFlag;

@end

@implementation DPHostFileDescriptorProfile

- (instancetype)initWithFileManager:(DConnectFileManager *)fileMgr
{
    self = [super init];
    if (self) {
        self.delegate = self;
        _openFileFlag = @"";
        
        _fileHandleDict = [NSMutableDictionary dictionary];
    }
    return self;
}

#pragma mark - Get Methods

- (BOOL)         profile:(DConnectFileDescriptorProfile *)profile
didReceiveGetOpenRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                    path:(NSString *)path
                    flag:(NSString *)flag
{
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    // pathが絶対であれ相対であれベースURLに追加する。
    path = [SELF_PLUGIN pathByAppendingPathComponent:path];
    
    if (!flag || flag.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"flag must be specified."];
        return YES;
    }
    _openFileFlag = flag;
    
    NSFileHandle *fileHandle;
    if ([flag isEqualToString:@"r"]) {
        // 読み込みのみ
        
        // ファイルが存在しなければならない。そして存在するとしてもディレクトリであってはならない。
        NSFileManager *sysFileMgr = [NSFileManager defaultManager];
        BOOL isDirectory;
        if (![sysFileMgr fileExistsAtPath:path isDirectory:&isDirectory]) {
            [response setErrorToUnknownWithMessage:@"File does not exist."];
            return YES;
        } else if (isDirectory) {
            [response setErrorToUnknownWithMessage:@"Directory can not be specified."];
            return YES;
        }
        
        fileHandle = [NSFileHandle fileHandleForReadingAtPath:path];
    } else if ([flag isEqualToString:@"rw"]) {
        // 読み込みと書き込み
        
        // ディレクトリーであってはならない。
        NSFileManager *sysFileMgr = [NSFileManager defaultManager];
        BOOL isDirectory;
        if ([sysFileMgr fileExistsAtPath:path isDirectory:&isDirectory]) {
            if (isDirectory) {
                [response setErrorToUnknownWithMessage:@"Directory can not be specified."];
                return YES;
            }
        } else {
            // ファイルが無い場合は空ファイルを作成する。
            [sysFileMgr createFileAtPath:path contents:nil attributes:nil];
        }
        
        fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:path];
    } else {
        [response setErrorToInvalidRequestParameterWithMessage:@"flag is invalid"];
        return YES;
    }
    
    _fileHandleDict[path] = fileHandle;
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

- (BOOL)         profile:(DConnectFileDescriptorProfile *)profile
didReceiveGetReadRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                    path:(NSString *)path
                  length:(NSNumber *)length
                position:(NSNumber *)position
{
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    // pathが絶対であれ相対であれベースURLに追加する。
    path = [SELF_PLUGIN pathByAppendingPathComponent:path];
    
    if (!length) {
        [response setErrorToInvalidRequestParameterWithMessage:@"length must be specified."];
        return YES;
    }
    if ([length compare:@0] == NSOrderedSame || [length compare:@0] == NSOrderedAscending) {
        [response setErrorToInvalidRequestParameterWithMessage:@"length must be greater than 0."];
        return YES;
    }
    
    if (position && [position compare:@0] == NSOrderedAscending) {
        [response setErrorToInvalidRequestParameterWithMessage:@"position must be a non-negative value."];
        return YES;
    }
    
    NSFileHandle *fileHandle;
    if ((fileHandle = _fileHandleDict[path])) {
        NSData *data;
        unsigned long long oldOffset = [fileHandle offsetInFile];
        @try {
            if (position) {
                [fileHandle seekToFileOffset:[position unsignedLongLongValue]];
            }
            data = [fileHandle readDataOfLength:[length unsignedIntegerValue]];
        }
        @catch (NSException *exception) {
            [fileHandle seekToFileOffset:oldOffset];
            [response setErrorToUnknownWithMessage:@"Failed to read data."];
            return YES;
        }
        NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:path.pathExtension];
        
        [DConnectFileDescriptorProfile setSize:data.length target:response];
        if (data.length > 0) {
            NSString *dataStr = [NSString stringWithFormat:@"data:%@;base64,%@", mimeType, [data base64EncodedStringWithOptions:0]];
            [DConnectFileDescriptorProfile setFileData:dataStr target:response];
        }
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToIllegalDeviceStateWithMessage:
         @"The file specified by path is not opened; use File Descriptor Open API first to open it."];
    }
    
    return YES;
}

#pragma mark - Put Methods

- (BOOL)          profile:(DConnectFileDescriptorProfile *)profile
didReceivePutCloseRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                     path:(NSString *)path
{
    _openFileFlag = @"";
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    // pathが絶対であれ相対であれベースURLに追加する。
    path = [SELF_PLUGIN pathByAppendingPathComponent:path];
    
    if (_fileHandleDict[path]) {
        [_fileHandleDict removeObjectForKey:path];
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToIllegalDeviceStateWithMessage:
         @"The file specified by path is not opened; use File Descriptor Open API first to open it."];
    }
    
    return YES;
}

- (BOOL)          profile:(DConnectFileDescriptorProfile *)profile
didReceivePutWriteRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                     path:(NSString *)path
                    media:(NSData *)media
                 position:(NSNumber *)position
{
    if (!media || media.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"No file data"];
        return YES;
    }
    
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    // pathが絶対であれ相対であれベースURLに追加する。
    path = [SELF_PLUGIN pathByAppendingPathComponent:path];
    
    if (position && [position compare:@0] == NSOrderedAscending) {
        [response setErrorToInvalidRequestParameterWithMessage:@"position must be a non-negative value."];
        return YES;
    }
    if (_openFileFlag) {
        NSRange range = [_openFileFlag rangeOfString:@"rw"];
        if (range.location != NSNotFound) {
            NSFileHandle *fileHandle;
            if ((fileHandle = _fileHandleDict[path])) {
                unsigned long long oldOffset = [fileHandle offsetInFile];
                @try {
                    if (position) {
                        [fileHandle seekToFileOffset:[position unsignedLongLongValue]];
                        [fileHandle writeData:media];
                        [fileHandle seekToFileOffset:[position unsignedLongLongValue]+[media length]];
                    } else {
                        [fileHandle writeData:media];
                        [fileHandle seekToFileOffset:[media length]];
                    }
                }
                @catch (NSException *exception) {
                    [fileHandle seekToFileOffset:oldOffset];
                    [response setErrorToUnknownWithMessage:@"Failed to write data."];
                    return YES;
                }
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToIllegalDeviceStateWithMessage:
                 @"The file specified by path is not opened; use File Descriptor Open API first to open it."];
            }
        } else {
            [response setErrorToIllegalDeviceStateWithMessage:@"Read mode only"];
        }
    } else {
        [response setErrorToIllegalDeviceStateWithMessage:@"Invalid Flag state"];
    }
    return YES;
}

@end
