//
//  LocalOAuthAccessTokenScope.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthAccessTokenScope.h"

@implementation LocalOAuthAccessTokenScope

+ (LocalOAuthAccessTokenScope *) accessTokenScopeWithScope: (NSString *)scope expirePeriod:(long)expirePeriod {
    
    LocalOAuthAccessTokenScope *accessTokenScope = [[LocalOAuthAccessTokenScope alloc]init];
    
    if (accessTokenScope) {
        accessTokenScope._scope = scope;
        accessTokenScope._expirePeriod = expirePeriod;
    }
    
    return accessTokenScope;
}

@end
