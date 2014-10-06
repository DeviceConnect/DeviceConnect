//
//  LocalOAuthDbCacheController.m
//  dConnectLocalOAuth
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthDbCacheController.h"

#import "DConnectSQLite.h"
#import "LocalOAuthClientDao.h"
#import "LocalOAuthDummy.h"

#define DCONNECT_EVENT_DB_VERSION (1)

/* AbstractClientManager.RESEED_CLIENTS */
#define RESEED_CLIENTS (100)

NSString *const LocalOAuthDbCacheControllerDBName = @"__dconnect_event.db";




@interface LocalOAuthDbCacheController()<DConnectSQLiteOpenHelperDelegate> {
    NSString *_key;
    DConnectSQLiteOpenHelper *_helper;
    int _count;
}

/* プライベートメソッド宣言 */
- (NSArray *) GET_DEFAULT_SUPPORTED_FLOWS_PUBLIC;
- (NSArray *) GET_DEFAULT_SUPPORTED_FLOWS_CONFIDENTIAL;
- (Client *) addClientData: (PackageInfoLOCAL *) packageInfo
                  database: (DConnectSQLiteDatabase *)database;
- (Client *) clientManager_createClient: (PackageInfoLOCAL *)packageInfo
                               clientId: (NSString *)clientId
                           clientSecret: (NSString *)clientSecret
                             clientType: (ClientType)clientType
                           redirectURIs: (NSArray *)redirectURIs
                                 params: (NSDictionary *)params
                               database: (DConnectSQLiteDatabase *)database;
- (Client *) clientManager_createClient: (PackageInfoLOCAL *)packageInfo
               clientType: (ClientType)clientType
             redirectURIs: (NSArray *)redirectURIs
               properties: (NSDictionary *)properties
                 database: (DConnectSQLiteDatabase *)database;
- (bool) isIssueClientSecretToPublicClients;
- (void) removeClientData: (NSString *)key
                 clientId: (NSString *)clientId
                 database: (DConnectSQLiteDatabase *)database;

@end

@implementation LocalOAuthDbCacheController

/* AbstractClientManager.DEFAULT_SUPPORTED_FLOWS_PUBLICから移植 */
- (NSArray *) GET_DEFAULT_SUPPORTED_FLOWS_PUBLIC {

    NSArray * DEFAULT_SUPPORTED_FLOWS_PUBLIC = @[
        [ResponseTypeUtil toString: token]
    ];

    return DEFAULT_SUPPORTED_FLOWS_PUBLIC;
}

/* AbstractClientManager.DEFAULT_SUPPORTED_FLOWS_CONFIDENTIALから移植 */
- (NSArray *) GET_DEFAULT_SUPPORTED_FLOWS_CONFIDENTIAL {

    NSArray *DEFAULT_SUPPORTED_FLOWS_CONFIDENTIAL = @[
        [ResponseTypeUtil toString: code],
        [GrantTypeUtil toString: authorization_code],
        [GrantTypeUtil toString: client_credentials],
        [GrantTypeUtil toString: refresh_token]
    ];

    return DEFAULT_SUPPORTED_FLOWS_CONFIDENTIAL;
}


#pragma mark - initialize
- (id) init {
    // 引数無しで初期化されたくないのでnilを返す。
    return nil;
}

- (id) initWithClass:(Class)clazz {
    return [self initWithKey:NSStringFromClass(clazz)];
}

- (id) initWithKey:(NSString *)key {
    self = [super init];
    
    if (self) {
        _key = key;
        _count = 0;
        NSString *name = [NSString stringWithFormat:@"%@%@", key, LocalOAuthDbCacheControllerDBName];
        _helper = [DConnectSQLiteOpenHelper helperWithDBName:name version:DCONNECT_EVENT_DB_VERSION];
        _helper.delegate = self;
        // databaseを呼び出すとDBを作成するのでここで作成しておく。
        DConnectSQLiteDatabase *db = [_helper database];
        [db close];
    }
    
    return self;
}


#pragma mark - LocalOAuthDbCacheController

