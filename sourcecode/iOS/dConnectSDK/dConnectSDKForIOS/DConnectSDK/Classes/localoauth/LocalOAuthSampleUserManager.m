//
//  LocalOAuthSampleUserManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSampleUserManager.h"

@implementation LocalOAuthSampleUserManager

+ (LocalOAuthSampleUserManager *)init {
    LocalOAuthSampleUserManager *userManager = [[LocalOAuthSampleUserManager alloc]init];
    
    if (userManager) {
        userManager.mUserSet = [NSMutableArray array];
    }
    
    return userManager;
}

- (LocalOAuthSampleUser *)addUser:(NSString *)userId {
    LocalOAuthSampleUser *user = [LocalOAuthSampleUser init: userId];
    
    if ([self findUserById: userId] == nil) {
        [_mUserSet addObject: user];
        return user;
    }
    return nil;
}

- (LocalOAuthSampleUser *) findUserById: (NSString *) userId {
    
    NSUInteger c = [_mUserSet count];
    for (NSUInteger i = 0; i < c; i++) {
        LocalOAuthSampleUser * user = [_mUserSet objectAtIndex: i];
        
        if ([userId isEqualToString: [user userId]]) {
            return user;
        }
    }
    return nil;
}

@end
