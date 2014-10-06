//
//  DConnectInterfaceDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectInterfaceDao.h"
#import "DConnectEventDao.h"

NSString *const DConnectInterfaceDaoTableName = @"Interface";
NSString *const DConnectInterfaceDaoClmName = @"name";
NSString *const DConnectInterfaceDaoClmPId = @"p_id";

NSString *const DConnectInterfaceDaoEmptyName = @"";

@implementation DConnectInterfaceDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ INTEGER NOT NULL, %@ TEXT DEFAULT '', %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@, %@));",
                            DConnectInterfaceDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectInterfaceDaoClmPId,
                            DConnectInterfaceDaoClmName,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectInterfaceDaoClmPId,
                            DConnectInterfaceDaoClmName);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    };
}

+ (long long) insertWithName:(NSString *)name
                   profileId:(long long)pid
                  toDatabase:(DConnectSQLiteDatabase *)database
{
    long long result = -1;
    
    if (!name) {
        name = DConnectInterfaceDaoEmptyName;
    }
    
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectInterfaceDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=? AND %@=?",
                                        DConnectInterfaceDaoClmName,
                                        DConnectInterfaceDaoClmPId)
                     bindParams:@[name, [NSNumber numberWithLongLong:pid]]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *currentTime = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectInterfaceDaoTableName
                                   columns:@[DConnectInterfaceDaoClmName, DConnectInterfaceDaoClmPId,
                                             DConnectEventDaoClmCreateDate, DConnectEventDaoClmUpdateDate]
                                    params:@[name, [NSNumber numberWithLongLong:pid], currentTime, currentTime]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    [cursor close];
    
    return result;
    
}

@end
