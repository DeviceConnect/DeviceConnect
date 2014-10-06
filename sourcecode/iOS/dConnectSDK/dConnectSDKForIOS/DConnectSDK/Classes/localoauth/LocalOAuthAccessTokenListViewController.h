//
//  LocalOAuthAccessTokenListViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>


// カスタムセル
@interface LocalOAuthAccessTokenListCell : UITableViewCell

/*!
    tableViewセルのアプリケーション名
 */
@property (weak, nonatomic) IBOutlet UILabel *tableCellApplicationName;

/*!
    tableViewセルのスコープ名
 */
@property (weak, nonatomic) IBOutlet UILabel *tableCellScopes;

@end



@interface LocalOAuthAccessTokenListViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UIAlertViewDelegate>


/*!
    アクセストークン一覧を表示するTableView
 */
@property (weak, nonatomic) IBOutlet UITableView *tableAccessTokenList;

/*!
    key設定
    @param key[in] key
 */
- (void)setKey: (NSString *)key;

    
@end
