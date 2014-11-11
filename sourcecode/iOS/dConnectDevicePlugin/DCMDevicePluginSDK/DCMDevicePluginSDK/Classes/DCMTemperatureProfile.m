//
//  DCMTemperatureProfileName.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DCMTemperatureProfile.h"
#import <DConnectSDK/DConnectUtil.h>

NSString *const DCMTemperatureProfileName = @"temperature";
NSString *const DCMTemperatureProfileParamTemperature = @"temperature";
NSString *const DCMTemperatureProfileParamType = @"type";

@interface DCMTemperatureProfile()
- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;
@end


@implementation DCMTemperatureProfile

/*
 プロファイル名。
 */
- (NSString *) profileName {
    return DCMTemperatureProfileName;
}

#pragma mark - DConnectProfile Method

/*
 GETリクエストを振り分ける。
 */
- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!self.delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *profile = [request profile];
    
    if ([profile isEqualToString:DCMTemperatureProfileName]
        && [self hasMethod:@selector(profile:didReceiveGetTemperatureRequest:response:deviceId:) response:response])
    {
        NSString *deviceId = [request deviceId];
        send = [_delegate profile:self didReceiveGetTemperatureRequest:request response:response
                         deviceId:deviceId];
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Private Methods

/**
 メソッドが存在するかを確認する。
 */
- (BOOL) hasMethod:(SEL)method
          response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}
@end