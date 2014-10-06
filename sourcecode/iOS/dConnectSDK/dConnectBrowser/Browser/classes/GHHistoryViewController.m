//
//  GHHistoryViewController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHHistoryViewController.h"
#import "GHPageViewCell.h"

@interface GHHistoryViewController ()
@property (nonatomic, strong) NSFetchedResultsController *fetchedResultsController;
@property (nonatomic, strong) NSPredicate* backForwardPred;
@end

@implementation GHHistoryViewController

- (void)dealloc
{
    self.fetchedResultsController.delegate = nil;
    self.fetchedResultsController = nil;
    self.backForwardPred = nil;
    
    _didHistorySelected = nil;
}


//--------------------------------------------------------------//
#pragma mark - 履歴消去
//--------------------------------------------------------------//
- (IBAction)eraseAll:(id)sender
{
    //0件の場合は無視
    NSArray* sections = [self.fetchedResultsController sections];
    if ([sections count] == 0) {
        return;
    }
    
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"確認"
                                                   message:@"履歴を削除します。よろしいでしょうか？"
                                                  delegate:self
                                         cancelButtonTitle:@"キャンセル"
                                         otherButtonTitles:@"履歴を消去", nil];
    [alert show];
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != alertView.cancelButtonIndex) {
        [[GHDataManager shareManager] deleteAllHistory];
    }
}


//--------------------------------------------------------------//
#pragma mark - 進む・戻る履歴
//--------------------------------------------------------------//
- (void)setPredicate:(NSPredicate*)pred
{
    self.backForwardPred = pred;
}


//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.cellID = @"history";
    [self.tableView registerNib:[UINib nibWithNibName:@"GHPageViewCell" bundle:[NSBundle mainBundle]]
         forCellReuseIdentifier:self.cellID];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];  
    self.title = @"履歴";
    
    //戻る・進む履歴の場合
    if (self.listType == kListType_history_back || self.listType == kListType_history_forward) {
        self.navigationController.toolbarHidden = YES;
        
        UIBarButtonItem *item = [[UIBarButtonItem alloc]
                                  initWithTitle:@"完了"
                                  style:UIBarButtonItemStylePlain
                                  target:self
                                 action:@selector(close:)];
        self.navigationItem.rightBarButtonItem = item;
    }
    
}


//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    Page* page = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    if(_didHistorySelected){
        _didHistorySelected(indexPath.row, page);
    }else{
        NSDictionary* dict = @{PAGE_URL:page.url};
        [GHUtils postNotification:dict withKey:SHOW_WEBPAGE];
    }
}


- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    id <NSFetchedResultsSectionInfo> sectionInfo = [self.fetchedResultsController sections][section];
    return [sectionInfo name];
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}

//--------------------------------------------------------------//
#pragma mark - Fetched results controller
//--------------------------------------------------------------//

- (NSFetchedResultsController *)fetchedResultsController
{
    if (_fetchedResultsController != nil) {
        return _fetchedResultsController;
    }
    
    NSManagedObjectContext *managedObjectContext = [[GHDataManager shareManager] managedObjectContext];
    
    //categoryが/historyのものを取得
    NSPredicate *pred;
    
    if (self.backForwardPred) {
        pred = self.backForwardPred;
    }else{
        pred = [NSPredicate predicateWithFormat:@"category = %@", CATEGORY_HISTORY];
    }
    
    NSFetchRequest *fetchRequest = [[GHDataManager shareManager]fetchRequest:pred withEntityName:@"Page"];
    
    // Set the batch size to a suitable number.
    [fetchRequest setFetchBatchSize:20];
    
    NSString* sectionKey = nil;
    NSSortDescriptor *sortDescriptor;
    if (self.listType == kListType_history_back || self.listType == kListType_history_forward) {
        //戻る・進む履歴の場合
        BOOL isAscending = NO;
        if (self.listType == kListType_history_forward) {
            isAscending = YES;
        }
        sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"created_date" ascending:isAscending];
    }else{
        sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"created_date" ascending:NO];
        sectionKey = @"sectionIndex";
    }
    
    NSArray *sortDescriptors = @[sortDescriptor];
    [fetchRequest setSortDescriptors:sortDescriptors];
    
    fetchRequest.returnsObjectsAsFaults = NO;
    
    NSFetchedResultsController *aFetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:fetchRequest
                                                                                                managedObjectContext:managedObjectContext
                                                                                                  sectionNameKeyPath:sectionKey
                                                                                                           cacheName:nil];
    aFetchedResultsController.delegate = self;
    self.fetchedResultsController = aFetchedResultsController;
    
	NSError *error = nil;
	if (![self.fetchedResultsController performFetch:&error]) {
	    LOG(@"Unresolved error %@, %@", error, [error userInfo]);
	    abort();
	}
    
    return _fetchedResultsController;
}


- (void)configureCell:(GHPageViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
           controller:(NSFetchedResultsController *)controller
{
    Page* page = [controller objectAtIndexPath:indexPath];
    [cell setItems:page];
}


@end
