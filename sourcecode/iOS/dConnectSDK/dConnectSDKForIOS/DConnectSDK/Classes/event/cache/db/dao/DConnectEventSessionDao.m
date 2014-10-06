//
//  DConnectEventSessionDao.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectEventDao.h"
#import "DConnectEventDeviceDao.h"
#import "DConnectClientDao.h"
#import "DConnectAttributeDao.h"
#import "DConnectDeviceDao.h"
#import "DConnectEventSessionDao.h"
#import "DConnectInterfaceDao.h"
#import "DConnectProfileDao.h"

NSString *const DConnectEventSessionDaoTableName = @"EventSession";
NSString *const DConnectEventSessionDaoClmEDId = @"ed_id";
NSString *const DConnectEventSessionDaoClmCId = @"c_id";

@implementation DConnectEventSession

@end

@implementation DConnectEventSessionDao

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database {
    
    NSString *sql = DCEForm(@"CREATE TABLE %@ (%@ INTEGER PRIMARY KEY AUTOINCREMENT, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, %@ INTEGER NOT NULL, UNIQUE(%@, %@));",
                            DConnectEventSessionDaoTableName,
                            DConnectEventDaoClmId,
                            DConnectEventSessionDaoClmEDId,
                            DConnectEventSessionDaoClmCId,
                            DConnectEventDaoClmCreateDate,
                            DConnectEventDaoClmUpdateDate,
                            DConnectEventSessionDaoClmEDId,
                            DConnectEventSessionDaoClmCId);
    
    if (![database execSQL:sql]) {
        @throw @"error";
    };
}

+ (long long) insertWithEventDeviceId:(long long)eventDeviceId
                             clientId:(long long)clientId
                           toDatabase:(DConnectSQLiteDatabase *)database
{
    
    NSNumber *edId = [NSNumber numberWithLongLong:eventDeviceId];
    NSNumber *cId = [NSNumber numberWithLongLong:clientId];
    
    long long result = -1;
    DConnectSQLiteCursor *cursor
    = [database selectFromTable:DConnectEventSessionDaoTableName
                        columns:@[DConnectEventDaoClmId]
                          where:DCEForm(@"%@=? AND %@=?",
                                        DConnectEventSessionDaoClmEDId,
                                        DConnectEventSessionDaoClmCId)
                     bindParams:@[edId, cId]];
    
    if (!cursor) {
        return result;
    }
    
    if (cursor.count == 0) {
        NSNumber *currentTime = [NSNumber numberWithLongLong:getCurrentTimeInMillis()];
        result = [database insertIntoTable:DConnectEventSessionDaoTableName
                                   columns:@[DConnectEventSessionDaoClmEDId, DConnectEventSessionDaoClmCId,
                                             DConnectEventDaoClmCreateDate, DConnectEventDaoClmUpdateDate]
                                    params:@[edId, cId, currentTime, currentTime]];
    } else if ([cursor moveToFirst]) {
        result = [cursor longLongValueAtIndex:0];
    }
    [cursor close];
    
    return result;
}


+ (DConnectEventError) deleteEvent:(DConnectEvent *)event
                        onDatabase:(DConnectSQLiteDatabase *)database
{
    
    DConnectEventSession *es = [DConnectEventSessionDao eventSessionForEvent:event onDatabase:database];
    if (!es) {
        return DConnectEventErrorNotFound;
    }
    
    int count = [database deleteFromTable:DConnectEventSessionDaoTableName
                                    where:DCEForm(@"%@=?", DConnectEventDaoClmId)
                               bindParams:@[[NSNumber numberWithLongLong:es.rowId]]];
    if (count == 0) {
        return DConnectEventErrorNotFound;
    } else if (count != 1) {
        return DConnectEventErrorFailed;
    }
    
    NSNumber *edId = [NSNumber numberWithLongLong:es.edId];
    DConnectSQLiteCursor *cursor = [database selectFromTable:DConnectEventSessionDaoTableName
                                                     columns:@[DConnectEventDaoClmId]
                                                       where:DCEForm(@"%@=?", DConnectEventSessionDaoClmEDId)
                                                  bindParams:@[edId]];
    
    if (!cursor) {
        return DConnectEventErrorFailed;
    }
    
    if (cursor.count == 0) {
        // デバイスに紐づくセッション情報がなくなったので削除
        count = [DConnectEventDeviceDao deleteWithId:es.edId onDatabase:database];
        if (count != 1) {
            [cursor close];
            return DConnectEventErrorFailed;
        }
    }
    [cursor close];
    
    return DConnectEventErrorNone;
}

