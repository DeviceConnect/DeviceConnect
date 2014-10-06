//
//  GHSettingController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHSettingController.h"
#import "GHDataManager.h"
#import <DConnectSDK/DConnectSDK.h>

@interface GHSettingController ()
@property (nonatomic, strong) NSArray* datasource;
@property (nonatomic, strong) UISwitch* cookieSW;
@end


#define CELL_ID @"setting"
#define ALERT_COOKIE  100
#define ALERT_HISTORY 101


@implementation GHSettingController
//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        self.datasource = @[@[@"Cookieの削除",
                              @"Cookie設定(ON/OFF)",
                              @"履歴の削除",
                              @"アクセストークン削除"]
                            ];
        
        self.title = @"設定";

    }
    return self;
}

- (void)dealloc
{
    self.datasource = nil;
    self.cookieSW   = nil;
}


//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //セルの登録
    [self.tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:CELL_ID];
    
    if (![GHUtils isiPad]) {
        //ナビボタンのセット
        UIBarButtonItem* close = [[UIBarButtonItem alloc]initWithTitle:@"閉じる"
                                                                 style:UIBarButtonItemStylePlain
                                                                target:self
                                                                action:@selector(close)];
        self.navigationItem.leftBarButtonItem = close;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}


- (void)close
{
    [self updateSwitchState];
    [self dismissViewControllerAnimated:YES completion:nil];
}

//--------------------------------------------------------------//
#pragma mark - CookieスイッチのON/OFF
//--------------------------------------------------------------//
- (void)updateSwitch:(UISwitch*)sender
{
    
}

///スイッチの状態を保存
- (void)updateSwitchState
{
    NSUserDefaults *def = [NSUserDefaults standardUserDefaults];
    [def setObject:@(self.cookieSW.isOn) forKey:IS_COOKIE_ACCEPT];
    [def synchronize];
    
    //Cookie許可設定
    [GHUtils setCookieAccept:self.cookieSW.isOn];
}


//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return [self.datasource count];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self.datasource objectAtIndex:section]count];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CELL_ID forIndexPath:indexPath];
    [self configureCell:cell atIndexPath:indexPath];
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 0) {
        switch (indexPath.row) {
            case 0:
                //Cookie削除
                [self showAlert:@"確認" message:@"Cookieを削除します。他のアプリで使用しているCookieも削除されます。よろしいでしょうか？" withTag:ALERT_COOKIE];
                break;
                
            case 2:
                //履歴削除
                [self showAlert:@"確認" message:@"履歴を削除します。よろしいでしょうか？" withTag:ALERT_HISTORY];
                break;
                
            case 3:
                //dConnectmanagerのアクセストークン削除
                [DConnectUtil showAccessTokenList];
                break;
                
            default:
                break;
        }
    }
}



- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 60;
}

/**
 * セルの表示内容をセット
 * @param cell 対象のセル
 * @param indexPath indexPath
 */
- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    cell.accessoryType = UITableViewCellAccessoryNone;
    cell.textLabel.text = [[self.datasource objectAtIndex:indexPath.section] objectAtIndex:indexPath.row];
    
    if (indexPath.section == 0) {
        switch (indexPath.row) {
            case 1:
            {
                //CookieのON/OFF
                if (!self.cookieSW ) {
                    self.cookieSW = [[UISwitch alloc]init];
                    [self.cookieSW addTarget:self action:@selector(updateSwitch:) forControlEvents:UIControlEventValueChanged];
                    
                    //スイッチの状態セット
                    NSUserDefaults *def = [NSUserDefaults standardUserDefaults];
                    BOOL sw = [def boolForKey:IS_COOKIE_ACCEPT];
                    [self.cookieSW setOn:sw animated:NO];
                    
                    cell.accessoryView = self.cookieSW;
                }
                
                cell.selectionStyle = UITableViewCellSelectionStyleNone;
            }
                break;
                
            case 3:
                //dConnectmanagerのアクセストークン削除
                cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
                break;
                
            default:
                break;
        }
    }
}


//--------------------------------------------------------------//
#pragma mark - UIAlertViewDelegate
//--------------------------------------------------------------//
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex != alertView.cancelButtonIndex) {
        switch (alertView.tag) {
            case ALERT_COOKIE:
                [GHUtils deleteCookie];
                [self showAlertOneBtn:@"Cookieを削除しました"];
                break;
                
            case ALERT_HISTORY:
            {
                [[GHDataManager shareManager] deleteAllHistory];
                
                __weak GHSettingController* _self = self;
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [_self showAlertOneBtn:@"履歴を削除しました"];
                });
            }
                break;
                
            default:
                break;
        }
    }
}




- (void)showAlert:(NSString*)title message:(NSString*)message withTag:(int)tag
{
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:title
                                                   message:message
                                                  delegate:self
                                         cancelButtonTitle:@"キャンセル"
                                         otherButtonTitles:@"OK", nil];
    alert.tag = tag;
    [alert show];
}

- (void)showAlertOneBtn:(NSString*)message
{
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:nil
                                                   message:message
                                                  delegate:nil
                                         cancelButtonTitle:@"OK"
                                         otherButtonTitles:nil, nil];
    [alert show];
}



@end
