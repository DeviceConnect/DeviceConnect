//
//  GHDataManager.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHDataManager.h"
#import "SVProgressHUD.h"

@implementation GHDataManager

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//

static GHDataManager* mgr = nil;

+(GHDataManager*)shareManager
{
    if (!mgr) {
        mgr = [[GHDataManager alloc]init];
    }
    
    return mgr;
}

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
- (id) init
{
	self = [super init];
	if (self != nil) {
        
        if (dispatch_get_current_queue() == dispatch_get_main_queue()) {
			_managedObjectContext = [self createManagedObjectContext];
		} else {
			dispatch_sync(dispatch_get_main_queue(), ^{
				_managedObjectContext = [self createManagedObjectContext];
			});
		}
        
        // ポリシー変更 コンフリクト解消用
        _managedObjectContext.mergePolicy = NSMergeByPropertyStoreTrumpMergePolicy;
    }
    
	return self;
}


-(void)dealoc
{
    self.managedObjectContext       = nil;
    self.managedObjectModel         = nil;
    self.persistentStoreCoordinator = nil;
}


//--------------------------------------------------------------//
#pragma mark - fetch
//--------------------------------------------------------------//


//フェッチリクエストの設定
-(NSFetchRequest*)fetchRequest:(NSPredicate*)predicate withEntityName:(NSString*)name
{
    NSFetchRequest *request = [self fetchRequest:predicate withEntityName:name context:self.managedObjectContext];
    return request;
}

-(NSFetchRequest*)fetchRequest:(NSPredicate*)predicate withEntityName:(NSString*)name  context:(NSManagedObjectContext *)moc
{
    NSFetchRequest *request = [self fetchRequest:predicate withSortDescriptors:nil entityName:name context:moc];
    return request;
}

- (NSFetchRequest*)fetchRequest:(NSPredicate*)predicate withSortDescriptors:(NSArray*)sortDescriptors entityName:(NSString*)name context:(NSManagedObjectContext *)moc
{
    //NSManagedObjectContextがnilの場合
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    NSFetchRequest *request = [[NSFetchRequest alloc]init];
    NSEntityDescription * entityDescription = [NSEntityDescription entityForName:name inManagedObjectContext:moc];
    [request setEntity:entityDescription];
    
    //IDで取得
    if (predicate != nil) {
        [request setPredicate:predicate];
    }
    
    if(sortDescriptors){
        [request setSortDescriptors:sortDescriptors];
    }
    
    return request;
}

//identifierからエンティティを取得
- (NSManagedObject*)getModelData:(NSString*)identifier withEntityName:(NSString*)name context:(NSManagedObjectContext *)moc
{
    //NSManagedObjectContextがnilの場合
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"identifier = %@", identifier];
    NSFetchRequest *request = [self fetchRequest:pred withEntityName:name context:moc];
    request.returnsObjectsAsFaults = NO;
    
    NSError *error;
    NSArray *objects = [moc executeFetchRequest:request error:&error];
    
    if ([objects count] > 0) {
        return [objects lastObject];
    }else{
        //        LOG(@"error %@", [error description]);
        return nil;
    }
}



//NSPredicateからエンティティを取得
- (NSArray*)getModelDataByPredicate:(NSPredicate*)pred
                     withEntityName:(NSString*)name
                            context:(NSManagedObjectContext *)moc
{
    //NSManagedObjectContextがnilの場合
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    NSFetchRequest *request = [self fetchRequest:pred withEntityName:name];
    request.returnsObjectsAsFaults = NO;
    
    NSError *error;
    NSArray *objects = [moc executeFetchRequest:request error:&error];
    
    if ([objects count] > 0) {
        return objects;
    }else{
        //                LOG(@"error %@", [error description]);
        return nil;
    }
}

