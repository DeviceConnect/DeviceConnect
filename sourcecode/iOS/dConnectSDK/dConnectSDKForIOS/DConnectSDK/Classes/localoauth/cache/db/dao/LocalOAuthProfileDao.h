//
//  LocalOAuthProfileDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "DConnectSQLite.h"
#import "LocalOAuthSQLiteProfile.h"

extern NSString *const LocalOAuthProfileDaoTableName;
extern NSString *const LocalOAuthProfileDaoId;
extern NSString *const LocalOAuthProfileDaoProfileName;
extern NSString *const LocalOAuthProfileDaoDescription;

@interface LocalOAuthProfileDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithName:(NSString *)name toDatabase:(DConnectSQLiteDatabase *)database;
+ (NSArray *) load: (DConnectSQLiteDatabase *) database;
+ (LocalOAuthSQLiteProfile *) findByProfileName: (NSString *)name
                                 database:(DConnectSQLiteDatabase *)database;
@end
