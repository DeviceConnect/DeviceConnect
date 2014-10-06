//
//  LocalOAuthDbCacheController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "DConnectBaseCacheController.h"
#import "DConnectSQLiteDatabase.h"
#import "LocalOAuthClientData.h"
#import "LocalOAuthPackageInfo.h"
#import "LocalOAuthClient.h"
#import "LocalOAuthAccessTokenData.h"
#import "LocalOAuthSQLiteToken.h"

@interface LocalOAuthDbCacheController : DConnectBaseCacheController

- (id) initWithClass:(Class)clazz;
- (id) initWithKey:(NSString *)key;

- (LocalOAuthClientData *) createClient: (LocalOAuthPackageInfo *)packageInfo;
- (void)removeClientData: (NSString *)clientId;
- (LocalOAuthClient *) findClientByClientId: (NSString *)clientId;
- (LocalOAuthAccessTokenData *) findAccessToken:(LocalOAuthPackageInfo *)packageInfo;
- (LocalOAuthToken *)findTokenByAccessToken: (NSString *)accessToken;
- (LocalOAuthToken *) findTokenByClientUsername: (LocalOAuthClient *)client
                                       username: (NSString *)username;
- (NSArray *) loadAllProfiles;
- (LocalOAuthToken *)generateToken: (LocalOAuthClient *)client
                          username: (NSString *)username
                            scopes: (NSArray *) scopes
                   applicationName: (NSString *)applicationName;




- (void)updateTokenAccessTime: (LocalOAuthSQLiteToken *)token;
- (void)destroyAccessToken: (LocalOAuthPackageInfo *)packageInfo;
- (NSArray *) findTokensByUsername: (NSString *)username;
- (void) revokeToken: (long long)tokenId;
- (void) revokeAllTokens: (NSString *)username;






+ (LocalOAuthDbCacheController *) controllerWithClass:(Class)clazz;
+ (LocalOAuthDbCacheController *) controllerWithKey:(NSString *)key;

@end
