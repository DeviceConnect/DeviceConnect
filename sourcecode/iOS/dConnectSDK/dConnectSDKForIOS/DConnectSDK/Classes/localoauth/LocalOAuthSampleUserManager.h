//
//  LocalOAuthSampleUserManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthSampleUser.h"

@interface LocalOAuthSampleUserManager : NSObject

/* UserSet */
@property NSMutableArray *mUserSet;

+ (LocalOAuthSampleUserManager *)init;
- (LocalOAuthSampleUser *)addUser:(NSString *)userId;
- (LocalOAuthSampleUser *) findUserById: (NSString *) userId;


@end
