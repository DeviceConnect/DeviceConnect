//
//  DConnectSQLiteCursor.m
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLiteCursor.h"

@interface DConnectSQLiteCursor() {
    sqlite3_stmt *_stmt;
}

@end

@implementation DConnectSQLiteCursor

- (id) initWithSQLiteStatement:(sqlite3_stmt *)statement {
    
    self = [super init];
    
    if (self) {
        _stmt = statement;
        sqlite3_reset(_stmt);
        _count = 0;
        while (sqlite3_step(_stmt) == SQLITE_ROW) {
            _count++;
        }
    }
    
    return self;
}

- (void) close {
    if (_stmt) {
        sqlite3_finalize(_stmt);
        _stmt = nil;
    }
}

- (BOOL) moveToFirst {
    sqlite3_reset(_stmt);
    return [self moveToNext];
}

- (BOOL) moveToNext {
    int result = sqlite3_step(_stmt);
    return result == SQLITE_ROW;
}

#pragma mark - values

- (int) intValueAtIndex:(int)columnIndex {
    return sqlite3_column_int(_stmt, columnIndex);
}

- (long long) longLongValueAtIndex:(int)columnIndex {
    return (long long) sqlite3_column_int64(_stmt, columnIndex);
}

- (double) doubleValueAtIndex:(int)columnIndex {
    return sqlite3_column_double(_stmt, columnIndex);
}

- (BOOL) boolValueAtIndex:(int)columnIndex {
    int boolVal = sqlite3_column_int(_stmt, columnIndex);
    return boolVal == 1;
}

- (NSString *) stringValueAtIndex:(int)columnIndex {
    const char *str = (const char *) sqlite3_column_text(_stmt, columnIndex);
    if (str == NULL) {
        return nil;
    }
    return [NSString stringWithUTF8String:str];
}

- (NSData *) blobValueAtIndex:(int)columnIndex {
    
    int dataSize = sqlite3_column_bytes(_stmt, columnIndex);
    const char *dataBuffer = sqlite3_column_blob(_stmt, columnIndex);
    
    if (dataBuffer == NULL) {
        return nil;
    }
    
    return [NSData dataWithBytes:(const void *)dataBuffer length:(NSUInteger)dataSize];
}

#pragma mark - static

+ (DConnectSQLiteCursor *) cursorWithStatement:(sqlite3_stmt *)statement {
    DConnectSQLiteCursor *cursor = [[DConnectSQLiteCursor alloc] initWithSQLiteStatement:statement];
    return cursor;
}


@end
