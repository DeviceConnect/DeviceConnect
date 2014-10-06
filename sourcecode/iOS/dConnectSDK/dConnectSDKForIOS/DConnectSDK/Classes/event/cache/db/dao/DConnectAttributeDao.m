//
//  DConnectAttributeDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectAttributeDao.h"
#import "DConnectEventDao.h"

NSString *const DConnectAttributeDaoTableName = @"Attribute";
NSString *const DConnectAttributeDaoClmName = @"name";
NSString *const DConnectAttributeDaoClmIId = @"i_id";

@implementation DConnectAttributeDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ INTEGER NOT NULL, %@ TEXT NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@, %@));",
                            DConnectAttributeDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectAttributeDaoClmIId,
                            DConnectAttributeDaoClmName,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectAttributeDaoClmIId,
                            DConnectAttributeDaoClmName);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithName:(NSString *)name
                 interfaceId:(long long)iid
                  toDatabase:(DConnectSQLiteDatabase *)database
{
        
    long long result = -1;
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectAttributeDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=? AND %@=?", DConnectAttributeDaoClmName, DConnectAttributeDaoClmIId)
                     bindParams:@[name, [NSNumber numberWithLongLong:iid]]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *currentTime = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectAttributeDaoTableName
                                   columns:@[DConnectAttributeDaoClmName, DConnectAttributeDaoClmIId,
                                             DConnectEventDaoClmCreateDate, DConnectEventDaoClmUpdateDate]
                                    params:@[name, [NSNumber numberWithLongLong:iid], currentTime, currentTime]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    [cursor close];
    
    return result;
}

@end
