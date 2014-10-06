//
//  LocalOAuthScopeDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

#import "DConnectSQLite.h"
#import "LocalOAuthSQLiteScopeDb.h"


extern NSString *const LocalOAuthScopeDaoTableName;
extern NSString *const LocalOAuthScopeDaoTokenId;
extern NSString *const LocalOAuthScopeDaoProfileId;
extern NSString *const LocalOAuthScopeDaoTimestamp;
extern NSString *const LocalOAuthScopeDaoExpirePeriod;



@interface LocalOAuthScopeDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithScopeData:(LocalOAuthSQLiteScopeDb *)scope toDatabase:(DConnectSQLiteDatabase *)database;
+ (NSArray *)loadScopes: (long long)tokenId database:(DConnectSQLiteDatabase *)database;
+ (void) updateTimestamp: (long long)tokenId
               profileId: (long long)profileId
                database: (DConnectSQLiteDatabase *)database;

@end
