//
//  DConnectClientDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEvent.h"
#import "DConnectSQLite.h"

extern NSString *const DConnectClientDaoTableName;
extern NSString *const DConnectClientDaoClmAccessToken;
extern NSString *const DConnectClientDaoClmSessionKey;

@interface DConnectClient : NSObject

@property (nonatomic) long long rowId;
@property (nonatomic, strong) NSString *accessToken;
@property (nonatomic, strong) NSString *sessionKey;
@property (nonatomic, strong) NSDate *esCreateDate;
@property (nonatomic, strong) NSDate *esUpdateDate;

@end

@interface DConnectClientDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithEvent:(DConnectEvent *)event toDatabase:(DConnectSQLiteDatabase *)database;
+ (NSArray *) clientsForSessionKey:(NSString *)sessionKey onDatabase:(DConnectSQLiteDatabase *)database;
+ (DConnectClient *) clientWithId:(long long) rowId onDatabase:(DConnectSQLiteDatabase *)database;
+ (NSArray *) clientsForAPIWithDeviceId:(DConnectEvent *)event onDatabase:(DConnectSQLiteDatabase *)database;

@end
