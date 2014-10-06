//
//  GHHistoryViewController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHBookmarkViewController.h"

/*
 履歴詳細controller（GHBookmarkViewController継承）
 */

typedef void (^DidHistorySelected)(NSInteger, Page*);

@interface GHHistoryViewController : GHBookmarkViewController<UIAlertViewDelegate>
@property(nonatomic, copy) DidHistorySelected didHistorySelected;
- (IBAction)eraseAll:(id)sender;
- (void)setPredicate:(NSPredicate*)pred;
@end
