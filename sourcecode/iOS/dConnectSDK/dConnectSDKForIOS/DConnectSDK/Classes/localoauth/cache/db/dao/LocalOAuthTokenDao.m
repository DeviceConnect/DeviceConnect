//
//  LocalOAuthTokenDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthTokenDao.h"
#import "LocalOAuthProfileDao.h"
#import "LocalOAuthScopeDao.h"
#import "LocalOAuthUtils.h"
#import "LocalOAuthDummy.h"
#import "LocalOAuthSQLiteToken.h"
#import "LocalOAuthSQLiteScopeDb.h"
#import "LocalOAuthScope.h"


NSString *const LocalOAuthTokenDaoTableName = @"tokens";
NSString *const LocalOAuthTokenDaoId = @"id";                              /* ID */
NSString *const LocalOAuthTokenDaoAccessToken = @"access_token";                 /* アクセストークン */
NSString *const LocalOAuthTokenDaoTokenType = @"token_type";           /* パッケージ名 */
NSString *const LocalOAuthTokenDaoClientId = @"client_id";                 /* clients.id(FK) */
NSString *const LocalOAuthTokenDaoUserId = @"users_userid";         /* クライアントシークレット */
NSString *const LocalOAuthTokenDaoRegistrationDate = @"registration_date"; /* 登録日時(System.currentTimeMillis()で取得した値を格納する) */
NSString *const LocalOAuthTokenDaoAccessDate = @"access_date"; /* 最終アクセス時間(System.currentTimeMills()で取得した時間) */
NSString *const LocalOAuthTokenDaoApplicationName = @"application_name"; /* アプリケーション名 */


@interface LocalOAuthTokenDao() {
    
}

/* private method */
+ (NSArray *) loadTokens: (NSString *)where
                database: (DConnectSQLiteDatabase *)database;
+ (void) deleteTokens_: (NSString *)where
             database: (DConnectSQLiteDatabase *)database;

@end


@implementation LocalOAuthTokenDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat: @"CREATE TABLE %@ ("
                            @"%@ INTEGER PRIMARY KEY AUTOINCREMENT"
                            @", %@ TEXT"
                            @", %@ TEXT"
                            @", %@ TEXT"
                            @", %@ INTEGER"
                            @", %@ INTEGER"
                            @", %@ INTEGER"
                            @", %@ TEXT"
                            @");",
                            LocalOAuthTokenDaoTableName,
                            LocalOAuthTokenDaoId,
                            LocalOAuthTokenDaoAccessToken,
                            LocalOAuthTokenDaoTokenType,
                            LocalOAuthTokenDaoClientId,
                            LocalOAuthTokenDaoUserId,
                            LocalOAuthTokenDaoRegistrationDate,
                            LocalOAuthTokenDaoAccessDate,
                            LocalOAuthTokenDaoApplicationName
                            ];
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithTokenData:(LocalOAuthSQLiteToken *)token toDatabase:(DConnectSQLiteDatabase *)database {
    NSArray *columns = @[
                         LocalOAuthTokenDaoAccessToken,
                         LocalOAuthTokenDaoTokenType,
                         LocalOAuthTokenDaoClientId,
                         LocalOAuthTokenDaoUserId,
                         LocalOAuthTokenDaoRegistrationDate,
                         LocalOAuthTokenDaoAccessDate,
                         LocalOAuthTokenDaoApplicationName
                         ];
    NSArray *params = @[
                        [token accessToken],
                        [token tokenType],
                        [token clientId],
                        [NSNumber numberWithLongLong:USERS_USER_ID],
                        [NSNumber numberWithLongLong:[token registrationDate]],
                        [NSNumber numberWithLongLong:[token accessDate]],
                        [token applicationName]
                        ];
    
    long long result = [database insertIntoTable:LocalOAuthTokenDaoTableName
                                         columns:columns
                                          params:params
                        ];
    
    return result;
}

