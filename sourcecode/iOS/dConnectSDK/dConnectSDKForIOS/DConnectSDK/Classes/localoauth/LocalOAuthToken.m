//
//  LocalOAuthToken.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthToken.h"

@implementation LocalOAuthToken
@synthesize delegate = _delegate;

/* 基底クラスなのでdelegateのメソッドを呼び出すメソッドを用意する */

- (NSString *) accessToken {
    NSString *accessToken = [_delegate accessToken];
    return accessToken;
}

- (NSString *) tokenType {
    NSString *tokenType =  [_delegate tokenType];
    return tokenType;
}

- (NSString *) refreshToken {
    NSString *refreshToken = [_delegate refreshToken];
    return refreshToken;
}

- (NSArray *) scope {
    NSArray *scope = [_delegate scope];
    return scope;
}


@end
