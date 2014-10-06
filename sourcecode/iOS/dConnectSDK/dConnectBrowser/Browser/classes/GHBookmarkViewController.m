//
//  GHBookmarkViewController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHBookmarkViewController.h"
#import "GHPageViewCell.h"
#import "GHFolderCreateController.h"
#import "GHFoldersListController.h"

@interface GHBookmarkViewController ()
{
    BOOL isEditing;
}
@property (nonatomic, strong) NSFetchedResultsController *fetchedResultsController;

@end

@implementation GHBookmarkViewController

- (void)dealloc
{
    self.fetchedResultsController.delegate = nil;
    self.fetchedResultsController = nil;
    self.parent = nil;
    self.cellID = nil;
}


//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.cellID = @"bookmark";
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:self.cellID];
    
    self.folderBtn.tintColor = [UIColor colorWithWhite:0 alpha:0];
    isEditing = NO;
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    self.title = self.parent.title;
    [self setEdiMode:isEditing];
    self.navigationController.toolbarHidden = NO;
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}


//--------------------------------------------------------------//
#pragma mark - ボタン制御
//--------------------------------------------------------------//
- (IBAction)close:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)edit:(id)sender
{
    isEditing = !isEditing;
    [self setEdiMode:isEditing];
}


- (void)setEdiMode:(BOOL)edit
{
    [self.tableView setEditing:isEditing animated:YES];
    self.tableView.allowsSelectionDuringEditing = YES;
    
    if (isEditing) {
        //編集モード
        self.doneBtn.tintColor = [UIColor colorWithWhite:0 alpha:0];
        self.doneBtn.enabled = NO;
        self.folderBtn.tintColor = nil;
        self.folderBtn.enabled = YES;
        [self.editBtn setTitle:@"完了"];
    }else{
        //通常
        self.doneBtn.tintColor = nil;
        self.doneBtn.enabled = YES;
        self.folderBtn.tintColor = [UIColor colorWithWhite:0 alpha:0];
        self.folderBtn.enabled = NO;
        [self.editBtn setTitle:@"編集"];
    }
    
    //iPadは常に完了ボタンを非表示
    if ([GHUtils isiPad]) {
        self.doneBtn.enabled = NO;
        self.doneBtn.tintColor = nil;
        self.navigationItem.rightBarButtonItem = nil;
    }
}


//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [[self.fetchedResultsController sections] count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    id <NSFetchedResultsSectionInfo> sectionInfo = [self.fetchedResultsController sections][section];
    return [sectionInfo numberOfObjects];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell* cell = [tableView dequeueReusableCellWithIdentifier:self.cellID forIndexPath:indexPath];
    [self configureCell:cell atIndexPath:indexPath controller:self.fetchedResultsController];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    Page* page = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    
    //編集中はブックマークのみ処理
    if (isEditing) {
        if ([page.type isEqualToString:TYPE_BOOKMARK]) {
            //移動先を選択
            [self showFolderSelection:page];
        }
    }else{
    
        //ブックマークまたは履歴なのでPageモデルを渡す
        if([page.type isEqualToString:TYPE_BOOKMARK]){
            NSDictionary* dict = @{PAGE_URL:page.url};
            [GHUtils postNotification:dict withKey:SHOW_WEBPAGE];
            
        }else if([page.type isEqualToString:TYPE_FOLDER]){
            //次の階層へ
            UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Bookmark" bundle:[NSBundle mainBundle]];
            GHBookmarkViewController *controller = (GHBookmarkViewController*)[storyboard instantiateInitialViewController];
            controller.listType = kListType_folder;
            controller.parent = page;
            [self.navigationController pushViewController:controller animated:YES];
        }
    }
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

//編集中にセルをハイライトしない
- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (isEditing) {
        Page* page = [self.fetchedResultsController objectAtIndexPath:indexPath];
        if ([page.type isEqualToString:TYPE_BOOKMARK]) {
            return YES;
        }else{
            return NO;
        }
    }else{
        return YES;
    }
}

/**
 * 移動先のフォルダ選択
 * @param page 移動するPageモデル
 */
- (void)showFolderSelection:(Page*)page
{
    //フォルダ選択
    __weak Page* _page = page;
    GHFoldersListController *folder = [[GHFoldersListController alloc]init];
    [folder setInitialSelection:page.parent];
    
    //フォルダ選択後のコールバック
    [folder setSelectFolder:^(Page* folder){
        _page.parent = folder;
        _page.priority = @([folder.children count] + PRIORITY);
    }];
    
    [self.navigationController pushViewController:folder animated:YES];
}

//--------------------------------------------------------------//
#pragma mark - Fetched results controller
//--------------------------------------------------------------//

