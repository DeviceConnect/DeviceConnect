//
//  SettingViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/09/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "SettingViewController.h"
#import "Utils.h"

@interface SettingViewController ()

- (void) refreshAccessToken;

@end

@implementation SettingViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.clearsSelectionOnViewWillAppear = NO;
}

- (void) refreshAccessToken {
    
    __weak typeof(self) _self = self;

    [Utils authorizeOrRefreshTokenWithForceRefresh:YES success:
     ^(NSString *clientId, NSString *clientSecret, NSString *accessToken) {
         
     } error:^(DConnectMessageErrorCodeType errorCode) {
         dispatch_async(dispatch_get_main_queue(), ^{
             int code = errorCode;
             
             if (code == DConnectMessageErrorCodeTimeout) {
                 [_self dismissViewControllerAnimated:YES completion:nil];
             }
             
             NSString *message = @"認証に失敗しました";
             
             UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"エラーが発生しました"
                                                             message:[NSString stringWithFormat:@"#%d : %@", code, message]
                                                            delegate:nil
                                                   cancelButtonTitle:nil
                                                   otherButtonTitles:@"閉じる", nil];
             [alert show];
         });
         
     }];

}

#pragma mark - Table view data source

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
    switch (indexPath.section) {
        case 0:
        {
            
            if (indexPath.row == 0) {
                [self refreshAccessToken];
            } else {
                [DConnectUtil showAccessTokenList];
            }
            
        }
            break;
            
        default:
            break;
    }
    
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
    
}

@end