/* クライアント追加 */
- (ClientData *) createClient:(NSString *)key packageInfo: (PackageInfoLOCAL *)packageInfo {
    
    __block BOOL result = false;
    __block ClientData *clientData = nil;
    
    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
        if (!database) {
            return;
        }
        
        do {
    
            NSString *clientId = nil;
            NSString *clientSecret = nil;
            
            /* パッケージ情報に対応するクライアントIDがすでに登録済なら破棄する */
            Client *client = [LocalOAuthClientDao findClientByPackageInfo: packageInfo
                                                                 database: database];
            if (client != nil) {
                clientId = [client getClientId];
                
                /* クライアントデータ削除 */
                [LocalOAuthClientDao removeClientData:clientId database:database];
                
                client = nil;
            }
            
            /* クライアントデータを新規生成して返す */
            client = [self addClientData: packageInfo
                                database: database];
            clientId = [client getClientId];
            clientSecret = [client getClientSecret];
            
            clientData = [ClientData initWithClientIdClientSecret: clientId clientSecret:clientSecret];
    
        } while (false);
        
        if (result) {
            [database commit];
        } else {
            [database rollback];
        }
    }];
    
    return clientData;
}

/* private */
- (Client *) addClientData: (PackageInfoLOCAL *) packageInfo
                  database: (DConnectSQLiteDatabase *)database {

    NSArray *redirectURIs = @[ DUMMY_REDIRECTURI ];
    NSDictionary *params = [NSDictionary dictionary];
    
    Client *client = [self clientManager_createClient: packageInfo
                                           clientType: CONFIDENTIAL
                                         redirectURIs: redirectURIs
                                           properties: params
                                             database: database
                      ];
    return client;
}

/* private - AbstractClientManager.createClient() */

- (Client *) clientManager_createClient: (PackageInfoLOCAL *)packageInfo
                             clientType: (ClientType)clientType
                           redirectURIs: (NSArray *)redirectURIs
                             properties: (NSDictionary *)properties
                               database: (DConnectSQLiteDatabase *)database {
    
    if (properties == nil) {
        properties = [NSDictionary dictionary];
    }
    
    id flows = [properties objectForKey: PROPERTY_SUPPORTED_FLOWS];
    if (flows == nil) {
        
        /* flows = defaultSupportedFlow.get(clientType)の代替処理 */
        if (clientType == PUBLIC) {
            flows = [self GET_DEFAULT_SUPPORTED_FLOWS_PUBLIC];
        } else if (clientType == CONFIDENTIAL) {
            flows = [self GET_DEFAULT_SUPPORTED_FLOWS_CONFIDENTIAL];
        }
        [properties setValue:flows forKey:PROPERTY_SUPPORTED_FLOWS];
    }
    
    /*
     * The authorization server MUST require the following clients to
     * register their redirection endpoint: o Public clients. o Confidential
     * clients utilizing the implicit grant type. (3.1.2.2. Registration
     * Requirements)
     */
    if (clientType == PUBLIC
        || (clientType == CONFIDENTIAL &&
            [(NSArray *)flows containsObject: [ResponseTypeUtil toString: token]])) {
        if (redirectURIs == nil || [redirectURIs count] == 0) {
            @throw @"RedirectionURI(s) required.";
        }
    }
    
    NSString *clientId = [[NSUUID UUID] UUIDString];
    NSString *clientSecret = nil;
    if (clientType == CONFIDENTIAL
        || (clientType == PUBLIC && [self isIssueClientSecretToPublicClients])) {
        // Issue a client secret to the confidential client.
        if (_count++ > RESEED_CLIENTS) {
            _count = 0;
        }
        
        /* 20桁の乱数をbase64に変換して出力する */
        NSMutableString *clientSecret_ = [[NSMutableString alloc] initWithString:@""];
        for (int i = 0; i < 20; i += 2) {
            int r = arc4random_uniform(255);
            [clientSecret_ appendFormat:@"%02x", r];
        }
        clientSecret = clientSecret_;
    }
    
    Client *client = [self clientManager_createClient: packageInfo
                                             clientId: clientId
                                         clientSecret: clientSecret
                                           clientType: clientType
                                         redirectURIs: redirectURIs
                                               params: properties
                                             database: database
                      ];
    return client;
}

