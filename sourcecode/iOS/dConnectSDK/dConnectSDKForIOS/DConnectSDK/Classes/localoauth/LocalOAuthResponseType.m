//
//  LocalOAuthResponseType.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthResponseType.h"

@implementation LocalOAuthResponseTypeUtil

static NSString *const STRING_RESPONSE_TYPE_CODE = @"code";
static NSString *const STRING_RESPONSE_TYPE_TOKEN = @"token";

+ (NSString *) toString: (LocalOAuthResponseType)responseType {
    if (responseType == RESPONSE_TYPE_CODE) {
        return STRING_RESPONSE_TYPE_CODE;
    } else if (responseType == RESPONSE_TYPE_TOKEN) {
        return STRING_RESPONSE_TYPE_TOKEN;
    } else {
        @throw @"responseType unknown value. ";
    }
}

+ (LocalOAuthResponseType) toValue: (NSString *)str {
    if ([str isEqualToString: STRING_RESPONSE_TYPE_CODE]) {
        return RESPONSE_TYPE_CODE;
    } else if ([str isEqualToString: STRING_RESPONSE_TYPE_TOKEN]) {
        return RESPONSE_TYPE_TOKEN;
    } else {
        @throw @"str(ResponseType) unknown value. ";
    }
}

@end