+ (LocalOAuthToken *) findToken: (LocalOAuthClient *)client
             username: (NSString *)username
             database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [NSString stringWithFormat:
                       @"%@='%@' AND %@='%lld'"
                       , LocalOAuthTokenDaoClientId
                       , [client clientId]
                       , LocalOAuthTokenDaoUserId
                       , (long long)USERS_USER_ID
                       ];
    NSArray *tokens = [self loadTokens: where
                                database: database];
    if (tokens == nil || [tokens count] == 0) {
        return nil;
    } else if ([tokens count] == 1) {
        return tokens[0];
    } else {
        @throw @"クライアントIDとユーザー名の組み合わせに該当するトークンが2件以上存在しています。";
    }
}

+ (LocalOAuthToken *) findTokenByAccessToken: (NSString *)accessToken
                                    database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [NSString stringWithFormat:
                       @"%@='%@'"
                       , LocalOAuthTokenDaoAccessToken
                       , accessToken
                       ];
    NSArray *tokens = [self loadTokens: where
                              database: database];
    if (tokens == nil || [tokens count] == 0) {
        return nil;
    } else if ([tokens count] == 1) {
        return tokens[0];
    } else {
        @throw @"アクセストークンに該当するトークンが2件以上存在しています。";
    }
}

+ (NSArray *) findTokensByUsername: (NSString *)username
                          database: (DConnectSQLiteDatabase *)database {
    NSString *where = [NSString stringWithFormat:
                       @"%@='%lld'"
                       , LocalOAuthTokenDaoUserId
                       , (long long)USERS_USER_ID
                       ];
    NSArray *tokens = [self loadTokens: where
                              database: database];
    return tokens;
}


/* private */
+ (NSArray *) loadTokens: (NSString *)where
                 database: (DConnectSQLiteDatabase *)database {
    
    NSMutableArray *tokens = nil;
    NSString *sql = [NSString stringWithFormat:
                     @"select"
                     @"  %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @" from %@"
                     @" where %@",
                     LocalOAuthTokenDaoId,
                     LocalOAuthTokenDaoAccessToken,
                     LocalOAuthTokenDaoTokenType,
                     LocalOAuthTokenDaoClientId,
                     LocalOAuthTokenDaoUserId,
                     LocalOAuthTokenDaoRegistrationDate,
                     LocalOAuthTokenDaoAccessDate,
                     LocalOAuthTokenDaoApplicationName,
                     LocalOAuthTokenDaoTableName,
                     where
                     ];
    DConnectSQLiteCursor *cursor = [database queryWithSQL: sql];
    if (!cursor) {
        return tokens;
    }
    
    if ([cursor moveToFirst]) {
        tokens = [NSMutableArray array];
        do {
            /* DBから読み込んだ値を格納する */
            LocalOAuthSQLiteToken *sqliteToken = [[LocalOAuthSQLiteToken alloc]init];
            [sqliteToken setId: [cursor longLongValueAtIndex:0]];
            [sqliteToken setAccessToken: [cursor stringValueAtIndex:1]];
            [sqliteToken setTokenType: [cursor stringValueAtIndex:2]];
            [sqliteToken setRefreshToken: nil];
            [sqliteToken setScope: nil];
            [sqliteToken setClientId: [cursor stringValueAtIndex:3]];
            [sqliteToken setUsername: nil];
            [sqliteToken setRegistrationDate: [cursor longLongValueAtIndex:5]];
            [sqliteToken setAccessDate: [cursor longLongValueAtIndex:6]];
            [sqliteToken setApplicationName: [cursor stringValueAtIndex:7]];
            
            /* LocalOAuthTokenのdelegateに入れて配列に格納する */
            LocalOAuthToken *token = [[LocalOAuthToken alloc]init];
            token.delegate = sqliteToken;
            [tokens addObject:token];
            
        } while ([cursor moveToNext]);
    }
    
    [cursor close];
    
    /* 1件以上データが存在する */
    if (tokens != nil && [tokens count] > 0) {
        
        /* tokenごとにscopesを読み込み、tokensのscopeに格納する */
        [self loadScopesWithTokens: tokens
                          database: database];
    }
    
    return tokens;
}




