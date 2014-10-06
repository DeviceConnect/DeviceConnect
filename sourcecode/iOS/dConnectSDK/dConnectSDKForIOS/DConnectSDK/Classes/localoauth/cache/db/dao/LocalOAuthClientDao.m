//
//  LocalOAuthClientDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthClientDao.h"
#import "LocalOAuthUtils.h"

NSString *const LocalOAuthClientDaoTableName = @"clients";
NSString *const LocalOAuthClientDaoId = @"id";                              /* ID */
NSString *const LocalOAuthClientDaoClientId = @"client_id";                 /* クライアントID */
NSString *const LocalOAuthClientDaoPackageName = @"package_name";           /* パッケージ名 */
NSString *const LocalOAuthClientDaoDeviceId = @"device_id";                 /* デバイスID(無しのときはnull) */
NSString *const LocalOAuthClientDaoClientSecret = @"client_secret";         /* クライアントシークレット */
NSString *const LocalOAuthClientDaoClientType = @"client_type";             /* クライアントタイプ */
NSString *const LocalOAuthClientDaoRegistrationDate = @"registration_date"; /* 登録日時(System.currentTimeMillis()で取得した値を格納する) */


@interface LocalOAuthClientDao() {
    
}

/* private method */
+ (NSArray *) loadClients: (NSString *)where
                 database: (DConnectSQLiteDatabase *)database;
+ (NSArray *) getLocalOAuthClientDaoColumns;


@end


@implementation LocalOAuthClientDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql =
    [NSString stringWithFormat: @"CREATE TABLE %@ ("
        @"%@ INTEGER PRIMARY KEY AUTOINCREMENT"
        @", %@ TEXT"
        @", %@ TEXT"
        @", %@ TEXT"
        @", %@ TEXT"
        @", %@ TEXT"
        @", %@ INTEGER"
        @");",
        LocalOAuthClientDaoTableName,
        LocalOAuthClientDaoId,
        LocalOAuthClientDaoClientId,
        LocalOAuthClientDaoPackageName,
        LocalOAuthClientDaoDeviceId,
        LocalOAuthClientDaoClientSecret,
        LocalOAuthClientDaoClientType,
        LocalOAuthClientDaoRegistrationDate
    ];
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithClientData:(LocalOAuthSQLiteClient *)client toDatabase:(DConnectSQLiteDatabase *)database
{
    NSMutableArray *columns = [NSMutableArray array];
    NSMutableArray *params = [NSMutableArray array];

    [columns addObject: LocalOAuthClientDaoClientId];
    [params addObject: client.clientId];

    [columns addObject: LocalOAuthClientDaoPackageName];
    [params addObject: client.packageInfo.packageName];
    
    if (client.packageInfo.deviceId != nil) {
        [columns addObject: LocalOAuthClientDaoDeviceId];
        [params addObject: client.packageInfo.deviceId];
    }
    
    [columns addObject: LocalOAuthClientDaoClientSecret];
    [params addObject: client.clientSecret];
    
    [columns addObject: LocalOAuthClientDaoClientType];
    [params addObject: [LocalOAuthClientTypeUtil toString: client.clientType]];
    
    [columns addObject: LocalOAuthClientDaoRegistrationDate];
    [params addObject: [NSNumber numberWithLongLong: client.registrationDate]];
    
    long long result = [database insertIntoTable:LocalOAuthClientDaoTableName
                                         columns:columns
                                          params:params
                        ];
    [client setId: result];
    return result;
}

+ (BOOL) removeClientData: (NSString *)clientId
                 database: (DConnectSQLiteDatabase *)database {
    
    BOOL result = NO;
    
    NSString *where = [NSString stringWithFormat:@"%@=?", LocalOAuthClientDaoClientId];
    NSArray *bindParams = @[clientId];
    
    int count = [database deleteFromTable:LocalOAuthClientDaoTableName where:where bindParams:bindParams];
    if (count < 0) {
        return result;
    }
    
    result = YES;
    
    return result;
}

+ (BOOL) cleanupClient: (long long)clientCleanupTime
              database: (DConnectSQLiteDatabase *)database {
    
    const long long msec = 1000; /* 1[sec] = 1000[msec] */
    const long long currentTimeMSec = [LocalOAuthUtils getCurrentTimeInMillis];
    const long long cleanupTimeMSec = currentTimeMSec - clientCleanupTime * msec;
    
    BOOL result = NO;
    
    /* (1)に該当するclientsレコードを削除する */
    NSString *sql1 =
    [NSString stringWithFormat:@"delete from clients where "
     @"not exists ( select * from tokens where clients.client_id = tokens.client_id ) "
     @"and clients.registration_date < %lld", cleanupTimeMSec];
    [database execSQL: sql1];
    
    /* (2)に該当するclientsレコードを削除する */
    NSString *sql2 = [NSString stringWithFormat:@"delete from clients where "
    @"exists (select * from tokens where clients.client_id = tokens.client_id) "
    @"and not exists ( select * from tokens, scopes "
    @"where clients.client_id = tokens.client_id and tokens.id = scopes.tokens_tokenid "
    @"and (scopes.timestamp + scopes.expire_period) > %lld)", cleanupTimeMSec];
    [database execSQL: sql2];
    
    /* (2)で削除されたclientsのtokensレコードを削除する(clientsがリンク切れしたtokensとscopesを削除する) */
    NSString *sql3 = [NSString stringWithFormat:@"delete from scopes where not exists ("
                      @"select * from tokens, clients "
                      @"where scopes.tokens_tokenid = tokens.id and tokens.client_id = clients.client_id)"];
    [database execSQL: sql3];
    
    NSString *sql4 = [NSString stringWithFormat:@"delete from tokens where not exists ("
                      @"select * from clients where tokens.client_id = clients.client_id)"];
    [database execSQL: sql4];
    
    result = YES;
    
    return result;
}

