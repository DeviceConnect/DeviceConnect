//
//  LocalOAuthClientDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "DConnectSQLite.h"
#import "LocalOAuthSQLiteClient.h"

extern NSString *const LocalOAuthClientDaoTableName;
extern NSString *const LocalOAuthClientDaoId;
extern NSString *const LocalOAuthClientDaoClientId;
extern NSString *const LocalOAuthClientDaoPackageName;
extern NSString *const LocalOAuthClientDaoDeviceId;
extern NSString *const LocalOAuthClientDaoClientSecret;
extern NSString *const LocalOAuthClientDaoClientType;
extern NSString *const LocalOAuthClientDaoRegistrationDate;

@interface LocalOAuthClientDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithClientData:(LocalOAuthSQLiteClient *)client toDatabase:(DConnectSQLiteDatabase *)database;
+ (BOOL) removeClientData: (NSString *)clientId
                 database: (DConnectSQLiteDatabase *)database;
+ (BOOL) cleanupClient: (long long)clientCleanupTime
              database: (DConnectSQLiteDatabase *)database;
+ (LocalOAuthClient *) findClientById: (NSString *)clientId
                   database: (DConnectSQLiteDatabase *)database;
+ (LocalOAuthClient *) findClientByPackageInfo: (LocalOAuthPackageInfo *)packageInfo
                            database: (DConnectSQLiteDatabase *)database;
+ (int) countClients: (DConnectSQLiteDatabase *)database ;

@end