//NSPredicateからエンティティを取得
- (NSArray*)getModelDataByPredicate:(NSPredicate*)pred
                withSortDescriptors:(NSArray *)sortDescriptors
                         entityName:(NSString*)name
                            context:(NSManagedObjectContext *)moc
{
    //NSManagedObjectContextがnilの場合
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    NSFetchRequest *request = [self fetchRequest:pred withSortDescriptors:sortDescriptors entityName:name context:moc];
    request.returnsObjectsAsFaults = NO;
    
    NSError *error;
    NSArray *objects = [moc executeFetchRequest:request error:&error];
    
    if ([objects count] > 0) {
        return objects;
    }else{
        //                LOG(@"error %@", [error description]);
        return nil;
    }
}


//エンティティのタイプでフェッチ
- (NSManagedObject*)getModelDataByType:(NSString*)type
                               context:(NSManagedObjectContext *)moc
{
    //NSManagedObjectContextがnilの場合
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    NSPredicate* pred = [NSPredicate predicateWithFormat:@"type = %@", type];
    NSFetchRequest *request = [self fetchRequest:pred
                             withSortDescriptors:@[[NSSortDescriptor sortDescriptorWithKey:@"priority" ascending:YES]]
                                      entityName:@"Page"
                                         context:moc];
    request.returnsObjectsAsFaults = NO;
    
    NSError *error;
    NSArray *objects = [moc executeFetchRequest:request error:&error];
    
    if ([objects count] > 0) {
        return [objects firstObject];
    }else{
        return nil;
    }
    
}


//--------------------------------------------------------------//
#pragma mark - CoreData初期化
//--------------------------------------------------------------//

- (NSManagedObjectContext *) createManagedObjectContext
{
	NSPersistentStoreCoordinator *coordinator = [self getPersistentStoreCoordinator];
	if (coordinator) {
		NSManagedObjectContext *context = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSMainQueueConcurrencyType];
		[context setPersistentStoreCoordinator:coordinator];
        [context setUndoManager:nil];
		return context;
	}
	return nil;
}


- (NSManagedObjectContext *) createAsyncManagedObjectContext
{
    NSManagedObjectContext *context = [[NSManagedObjectContext alloc] initWithConcurrencyType:NSPrivateQueueConcurrencyType];
    context.parentContext = _managedObjectContext;
    [context setUndoManager:nil];
    return context;
}


- (NSManagedObjectModel *)getManagedObjectModel
{
	if (_managedObjectModel) {
		return _managedObjectModel;
	}
    
	NSURL *modelURL = [[NSBundle mainBundle] URLForResource:@"db" withExtension:@"momd"];
	_managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:modelURL];
	return _managedObjectModel;
}

- (NSPersistentStoreCoordinator *)getPersistentStoreCoordinator
{
	if (_persistentStoreCoordinator) {
		return _persistentStoreCoordinator;
	}
    
    NSURL *storeURL = [[self applicationDocumentsDirectory] URLByAppendingPathComponent:@"db.sqlite"];
    

    //DBのマイグレーションが必要かチェック
    NSError* error = nil;
    NSDictionary *options = nil;
    NSDictionary *sourceMetaData = [NSPersistentStoreCoordinator metadataForPersistentStoreOfType:NSSQLiteStoreType URL:storeURL error:&error];
    
    if (sourceMetaData) {
        //コンテキストに適合性を問い合わせる
        BOOL isCompatible = [[self getManagedObjectModel] isConfiguration:nil compatibleWithStoreMetadata:sourceMetaData];
        
        if (!isCompatible) {
            LOG(@"マイグレーションが必要");
            
            //CoreDataの自動マイグレーションオプションを設定
            options = @{NSMigratePersistentStoresAutomaticallyOption:@(YES),
                        NSInferMappingModelAutomaticallyOption:@(YES)};
            
            [self performSelectorInBackground:@selector(startMigration) withObject:nil];
        }
    }
    
	error = nil;
	_persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self getManagedObjectModel]];
	if (![_persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType configuration:nil URL:storeURL options:options error:&error]) {
		LOG(@"Unresolved error %@, %@", error, [error userInfo]);
		return nil;
	}

    return _persistentStoreCoordinator;
}

