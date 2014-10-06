//
//  LocalOAuthSQLiteProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteProfile.h"

@implementation LocalOAuthSQLiteProfile

+ (LocalOAuthSQLiteProfile *)initWithProfileName: (NSString *)profileName_ {
    
    LocalOAuthSQLiteProfile *profile = [[LocalOAuthSQLiteProfile alloc]init];
    
    if (profile) {
        profile.id_ = 0;
        profile.profileName = profileName_;
        profile.profileDescription = nil;
    }
    
    return profile;
}

@end
