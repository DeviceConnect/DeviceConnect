//
//  LocalOAuthScopeDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthScopeDao.h"

#import "LocalOAuthUtils.h"
#import "LocalOAuthProfileDao.h"
#import "LocalOAuthSQLiteScopeInfo.h"

NSString *const LocalOAuthScopeDaoTableName = @"scopes";
NSString *const LocalOAuthScopeDaoId = @"id";   /* id(PK) */
NSString *const LocalOAuthScopeDaoTokenId = @"tokens_tokenid";  /* Tokens.id(FK) */
NSString *const LocalOAuthScopeDaoProfileId = @"profiles_profileid";    /* profiles.id(FK) */
NSString *const LocalOAuthScopeDaoTimestamp = @"timestamp"; /* アクセス承認時間(System.currentTimeMills()で取得した時間) */
NSString *const LocalOAuthScopeDaoExpirePeriod = @"expire_period";  /* 有効期限[sec]({timestamp + expire_period * 1000} が有効期限が切れる時間) */

@implementation LocalOAuthScopeDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat:
        @"CREATE TABLE %@ ("
        @"%@ INTEGER PRIMARY KEY AUTOINCREMENT"
        @", %@ INTEGER"
        @", %@ INTEGER"
        @", %@ INTEGER"
        @", %@ INTEGER"
        @");",
        LocalOAuthScopeDaoTableName,
        LocalOAuthScopeDaoId,
        LocalOAuthScopeDaoTokenId,
        LocalOAuthScopeDaoProfileId,
        LocalOAuthScopeDaoTimestamp,
        LocalOAuthScopeDaoExpirePeriod];
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithScopeData:(LocalOAuthSQLiteScopeDb *)scope toDatabase:(DConnectSQLiteDatabase *)database
{
    NSArray *columns = @[
                         LocalOAuthScopeDaoTokenId,
                         LocalOAuthScopeDaoProfileId,
                         LocalOAuthScopeDaoTimestamp,
                         LocalOAuthScopeDaoExpirePeriod
                         ];
    NSArray *params = @[
                        [NSNumber numberWithLongLong: scope.tokensTokenId],
                        [NSNumber numberWithLongLong: scope.profilesProfileId],
                        [NSNumber numberWithLongLong: scope.timestamp],
                        [NSNumber numberWithLongLong: scope.expirePeriod]
                        ];
    
    long long result = [database insertIntoTable:LocalOAuthScopeDaoTableName
                                         columns:columns
                                          params:params
                        ];
    
    return result;
}

+ (void) updateTimestamp: (long long)tokenId
               profileId: (long long)profileId
                database: (DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat:
        @"UPDATE %@"
        @" SET %@ = %lld"
        @" WHERE %@ = %lld AND %@ = %lld",
        LocalOAuthScopeDaoTableName,
        LocalOAuthScopeDaoTimestamp, [LocalOAuthUtils getCurrentTimeInMillis],
        LocalOAuthScopeDaoTokenId, tokenId,
        LocalOAuthScopeDaoProfileId, profileId
    ];
    
    [database execSQL: sql];
}




/* SQLiteTokenManager.dbLoadScopes() */
/**
 * tokenIdが一致するscopesデータをdbから読み込む.
 *
 * @param db DBオブジェクト
 * @param tokenId トークンID
 * @return SQLiteScopeInfo配列
 * @throws SQLiteException SQLite例外
 */
+ (NSArray *)loadScopes: (long long)tokenId database:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat:
        @"select %@.%@, %@.%@, %@.%@, %@.%@, %@.%@"
        @" from %@, %@"
        @" where %@.%@ = %lld and %@.%@ = %@.%@",
        /* select */
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoTokenId,
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoProfileId,
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoTimestamp,
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoExpirePeriod,
        LocalOAuthProfileDaoTableName, LocalOAuthProfileDaoProfileName,
        /* from */
        LocalOAuthScopeDaoTableName, LocalOAuthProfileDaoTableName,
        /* where */
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoTokenId, tokenId,
        LocalOAuthScopeDaoTableName, LocalOAuthScopeDaoProfileId,
        LocalOAuthProfileDaoTableName, LocalOAuthProfileDaoId
    ];
    
    NSMutableArray *scopeInfos = nil;    /* SQLiteScopeInfo[] */

    DConnectSQLiteCursor *cursor = [database queryWithSQL: sql];
    if (!cursor) {
        return scopeInfos;
    }
    
    if ([cursor moveToFirst]) {
        scopeInfos = [NSMutableArray array];
        do {
            LocalOAuthSQLiteScopeInfo *scopeInfo =
            [[LocalOAuthSQLiteScopeInfo alloc]initWithParameter:[cursor longLongValueAtIndex:0]
                                 profilesProfileId:[cursor longLongValueAtIndex:1]
                                         timestamp:[cursor longLongValueAtIndex:2]
                                      expirePeriod:[cursor longLongValueAtIndex:3]
                                       profileName:[cursor stringValueAtIndex:4]
            ];
            
            [scopeInfos addObject:scopeInfo];
        } while ([cursor moveToNext]);
    }
    
    [cursor close];
    
    return scopeInfos;
}


@end



