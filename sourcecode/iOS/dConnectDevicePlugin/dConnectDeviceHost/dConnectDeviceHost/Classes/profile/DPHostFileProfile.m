//
//  DPHostFileProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectFileManager.h>
#import "DPHostFileProfile.h"
#import "DPHostDevicePlugin.h"
#import "DPHostUtils.h"

@interface DPHostFileProfile ()

//@property NSMutableDictionary *mimeExtDict;
@property NSMutableDictionary *mediaIdUriDict;

@end

@implementation DPHostFileProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectFileProfileDelegate
#pragma mark Get Methods
- (BOOL)            profile:(DConnectFileProfile *)profile
didReceiveGetReceiveRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                       path:(NSString *)path
{
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    // pathが絶対であれ相対であれベースURLに追加する。
    NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
    if ([self checkPath:dstPath]) {
        dstPath = path;
    }
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    BOOL isDirectory;
    if (![sysFileMgr fileExistsAtPath:dstPath isDirectory:&isDirectory]) {
        [response setErrorToUnknownWithMessage:@"File does not exists."];
        return YES;
    } else if (isDirectory) {
        [response setErrorToUnknownWithMessage:@"Directory can not be specified."];
        return YES;
    }
    
    [DConnectFileProfile setURI:[[NSURL fileURLWithPath:dstPath] absoluteString] target:response];
    NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:[dstPath pathExtension]];
    if (!mimeType) {
        mimeType = [DConnectFileManager mimeTypeForArbitraryData];
    }
    [DConnectFileProfile setMIMEType:mimeType target:response];
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

