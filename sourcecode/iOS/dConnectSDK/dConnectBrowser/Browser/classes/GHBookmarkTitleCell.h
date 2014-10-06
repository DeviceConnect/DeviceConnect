//
//  GHBookmarkTitleCell.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHData.h"
@interface GHBookmarkTitleCell : UITableViewCell

@property (nonatomic, weak) IBOutlet UITextField *titleField;
@property (nonatomic, weak) IBOutlet UILabel     *urlLabel;
@property (nonatomic, weak) IBOutlet UIImageView *favicon;

- (void)setItem:(GHPageModel*)page;
@end
