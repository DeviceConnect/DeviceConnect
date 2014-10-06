//
//  GHDataManager.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "GHData.h"

@interface GHDataManager : NSObject

@property (nonatomic, strong) NSManagedObjectContext       *managedObjectContext;
@property (nonatomic, strong) NSManagedObjectModel         *managedObjectModel;
@property (nonatomic, strong) NSPersistentStoreCoordinator *persistentStoreCoordinator;

/**
 * シングルトンで使用する。
 * @return GHDataManager
 */
+ (GHDataManager*)shareManager;

/**
 * 非同期処理用のNSManagedObjectContextの作成
 * @return NSManagedObjectContext
 */
- (NSManagedObjectContext *) createAsyncManagedObjectContext;

/**
 * フェッチ処理（同期用）
 * @param predicate 検索項目
 * @param name フェッチ対象のエンティティ名
 * @return NSFetchRequest
 */
- (NSFetchRequest*)fetchRequest:(NSPredicate*)predicate withEntityName:(NSString*)name;

/**
 * DBにエンティティを挿入
 * @param data 挿入するNSManagedObject
 * @param moc 使用するNSManagedObjectContext
 */
- (void)insertEntity:(NSManagedObject*)data
             context:(NSManagedObjectContext *)moc;

/**
 * Pageエンティティを作成
 * @param model GHPageModel
 * @param moc 使用するNSManagedObjectContext
 * @return Page
 */
- (Page*)createPageEntity:(GHPageModel*)model context:(NSManagedObjectContext *)moc;

/**
 * 履歴エンティティを作成
 * @param model GHPageModel
 * @param moc 使用するNSManagedObjectContext
 */
- (Page*)addHistory:(GHPageModel*)model context:(NSManagedObjectContext *)moc;
- (void)addBookmark:(GHPageModel*)model parent:(Page*)parent context:(NSManagedObjectContext *)moc;

/**
 * エンティティのidentifierでフェッチ
 * @param identifier フェッチするidentifier
 * @param name フェッチ対象のエンティティ名
 * @param moc 使用するNSManagedObjectContext
 * @return NSManagedObject 取得したエンティティを返却
 */
- (NSManagedObject*)getModelData:(NSString*)identifier
                  withEntityName:(NSString*)name
                         context:(NSManagedObjectContext *)moc;



/**
 * NSPredicateからエンティティを取得
 * @param pred 
 * @param name フェッチ対象のエンティティ名
 * @param moc 使用するNSManagedObjectContext
 * @return NSArray 検索結果を配列で返却
 */
- (NSArray*)getModelDataByPredicate:(NSPredicate*)pred
                     withEntityName:(NSString*)name
                            context:(NSManagedObjectContext *)moc;

/**
 * NSPredicateからエンティティを取得
 * @param pred
 * @param sortDescriptors
 * @param name フェッチ対象のエンティティ名
 * @param moc 使用するNSManagedObjectContext
 * @return NSArray 検索結果を配列で返却
 */
- (NSArray*)getModelDataByPredicate:(NSPredicate*)pred
                withSortDescriptors:(NSArray*)sortDescriptors
                         entityName:(NSString*)name
                            context:(NSManagedObjectContext *)moc;


/**
 * エンティティのタイプでフェッチ
 * @param type フェッチするtype
 * @param moc 使用するNSManagedObjectContext
 * @return NSManagedObject 取得したエンティティを返却
 */
- (NSManagedObject*)getModelDataByType:(NSString*)type
                      context:(NSManagedObjectContext *)moc;


///履歴の全削除
- (void)deleteAllHistory;



/**
 * エンティティの削除
 * @param entity 削除するNSManagedObject
 */
- (void)deleteEntity:(NSManagedObject*)entity;


/**
 * DB保存
 */
-(void)save;

///お気に入り、履歴フォルダの作成
- (void)initPrefs;


/**
 * マネージャーをリリースする（DBを切り替える場合に使用）
 */
- (void)releaseManager;

@end
