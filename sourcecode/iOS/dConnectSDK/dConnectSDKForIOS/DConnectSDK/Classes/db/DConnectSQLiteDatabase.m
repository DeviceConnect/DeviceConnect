//
//  DConnectSQLiteDatabase.m
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLiteDatabase.h"
#import <sqlite3.h>

NSString *const DConnectSQLiteDomain = @"com.nttdocomo.sqlite3.Error";
NSString *const DConnectSQLiteExceptionRase = @"DConnectSQLiteException";

@interface DConnectSQLiteDatabase() {
    sqlite3 *_db;
    int _refCount;
}

- (id) initWithName:(NSString *)name;
- (BOOL) open;
- (void) checkState;

- (void) bindParam:(id)param index:(int)index statement:(sqlite3_stmt *)statement;
- (int) compileSQL:(const char *)sql stmt:(sqlite3_stmt **)stmt;

- (NSString *) lastErrorMessage;
- (int) lastErrorCode;

- (void) setSQLError:(NSError **)error;
- (void) releaseDB;

@end

@implementation DConnectSQLiteDatabase

#pragma mark - static

+ (DConnectSQLiteDatabase *) openDatabaseWithDBName:(NSString *)name {
    
    DConnectSQLiteDatabase *db = [[DConnectSQLiteDatabase alloc] initWithName:name];
    BOOL isOpened = [db open];
    
    if (isOpened) {
        return db;
    }
    return nil;
}


#pragma mark - init
- (id) init {
    return nil;
}

- (id) initWithName:(NSString *)name {
    
    self = [super init];
    
    if (self) {
        _db = nil;
        _dbName = name;
        _refCount = 1;
    }
    
    return self;
}

#pragma mark - public
- (void) close {
    [self releaseDB];
}

- (void) setVersion:(int)version {
    if (version <= 0) {
        [NSException raise:DConnectSQLiteExceptionRase format:@"Invalid version. Version should be larger then 0."];
        return;
    }
    
    [self execSQL:[NSString stringWithFormat:@"PRAGMA user_version = %d;", version]];
}

- (int) version {
    
    [self checkState];
    
    int version = -1;
    DConnectSQLiteCursor *cursor = [self queryWithSQL:@"PRAGMA user_version;"];
    if (cursor) {
        
        if ([cursor moveToFirst]) {
            version = [cursor intValueAtIndex:0];
        }
        
        [cursor close];
    }
    
    return version;
}

- (void) beginTransaction {
    [self execSQL:@"BEGIN EXCLUSIVE;"];
}

- (void) beginTransactionNonExclusive {
    [self execSQL:@"BEGIN DEFERRED;"];
}

- (void) rollback {
    [self execSQL:@"ROLLBACK;"];
}

- (void) commit {
    [self execSQL:@"COMMIT;"];
}

- (BOOL) isOpen {
    @synchronized (self) {
        return _refCount != 0;
    }
}

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql {
    return [self queryWithSQL:sql bindParams:nil];
}

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql bindParams:(NSArray *)params {
    return [self queryWithSQL:sql bindParams:params error:nil];
}

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql
                                  error:(NSError *__autoreleasing *)error
{
    return [self queryWithSQL:sql bindParams:nil error:error ];
}

- (DConnectSQLiteCursor *) queryWithSQL:(NSString *)sql
                             bindParams:(NSArray *)params
                                  error:(NSError *__autoreleasing *)error
{
    sqlite3_stmt *stmt;
    
    int result = [self compileSQL:[sql UTF8String] stmt:&stmt];
    if (result != SQLITE_OK) {
        [self setSQLError:error];
        return nil;
    }
    
    int bindCount = sqlite3_bind_parameter_count(stmt);
    
    id param;
    
    for (int i = 0; i < bindCount; i++) {
        param = [params objectAtIndex:i];
        [self bindParam:param index:i + 1 statement:stmt];
    }
    DCLogD(@"sql : %@ %@", sql, params);
    return [DConnectSQLiteCursor cursorWithStatement:stmt];
}

- (BOOL) execSQL:(NSString *)sql {
    return [self execSQL:sql error:nil];
}

- (BOOL) execSQL:(NSString *)sql bindParams:(NSArray *)params {
    return [self execSQL:sql bindParams:params error:nil];
}

- (BOOL) execSQL:(NSString *)sql error:(NSError *__autoreleasing *)error {
    
    int result = sqlite3_exec(_db, [sql UTF8String], NULL, NULL, NULL);
    DCLogD(@"sql : %@", sql);
    
    if (result != SQLITE_OK) {
        [self setSQLError:error];
        return NO;
    }
    
    return YES;
}

