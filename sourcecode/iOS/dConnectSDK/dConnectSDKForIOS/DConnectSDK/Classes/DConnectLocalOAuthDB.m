//
//  DConnectLocalOAuthDB.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectLocalOAuthDB.h"
#import "DConnectSQLite.h"
#import "DConnectEventDaoHeader.h"

/*!
 @define データベースのバージョンを定義。
 */
#define DCONNECT_LOCAL_OAUTH_DB_VERSION 1

/*!
 @define DBファイル名を定義。
 */
#define DCONNECT_DB_NAME @"dconnect_localoauth.db"

/*!
 @define AuthDataを格納するテーブル名を定義。
 */
#define DConnectAuthDataTbl @"oauth_data_tbl"

/*!
 @define AccessTokenを格納するテーブル名を定義。
 */
#define DConnectAccessTokenTbl @"access_token_tbl"

/*!
 @define AuthDataのデバイスIDを格納するカラム名を定義。
 */
#define DConnectDeviceId @"device_id"

/*!
 @define AuthDataのクライアントIDを格納するカラム名を定義。
 */
#define DConnectClientId @"client_id"

/*!
 @define AuthDataのクライアントシークレットを格納するカラム名を定義。
 */
#define DConnectClientSecret @"client_secret"

/*!
 @define AccessTokenのAuthDataのIDを格納するカラム名を定義。
 */
#define DConnectAuthId @"oauth_id"

/*!
 @define AccessTokenを格納するカラム名を定義。
 */
#define DConnectAccessToken @"access_token"

@implementation DConnectAuthData
@end

@interface DConnectLocalOAuthDB () <DConnectSQLiteOpenHelperDelegate>

/*!
 @brief DB操作ユーティリティ。
 */
@property (nonatomic) DConnectSQLiteOpenHelper *helper;

/*!
 @brief AuthDataテーブルを作成する。
 @param[in] database 操作するデータベース
 */
- (void) createAuthDataTable:(DConnectSQLiteDatabase *) database;

/*!
 @brief AccessTokenテーブルを作成する。
 @param[in] database 操作するデータベース
 */
- (void) createAccessTokenTable:(DConnectSQLiteDatabase *) database;

/*!
 @brief AuthDataにデータを格納する。
 
 @param[in] deviceId デバイスID
 @param[in] clientId クライアントID
 @param[in] clientSecret クライアントシークレット
 @param[in] database 操作するデータベース
 
 @retval YES データの挿入に成功
 @retval NO データの挿入に失敗
 */
- (BOOL)insertAuthDataWithDeviceId:(NSString *)deviceId
                          clientId:(NSString *)clientId
                      clientSecret:(NSString *)clientSecret
                        toDatabase:(DConnectSQLiteDatabase *)database;

/*!
 @brief 指定されたデバイスIDのAuthDataを取得する。
 
 @param[in] deviceId デバイスID
 @param[in] database 操作するデータベース
 
 @retval デバイスIDに対応したAuthData
 @retval nil DBにデータが存在しない場合
 */
- (DConnectAuthData *)selectAuthDataByDeviceId:(NSString *)deviceId
                                  fromDatabase:(DConnectSQLiteDatabase *)database;

/*!
 @brief アクセストークンをDBに格納する。
 
 @param[in] accessToken アクセストークン
 @param[in] authId 対応するAuthDataのID
 @param[in] database 操作するデータベース
 
 @retval YES データの挿入に成功
 @retval NO データの挿入に失敗
 */
- (BOOL)insertAccessToken:(NSString *)accessToken withAuthId:(int)authId toDatabase:(DConnectSQLiteDatabase *)database;

/*!
 @brief 指定されたauth_idに対応するアクセストークンを取得する。
 
 @param[in] authId AuthDataのID
 @param[in] database 操作するデータベース
 
 @retval アクセストークン
 @retval nil アクセストークンが存在しない場合
 */
- (NSString *)selectAccessTokenByAuthId:(int)authId fromDatabase:(DConnectSQLiteDatabase *)database;

@end


@implementation DConnectLocalOAuthDB

+ (DConnectLocalOAuthDB *) sharedLocalOAuthDB {
    static DConnectLocalOAuthDB *sharedDConnectLocalOAuthDB = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedDConnectLocalOAuthDB = [DConnectLocalOAuthDB new];
    });
    return sharedDConnectLocalOAuthDB;
}

- (id) init {
    self = [super init];
    if (self) {
        _helper = [DConnectSQLiteOpenHelper helperWithDBName:DCONNECT_DB_NAME
                                                     version:DCONNECT_LOCAL_OAUTH_DB_VERSION];
        _helper.delegate = self;
        [[_helper database] close];
    }
    return self;
}

- (DConnectAuthData *)getAuthDataByDeviceId:(NSString *)deviceId {
    __block DConnectAuthData *auth = nil;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        auth = [self selectAuthDataByDeviceId:deviceId fromDatabase:database];
    }];
    return auth;
}

- (BOOL)deleteAuthDataByDeviceId:(NSString *)deviceId {
    __block BOOL result = NO;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        NSString *where = [NSString stringWithFormat:@"%@='%@'",
                           DConnectDeviceId, deviceId];
        int count = [database deleteFromTable:DConnectAuthDataTbl
                                        where:where
                                   bindParams:nil];
        result = count > 0;
    }];
    return result;
}


