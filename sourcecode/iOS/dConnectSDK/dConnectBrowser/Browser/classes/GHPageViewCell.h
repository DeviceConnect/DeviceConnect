//
//  GHPageViewCell.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHData.h"

@interface GHPageViewCell : UITableViewCell

@property (nonatomic, weak) IBOutlet UILabel* titleLabel;
@property (nonatomic, weak) IBOutlet UILabel* urlLabel;
@property (nonatomic, weak) Page *myPage;

- (void)setItems:(Page*)page;

@end