- (BOOL)         profile:(DConnectFileProfile *)profile
didReceiveGetListRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
                    path:(NSString *)path
                mimeType:(NSString *)mimeType
                   order:(NSArray *)order
                  offset:(NSNumber *)offset
                   limit:(NSNumber *)limit
{
    DConnectFileManager *fileMgr = [SELF_PLUGIN fileMgr];
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    if (path) {
        // pathが絶対であれ相対であれベースURLに追加する。
        if ([path isEqualToString:@".."]) {
            path = @"/";
        }
        NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
        if (![self checkPath:dstPath]) {
            path = dstPath;
        }
        if (![sysFileMgr fileExistsAtPath:path]) {
            [response setErrorToInvalidRequestParameterWithMessage:@"path is invalid"];
            return YES;
        }
    } else {
        path = fileMgr.URL.path;
    }
    NSString *sortTarget;
    NSString *sortOrder;
    if (order) {
        if (order.count != 2) {
            [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
            return YES;
        }
        
        sortTarget = order[0];
        sortOrder = order[1];
        
        if (!sortTarget || !sortOrder) {
            [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
            return YES;
        }
    } else {
        sortTarget = DConnectFileProfileParamPath;
        sortOrder = DConnectFileProfileOrderASC;
    }
    
    // ソート対象の文字列表現を返却するブロックを用意する。
    __block id (^accessor)(id);
    if ([sortTarget isEqualToString:DConnectFileProfileParamPath]) {
        accessor = ^id(id obj) {
            return [(DConnectMessage *)obj stringForKey:DConnectFileProfileParamPath];
        };
    } else if ([sortTarget isEqualToString:DConnectFileProfileParamFileName]) {
        accessor = ^id(id obj) {
            return [(DConnectMessage *)obj stringForKey:DConnectFileProfileParamFileName];
        };
    } else if ([sortTarget isEqualToString:DConnectFileProfileParamMIMEType]) {
        accessor = ^id(id obj) {
            return [(DConnectMessage *)obj stringForKey:DConnectFileProfileParamMIMEType];
        };
    } else if ([sortTarget isEqualToString:DConnectFileProfileParamUpdateDate]) {
        accessor = ^id(id obj) {
            return [(DConnectMessage *)obj stringForKey:DConnectFileProfileParamUpdateDate];
        };
    } else if ([sortTarget isEqualToString:DConnectFileProfileParamFileSize]) {
        accessor = ^id(id obj) {
            return [[(DConnectMessage *)obj objectForKey:DConnectFileProfileParamFileSize]
                    descriptionWithLocale:nil];
        };
    } else if ([sortTarget isEqualToString:DConnectFileProfileParamFileType]) {
        accessor = ^id(id obj) {
            return [[(DConnectMessage *)obj objectForKey:DConnectFileProfileParamFileType]
                    descriptionWithLocale:nil];
        };
    } else {
        [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
        return YES;
    }
    
    NSComparator comp;
    if ([sortOrder isEqualToString:DConnectFileProfileOrderASC]) {
        comp = ^NSComparisonResult(id obj1, id obj2) {
            id obj1Tmp = accessor(obj1);
            id obj2Tmp = accessor(obj2);
            return [obj1Tmp localizedCaseInsensitiveCompare:obj2Tmp];
        };
    } else if ([sortOrder isEqualToString:DConnectFileProfileOrderDESC]) {
        comp = ^NSComparisonResult(id obj1, id obj2) {
            id obj1Tmp = accessor(obj1);
            id obj2Tmp = accessor(obj2);
            return [obj2Tmp localizedCaseInsensitiveCompare:obj1Tmp];
        };
    } else {
        [response setErrorToInvalidRequestParameterWithMessage:@"order is invalid."];
        return YES;
    }
    
    if (offset) {
        if (offset.integerValue < 0) {
            [response setErrorToInvalidRequestParameterWithMessage:@"offset must be a non-negative value."];
            return YES;
        }
    }
    
    if (limit) {
        if (limit.integerValue < 0) {
            [response setErrorToInvalidRequestParameterWithMessage:@"limit must be a positive value."];
            return YES;
        }
    }
    
    NSMutableArray *fileArr = [NSMutableArray array];
    
    // # NSDirectoryEnumeratorからプロパティを取得する方法
    // 「- enumeratorAtURL:includingPropertiesForKeys:options:errorHandler:」で返ってきたNSDirectoryEnumerator
    // の「- directoryAttributes」や「- fileAttributes」はnilを返す。
    // 代わりにNSURLの「- resourceValuesForKeys:error:」などで取得すること。
    NSString *rootPath = [@"/private" stringByAppendingString:fileMgr.URL.path];
    BOOL mkBackRoot = NO;
    NSDirectoryEnumerator *dirIter =
    [fileMgr enumeratorWithOptions:NSDirectoryEnumerationSkipsSubdirectoryDescendants dirPath:path];
    NSURL *url;
    while ((url = dirIter.nextObject))
    {
        NSString *pathItr = url.path;
        // MIMEタイプ検索
        if (mimeType) {
            NSString *thisMimeType = [DConnectFileManager searchMimeTypeForExtension:url.pathExtension];
            NSRange result = [thisMimeType rangeOfString:mimeType.lowercaseString];
            if (result.location == NSNotFound && result.length == 0) {
                // MIMEタイプにマッチせず；スキップ。
                continue;
            }
        }
        
        DConnectMessage *file = [DConnectMessage message];
        
        [DConnectFileProfile setPath:pathItr target:file];
        [DConnectFileProfile setFileName:url.lastPathComponent target:file];
        
        NSDate *modifiedDate;
        [url getResourceValue:&modifiedDate forKey:NSURLAttributeModificationDateKey error:nil];
        NSDateFormatter *rfc3339DateFormatter = [[NSDateFormatter alloc] init];
        [rfc3339DateFormatter setLocale:[[NSLocale alloc] initWithLocaleIdentifier:@"en_US_POSIX"]];
        [rfc3339DateFormatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ssZZZ"];
        [rfc3339DateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
        [DConnectFileProfile setUpdateDate:[rfc3339DateFormatter stringFromDate:modifiedDate] tareget:file];
        
        NSNumber *fileSize;
        [url getResourceValue:&fileSize forKey:NSURLFileSizeKey error:nil];
        [DConnectFileProfile setFileSize:[fileSize longLongValue] target:file];
        
        NSString *pluginRootPath = [pathItr stringByReplacingOccurrencesOfString:rootPath withString:@""];
        NSArray *dirCount = [pluginRootPath componentsSeparatedByString:@"/"];
        //rootディレクトリでなければ、一つ上のディレクトリに戻るためのディレクトリを追加する。
        if (dirCount.count > 2 && !mkBackRoot) {
            DConnectMessage *upDir = [DConnectMessage message];
            [DConnectFileProfile setPath:rootPath target:upDir];
            [DConnectFileProfile setFileName:@".." target:upDir];
            [DConnectFileProfile setUpdateDate:[rfc3339DateFormatter stringFromDate:modifiedDate] tareget:upDir];
            [DConnectFileProfile setMIMEType:@"dir/folder" target:upDir];
            [DConnectFileProfile setFileType:1 target:upDir];
            [DConnectFileProfile setFileSize:0 target:upDir];
            [fileArr addObject:upDir];
            mkBackRoot = YES;
        }
        BOOL isDirectory;
        [sysFileMgr fileExistsAtPath:pathItr isDirectory:&isDirectory];
        if (isDirectory) {
            [DConnectFileProfile setMIMEType:@"dir/folder" target:file];
            [DConnectFileProfile setFileType:1 target:file];
        } else {
            NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:url.pathExtension.lowercaseString];
            if (!mimeType) {
                mimeType = [DConnectFileManager mimeTypeForArbitraryData];
            }
            [DConnectFileProfile setMIMEType:mimeType target:file];
            [DConnectFileProfile setFileType:0 target:file];
        }
        
        [fileArr addObject:file];
    }
    
    if (offset) {
        if (offset.integerValue >= fileArr.count) {
            [response setErrorToInvalidRequestParameterWithMessage:@"offset exceeds the size of the media list."];
            return YES;
        }
    }
    
    // 並び替えを実行
    NSArray *tmpArr = [fileArr sortedArrayUsingComparator:comp];
    
    // ページングのために配列の一部分だけ抜き出し
    if (offset || limit) {
        NSUInteger offsetVal = offset ? offset.unsignedIntegerValue : 0;
        NSUInteger limitVal = limit ? limit.unsignedIntegerValue : fileArr.count;
        tmpArr = [tmpArr subarrayWithRange:
                  NSMakeRange(offset.unsignedIntegerValue,
                              MIN(fileArr.count - offsetVal, limitVal))];
    }
    
    [DConnectFileProfile setCount:(int)tmpArr.count target:response];
    [DConnectFileProfile setFiles:[DConnectArray initWithArray:tmpArr] target:response];
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

#pragma mark - Post Methods
- (BOOL)          profile:(DConnectFileProfile *)profile
didReceivePostSendRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                     path:(NSString *)path
                 mimeType:(NSString *)mimeType
                     data:(NSData *)data
{
    if (!data || data.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"No file data"];
        return YES;
    }
    
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    DConnectFileManager *fileMgr = [SELF_PLUGIN fileMgr];
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    
    // pathが絶対であれ相対であれベースURLに追加する。
    NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
    if ([self checkPath:dstPath]) {
        dstPath = path;
    }
    if ([sysFileMgr fileExistsAtPath:dstPath]) {
        // ファイルが既に存在している
        [response setErrorToInvalidRequestParameterWithMessage:
         @"File already exists at the specified path."];
    } else {
        NSString *resultPath = [fileMgr createFileForPath:dstPath contents:data];
        if (resultPath) {
            [DConnectFileProfile setPath:dstPath target:response];
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknownWithMessage:@"File creation failed"];
        }
    }
    
    return YES;
}

- (BOOL)           profile:(DConnectFileProfile *)profile
didReceivePostMkdirRequest:(DConnectRequestMessage *)request
                  response:(DConnectResponseMessage *)response
                  deviceId:(NSString *)deviceId
                      path:(NSString *)path
{
    if (!path) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    
    // pathが絶対であれ相対であれベースURLに追加する。
    NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
    if ([self checkPath:dstPath]) {
        dstPath = path;
    }
    if ([sysFileMgr fileExistsAtPath:dstPath]) {
        // ディレクトリが既に存在している
        [response setErrorToUnknownWithMessage:
         @"File/directory already exists at the specified path."];
    } else {
        BOOL result = [sysFileMgr createDirectoryAtPath:dstPath
                            withIntermediateDirectories:YES attributes:nil error:nil];
        if (result) {
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToUnknownWithMessage:@"File creation failed"];
        }
    }
    
    return YES;
}

#pragma mark - Delete Methods
- (BOOL)              profile:(DConnectFileProfile *)profile
didReceiveDeleteRemoveRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
                         path:(NSString *)path
{
    if (!path || path.length == 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    NSError *error;

    // pathが絶対であれ相対であれベースURLに追加する。
    NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
    if ([self checkPath:dstPath]) {
        dstPath = path;
    }
    BOOL isDirectory;
    if (![sysFileMgr fileExistsAtPath:dstPath isDirectory:&isDirectory]) {
        [response setErrorToUnknownWithMessage:@"File does not exist."];
        return YES;
    } else if (isDirectory) {
        [response setErrorToUnknownWithMessage:@"Directory can not be specified; use Remove Directory API instead."];
        return YES;
    }
    
    [sysFileMgr removeItemAtPath:dstPath error:&error];
    if ([sysFileMgr fileExistsAtPath:dstPath] || error) {
        [response setErrorToUnknownWithMessage:@"File operation failed."];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
    }
    
    return YES;
}

- (BOOL)             profile:(DConnectFileProfile *)profile
didReceiveDeleteRmdirRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
                        path:(NSString *)path
                       force:(BOOL)force
{
    if (!path) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path must be specified."];
        return YES;
    }
    
    NSFileManager *sysFileMgr = [NSFileManager defaultManager];
    
    // pathが絶対であれ相対であれベースURLに追加する。
    NSString *dstPath = [SELF_PLUGIN pathByAppendingPathComponent:path];
    if ([self checkPath:dstPath]) {
        dstPath = path;
    }
    BOOL isDirectory;
    if (![sysFileMgr fileExistsAtPath:dstPath isDirectory:&isDirectory]) {
        // ディレクトリが存在しない
        [response setErrorToUnknownWithMessage:
         @"Directory does not exist at the specified path."];
    } else {
        if (isDirectory) {
            NSArray *contents = [sysFileMgr contentsOfDirectoryAtPath:dstPath error:nil];
            if (contents.count != 0 && !force) {
                [response setErrorToUnknownWithMessage:
                 @"Could not delete a directory containing files; set force to YES for a recursive deletion."];
            } else {
                BOOL result = [sysFileMgr removeItemAtPath:dstPath error:nil];
                if (result) {
                    [response setResult:DConnectMessageResultTypeOk];
                } else {
                    [response setErrorToUnknownWithMessage:@"Failed to remove the speficified directory."];
                }
            }
        } else {
            // パスでしていされた項目がディレクトリではない
            [response setErrorToUnknownWithMessage:
             @"File specified by path is not a directory."];
        }
    }
    
    return YES;
}

//不正なパスかどうかを検査する
-(BOOL)checkPath:(NSString*)dstPath {
    NSMutableArray *results = [NSMutableArray array];
    NSRange target = NSMakeRange(0, [dstPath length]);
    NSString *word = @"/var/mobile/Applications";
    
    // 全件検索
    while (target.location != NSNotFound) {
        
        // 検索
        target = [dstPath rangeOfString:word options:0 range:target];
        if (target.location != NSNotFound) {
            
            // 結果格納
            [results addObject:[NSValue valueWithRange:target]];
            
            // 次の検索範囲を設定
            int from = (int) (target.location + [word length]);
            int end = (int) ([dstPath length] - from);
            target = NSMakeRange(from, end);
        }
    }
    if ([results count] >= 2) {
        return YES;
    } else {
        return NO;
    }
}
@end
