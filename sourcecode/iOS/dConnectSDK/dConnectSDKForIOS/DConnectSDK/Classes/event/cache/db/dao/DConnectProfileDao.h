//
//  DConnectProfileDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"

extern NSString *const DConnectProfileDaoTableName;
extern NSString *const DConnectProfileDaoClmName;

@interface DConnectProfileDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithName:(NSString *)name toDatabase:(DConnectSQLiteDatabase *)database;

@end
