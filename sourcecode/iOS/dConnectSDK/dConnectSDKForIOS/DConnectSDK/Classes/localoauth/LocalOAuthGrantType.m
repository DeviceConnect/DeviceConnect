//
//  LocalOAuthGrantType.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthGrantType.h"

@implementation LocalOAuthGrantTypeUtil

static NSString *const STRING_GRANT_TYPE_AUTHORIZATION_CODE = @"authorization_code";
static NSString *const STRING_GRANT_TYPE_PASSWORD = @"password";
static NSString *const STRING_GRANT_TYPE_CLIENT_CREDENTIALS = @"client_credentials";
static NSString *const STRING_GRANT_TYPE_REFRESH_TOKEN = @"refresh_token";

+ (NSString *) toString: (LocalOAuthGrantType) grantType {
    
    if (grantType == GRANT_TYPE_AUTHORIZATION_CODE) {
        return STRING_GRANT_TYPE_AUTHORIZATION_CODE;
    } else if (grantType == GRANT_TYPE_PASSWORD) {
        return STRING_GRANT_TYPE_PASSWORD;
    } else if (grantType == GRANT_TYPE_CLIENT_CREDENTIALS) {
        return STRING_GRANT_TYPE_CLIENT_CREDENTIALS;
    } else if (grantType == GRANT_TYPE_REFRESH_TOKEN) {
        return STRING_GRANT_TYPE_REFRESH_TOKEN;
    } else {
        @throw @"grantType unknown value. ";
    }
}

+ (LocalOAuthGrantType) toValue: (NSString *) str {
    
    if ([str isEqualToString: STRING_GRANT_TYPE_AUTHORIZATION_CODE]) {
        return GRANT_TYPE_AUTHORIZATION_CODE;
    } else if ([str isEqualToString: STRING_GRANT_TYPE_PASSWORD]) {
        return GRANT_TYPE_PASSWORD;
    } else if ([str isEqualToString: STRING_GRANT_TYPE_CLIENT_CREDENTIALS]) {
        return GRANT_TYPE_CLIENT_CREDENTIALS;
    } else if ([str isEqualToString: STRING_GRANT_TYPE_REFRESH_TOKEN]) {
        return GRANT_TYPE_REFRESH_TOKEN;
    } else {
        @throw @"str(GrantType) unknown value. ";
    }
}

@end
