//
//  DPPebbleFileProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleFileProfile.h"

@interface DPPebbleFileProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;
@end


@implementation DPPebbleFileProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectFileProfileDelegate

- (BOOL) profile:(DConnectFileProfile *)profile didReceivePostSendRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
            data:(NSData *)data
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if ( deviceId == nil ) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else if (data == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"data is not specied to update a file."];
        return YES;
    } else if (path == nil||path.length==0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"path is not specied to update a file."];
        return YES;
        
    } else {
        [self.mgr sendDataToPebble:data callback:^(BOOL success) {
            if (success) {
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

@end
