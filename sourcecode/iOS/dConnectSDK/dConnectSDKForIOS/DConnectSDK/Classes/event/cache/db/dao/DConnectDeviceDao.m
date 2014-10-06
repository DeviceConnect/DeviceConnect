//
//  DConnectDeviceDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectDeviceDao.h"
#import "DConnectEventDao.h"

NSString *const DConnectDeviceDaoTableName = @"Device";
NSString *const DConnectDeviceDaoClmDeviceId = @"device_id";

NSString *const DConnectDeviceDaoEmptyDeviceId = @"";

@implementation DConnectDeviceDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ TEXT NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@));",
                            DConnectDeviceDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectDeviceDaoClmDeviceId,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectDeviceDaoClmDeviceId);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithDeviceId:(NSString *)deviceId toDatabase:(DConnectSQLiteDatabase *)database {
    
    deviceId = (deviceId != nil) ? deviceId : DConnectDeviceDaoEmptyDeviceId;
    long long result = -1;
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectDeviceDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=?", DConnectDeviceDaoClmDeviceId)
                     bindParams:@[deviceId]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *current = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectDeviceDaoTableName
                                   columns:@[DConnectDeviceDaoClmDeviceId, DConnectEventDaoClmCreateDate,
                                             DConnectEventDaoClmUpdateDate]
                                    params:@[deviceId, current, current]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    
    [cursor close];
    
    return result;
}

@end
