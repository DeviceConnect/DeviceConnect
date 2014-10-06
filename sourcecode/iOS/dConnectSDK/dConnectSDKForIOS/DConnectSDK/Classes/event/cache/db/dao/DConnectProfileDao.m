//
//  DConnectProfileDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectProfileDao.h"
#import "DConnectEventDao.h"

NSString *const DConnectProfileDaoTableName = @"Profile";
NSString *const DConnectProfileDaoClmName = @"name";

@implementation DConnectProfileDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ TEXT NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@));",
                            DConnectProfileDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectProfileDaoClmName,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectProfileDaoClmName);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    }
}

+ (long long) insertWithName:(NSString *)name toDatabase:(DConnectSQLiteDatabase *)database
{
    
    long long result = -1;
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectProfileDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=?", DConnectProfileDaoClmName)
                     bindParams:@[name]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *current = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectProfileDaoTableName
                                   columns:@[DConnectProfileDaoClmName, DConnectEventDaoClmCreateDate,
                                             DConnectEventDaoClmUpdateDate]
                                    params:@[name, current, current]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    
    [cursor close];
    
    return result;
}


@end
