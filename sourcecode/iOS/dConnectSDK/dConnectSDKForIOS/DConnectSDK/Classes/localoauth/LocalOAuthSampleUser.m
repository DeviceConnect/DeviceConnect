//
//  LocalOAuthSampleUser.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSampleUser.h"

/** LocalOAuth用ログインユーザーID. */
NSString *const LOCALOAUTH_USER = @"LocalOAuthUser";

/** LocalOAuth用ログインパスワード. */
NSString *const LOCALOAUTH_PASS = @"LocalOAuthPass";

/** DBに保存するユーザー名. */
NSString *const LOCALOAUTH_USERNAME = @"username";


@implementation LocalOAuthSampleUser

+ (LocalOAuthSampleUser *)init: (NSString *)userId {
    LocalOAuthSampleUser *user = [[LocalOAuthSampleUser alloc]init];
    
    if (user) {
        user.userId = userId;
        user.password = nil;
        user.status = nil;
    }
    
    return user;
}

- (BOOL)equals: (id)obj {
    
    if (obj == nil) {
        return NO;
    }
    
    NSString *selfClassName = NSStringFromClass([self class]);
    NSString *objClassName = NSStringFromClass([obj class]);
    if (![selfClassName isEqualToString: objClassName]) {
        return NO;
    }
    
    LocalOAuthSampleUser *objUser = (LocalOAuthSampleUser *)obj;
    
    NSString *selfUserId = self.userId;
    NSString *objUserId = objUser.userId;
    
    if (selfUserId != nil && objUserId != nil) {
        if ([selfUserId isEqualToString:objUserId]) {
            return YES;
        }
    }
    return NO;
}

@end
