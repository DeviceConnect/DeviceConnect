//
//  GHListViewController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHDataManager.h"

/*
 ブックマーク一覧controller（最上位）
 */

@interface GHBookmarkTopController : UITableViewController<NSFetchedResultsControllerDelegate>
@property (nonatomic, strong) NSFetchedResultsController *fetchedResultsController;

@property (nonatomic, weak) IBOutlet UIBarButtonItem *editBtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem *folderBtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem *doneBtn;

///完了ボタン
- (IBAction)close:(id)sender;

///編集ボタン
- (IBAction)edit:(id)sender;

@end