- (BOOL) execSQL:(NSString *)sql bindParams:(NSArray *)params error:(NSError *__autoreleasing *)error
{
    
    sqlite3_stmt *stmt;
    
    int result = [self compileSQL:[sql UTF8String] stmt:&stmt];
    if (result != SQLITE_OK) {
        [self setSQLError:error];
        return NO;
    }
    
    int bindCount = sqlite3_bind_parameter_count(stmt);
    id param;
    for (int i = 0; i < bindCount; i++) {
        param = [params objectAtIndex:i];
        [self bindParam:param index:i + 1 statement:stmt];
    }
    
    result = sqlite3_step(stmt);
    sqlite3_finalize(stmt);
    
    DCLogD(@"sql : %@, %@", sql, params);
    
    if (result != SQLITE_DONE) {
        [self setSQLError:error];
        return NO;
    }
    
    return YES;
}

- (long long) insertIntoTable:(NSString *)table columns:(NSArray *)columns params:(NSArray *)params {
    return [self insertIntoTable:table columns:columns params:params error:nil];
}

- (long long) insertIntoTable:(NSString *)table
                      columns:(NSArray *)columns
                       params:(NSArray *)params
                        error:(NSError *__autoreleasing *)error
{
    if (columns.count != params.count) {
        @throw @"Invalid Parameter. Columns count is not equals to params count.";
    }
    
    NSMutableString *sql = [NSMutableString stringWithFormat:@"INSERT INTO %@ (", table];
    NSMutableString *paramSql = [NSMutableString stringWithString:@") VALUES ("];
    
    int last = (int) columns.count - 1;
    for (int i = 0; i < columns.count; i++) {
        [sql appendString:[columns objectAtIndex:i]];
        [paramSql appendString:@"?"];
        if (i == last) {
            [paramSql appendString:@");"];
        } else {
            [sql appendString:@","];
            [paramSql appendString:@","];
        }
    }
    
    [sql appendString:paramSql];
    BOOL result = [self execSQL:sql bindParams:params error:error];
    
    if (result) {
        long long rowId = sqlite3_last_insert_rowid(_db);
        return rowId;
    }
    
    return -1;
}

- (int) deleteFromTable:(NSString *)table
                  where:(NSString *)where
             bindParams:(NSArray *)params
                  error:(NSError *__autoreleasing *)error
{
    NSString *sql;
    
    if (where) {
        sql = [NSString stringWithFormat:@"DELETE FROM %@ WHERE %@;", table, where];
    } else {
        sql = [NSString stringWithFormat:@"DELETE FROM %@;", table];
    }
    
    BOOL result = [self execSQL:sql bindParams:params error:error];
    if (result) {
        return sqlite3_changes(_db);
    }
    return -1;
}

- (int) deleteFromTable:(NSString *)table where:(NSString *)where bindParams:(NSArray *)params {
    return [self deleteFromTable:table where:where bindParams:params error:nil];
}

- (DConnectSQLiteCursor *) selectFromTable:(NSString *)table
                                   columns:(NSArray *)columns
                                     where:(NSString *)where
                                bindParams:(NSArray *)params
                                     error:(NSError *__autoreleasing *)error
{
    NSMutableString *sql = [NSMutableString stringWithString:@"SELECT "];
    int i = 1;
    for (NSString *column in columns) {
        [sql appendString:column];
        
        if (i == columns.count) {
            if (where) {
                [sql appendFormat:@" FROM %@ WHERE %@", table, where];
            } else {
                [sql appendFormat:@" FROM %@;", table];
            }
        } else {
            [sql appendString:@", "];
        }
        i++;
    }
    
    DConnectSQLiteCursor *cursor = [self queryWithSQL:sql bindParams:params error:error];
    return cursor;
}

- (DConnectSQLiteCursor *) selectFromTable:(NSString *)table
                                   columns:(NSArray *)columns
                                     where:(NSString *)where
                                bindParams:(NSArray *)params
{
    return [self selectFromTable:table columns:columns where:where bindParams:params error:nil];
}

- (int) updateTable:(NSString *)table
            columns:(NSArray *)columns
              where:(NSString *)where
         bindParams:(NSArray *)params
              error:(NSError *__autoreleasing *)error
{
    NSMutableString *sql = [NSMutableString stringWithFormat:@"UPDATE %@ SET ", table];
    
    int i = 1;
    for (NSString *column in columns) {
        [sql appendString:column];
        [sql appendString:@" = ?"];
        
        if (i == columns.count) {
            if (where) {
                [sql appendFormat:@" WHERE %@;", where];
            } else {
                [sql appendString:@";"];
            }
        } else {
            [sql appendString:@", "];
        }
        i++;
    }
    
    BOOL result = [self execSQL:sql bindParams:params error:error];
    
    if (result) {
        return sqlite3_changes(_db);
    }
    return -1;
    
}

- (int) updateTable:(NSString *)table
            columns:(NSArray *)columns
              where:(NSString *)where
         bindParams:(NSArray *)params
{
    return [self updateTable:table columns:columns where:where bindParams:params error:nil];
}

