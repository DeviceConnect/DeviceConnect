//
//  DConnectEventDeviceDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"

extern NSString *const DConnectEventDeviceDaoTableName;
extern NSString *const DConnectEventDeviceDaoClmAId;
extern NSString *const DConnectEventDeviceDaoClmDId;

@interface DConnectEventDeviceDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithAttributeId:(long long)attributeId
                           deviceId:(long long)deviceId
                         toDatabase:(DConnectSQLiteDatabase *)database;

+ (int) deleteWithId:(long long)rowId onDatabase:(DConnectSQLiteDatabase *)database;

@end
