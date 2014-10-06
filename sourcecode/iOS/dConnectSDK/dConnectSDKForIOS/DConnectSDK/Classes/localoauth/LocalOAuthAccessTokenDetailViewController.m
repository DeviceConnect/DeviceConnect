//
//  LocalOAuthAccessTokenDetailViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthAccessTokenDetailViewController.h"
#import "LocalOAuthScope.h"
#import "LocalOAuthUtils.h"
#import "LocalOAuthScopeUtil.h"

@implementation LocalOAuthAccessTokenDetailViewCell

@end

@interface LocalOAuthAccessTokenDetailViewController () {
    
    /*!
        トークンデータ
     */
    LocalOAuthSQLiteToken *_sqliteToken;
    
    
    /*!
        スコープデータ
     */
    NSArray *_scopes;
    
    NSBundle *_bundle;
    
}

/*!
 スコープ名表示文字列を取得する(日本語表示できる場合は日本語名に変換して返す).
 @param scope[in] スコープ名
 @return スコープ名(日本語表示できる場合は日本語名に変換して返す)
 */
- (NSString *) displayScope: (NSString *)scope;

@end


@implementation LocalOAuthAccessTokenDetailViewController

- (void) setToken: (LocalOAuthSQLiteToken *)sqliteToken {
    _sqliteToken = sqliteToken;
    if (_sqliteToken != nil) {
        _scopes = [_sqliteToken scope];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = [_sqliteToken applicationName];
    
    _scopeTableView.delegate = self;
    _scopeTableView.dataSource = self;
    _scopeTableView.allowsSelection = NO;
    
    _bundle = DCBundle();
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    NSInteger count = 0;
    if (_scopes != nil) {
        count = [_scopes count];
    }
    return count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    NSString *scope = @"";
    NSString *expirePeriod = @"";
    
    if (_scopes != nil) {
        
        LocalOAuthScope *s = [_scopes objectAtIndex: indexPath.row];
        
        /* スコープ名 */
        scope = [self displayScope: [s scope]];
        
        /* 有効期限 */
        expirePeriod = [NSString stringWithFormat:DCLocalizedString(_bundle, @"token_expiration_date"),
                        [s getStrExpirePeriod]];
    }
    
    /* セルに値を設定する */
    static NSString *cellIdentifier = @"scopeCell";
    LocalOAuthAccessTokenDetailViewCell *cell =
    (LocalOAuthAccessTokenDetailViewCell *)[tableView dequeueReusableCellWithIdentifier: cellIdentifier
                                                                     forIndexPath: indexPath];
    
    // セルの値を設定
    cell.scopeLabel.text = scope;
    cell.expirePeriodLabel.text = expirePeriod;
    
    return cell;
}

- (NSString *) displayScope: (NSString *)scope {
    
    NSString *displayScope_ = [LocalOAuthScopeUtil displayScope: scope devicePlugin: nil];
    return displayScope_;
}

@end