- (NSURL *)applicationDocumentsDirectory
{
	return [[[NSFileManager defaultManager] URLsForDirectory:NSDocumentDirectory inDomains:NSUserDomainMask] lastObject];
}

- (void)startMigration
{
    [SVProgressHUD showWithStatus:NSLocalizedString(@"データベースをアップデートしています。数分かかり場合があります。", @"サーバーと同期中...") maskType:SVProgressHUDMaskTypeBlack];
}

//--------------------------------------------------------------//
#pragma mark - DB操作
//--------------------------------------------------------------//

//DB保存
-(void)save
{
    if (_managedObjectContext) {
        
        [_managedObjectContext performBlock:^{
            
            NSError *error = nil;
            if ([_managedObjectContext hasChanges] && ![_managedObjectContext save:&error]) {
                
                UIAlertView *alert = [[UIAlertView alloc]initWithTitle:NSLocalizedString(@"DBへの保存に失敗しました", nil)
                                                               message:[error description]
                                                              delegate:nil
                                                     cancelButtonTitle:@"OK"
                                                     otherButtonTitles:nil];
                [alert show];
            }
        }];
    }
}


//マネージャーをリリース
- (void)releaseManager
{
    if (mgr) {
        mgr = nil;
    }
}

//--------------------------------------------------------------//
#pragma mark - データ挿入
//--------------------------------------------------------------//

//エンティティ作成
- (NSEntityDescription *)createEntity:(NSManagedObjectContext *)moc withClassName:(NSString*)classname
{
    Class class = NSClassFromString(classname);
	return [NSEntityDescription insertNewObjectForEntityForName:NSStringFromClass([class class])
                                         inManagedObjectContext:moc];
}


//DBにNSManagedObject挿入
- (void)insertEntity:(NSManagedObject*)data context:(NSManagedObjectContext *)moc
{
    [moc insertObject:data];
}




//--------------------------------------------------------------//
#pragma mark - Pageエンティティ
//--------------------------------------------------------------//
- (Page*)createPageEntity:(GHPageModel*)model context:(NSManagedObjectContext *)moc
{
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    Page* page = (Page*)[self createEntity:moc withClassName:@"Page"];
    [self setPageData:model data:page];
    [self insertEntity:page context:moc];
    [self save];
    return page;
}


- (Page*)setPageData:(GHPageModel*)model data:(Page*)page
{
    page.title        = model.title;
    page.url          = model.url;
    page.type         = model.type;
    page.category     = model.category;
    if (model.priority) {
        page.priority     = model.priority;
    }
    page.created_date = [NSDate date];
    page.sectionIndex = [GHUtils dateToString:page.created_date];
    page.identifier   = [GHUtils createUUID];
    
    return page;
}




- (Page*)addHistory:(GHPageModel*)model context:(NSManagedObjectContext *)moc
{
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    Page* page = (Page*)[self createEntity:moc withClassName:@"Page"];
    
    page.title        = model.title;
    page.url          = model.url;
    page.type         = model.type;
    page.category     = model.category;
    if (model.priority) {
        page.priority     = model.priority;
    }
    page.created_date = [NSDate date];
    page.sectionIndex = [GHUtils dateToString:page.created_date];
    page.identifier   = [GHUtils createUUID];
    
    NSArray *parents = [self getModelDataByPredicate:[NSPredicate predicateWithFormat:@"type = %@", TYPE_HISTORY]
                                      withEntityName:@"Page" context:moc];
    //リレーションセット
    if ([parents count] > 0) {
        page.parent = [parents firstObject];
    }
    
    [self insertEntity:page context:moc];
    [self save];
    
    return page;
    
}



