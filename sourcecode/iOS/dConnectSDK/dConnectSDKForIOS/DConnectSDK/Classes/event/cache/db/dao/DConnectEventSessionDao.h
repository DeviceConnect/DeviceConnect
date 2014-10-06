//
//  DConnectEventSessionDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"
#import "DConnectEvent.h"

extern NSString *const DConnectEventSessionDaoTableName;
extern NSString *const DConnectEventSessionDaoClmEDId;
extern NSString *const DConnectEventSessionDaoClmCId;

@interface DConnectEventSession : NSObject

@property (nonatomic) long long rowId;
@property (nonatomic) long long edId;
@property (nonatomic) long long cId;
@property (nonatomic, strong) NSDate *createDate;
@property (nonatomic, strong) NSDate *updateDate;

@end

@interface DConnectEventSessionDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithEventDeviceId:(long long)eventDeviceId
                             clientId:(long long)clientId
                           toDatabase:(DConnectSQLiteDatabase *)database;

+ (DConnectEventError) deleteEvent:(DConnectEvent *)event onDatabase:(DConnectSQLiteDatabase *)database;
+ (DConnectEventError) deleteWithIds:(NSArray *)ids onDatabase:(DConnectSQLiteDatabase *)database;
+ (DConnectEventSession *) eventSessionForEvent:(DConnectEvent *)event onDatabase:(DConnectSQLiteDatabase *)database;

@end
