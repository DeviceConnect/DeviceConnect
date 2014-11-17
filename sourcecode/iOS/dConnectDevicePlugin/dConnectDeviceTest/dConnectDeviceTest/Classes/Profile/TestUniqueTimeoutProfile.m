//
//  TestUniqueTimeoutProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestUniqueTimeoutProfile.h"

#define DCONNECT_RESPONSE_TIMEOUT_SEC 60

NSString *const UniqueTimeoutProfileProfileName = @"timeout";
NSString *const UniqueTimeoutProfileAttributeSync = @"sync";
NSString *const UniqueTimeoutProfileAttributeAsync = @"async";

@implementation TestUniqueTimeoutProfile

#pragma mark - DConnect Profile Methods

- (NSString *) profileName {
    return UniqueTimeoutProfileProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
{
    return [self didReceiveRequestCommon:request response:response];
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
{
    return [self didReceiveRequestCommon:request response:response];
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
{
    return [self didReceiveRequestCommon:request response:response];
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response
{
    return [self didReceiveRequestCommon:request response:response];
}

#pragma mark - Private Methods

- (BOOL) didReceiveRequestCommon:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response
{
    NSString *interface = request.interface;
    NSString *attribute = request.attribute;
    BOOL send = YES;
    if (interface && attribute) {
        [response setErrorToNotSupportAttribute];
    } else if (attribute)  {
        if ([attribute isEqualToString:UniqueTimeoutProfileAttributeSync]) {
            // 同期処理としてタイムアウト発生.
            [NSThread sleepForTimeInterval:DCONNECT_RESPONSE_TIMEOUT_SEC + 1];
        } else if ([attribute isEqualToString:UniqueTimeoutProfileAttributeAsync]) {
            // 非同期処理としてタイムアウト発生.
            send = NO;
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [response setErrorToNotSupportAttribute];
    }
    return send;
}

@end
