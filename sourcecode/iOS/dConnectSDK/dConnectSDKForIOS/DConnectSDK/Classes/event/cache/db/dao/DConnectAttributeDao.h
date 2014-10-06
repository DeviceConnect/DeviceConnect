//
//  DConnectAttributeDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"

extern NSString *const DConnectAttributeDaoTableName;
extern NSString *const DConnectAttributeDaoClmName;
extern NSString *const DConnectAttributeDaoClmIId;

@interface DConnectAttributeDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;

+ (long long) insertWithName:(NSString *)name
                 interfaceId:(long long)iid
                  toDatabase:(DConnectSQLiteDatabase *)database;

@end