- (NSFetchedResultsController *)fetchedResultsController
{
    if (_fetchedResultsController != nil) {
        return _fetchedResultsController;
    }
    
    NSManagedObjectContext *managedObjectContext = [[GHDataManager shareManager]managedObjectContext];
    
    
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"parent = %@", self.parent];
    NSFetchRequest *fetchRequest = [[GHDataManager shareManager]fetchRequest:pred withEntityName:@"Page"];
    
    // Set the batch size to a suitable number.
    [fetchRequest setFetchBatchSize:20];
    
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"priority" ascending:YES];
    NSArray *sortDescriptors = @[sortDescriptor];
    [fetchRequest setSortDescriptors:sortDescriptors];
    
    fetchRequest.returnsObjectsAsFaults = NO;
    
    NSFetchedResultsController *aFetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest
                                                                                                managedObjectContext:managedObjectContext
                                                                                                  sectionNameKeyPath:nil                                                                                                                cacheName:nil];
    aFetchedResultsController.delegate = self;
    self.fetchedResultsController = aFetchedResultsController;
    
	NSError *error = nil;
	if (![self.fetchedResultsController performFetch:&error]) {
	    LOG(@"Unresolved error %@, %@", error, [error userInfo]);
	    abort();
	}
    
    return _fetchedResultsController;
}

- (void)controllerWillChangeContent:(NSFetchedResultsController *)controller
{
    [self.tableView beginUpdates];
}

- (void)controller:(NSFetchedResultsController *)controller didChangeSection:(id <NSFetchedResultsSectionInfo>)sectionInfo
           atIndex:(NSUInteger)sectionIndex forChangeType:(NSFetchedResultsChangeType)type
{
    switch(type) {
        case NSFetchedResultsChangeInsert:
            [self.tableView insertSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeDelete:
            [self.tableView deleteSections:[NSIndexSet indexSetWithIndex:sectionIndex] withRowAnimation:UITableViewRowAnimationFade];
            break;
    }
}

- (void)controller:(NSFetchedResultsController *)controller didChangeObject:(id)anObject
       atIndexPath:(NSIndexPath *)indexPath forChangeType:(NSFetchedResultsChangeType)type
      newIndexPath:(NSIndexPath *)newIndexPath
{
    UITableView *tableView = self.tableView;
    
    switch(type) {
        case NSFetchedResultsChangeInsert:
            [tableView insertRowsAtIndexPaths:@[newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeDelete:
            [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
            
        case NSFetchedResultsChangeUpdate:
            [self configureCell:(GHPageViewCell *)[tableView cellForRowAtIndexPath:indexPath]
                    atIndexPath:indexPath
                     controller:controller];
            break;
            
        case NSFetchedResultsChangeMove:
            [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
            [tableView insertRowsAtIndexPaths:@[newIndexPath] withRowAnimation:UITableViewRowAnimationFade];
            break;
    }
    
    
}

- (void)controllerDidChangeContent:(NSFetchedResultsController *)controller
{
    [self.tableView endUpdates];
}


- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
           controller:(NSFetchedResultsController *)controller
{
    cell.editingAccessoryType = UITableViewCellAccessoryNone;
    
    Page* page = [controller objectAtIndexPath:indexPath];
    cell.textLabel.text = page.title;
    
    if ([page.type isEqualToString:TYPE_BOOKMARK]){
        //ブックマーク
        cell.imageView.image = [UIImage imageNamed:@"bookmark"];
        
        //編集中のアクセサリー
        cell.editingAccessoryType = UITableViewCellAccessoryDisclosureIndicator;
        
    }else if ([page.type isEqualToString:TYPE_FOLDER]){
        //フォルダ
        cell.imageView.image = [UIImage imageNamed:@"folder"];
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
}


- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}


- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        Page *delete = (Page*)[self.fetchedResultsController objectAtIndexPath:indexPath];
        [[GHDataManager shareManager]deleteEntity:delete];
        [[GHDataManager shareManager]save];
    }
}



- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
    Page* fromPage = [self.fetchedResultsController objectAtIndexPath:fromIndexPath];
    NSNumber *fromPriority = fromPage.priority;
    
    Page* toPage   = [self.fetchedResultsController objectAtIndexPath:toIndexPath];
    NSNumber *toPriority = toPage.priority;
    
    fromPage.priority = toPriority;
    toPage.priority = fromPriority;
    
    [[GHDataManager shareManager]save];
}



- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}


//特定のセルのみを固定
- (NSIndexPath*)tableView:(UITableView*)tableView
targetIndexPathForMoveFromRowAtIndexPath:(NSIndexPath*)sourceIndexPath
      toProposedIndexPath:(NSIndexPath*)proposedDestinationIndexPath
{
    if (  (proposedDestinationIndexPath.row > 1) && (proposedDestinationIndexPath.section == sourceIndexPath.section)) {
        return proposedDestinationIndexPath;
    } else {
        return sourceIndexPath;
    }
}



//--------------------------------------------------------------//
#pragma mark - Navigation
//--------------------------------------------------------------//

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    //フォルダ作成時に作成される階層を渡す
    if ([@"CreateFolder" isEqualToString:segue.identifier]) {
        GHFolderCreateController *folder = (GHFolderCreateController *)segue.destinationViewController;
        folder.directory = self.parent;
    }
}

@end
