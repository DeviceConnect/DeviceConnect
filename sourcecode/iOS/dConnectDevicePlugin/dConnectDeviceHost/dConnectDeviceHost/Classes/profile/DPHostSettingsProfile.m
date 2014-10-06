//
//  DPHostSettingsProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHostSettingsProfile.h"

@implementation DPHostSettingsProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - Get Methods

- (BOOL)         profile:(DConnectSettingsProfile *)profile
didReceiveGetDateRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
    NSDateFormatter *rfc3339DateFormatter = [[NSDateFormatter alloc] init];
    NSLocale *enUSPOSIXLocale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US_POSIX"];
    
    [rfc3339DateFormatter setLocale:enUSPOSIXLocale];
    [rfc3339DateFormatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ssZZZ"];
    [rfc3339DateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    
    [DConnectSettingsProfile setDate:[rfc3339DateFormatter stringFromDate:[NSDate date]] target:response];
    [response setResult:DConnectMessageResultTypeOk];

    return YES;
}

- (BOOL)          profile:(DConnectSettingsProfile *)profile
didReceiveGetLightRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
    [DConnectSettingsProfile setLightLevel:[UIScreen mainScreen].brightness target:response];
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

#pragma mark - Put Methods

- (BOOL)          profile:(DConnectSettingsProfile *)profile
didReceivePutLightRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                    level:(NSNumber *)level
{
    if (!level) {
        [response setErrorToInvalidRequestParameterWithMessage:@"level must be specified."];
        return YES;
    }
    if ([level compare:@0] == NSOrderedAscending || [level compare:@1] == NSOrderedDescending) {
        [response setErrorToInvalidRequestParameterWithMessage:@"level must be within range of [0, 1.0]."];
        return YES;
    }
    
    [UIScreen mainScreen].brightness = [level doubleValue];
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

@end
