//
//  DConnectSQLiteOpenHelper.m
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLiteOpenHelper.h"
#import "DConnectSQLiteDatabase.h"
#import "DConnectSQLiteCursor.h"

#define DCONNECT_SQL_DOMAIN "org.deviceconnect.ios.sqlite3"

@interface DConnectSQLiteOpenHelper() {
    DConnectSQLiteDatabase *_db;
    dispatch_queue_t _queryQueue;
}

@property (nonatomic, strong) NSString *dbName;
@property (nonatomic) int version;

@end

@implementation DConnectSQLiteOpenHelper

#pragma mark - init

- (id) init {
    return nil;
}

- (id) initWithDBName:(NSString *)dbName version:(int)version {
    self = [super init];
    
    if (self) {
        self.dbName = dbName;
        self.version = version;
        _queryQueue = dispatch_queue_create(DCONNECT_SQL_DOMAIN, DISPATCH_QUEUE_SERIAL);
        _db = nil;
    }
    
    return self;
}

- (DConnectSQLiteDatabase *) database {
    
    @synchronized (self) {
        if (_db && [_db isOpen]) {
            [_db retainDB];
            return _db;
        }
        
        _db = [DConnectSQLiteDatabase openDatabaseWithDBName:self.dbName];
        
        if (!_db) {
            return nil;
        }
        
        if (_delegate) {
            
            int old = _db.version;
            int new = self.version;
            
            if (old != new) {
                _db.version = self.version;
                
                if (old == 0) {
                    [_delegate openHelper:self didCreateDatabase:_db];
                } else if (new > old) {
                    [_delegate openHelper:self didUpgradeDatabase:_db oldVersion:old newVersion:new];
                } else if (new < old
                           && [_delegate respondsToSelector:@selector(openHelper:didDowngradeDatabase:oldVersion:newVersion:)])
                {
                    [_delegate openHelper:self didDowngradeDatabase:_db oldVersion:old newVersion:new];
                }
            }
        }
        
        return _db;
    }
}

- (void) execQueryInQueue:(DConnectQueryBlock)block {
    // キューイングし、非同期のアクセス時には直列的に実行されるようにする。
    // また、セッションを使い回すため、リファレンスカウントは非同期的に上げる。
    DConnectSQLiteDatabase *database = self.database;
    dispatch_sync(_queryQueue, ^{
        block(database);
        if (database) {
            [database close];
        }
    });
}

#pragma mark - static

+ (DConnectSQLiteOpenHelper *) helperWithDBName:(NSString *)dbName version:(int)version {
    DConnectSQLiteOpenHelper *helper = [[DConnectSQLiteOpenHelper alloc] initWithDBName:dbName version:version];
    return helper;
}

@end
