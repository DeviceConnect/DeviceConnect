//
//  LocalOAuthAccessTokenListViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthAccessTokenListViewController.h"
#import "LocalOAuth2Main.h"
#import "LocalOAuthSQLiteToken.h"
#import "LocalOAuthScope.h"
#import "LocalOAuthAccessTokenDetailViewController.h"
#import "LocalOAuthScopeUtil.h"
#import <QuartzCore/QuartzCore.h>

@implementation LocalOAuthAccessTokenListCell

@end


@interface LocalOAuthAccessTokenListViewController () {
    
    /*! key */
    NSString *_key;
    
    /*! LocalOAuthインスタンス */
    LocalOAuth2Main *_oauth;
    
    /*! 全アクセストークンデータ(SQLiteToken)の配列 / null: アクセストークンなし */
    NSMutableArray *_accessTokens;
    
    /*!
     アクセストークン全削除ボタン押下時の確認AlertViewポインタ
     */
    UIAlertView *_accessTokenAllDeleteAlertView;
    
    /*!
     アクセストークン削除ボタン押下時の確認AlertViewポインタ
     */
    UIAlertView *_accessTokenDeleteAlertView;
    
    /*!
     アクセストークン削除ボタン押下時の対象データインデックス
     */
    NSUInteger _accessTokenDeleteAlertViewDataIndex;
}

@property (weak, nonatomic) IBOutlet UIBarButtonItem *allRemoveBtn;

@property (weak, nonatomic) IBOutlet UIView *noTokenView;

/*!
 トークンを1件削除
 @param index トークンデータのiti
 */
- (void)deleteToken: (NSUInteger) index;

/**
 * アクセストークンを全て削除.
 */
- (void)deleteAllToken;

@end


@implementation LocalOAuthAccessTokenListViewController

- (void)setKey: (NSString *)key {
    _key = key;
}

#pragma mark - UIViewController Override

- (void)viewWillLayoutSubviews
{
    [super viewWillLayoutSubviews];
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        self.navigationController.view.layer.cornerRadius = 10;
        self.navigationController.view.superview.backgroundColor = [UIColor clearColor];
        self.view.superview.layer.cornerRadius = 10;
        self.view.superview.backgroundColor = [UIColor clearColor];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    /* アクセストークンのインスタンスを取得 */
    _oauth = [LocalOAuth2Main sharedOAuthForKey: _key];
    
    /* アクセストークン一覧を読み込む */
    _accessTokens = [[_oauth allAccessTokens] mutableCopy];
    
    /* TableView準備 */
    _tableAccessTokenList.delegate = self;
    _tableAccessTokenList.dataSource = self;
}

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"TokenDetail"]) {
        LocalOAuthAccessTokenDetailViewController *controller =
        [segue destinationViewController];
        [controller setToken:(LocalOAuthSQLiteToken *)sender];
    }
}

/*!
 全削除ボタンが押されたときの処理
 */
- (IBAction)deleteAllAccessToken:(id)sender {
    
    NSBundle *b = DCBundle();
    
    _accessTokenAllDeleteAlertView =
    [[UIAlertView alloc] initWithTitle:DCLocalizedString(b, @"alert_title_all_delete")
                               message:DCLocalizedString(b, @"alert_message_all_delete")
                              delegate:self
                     cancelButtonTitle:DCLocalizedString(b, @"alert_btn_cancel")
                     otherButtonTitles:DCLocalizedString(b, @"alert_btn_delete"), nil];
    [_accessTokenAllDeleteAlertView show];
}

/*!
 閉じるボタンが押されたときの処理
 */
- (IBAction)closeViewController:(id)sender {
    // 閉じたあとにテーブルビューの処理が発生してクラッシュするのを防ぐため
    // デリゲートとデータソースを消しておく
    _tableAccessTokenList.delegate = nil;
    _tableAccessTokenList.dataSource = nil;
    _tableAccessTokenList.editing = NO;
    [self dismissViewControllerAnimated:YES completion:nil];
}

#pragma mark - UITableViewDelegate

/**
 * テーブルの行数
 */
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    NSInteger rowCount = 0;
    if (_accessTokens != nil) {
        rowCount = [_accessTokens count];
    }
    
    if (rowCount <= 0) {
        tableView.hidden = YES;
        _noTokenView.hidden = NO;
        _allRemoveBtn.enabled = NO;
    } else {
        _noTokenView.hidden = YES;
        _allRemoveBtn.enabled = YES;
    }
    
    return rowCount;
}

