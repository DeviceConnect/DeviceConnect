//
//  DConnectDeviceDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"

extern NSString *const DConnectDeviceDaoTableName;
extern NSString *const DConnectDeviceDaoClmDeviceId;

extern NSString *const DConnectDeviceDaoEmptyDeviceId;

@interface DConnectDeviceDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithDeviceId:(NSString *)deviceId toDatabase:(DConnectSQLiteDatabase *)database;

@end
