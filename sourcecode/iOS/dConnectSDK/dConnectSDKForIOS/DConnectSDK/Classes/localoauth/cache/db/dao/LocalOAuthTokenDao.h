//
//  LocalOAuthTokenDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "DConnectSQLite.h"
#import "LocalOAuthSQLiteToken.h"
#import "LocalOAuthClient.h"
#import "LocalOAuthToken.h"

extern NSString *const LocalOAuthTokenDaoTableName;
extern NSString *const LocalOAuthTokenDaoId;
extern NSString *const LocalOAuthTokenDaoAccessToken;
extern NSString *const LocalOAuthTokenDaoTokenType;
extern NSString *const LocalOAuthTokenDaoClientId;
extern NSString *const LocalOAuthTokenDaoUserId;
extern NSString *const LocalOAuthTokenDaoRegistrationDate;
extern NSString *const LocalOAuthTokenDaoAccessDate;
extern NSString *const LocalOAuthTokenDaoApplicationName;




@interface LocalOAuthTokenDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithTokenData:(LocalOAuthSQLiteToken *)token toDatabase:(DConnectSQLiteDatabase *)database;
+ (LocalOAuthToken *) findToken: (LocalOAuthClient *)client
             username: (NSString *)username
             database: (DConnectSQLiteDatabase *)database;
+ (LocalOAuthToken *) findTokenByAccessToken: (NSString *)accessToken
                                    database: (DConnectSQLiteDatabase *)database;
+ (NSArray *) findTokensByUsername: (NSString *)username
                          database: (DConnectSQLiteDatabase *)database;
+ (void) updateAccessTime: (long long)tokenId
                 database: (DConnectSQLiteDatabase *)database;

+ (void) deleteToken: (long long)tokenId
            database: (DConnectSQLiteDatabase *)database;
+ (void) deleteTokens: (long long)userId
             database: (DConnectSQLiteDatabase *)database;
+ (void) deleteTokensByUsername: (NSString *)username
                       database: (DConnectSQLiteDatabase *)database;


@end
