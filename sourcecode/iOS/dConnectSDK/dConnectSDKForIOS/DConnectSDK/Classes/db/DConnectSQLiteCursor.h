//
//  DConnectSQLiteCursor.h
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <sqlite3.h>

@interface DConnectSQLiteCursor : NSObject

@property (nonatomic, readonly) int count;

- (id) initWithSQLiteStatement:(sqlite3_stmt *)statement;
- (void) close;

- (BOOL) moveToFirst;
- (BOOL) moveToNext;

#pragma mark - values
- (int) intValueAtIndex:(int)columnIndex;
- (double) doubleValueAtIndex:(int)columnIndex;
- (long long) longLongValueAtIndex:(int)columnIndex;
- (BOOL) boolValueAtIndex:(int)columnIndex;
- (NSString *) stringValueAtIndex:(int)columnIndex;
- (NSData *) blobValueAtIndex:(int)columnIndex;

+ (DConnectSQLiteCursor *) cursorWithStatement:(sqlite3_stmt *)statement;

@end
