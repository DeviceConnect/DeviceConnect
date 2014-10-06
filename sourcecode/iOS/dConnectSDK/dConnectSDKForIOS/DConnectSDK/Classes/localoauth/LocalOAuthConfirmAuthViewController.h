//
//  LocalOAuthConfirmAuthViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>

typedef void (^LocalOAuthApprovalCallback)(BOOL isApproval);


@interface LocalOAuthConfirmAuthViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, UIGestureRecognizerDelegate>

/* 有効期限表示 */
@property (weak, nonatomic) IBOutlet UILabel *labelExpirePeriod;

/* アプリケーション名 */
@property (weak, nonatomic) IBOutlet UILabel *labelApplicationName;

/* スコープ名リスト */
@property (weak, nonatomic) IBOutlet UITableView *tableScopeView;


- (void)setParameter: (NSObject *)confirmAuthParams
       displayScopes: (NSArray *)displayScopes
     setAutoTestMode: (BOOL)autoTestMode
  approvalCallback: (LocalOAuthApprovalCallback)approvalCallback;


@end