+ (void) updateAccessTime: (long long)tokenId
                 database: (DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat:
                     @"UPDATE %@ SET %@ = %lld WHERE %@ = %lld",
                     LocalOAuthTokenDaoTableName,
                     LocalOAuthTokenDaoAccessDate,
                     [LocalOAuthUtils getCurrentTimeInMillis],
                     LocalOAuthTokenDaoId,
                     tokenId
                     ];
    [database execSQL: sql];
}

+ (void) deleteToken: (long long)tokenId
             database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [ NSString stringWithFormat: @"%@=%lld",
                        LocalOAuthTokenDaoId,
                        tokenId
                        ];
    [self deleteTokens_: where database:database];
}

+ (void) deleteTokens: (long long)userId
             database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [ NSString stringWithFormat: @"%@=%lld",
                       LocalOAuthTokenDaoUserId,
                       userId
                       ];
    [self deleteTokens_: where database:database];
}

+ (void) deleteTokensByUsername: (NSString *)username
             database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [ NSString stringWithFormat: @"%@=%lld",
                       LocalOAuthTokenDaoUserId,
                       USERS_USER_ID
                       ];
    [self deleteTokens_: where database:database];
}





/* private */
+ (void) deleteTokens_: (NSString *)where
             database: (DConnectSQLiteDatabase *)database {
    
    NSString *sql = [NSString stringWithFormat:
                     @"DELETE FROM %@ WHERE %@",
                     LocalOAuthTokenDaoTableName,
                     where
                     ];
    [database execSQL: sql];
}

/* private */
/* SQLiteTokenManager.dbLoadScopesStoreToToken() */
/* tokenごとにscopesを読み込み、tokensのscopeに格納する */
+ (void) loadScopesWithTokens: (NSArray *)tokens
                     database: database {
    
    NSString *sqlSelectFrom = [NSString stringWithFormat:
        @"select %@.%@, %@.%@, %@.%@ "
        @"from %@, %@",
        /* select */
        LocalOAuthProfileDaoTableName,
        LocalOAuthProfileDaoProfileName,
        LocalOAuthScopeDaoTableName,
        LocalOAuthScopeDaoTimestamp,
        LocalOAuthScopeDaoTableName,
        LocalOAuthScopeDaoExpirePeriod,
        /* from */
        LocalOAuthScopeDaoTableName,
        LocalOAuthProfileDaoTableName
    ];
    
    for (LocalOAuthToken *token in tokens) {
        LocalOAuthSQLiteToken *sqliteToken = token.delegate;
        
        NSString *sqlWhere = [NSString stringWithFormat:
            @"where %@.%@ = %lld and %@.%@ = %@.%@",
            LocalOAuthScopeDaoTableName,
            LocalOAuthScopeDaoTokenId,
            [sqliteToken id_],
            LocalOAuthScopeDaoTableName,
            LocalOAuthScopeDaoProfileId,
            LocalOAuthProfileDaoTableName,
            LocalOAuthProfileDaoId
        ];
        
        NSString *sql = [NSString stringWithFormat:@"%@ %@", sqlSelectFrom, sqlWhere];
        
        NSMutableArray *scopes = nil;   /* LocalOAuthScope[] */
        
        DConnectSQLiteCursor *cursor = [database queryWithSQL: sql];
        if (!cursor) {
            return;
        }
        
        if ([cursor moveToFirst]) {
            scopes = [NSMutableArray array];
            do {
                LocalOAuthScope *scope =
                    [[LocalOAuthScope alloc]initWithScope: [cursor stringValueAtIndex:0]
                                                timestamp: [cursor longLongValueAtIndex:1]
                                             expirePeriod: [cursor longLongValueAtIndex:2]
                    ];
                [scopes addObject: scope];
            } while ([cursor moveToNext]);
        }
        
        [cursor close];
        
        sqliteToken.scope = scopes;
    }
}




@end
