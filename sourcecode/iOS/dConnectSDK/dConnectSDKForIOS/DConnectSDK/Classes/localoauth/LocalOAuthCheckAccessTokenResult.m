//
//  LocalOAuthCheckAccessTokenResult.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthCheckAccessTokenResult.h"

@implementation LocalOAuthCheckAccessTokenResult

+ (LocalOAuthCheckAccessTokenResult *)checkAccessTokenResultWithFlags:
    (BOOL)isExistClientId
    isExistAccessToken:(BOOL)isExistAccessToken
    isExistScope:(BOOL)isExistScope
    isNotExpired:(BOOL)isNotExpired {
    
    LocalOAuthCheckAccessTokenResult *result = [[LocalOAuthCheckAccessTokenResult alloc]init];
    
    if (result) {
        result._isExistClientId = isExistClientId;
        result._isExistAccessToken = isExistAccessToken;
        result._isExistScope = isExistScope;
        result._isNotExpired = isNotExpired;
    }
    
    return result;
}

- (BOOL) checkResult {
    BOOL result = NO;
    
    if (self._isExistClientId && self._isExistAccessToken && self._isExistScope && self._isNotExpired) {
        result = YES;
    }
    
    return result;
}

- (BOOL)isExistClientId {
    return self._isExistClientId;
}

- (BOOL)isExistAccessToken {
    return self._isExistAccessToken;
}

- (BOOL)isExistScope {
    return self._isExistScope;
}

- (BOOL)isExistNotExpired {
    return self._isNotExpired;
}



@end
