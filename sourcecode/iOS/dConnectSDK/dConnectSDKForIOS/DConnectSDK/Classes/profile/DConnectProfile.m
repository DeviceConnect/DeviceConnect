//
//  DConnectProfile.m
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectProfile.h"
#import "DConnectManager.h"
#import "LocalOAuth2Settings.h"

@implementation DConnectProfile

- (NSString *) profileName {
    return nil;
}

- (NSString *) displayName {
    return nil;
}

- (NSString *) detail {
    return nil;
}

- (long long) expirePeriod {
    return LocalOAuth2Settings_DEFAULT_TOKEN_EXPIRE_PERIOD / 60;
}

- (BOOL) didReceiveRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
    NSUInteger action = [request action];
    
    BOOL send = YES;
    if (DConnectMessageActionTypeGet == action) {
        send = [self didReceiveGetRequest:request response:response];
    } else if (DConnectMessageActionTypePost == action) {
        send = [self didReceivePostRequest:request response:response];
    } else if (DConnectMessageActionTypePut == action) {
        send = [self didReceivePutRequest:request response:response];
    } else if (DConnectMessageActionTypeDelete == action) {
        send = [self didReceiveDeleteRequest:request response:response];
    } else {
        [response setErrorToNotSupportAction];
    }
    
    return send;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    [response setErrorToNotSupportAction];
    return YES;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    [response setErrorToNotSupportAction];
    return  YES;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    [response setErrorToNotSupportAction];
    return YES;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    [response setErrorToNotSupportAction];
    return YES;
}

@end
