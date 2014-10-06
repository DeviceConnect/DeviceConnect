//
//  DConnectInterfaceDao.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLite.h"

extern NSString *const DConnectInterfaceDaoTableName;
extern NSString *const DConnectInterfaceDaoClmName;
extern NSString *const DConnectInterfaceDaoClmPId;

extern NSString *const DConnectInterfaceDaoEmptyName;

@interface DConnectInterfaceDao : NSObject

+ (void) createWithDatabase:(DConnectSQLiteDatabase *)database;
+ (long long) insertWithName:(NSString *)name
                   profileId:(long long)pid
                  toDatabase:(DConnectSQLiteDatabase *)database;

@end