/* private - AbstractClientManager.isIssueClientSecretToPublicClients() */
- (bool) isIssueClientSecretToPublicClients {
    // XXX [MEMO]使用しているので外せない。(issueClientSecretToPublicClients = false と初期化した後どこからも変更されていないので常時falseを返す)
    return false;
}




/* private - SQLiteClientManager.createClient() */
- (Client *) clientManager_createClient: (PackageInfoLOCAL *)packageInfo
                               clientId: (NSString *)clientId
                           clientSecret: (NSString *)clientSecret
                             clientType: (ClientType)clientType
                           redirectURIs: (NSArray *)redirectURIs
                                 params: (NSDictionary *)params
                               database: (DConnectSQLiteDatabase *)database {
    
    /* clientデータ設定 */
    SQLiteClient *client = [[SQLiteClient alloc]init: clientId
                  packageInfo: packageInfo
                   clientType: clientType
                 redirectURIs: redirectURIs
                   properties: params];
    if (client != nil) {
        client.mClientSecret = clientSecret;
    }
    
    /* DBに1件追加(失敗したらSQLiteException発生) */
    [LocalOAuthClientDao insertWithClientData:client toDatabase:database];
    
    return client;
}

/* private - LocalOAuth2Main.removeClientData() */
- (void) removeClientData: (NSString *)key
                 clientId: (NSString *)clientId
                 database: (DConnectSQLiteDatabase *)database {

    Client *client = [LocalOAuthClientDao findClientById: clientId
                                                database: database];
    if (client != nil) {
        
//      /* このクライアントが発行しているトークンデータを無効にする */
//      tokenManager.revokeToken(client);
        
        /* クライアントデータ削除 */
        [LocalOAuthClientDao removeClientData: clientId
                                     database: database];
    }
}








//- (DConnectEventError) removeEvent:(DConnectEvent *)event {
//
//    if (![self checkParameterOfEvent:event]) {
//        return DConnectEventErrorInvalidParameter;
//    }
//    
//    __block DConnectEventError error = DConnectEventErrorFailed;
//    
//    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
//        if (!database) {
//            return;
//        }
//        
//        [database beginTransaction];
//        error = [DConnectEventSessionDao deleteEvent:event onDatabase:database];
//        if (error == DConnectEventErrorNone || error == DConnectEventErrorNotFound) {
//            [database commit];
//        } else {
//            [database rollback];
//        }
//    }];
//    
//    return error;
//}

//- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey {
//    
//    __block BOOL result = false;
//    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
//        if (!database) {
//            return;
//        }
//        
//        do {
//            [database beginTransaction];
//            NSArray *clients = [DConnectClientDao clientsForSessionKey:sessionKey
//                                                            onDatabase:database];
//            if (!clients) {
//                break;
//            } else if (clients.count == 0) {
//                result = true;
//                break;
//            }
//            
//            NSMutableArray *ids = [NSMutableArray array];
//            for (DConnectClient *client in clients) {
//                [ids addObject:[NSNumber numberWithLongLong:client.rowId]];
//            }
//            
//            DConnectEventError error = [DConnectEventSessionDao deleteWithIds:nil
//                                                                   onDatabase:database];
//            if (error == DConnectEventErrorFailed
//                || error == DConnectEventErrorInvalidParameter)
//            {
//                break;
//            }
//            
//            result = true;
//            
//        } while (false);
//        
//        if (result) {
//            [database commit];
//        } else {
//            [database rollback];
//        }
//    }];
//    
//    return result;
//}

//- (BOOL) removeAll {
//    
//    __block BOOL result = false;
//    
//    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
//        if (!database) {
//            return;
//        }
//        
//        [database beginTransaction];
//        NSArray *tables = @[DConnectEventDeviceDaoTableName, DConnectAttributeDaoTableName,
//                            DConnectInterfaceDaoTableName, DConnectProfileDaoTableName,
//                            DConnectClientDaoTableName, DConnectDeviceDaoTableName];
//        
//        for (NSString *table in tables) {
//            int count = [database deleteFromTable:table where:nil bindParams:nil];
//            if (count < 0) {
//                [database rollback];
//                return;
//            }
//        }
//        
//        [database commit];
//        result = true;
//    }];
//    
//    
//    return result;
//}