+ (DConnectEventError) deleteWithIds:(NSArray *)ids
                          onDatabase:(DConnectSQLiteDatabase *)database
{
    
    NSMutableString *inStr = [NSMutableString string];
    
    int last = (int) ids.count - 1;
    for (int i = 0; i < ids.count; i++) {
        [inStr appendString:@"?"];
        if (i != last) {
            [inStr appendString:@","];
        }
    }
    
    int count = [database deleteFromTable:DConnectEventSessionDaoTableName
                                    where:DCEForm(@"%@ IN (%@)",
                                                  DConnectEventSessionDaoClmCId, inStr)
                               bindParams:ids];
    
    if (count == 0) {
        return DConnectEventErrorNotFound;
    } else if (count < 0) {
        return DConnectEventErrorFailed;
    }
    
    return DConnectEventErrorNone;
}

+ (DConnectEventSession *) eventSessionForEvent:(DConnectEvent *)event onDatabase:(DConnectSQLiteDatabase *)database
{
    DConnectEventSession *eventSession = nil;
    NSString *sql
    = DCEForm(@"SELECT es.%@, es.%@, es.%@, es.%@, es.%@ FROM %@ AS p INNER JOIN %@ AS i ON p.%@ = i.%@ INNER JOIN %@ AS a ON i.%@ = a.%@ INNER JOIN %@ AS ed ON a.%@ = ed.%@ INNER JOIN %@ AS d ON ed.%@ = d.%@ INNER JOIN %@ AS es ON es.%@ = ed.%@ INNER JOIN %@ AS c ON es.%@ = c.%@ WHERE p.%@ = ? AND i.%@ = ? AND a.%@ = ? AND d.%@ = ? AND c.%@ = ?;",
              DConnectEventDaoClmId,
              DConnectEventSessionDaoClmEDId,
              DConnectEventSessionDaoClmCId,
              DConnectEventDaoClmCreateDate,
              DConnectEventDaoClmUpdateDate,
              DConnectProfileDaoTableName,
              DConnectInterfaceDaoTableName,
              DConnectEventDaoClmId,
              DConnectInterfaceDaoClmPId,
              DConnectAttributeDaoTableName,
              DConnectEventDaoClmId,
              DConnectAttributeDaoClmIId,
              DConnectEventDeviceDaoTableName,
              DConnectEventDaoClmId,
              DConnectEventDeviceDaoClmAId,
              DConnectDeviceDaoTableName,
              DConnectEventDeviceDaoClmDId,
              DConnectEventDaoClmId,
              DConnectEventSessionDaoTableName,
              DConnectEventSessionDaoClmEDId,
              DConnectEventDaoClmId,
              DConnectClientDaoTableName,
              DConnectEventSessionDaoClmCId,
              DConnectEventDaoClmId,
              DConnectProfileDaoClmName,
              DConnectInterfaceDaoClmName,
              DConnectAttributeDaoClmName,
              DConnectDeviceDaoClmDeviceId,
              DConnectClientDaoClmSessionKey);
    
    NSString *interface = (event.interface) ? event.interface : DConnectInterfaceDaoEmptyName;
    NSString *deviceId = (event.deviceId) ? event.deviceId : DConnectDeviceDaoEmptyDeviceId;
    
    DConnectSQLiteCursor *cursor = [database queryWithSQL:sql
                                               bindParams:@[event.profile, interface,
                                                            event.attribute, deviceId,
                                                            event.sessionKey]];
    
    if (!cursor) {
        return eventSession;
    }
    
    if ([cursor moveToFirst]) {
        eventSession = [DConnectEventSession new];
        eventSession.rowId = [cursor longLongValueAtIndex:0];
        eventSession.edId = [cursor longLongValueAtIndex:1];
        eventSession.cId = [cursor longLongValueAtIndex:2];
        eventSession.createDate = [NSDate dateWithTimeIntervalSince1970:[cursor longLongValueAtIndex:3]];
        eventSession.updateDate = [NSDate dateWithTimeIntervalSince1970:[cursor longLongValueAtIndex:4]];
    }
    
    [cursor close];
    
    return eventSession;
    
}

@end
