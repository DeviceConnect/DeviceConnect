//
//  DConnectFilesProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <AssetsLibrary/AssetsLibrary.h>

#import "DConnectFilesProfile.h"
#import "DConnectFileManager.h"

@implementation DConnectFilesProfile

NSString *const DConnectFilesProfileName = @"files";
NSString *const DConnectFilesProfileParamMimeType = @"mimeType";
NSString *const DConnectFilesProfileParamData = @"data";
NSString *const DConnectFilesProfileParamUri = @"uri";

- (NSString *) profileName {
    return DConnectFilesProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    // NSURLを文字列からインスタンス化するには、URIで禁止されている文字をエスケープしなければならない。
    NSCharacterSet *allowedCharSet =
    [[NSCharacterSet characterSetWithCharactersInString:@";@$+{}<>, "] invertedSet];
    NSURL *uri = [NSURL URLWithString:
                  [[request stringForKey:DConnectFilesProfileParamUri]
                   stringByAddingPercentEncodingWithAllowedCharacters:allowedCharSet]];
    if (!uri) {
        [response setErrorToInvalidRequestParameterWithMessage:@"uri cannot be omitted."];
        return YES;
    }
    NSString *mimeType = [DConnectFileManager searchMimeTypeForExtension:[uri.path pathExtension]];
    if (!mimeType) {
        mimeType = [DConnectFileManager mimeTypeForArbitraryData];
    }
    
    if ([[uri scheme] isEqualToString:@"assets-library"]) {
        // アセット：専用のアクセス手段を必要とする。
        // 常に待つので0を指定しておく
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 10);
        
        ALAssetsLibrary *library = [ALAssetsLibrary new];
        [library assetForURL:uri
                 resultBlock:
         ^(ALAsset *asset) {
             if (!asset) {
                 [response setErrorToUnknownWithMessage:@"Failed to access data (4)."];
                 dispatch_semaphore_signal(semaphore);
                 return;
             }
             ALAssetRepresentation *rep = [asset defaultRepresentation];
             NSError *error;
             UInt8 *buffer = (UInt8 *)malloc(rep.size);
             NSUInteger bufferedLen = [rep getBytes:buffer fromOffset:0 length:rep.size error:&error];
             if (error) {
                 [response setErrorToUnknownWithMessage:@"Failed to access data (2)."];
                 dispatch_semaphore_signal(semaphore);
                 return;
             }
             NSData *data = [NSData dataWithBytesNoCopy:buffer length:bufferedLen freeWhenDone:YES];
             if (!data) {
                 [response setErrorToUnknownWithMessage:@"Failed to access data (3)."];
                 dispatch_semaphore_signal(semaphore);
                 return;
             }
             [response setData:data forKey:DConnectFilesProfileParamData];
             [response setString:mimeType forKey:DConnectFilesProfileParamMimeType];
             [response setResult:DConnectMessageResultTypeOk];
             
             dispatch_semaphore_signal(semaphore);
         }
                failureBlock:
         ^(NSError *error) {
             [response setErrorToUnknownWithMessage:@"Failed to access data (1)."];
             dispatch_semaphore_signal(semaphore);
         }];
        
        dispatch_semaphore_wait(semaphore, timeout);
        
        return YES;
    } else {
        NSFileHandle *handle = [NSFileHandle fileHandleForReadingFromURL:uri error:nil];
        if (handle) {
            NSData *data = [handle availableData];
            if (data) {
                [response setData:data forKey:DConnectFilesProfileParamData];
                [response setString:mimeType forKey:DConnectFilesProfileParamMimeType];
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToInvalidRequestParameter];
            }
        } else {
            [response setErrorToInvalidRequestParameterWithMessage:@"File does not exist."];
        }
        return YES;
    }
}

@end