#pragma mark - private

- (BOOL) open {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                         NSUserDomainMask, YES);
    NSString *path = [paths objectAtIndex:0];
    NSString *dbFilePath = [path stringByAppendingPathComponent:self.dbName];
    
    int result = sqlite3_open([dbFilePath UTF8String], &_db);
    BOOL opened = (result == SQLITE_OK);
    if (!opened) {
        [self close];
    }
    
    return opened;
}

#pragma mark reference count

- (void) retainDB {
    @synchronized (self) {
        
        if (_refCount == 0) {
            [NSException raise:DConnectSQLiteExceptionRase format:@"Database is already closed."];
        }
        _refCount++;
    }
}

- (void) releaseDB {
    
    @synchronized (self) {
        
        if (_refCount == 0) {
            [NSException raise:DConnectSQLiteExceptionRase format:@"Database is already closed."];
        }
        _refCount--;
        if (_refCount == 0) {
            sqlite3_close(_db);
            _db = nil;
            DCLogD(@"DB Closed");
        }
    }
    
}

#pragma mark

- (void) checkState {
    if (![self isOpen]) {
        [NSException raise:DConnectSQLiteExceptionRase
                    format:@"Database is not opening."];
    }
}

- (void) bindParam:(id)param index:(int)index statement:(sqlite3_stmt *)stmt {
    
    if (!param || param == [NSNull null]) {
        sqlite3_bind_null(stmt, index);
    } else if ([param isKindOfClass:[NSData class]]) {
        const void *bytes = [param bytes];
        if (!bytes) {
            bytes = "";
        }
        sqlite3_bind_blob(stmt, index, bytes, (int)[param length], SQLITE_STATIC);
    } else if ([param isKindOfClass:[NSNumber class]]) {
        
        if (strcmp([param objCType], @encode(BOOL)) == 0) {
            sqlite3_bind_int(stmt, index, ([param boolValue] ? 1 : 0));
        } else if (strcmp([param objCType], @encode(char)) == 0) {
            sqlite3_bind_int(stmt, index, [param charValue]);
        } else if (strcmp([param objCType], @encode(unsigned char)) == 0) {
            sqlite3_bind_int(stmt, index, [param unsignedCharValue]);
        } else if (strcmp([param objCType], @encode(short)) == 0) {
            sqlite3_bind_int(stmt, index, [param shortValue]);
        } else if (strcmp([param objCType], @encode(unsigned short)) == 0) {
            sqlite3_bind_int(stmt, index, [param unsignedShortValue]);
        } else if (strcmp([param objCType], @encode(int)) == 0) {
            sqlite3_bind_int(stmt, index, [param intValue]);
        } else if (strcmp([param objCType], @encode(unsigned int)) == 0) {
            sqlite3_bind_int64(stmt, index, (long long)[param unsignedIntValue]);
        } else if (strcmp([param objCType], @encode(long)) == 0) {
            sqlite3_bind_int64(stmt, index, [param longValue]);
        } else if (strcmp([param objCType], @encode(unsigned long)) == 0) {
            sqlite3_bind_int64(stmt, index, (long long)[param unsignedLongValue]);
        } else if (strcmp([param objCType], @encode(long long)) == 0) {
            sqlite3_bind_int64(stmt, index, [param longLongValue]);
        } else if (strcmp([param objCType], @encode(unsigned long long)) == 0) {
            sqlite3_bind_int64(stmt, index, (long long)[param unsignedLongLongValue]);
        } else if (strcmp([param objCType], @encode(float)) == 0) {
            sqlite3_bind_double(stmt, index, [param floatValue]);
        } else if (strcmp([param objCType], @encode(double)) == 0) {
            sqlite3_bind_double(stmt, index, [param doubleValue]);
        } else {
            sqlite3_bind_text(stmt, index, [[param description] UTF8String], -1, SQLITE_STATIC);
        }
    } else if ([param isKindOfClass:[NSString class]]) {
        sqlite3_bind_text(stmt, index, [((NSString *)param) UTF8String], -1,
                          SQLITE_STATIC);
    }
}

- (int) compileSQL:(const char *)sql stmt:(sqlite3_stmt **)stmt {
    return sqlite3_prepare_v2(_db, sql, -1, stmt, NULL);
}

- (NSString *) lastErrorMessage {
    return [NSString stringWithUTF8String:sqlite3_errmsg(_db)];
}

- (int) lastErrorCode {
    return sqlite3_errcode(_db);
}

- (void) setSQLError:(NSError *__autoreleasing *)error {
    if (error) {
        NSDictionary *errorUserInfo
        = @{NSLocalizedDescriptionKey:[self lastErrorMessage]};
        *error = [NSError errorWithDomain:DConnectSQLiteDomain
                                     code:[self lastErrorCode]
                                 userInfo:errorUserInfo];
    }
}

@end
