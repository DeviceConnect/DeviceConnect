//
//  LocalOAuthSampleUser.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/** LocalOAuth用ログインユーザーID. */
extern NSString *const LOCALOAUTH_USER;

/** LocalOAuth用ログインパスワード. */
extern NSString *const LOCALOAUTH_PASS;

/** DBに保存するユーザー名. */
extern NSString *const LOCALOAUTH_USERNAME;


@interface LocalOAuthSampleUser : NSObject

@property NSString *userId;
    
@property NSString *password;
    
@property NSString *status;

+ (LocalOAuthSampleUser *)init: (NSString *)userId;

- (BOOL)equals: (id)obj;




@end
