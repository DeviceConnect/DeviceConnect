//
//  GHFoldersController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHDataManager.h"

/*
 フォルダ階層controller
 */

typedef void (^SelectFolderCallback)(Page* folder);

@interface GHFoldersListController : UIViewController
@property (nonatomic, copy) SelectFolderCallback selectFolder;
@property (nonatomic, weak) Page* myPage;

- (void)setInitialSelection:(Page*)page;

@end