- (void)addBookmark:(GHPageModel*)model parent:(Page*)parent context:(NSManagedObjectContext *)moc
{
    if (!moc) {
        moc = _managedObjectContext;
    }
    
    Page* page = (Page*)[self createEntity:moc withClassName:@"Page"];
    
    page.title        = model.title;
    page.url          = model.url;
    page.type         = model.type;
    page.category     = model.category;
    if (model.priority) {
        page.priority     = model.priority;
    }
    page.created_date = [NSDate date];
    page.sectionIndex = [GHUtils dateToString:page.created_date];
    page.identifier   = [GHUtils createUUID];
    
    //リレーションセット
    page.parent = parent;
    
    [self insertEntity:page context:moc];
    [self save];
    
}



//--------------------------------------------------------------//
#pragma mark - エンティティの削除
//--------------------------------------------------------------//
- (void)deleteEntity:(NSManagedObject*)entity
{
    if (entity) {
        [_managedObjectContext deleteObject:entity];
    }
}



- (void)deleteAllHistory
{
    [self deleteAll];
    
    //2度削除しないと完全に消えない
    [self performSelector:@selector(deleteAll) withObject:nil afterDelay:0.2];
    
    //ApplicationCashのあるフォルダ毎削除する
    [self deleteCashes];


    //履歴消去通知
    [GHUtils postNotification:nil withKey:CLEAR_HISTORY];
}


- (void)deleteAll
{
    __weak GHDataManager *_self = self;
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"category = %@", CATEGORY_HISTORY];
    NSArray* history = [_self getModelDataByPredicate:pred withEntityName:@"Page" context:nil];
    
    NSManagedObjectContext *context = [self managedObjectContext];
    [context performBlock:^{
        for (Page* page in history) {
            [context deleteObject:page];
            
            NSError *error = nil;
            if (![context save:&error]) {
                //Note: You should really do something more useful than log this
                NSLog(@"Can't Delete! %@ %@", error, [error localizedDescription]);
            }
        }
    }];
}


//ApplicationCashのあるディレクトリを削除
- (void)deleteCashes
{
    NSString* cashes = [GHUtils cashesDirectory];
    NSString* bundle = [[NSBundle mainBundle] bundleIdentifier];
    NSString* dir = [NSString stringWithFormat:@"%@/%@",cashes, bundle];
    
    if([[NSFileManager defaultManager]fileExistsAtPath:dir]){
        
        NSError *error;
        if (![[NSFileManager defaultManager]removeItemAtPath:dir
                                                      error:&error]){
            LOG(@"error %@", error);
        } ;
        
        NSURLCache* cache = [NSURLCache sharedURLCache];
        [cache removeAllCachedResponses];
    }

}

//--------------------------------------------------------------//
#pragma mark - 初期フォルダセット
//--------------------------------------------------------------//
- (void)initPrefs
{
    //お気に入りフォルダを探す。　なければ初期値をセット
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"type = %@", TYPE_FAVORITE];
    NSArray* prefs = [self getModelDataByPredicate:pred withEntityName:@"Page" context:nil];
    if ([prefs count] == 0) {
        //初期値セット
        
        GHPageModel *favor = [[GHPageModel alloc]init];
        favor.title = @"お気に入り";
        favor.type = TYPE_FAVORITE;
        favor.priority = @(1);
        [self createPageEntity:favor context:nil];
        
        GHPageModel *history = [[GHPageModel alloc]init];
        history.title = @"履歴";
        history.type = TYPE_HISTORY;
        history.priority = @(2);
        [self createPageEntity:history context:nil];
        
        GHPageModel *bookmark = [[GHPageModel alloc]init];
        bookmark.title = @"ブックマーク";
        bookmark.type = TYPE_BOOKMARK_FOLDER;
        bookmark.priority = @(3);
        [self createPageEntity:bookmark context:nil];
    }else{
        [GHUtils clearCashes];
    }
}



@end
