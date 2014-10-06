//
//  GHAddBookmarkController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHDataManager.h"
#import "GHBookmarkTitleCell.h"

/*
 ブックマーク追加controller
 */

@interface GHAddBookmarkController : UITableViewController

#define CELL_TITLE @"cell_title"
#define CELL_LOC   @"cell_loc"

///追加するページ情報モデル
@property (nonatomic, strong) GHPageModel* myPage;

@property (nonatomic, strong) NSArray* datasource;
@property (nonatomic, strong) Page* directory;

- (id)initWithPage:(GHPageModel*)page;

- (void)cancel;
- (void)done;
- (void)updateFolder:(Page*)directory;
- (void)setDirectory;

@end
