//
//  DConnectEventDeviceDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEventDeviceDao.h"
#import "DConnectEventDao.h"

NSString *const DConnectEventDeviceDaoTableName = @"EventDevice";
NSString *const DConnectEventDeviceDaoClmAId = @"a_id";
NSString *const DConnectEventDeviceDaoClmDId = @"d_id";

@implementation DConnectEventDeviceDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@, %@));",
                            DConnectEventDeviceDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectEventDeviceDaoClmAId,
                            DConnectEventDeviceDaoClmDId,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectEventDeviceDaoClmAId,
                            DConnectEventDeviceDaoClmDId);
    
    if (![database execSQL:sql]) {
        @throw @"errro";
    };
}

+ (long long) insertWithAttributeId:(long long)attributeId
                           deviceId:(long long)deviceId
                         toDatabase:(DConnectSQLiteDatabase *)database
{
    long long result = -1;
    
    NSNumber *aid = [NSNumber numberWithLongLong:attributeId];
    NSNumber *did = [NSNumber numberWithLongLong:deviceId];
    
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectEventDeviceDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=? AND %@=?", DConnectEventDeviceDaoClmAId,
                                        DConnectEventDeviceDaoClmDId)
                     bindParams:@[aid, did]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *current = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectEventDeviceDaoTableName
                                   columns:@[DConnectEventDeviceDaoClmAId, DConnectEventDeviceDaoClmDId,
                                             DConnectEventDaoClmCreateDate, DConnectEventDaoClmUpdateDate]
                                    params:@[aid, did, current, current]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    
    [cursor close];
    
    return result;
}

+ (int) deleteWithId:(long long)rowId onDatabase:(DConnectSQLiteDatabase *)database {
    
    int result = -1;
    result = [database deleteFromTable:DConnectEventDeviceDaoTableName
                                 where:DCEForm(@"%@=?", DConnectEventDaoClmId)
                            bindParams:@[[NSNumber numberWithLongLong:rowId]]];
    
    return result;
}

@end
