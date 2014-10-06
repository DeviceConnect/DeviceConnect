//
//  DConnectSQLiteDatabase.h
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLiteCursor.h"

extern NSString *const DConnectSQLiteDomain;

@interface DConnectSQLiteDatabase : NSObject

@property (nonatomic, strong, readonly) NSString *dbName;

- (void) close;

- (void) setVersion:(int)version;
- (int) version;

- (void) beginTransaction;
- (void) beginTransactionNonExclusive;
- (void) commit;
- (void) rollback;

- (BOOL) isOpen;

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql;

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql
                             bindParams:(NSArray *)params;

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql
                                  error:(NSError **)error;

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql
                             bindParams:(NSArray *)params
                                  error:(NSError **)error;

- (BOOL) execSQL:(NSString *)sql;
- (BOOL) execSQL:(NSString *)sql bindParams:(NSArray *)params;
- (BOOL) execSQL:(NSString *)sql error:(NSError **)error;
- (BOOL) execSQL:(NSString *)sql bindParams:(NSArray *)params error:(NSError **)error;

- (long long) insertIntoTable:(NSString *)table
                      columns:(NSArray *)columns
                       params:(NSArray *)params;

- (long long) insertIntoTable:(NSString *)table
                      columns:(NSArray *)columns
                       params:(NSArray *)params
                        error:(NSError **)error;

- (int) deleteFromTable:(NSString *)table
                  where:(NSString *)where
             bindParams:(NSArray *)params;

- (int) deleteFromTable:(NSString *)table
                  where:(NSString *)where
             bindParams:(NSArray *)params
                  error:(NSError **)error;

- (int) updateTable:(NSString *)table
            columns:(NSArray *)columns
              where:(NSString *)where
         bindParams:(NSArray *)params;

- (int) updateTable:(NSString *)table
            columns:(NSArray *)columns
              where:(NSString *)where
         bindParams:(NSArray *)params
              error:(NSError **)error;

- (DConnectSQLiteCursor *) selectFromTable:(NSString *)table
                                   columns:(NSArray *)columns
                                     where:(NSString *)where
                                bindParams:(NSArray *)params;

- (DConnectSQLiteCursor *) selectFromTable:(NSString *)table
                                   columns:(NSArray *)columns
                                     where:(NSString *)where
                                bindParams:(NSArray *)params
                                     error:(NSError **)error;

- (void) retainDB;

+ (DConnectSQLiteDatabase *) openDatabaseWithDBName:(NSString *)name;

@end