//- (DConnectEvent *) eventForDeviceId:(NSString *)deviceId profile:(NSString *)profile
//                           interface:(NSString *)interface attribute:(NSString *)attribute
//                          sessionKey:(NSString *)sessionKey
//{
//    
//    __block DConnectEvent *event = nil;
//    __block DConnectDBCacheController *_self = self;
//    
//    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
//        
//        if (!database) {
//            return;
//        }
//        
//        DConnectEvent *search = [DConnectEvent new];
//        search.deviceId = deviceId;
//        search.profile = profile;
//        search.interface = interface;
//        search.attribute = attribute;
//        search.sessionKey = sessionKey;
//        
//        if (![_self checkParameterOfEvent:search]) {
//            return;
//        }
//        
//        DConnectEventSession *data = [DConnectEventSessionDao eventSessionForEvent:search
//                                                                        onDatabase:database];
//        if (!data) {
//            return;
//        }
//        
//        DConnectClient *client = [DConnectClientDao clientWithId:data.cId
//                                                      onDatabase:database];
//        if (!client) {
//            return;
//        }
//        
//        search.accessToken = client.accessToken;
//        search.createDate = data.createDate;
//        search.updateDate = data.updateDate;
//        
//        event = search;
//        
//    }];
//    
//    return event;
//}

//- (NSArray *) eventsForDeviceId:(NSString *)deviceId profile:(NSString *)profile
//                      interface:(NSString *)interface attribute:(NSString *)attribute
//{
//    
//    NSMutableArray *result = [NSMutableArray array];
//    __block DConnectDBCacheController *_self = self;
//    
//    [_helper execQueryInQueue:^(DConnectSQLiteDatabase *database) {
//        
//        if (!database) {
//            return;
//        }
//        
//        DConnectEvent *search = [DConnectEvent new];
//        search.deviceId = deviceId;
//        search.profile = profile;
//        search.interface = interface;
//        search.attribute = attribute;
//        search.sessionKey = @"dummy";
//        
//        if (![_self checkParameterOfEvent:search]) {
//            return;
//        }
//        
//        NSArray *clients = [DConnectClientDao clientsForAPIWithDeviceId:search onDatabase:database];
//        if (!clients) {
//            return;
//        }
//        
//        for (DConnectClient *client in clients) {
//            DConnectEvent *event = [DConnectEvent new];
//            event.deviceId = deviceId;
//            event.profile = profile;
//            event.interface = interface;
//            event.attribute = attribute;
//            event.sessionKey = client.sessionKey;
//            event.accessToken = client.accessToken;
//            event.createDate = client.esCreateDate;
//            event.updateDate = client.esUpdateDate;
//            [result addObject:event];
//        }
//    }];
//    
//    return result;
//}

- (void) flush {
    // do nothing.
}

#pragma mark - DConnectSQLiteOpenHelperDelegate

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper didCreateDatabase:(DConnectSQLiteDatabase *)database {
    
    @try {
//        [DConnectProfileDao createWithDatabase:database];
    }
    @catch (NSString *exception) {
        
        // 本来は上で閉じるがエラーを投げるのでとりあえず閉じておく。
        [database close];
        
        // 作成に失敗したらDBを削除。
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                             NSUserDomainMask, YES);
        NSString *path = [paths objectAtIndex:0];
        NSString *dbFilePath = [path stringByAppendingPathComponent:database.dbName];
        NSFileManager *fm = [NSFileManager defaultManager];
        [fm removeItemAtPath:dbFilePath error:nil];
        
        @throw @"ERROR: Could not create DB.";
    }
}

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper didUpgradeDatabase:(DConnectSQLiteDatabase *)database
         oldVersion:(int)oldVersion
         newVersion:(int)newVersion
{
    // バージョン1なので特に処理無し。バージョンの変更がある場合は要対応。
    
}

#pragma mark - static

+ (LocalOAuthDbCacheController *) controllerWithClass:(Class)clazz {
    return [[LocalOAuthDbCacheController alloc] initWithClass:clazz];
}

+ (LocalOAuthDbCacheController *) controllerWithKey:(NSString *)key {
    return [[LocalOAuthDbCacheController alloc] initWithKey:key];
}

@end
