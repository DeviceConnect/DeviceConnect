//
//  LocalOAuthRedirectionURI.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthRedirectionURI.h"

@implementation LocalOAuthRedirectionURI

+ (LocalOAuthRedirectionURI *) init: (NSString *)uri_ {
    LocalOAuthRedirectionURI *redirectionURI = [self initWithdynamicConfigured:uri_ dynamicConfigured:NO];
    return redirectionURI;
}

+ (LocalOAuthRedirectionURI *) initWithdynamicConfigured:
    (NSString *)uri_ dynamicConfigured:(BOOL)dynamicConfigured {
    
    LocalOAuthRedirectionURI *redirectionURI = [[LocalOAuthRedirectionURI alloc]init];
    
    if (redirectionURI) {
        redirectionURI.uri = uri_;
        redirectionURI.dynamicConfigured = dynamicConfigured;
    }
    
    return redirectionURI;
}

- (NSString *)toString {
    return [self getURI];
}

- (NSString *)getURI {
    return [self uri];
}

- (BOOL) isDynamicConfigured {
    return [self dynamicConfigured];
}

@end