- (BOOL)addAuthDataWithDeviceId:(NSString *)deviceId
                       clientId:(NSString *)clientId
                   clientSecret:(NSString *)clientSecret {
    __block BOOL result = NO;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        [database beginTransaction];
        result = [self insertAuthDataWithDeviceId:deviceId
                                         clientId:clientId
                                     clientSecret:clientSecret
                                       toDatabase:database];
        if (result) {
            [database commit];
        } else {
            [database rollback];
        }
    }];
    return result;
}

- (NSString *)getAccessTokenByAuthData:(DConnectAuthData *)data {
    __block NSString *accessToken = nil;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        accessToken = [self selectAccessTokenByAuthId:data.id fromDatabase:database];
    }];
    return accessToken;
}

- (BOOL)deleteAccessToken:(NSString *)accessToken {
    __block BOOL result = NO;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        NSString *where = [NSString stringWithFormat:@"%@='%@'",
                           DConnectAccessToken, accessToken];
        int count = [database deleteFromTable:DConnectAccessTokenTbl
                                        where:where
                                   bindParams:nil];
        result = count > 0;
    }];
    return result;
}

- (BOOL)deleteAccessTokenByAuthData:(DConnectAuthData *)data {
    __block BOOL result = NO;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        NSString *where = [NSString stringWithFormat:@"%@=%d",
                           DConnectAuthId, data.id];
        int count = [database deleteFromTable:DConnectAccessTokenTbl
                                        where:where
                                   bindParams:nil];
        result = count > 0;
    }];
    return result;
}

- (BOOL)addAccessToken:(NSString *)accessToken withAuthData:(DConnectAuthData *)data {
    __block BOOL result = NO;
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        [database beginTransaction];
        result = [self insertAccessToken:accessToken
                              withAuthId:data.id
                              toDatabase:database];
        if (result) {
            [database commit];
        } else {
            [database rollback];
        }
    }];
    return result;
}


#pragma mark - Private Method

- (void) createAuthDataTable:(DConnectSQLiteDatabase *) database {
    NSString *sql = DCEForm(@"CREATE TABLE %@ (id INTEGER PRIMARY KEY AUTOINCREMENT, %@ TEXT NOT NULL, %@ TEXT NOT NULL, %@ TEXT NOT NULL);", DConnectAuthDataTbl, DConnectDeviceId, DConnectClientId, DConnectClientSecret);
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

- (void) createAccessTokenTable:(DConnectSQLiteDatabase *) database {
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER, %@ TEXT NOT NULL);", DConnectAccessTokenTbl, DConnectAuthId, DConnectAccessToken);
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

- (BOOL)insertAuthDataWithDeviceId:(NSString *)deviceId
                          clientId:(NSString *)clientId
                      clientSecret:(NSString *)clientSecret
                        toDatabase:(DConnectSQLiteDatabase *)database {
    long long result = [database insertIntoTable:DConnectAuthDataTbl
                               columns:@[DConnectDeviceId, DConnectClientId, DConnectClientSecret]
                                params:@[deviceId, clientId, clientSecret]];
    return (result > 0);
}

- (DConnectAuthData *)selectAuthDataByDeviceId:(NSString *)deviceId
                                  fromDatabase:(DConnectSQLiteDatabase *)database
{
    NSString *sql = DCEForm(@"SELECT %@,%@,%@ FROM %@ WHERE %@='%@';", @"id", DConnectClientId, DConnectClientSecret, DConnectAuthDataTbl, DConnectDeviceId, deviceId);
    
    DConnectAuthData *auth = nil;
    DConnectSQLiteCursor *cursor = [database queryWithSQL:sql];
    if ([cursor moveToFirst]) {
        auth = [DConnectAuthData new];
        auth.deviceId = deviceId;
        auth.id = [cursor longLongValueAtIndex:0];
        auth.clientId = [cursor stringValueAtIndex:1];
        auth.clientSecret = [cursor stringValueAtIndex:2];
    }
    [cursor close];
    
    return auth;
}

- (BOOL)insertAccessToken:(NSString *)accessToken
               withAuthId:(int)authId
               toDatabase:(DConnectSQLiteDatabase *)database
{
    long long result = [database insertIntoTable:DConnectAccessTokenTbl
                                         columns:@[DConnectAccessToken, DConnectAuthId]
                                          params:@[accessToken, @(authId)]];
    return (result > 0);
}

- (NSString *)selectAccessTokenByAuthId:(int)authId fromDatabase:(DConnectSQLiteDatabase *)database
{
    NSString *sql = DCEForm(@"SELECT %@ FROM %@ WHERE %@=%d;", DConnectAccessToken, DConnectAccessTokenTbl, DConnectAuthId, authId);
    
    NSString *accessToken = nil;
    DConnectSQLiteCursor *cursor = [database queryWithSQL:sql];
    if ([cursor moveToFirst]) {
        accessToken = [cursor stringValueAtIndex:0];
    }
    [cursor close];
    
    return accessToken;
}

#pragma mark - DConnectSQLiteOpenHelperDelegate

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper
  didCreateDatabase:(DConnectSQLiteDatabase *)database {
    @try {
        [self createAuthDataTable:database];
        [self createAccessTokenTable:database];
    }
    @catch (NSString *exception) {
        [database close];
        
        // 失敗したときの後始末
    }
}

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper
 didUpgradeDatabase:(DConnectSQLiteDatabase *)database
         oldVersion:(int)oldVersion
         newVersion:(int)newVersion {
}

@end
