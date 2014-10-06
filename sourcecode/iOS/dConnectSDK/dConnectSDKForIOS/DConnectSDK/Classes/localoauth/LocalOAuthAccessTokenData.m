//
//  LocalOAuthAccessTokenData.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthAccessTokenData.h"

@implementation LocalOAuthAccessTokenData

+ (LocalOAuthAccessTokenData *)accessTokenDataWithAccessToken: (NSString *)accessToken scopes:(NSArray *)scopes {

    LocalOAuthAccessTokenData *accessTokenData = [[LocalOAuthAccessTokenData alloc]init];
    
    if (accessTokenData) {
        accessTokenData._accessToken = accessToken;
        accessTokenData._scopes = scopes;
    }
    
    return accessTokenData;
}


@end