+ (LocalOAuthClient *) findClientById: (NSString *)clientId
                   database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [NSString stringWithFormat:
                       @"%@='%@'"
                       , LocalOAuthClientDaoClientId
                       , clientId
                       ];
    NSArray *clients = [self loadClients: where
                                database: database];
    if (clients == nil || [clients count] == 0) {
        return nil;
    } else if ([clients count] == 1) {
        return clients[0];
    } else {
        @throw @"クライアントIDが2件以上のクライアントデータに設定されています。";
    }
}

+ (LocalOAuthClient *) findClientByPackageInfo: (LocalOAuthPackageInfo *)packageInfo
                   database: (DConnectSQLiteDatabase *)database {
    
    NSString *where = [packageInfo deviceId] != nil ?
                [NSString stringWithFormat: @"%@='%@' AND %@='%@'"
                 , LocalOAuthClientDaoPackageName
                 , [packageInfo packageName]
                 , LocalOAuthClientDaoDeviceId
                 , [packageInfo deviceId]
                 ] :
                [NSString stringWithFormat: @"%@='%@' AND %@ IS NULL"
                 , LocalOAuthClientDaoPackageName
                 , [packageInfo packageName]
                 , LocalOAuthClientDaoDeviceId
                 ];
    
    NSArray *clients = [self loadClients: where
                                database: database];
    
    if (clients == nil || [clients count] == 0) {
        return nil;
    } else if ([clients count] == 1) {
        return clients[0];
    } else {
        @throw @"クライアントIDが2件以上のクライアントデータに設定されています。";
    }
}

+ (NSArray *) loadClients: (NSString *)where
                 database: (DConnectSQLiteDatabase *)database {
    
    NSMutableArray *clients = nil;
    NSString *sql = [NSString stringWithFormat:
                     @"select"
                     @"  %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @", %@"
                     @" from %@"
                     @" where %@",
                     LocalOAuthClientDaoId,
                     LocalOAuthClientDaoClientId,
                     LocalOAuthClientDaoPackageName,
                     LocalOAuthClientDaoDeviceId,
                     LocalOAuthClientDaoClientSecret,
                     LocalOAuthClientDaoClientType,
                     LocalOAuthClientDaoRegistrationDate,
                     LocalOAuthClientDaoTableName,
                     where
                     ];
    DConnectSQLiteCursor *cursor = [database queryWithSQL: sql];
    if (!cursor) {
        return clients;
    }
    
    if ([cursor moveToFirst]) {
        clients = [NSMutableArray array];
        do {
            /* DBから読み込んだ値を格納する */
            LocalOAuthSQLiteClient *sqliteClient = [[LocalOAuthSQLiteClient alloc]init];
            [sqliteClient setId: [cursor longLongValueAtIndex:0]];
            [sqliteClient setClientId: [cursor stringValueAtIndex:1]];
            NSString *packageName = [cursor stringValueAtIndex:2];
            NSString *deviceId = [cursor stringValueAtIndex:3];
            
            [sqliteClient setPackageInfo:
                [[LocalOAuthPackageInfo alloc] initWithPackageNameDeviceId: packageName
                                                     deviceId: deviceId]];
            [sqliteClient setClientSecret: [cursor stringValueAtIndex:4]];
            NSString *clientTypeString = [cursor stringValueAtIndex:5];
            [sqliteClient setClientType: [LocalOAuthClientTypeUtil toValue: clientTypeString]];
            [sqliteClient setRegistrationDate: [cursor longLongValueAtIndex:6]];
            
            /* LocalOAuthClientのdelegateに入れて配列に格納する */
            LocalOAuthClient *client = [[LocalOAuthClient alloc]init];
            client.delegate = sqliteClient;
            [clients addObject:client];
            
        } while ([cursor moveToNext]);
    }
    
    [cursor close];
    
    return clients;
}

+ (int) countClients: (DConnectSQLiteDatabase *)database {

    int count = 0;
    NSString *sql = [NSString stringWithFormat:@"select count(*) from %@", LocalOAuthClientDaoTableName];
    DConnectSQLiteCursor *cursor = [database queryWithSQL:sql];
    if (cursor) {
        if ([cursor moveToFirst]) {
            count = [cursor longLongValueAtIndex:0];
        }
        [cursor close];
    }
    return count;
}

+ (NSArray *) getLocalOAuthClientDaoColumns {
    NSArray *columns = @[
        LocalOAuthClientDaoId,
        LocalOAuthClientDaoClientId,
        LocalOAuthClientDaoPackageName,
        LocalOAuthClientDaoDeviceId,
        LocalOAuthClientDaoClientSecret,
        LocalOAuthClientDaoClientType,
        LocalOAuthClientDaoRegistrationDate
    ];
    return columns;
}
@end
