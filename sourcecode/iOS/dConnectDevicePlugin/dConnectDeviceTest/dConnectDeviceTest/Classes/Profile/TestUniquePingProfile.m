//
//  TestUniquePingProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "TestUniquePingProfile.h"

NSString *const UniquePingProfileProfileName = @"ping";
NSString *const UniquePingProfileInterfacePing = @"ping";
NSString *const UniquePingProfileAttributePing = @"ping";
NSString *const UniquePingProfileParamPath = @"path";

@implementation TestUniquePingProfile

#pragma mark - DConnect Profile Methods

- (NSString *) profileName {
    return UniquePingProfileProfileName;
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
    if (interface && attribute) {
        if ([interface isEqualToString:UniquePingProfileInterfacePing] &&
            [attribute isEqualToString:UniquePingProfileAttributePing]) {
            [TestUniquePingProfile setResponseParametersWithPath:@"/ping/ping/ping" response:response reqeust:request];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else if (attribute)  {
        if ([attribute isEqualToString:UniquePingProfileAttributePing]) {
            [TestUniquePingProfile setResponseParametersWithPath:@"/ping/ping" response:response reqeust:request];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [TestUniquePingProfile setResponseParametersWithPath:@"/ping" response:response reqeust:request];
    }
    return YES;
}

+ (void) setResponseParametersWithPath:(NSString*)path
                              response:(DConnectResponseMessage*)response
                               reqeust:(DConnectRequestMessage*)request
{
    response.result = DConnectMessageResultTypeOk;
    [TestUniquePingProfile setPingPath:path target:response];
    [TestUniquePingProfile copyStringsWithReqeust:request target:response];
}

+ (void) setPingPath:(NSString *)path target:(DConnectResponseMessage*)target
{
    [target setString:path forKey:UniquePingProfileParamPath];
}

+ (void) copyStringsWithReqeust:(DConnectRequestMessage*)request target:(DConnectResponseMessage*)target
{
    for (NSString *key in [request allKeys]) {
        // 以下の特別なパラメータはコピーしない.
        if ([key isEqualToString:DConnectMessageDeviceId]
            || [key isEqualToString:DConnectMessagePluginId]
            || [key isEqualToString:DConnectMessageSessionKey]
            || [key isEqualToString:DConnectMessageAccessToken]
            || [key isEqualToString:DConnectMessageProfile]
            || [key isEqualToString:DConnectMessageInterface]
            || [key isEqualToString:DConnectMessageAttribute]
            || [key isEqualToString:DConnectMessageAction]
            || [key isEqualToString:DConnectMessageResult]
            || [key isEqualToString:DConnectMessageErrorCode]
            || [key isEqualToString:DConnectMessageErrorMessage])
        {
            continue;
        }
        [target setString:[request objectForKey:key] forKey:key];
    }
}

@end