/**
 * 行に表示するデータ
 */
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    LocalOAuthToken *token = nil;
    LocalOAuthSQLiteToken *sqliteToken = nil;
    if (_accessTokens != nil
        && indexPath.row < [_accessTokens count]) {
        token = [_accessTokens objectAtIndex: indexPath.row];
        if (token != nil) {
            sqliteToken = token.delegate;
        }
    }
    
    NSString *applicationName = @"";
    NSString *scopes = @"";
    
    if (sqliteToken != nil) {
        applicationName = [sqliteToken applicationName];
        NSArray *s = [sqliteToken scope];
        if (s != nil) {
            int sc = [s count];
            if (sc == 1) {
                LocalOAuthScope *ss = [s
                                       objectAtIndex: 0];
                scopes = [LocalOAuthScopeUtil displayScope: [ss scope] devicePlugin: nil];
            } else if (sc >= 2) {
                
                NSBundle *b = DCBundle();
                LocalOAuthScope *ss = [s objectAtIndex: 0];
                scopes =
                [NSString stringWithFormat:DCLocalizedString(b, @"token_list_scopes"),
                 [LocalOAuthScopeUtil displayScope: [ss scope] devicePlugin: nil], (sc - 1)];
            }
        }
    }
    
    /* セルに値を設定する */
    static NSString *CellIdentifier = @"tableCell";
    
    LocalOAuthAccessTokenListCell *cell =
    (LocalOAuthAccessTokenListCell *) [tableView dequeueReusableCellWithIdentifier:CellIdentifier forIndexPath:indexPath];
    
    // セルの値を設定
    cell.tableCellApplicationName.text = applicationName;
    cell.tableCellScopes.text = scopes;
    
    return cell;
}

/*!
 セルをタップしたときの処理
 */
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    LocalOAuthToken *token = nil;
    LocalOAuthSQLiteToken *sqliteToken = nil;
    if (_accessTokens != nil && indexPath.row < [_accessTokens count]) {
        token = [_accessTokens objectAtIndex: indexPath.row];
        if (token != nil) {
            sqliteToken = token.delegate;
        }
    }
    
    if (sqliteToken != nil) {
        [tableView deselectRowAtIndexPath:indexPath animated:NO];
        [self performSegueWithIdentifier:@"TokenDetail" sender:sqliteToken];
    }
}

- (BOOL) tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}

- (void) tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
 forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        [self deleteAtIndex:indexPath];
    }
}

/*!
 アクセストークン削除ボタン押下時の処理
 */
- (void) deleteAtIndex:(NSIndexPath *) indexPath {
    
    if (0 <= indexPath.row && indexPath.row < [_accessTokens count]) {
        
        LocalOAuthToken *token = [_accessTokens objectAtIndex: indexPath.row];
        LocalOAuthSQLiteToken *sqliteToken = token.delegate;
        
        NSBundle *b = DCBundle();
        
        _accessTokenDeleteAlertViewDataIndex = indexPath.row;
        _accessTokenDeleteAlertView =
        [[UIAlertView alloc] initWithTitle:[sqliteToken applicationName]
                                   message:DCLocalizedString(b, @"alert_message_delete")
                                  delegate:self
                         cancelButtonTitle:DCLocalizedString(b, @"alert_btn_cancel")
                         otherButtonTitles:DCLocalizedString(b, @"alert_btn_delete"), nil];
        [_accessTokenDeleteAlertView show];
    }
}

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    /* アクセストークン全削除確認画面からの応答 */
    if (_accessTokenAllDeleteAlertView == alertView) {
        
        /* 確認画面で削除ボタンが押された */
        if (buttonIndex == 1) {
            
            /* 全削除処理 */
            [self deleteAllToken];
        }
        
        /* アクセストークン個別削除確認画面からの応答 */
    } else if (_accessTokenDeleteAlertView == alertView) {
        
        /* 確認画面で削除ボタンが押された＆indexが範囲内 */
        
        if (buttonIndex == 1
            && _accessTokenDeleteAlertViewDataIndex < [_accessTokens count]) {
            
            /* トークン削除 */
            [self deleteToken: _accessTokenDeleteAlertViewDataIndex];
        }
        
    }
}

/*!
 トークンを1件削除
 @param index トークンデータのiti
 */
- (void)deleteToken: (NSUInteger) index {
    
    LocalOAuthToken *token = [_accessTokens objectAtIndex: _accessTokenDeleteAlertViewDataIndex];
    LocalOAuthSQLiteToken *sqliteToken = token.delegate;
    
    /* アクセストークン削除 */
    long long tokenId = [sqliteToken id_];
    [_oauth destroyAccessTokenByTokenId: tokenId];
    
    /* 配列から1件削除 */
    [_accessTokens removeObjectAtIndex: index];
    
    /* 表示更新 */
    dispatch_async(dispatch_get_main_queue(), ^{
        [_tableAccessTokenList reloadData];
    });
}

/**
 * アクセストークンを全て削除.
 */
- (void)deleteAllToken {
    
    /* アクセストークン削除 */
    [_oauth destroyAllAccessTokens];
    
    /* 配列から全件削除 */
    [_accessTokens removeAllObjects];
    
    /* 表示更新 */
    dispatch_async(dispatch_get_main_queue(), ^{
        [_tableAccessTokenList reloadData];
    });
}

@end
