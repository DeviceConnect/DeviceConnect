//
//  DConnectSQLiteOpenHelper.h
//  SQLiteLibrary
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSQLiteDatabase.h"

@class DConnectSQLiteOpenHelper;

typedef void (^DConnectQueryBlock)(DConnectSQLiteDatabase *database);

@protocol DConnectSQLiteOpenHelperDelegate <NSObject>
@optional

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper didDowngradeDatabase:(DConnectSQLiteDatabase *)database
         oldVersion:(int)oldVersion
         newVersion:(int)newVersion;

@required

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper didCreateDatabase:(DConnectSQLiteDatabase *)database;

- (void) openHelper:(DConnectSQLiteOpenHelper *)helper didUpgradeDatabase:(DConnectSQLiteDatabase *)database
         oldVersion:(int)oldVersion
         newVersion:(int)newVersion;


@end

@interface DConnectSQLiteOpenHelper : NSObject

@property (nonatomic, weak) id<DConnectSQLiteOpenHelperDelegate> delegate;

- (id) initWithDBName:(NSString *)dbName version:(int)version;

/*!
 データベースを作成、または開く。
 このメソッドで得たデータベースに対して必ずcloseを呼ぶ必要がある。
 */
- (DConnectSQLiteDatabase *) database;

/*!
 処理をキューイングし、直列的に実行していく。
 非同期で実装する場合にはこれをつかうこと。
 このメソッドの処理自体は同期的に処理される。
 実行後、ブロックの引数のdbはクローズされる。
 */
- (void) execQueryInQueue:(DConnectQueryBlock)block;

+ (DConnectSQLiteOpenHelper *) helperWithDBName:(NSString *)dbName version:(int)version;

@end
