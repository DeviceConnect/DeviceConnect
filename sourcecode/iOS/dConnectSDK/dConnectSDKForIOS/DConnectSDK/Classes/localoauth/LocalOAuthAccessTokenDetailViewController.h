//
//  LocalOAuthAccessTokenDetailViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "LocalOAuthSQLiteToken.h"

@interface LocalOAuthAccessTokenDetailViewCell : UITableViewCell

/*!
    スコープ名
 */
@property (weak, nonatomic) IBOutlet UILabel *scopeLabel;

/*!
    有効期限
 */
@property (weak, nonatomic) IBOutlet UILabel *expirePeriodLabel;

@end

@interface LocalOAuthAccessTokenDetailViewController : UIViewController<UITableViewDataSource, UITableViewDelegate>

/*!
    スコープ詳細表示TableView
 */
@property (weak, nonatomic) IBOutlet UITableView *scopeTableView;



- (void) setToken: (LocalOAuthSQLiteToken *)sqliteToken;


@end
