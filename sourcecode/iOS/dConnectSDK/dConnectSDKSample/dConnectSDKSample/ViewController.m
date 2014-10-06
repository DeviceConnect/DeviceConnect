//
//  ViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "ViewController.h"
#import <DConnectSDK/DConnectSDK.h>
#import "RequestViewController.h"
#import "Utils.h"

@interface ViewController ()<UITableViewDataSource, UITableViewDelegate>
{
    int _selectedIndex;
    int _searchedType;
    
}

@property (nonatomic, strong) NSMutableArray *devices;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *indView;
@property (weak, nonatomic) IBOutlet UISegmentedControl *searchType;

- (void) showNoResultAlert;
- (void) showErrorAlertWithResponse:(DConnectResponseMessage *)response;

@end

@implementation ViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _devices = [NSMutableArray array];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _indView.hidden = YES;
    _selectedIndex = -1;
    
    __weak typeof(self) _self = self;
    
    [Utils authorizeOrRefreshTokenWithForceRefresh:NO
                                           success:
     ^(NSString *clientId, NSString *clientSecret, NSString *accessToken) {
     } error:
     ^(DConnectMessageErrorCodeType errorCode) {
         dispatch_async(dispatch_get_main_queue(), ^{
             int code = errorCode;
             
             if (code == DConnectMessageErrorCodeTimeout) {
                 [_self dismissViewControllerAnimated:YES completion:nil];
             }
             
             NSString *message = @"認証に失敗しました";
             
             UIAlertView *alert = [[UIAlertView alloc]
                                   initWithTitle:@"エラーが発生しました"
                                   message:[NSString stringWithFormat:@"#%d : %@", code, message]
                                   delegate:nil
                                   cancelButtonTitle:nil
                                   otherButtonTitles:@"閉じる", nil];
             [alert show];
         });
         
     }];
}

- (void) viewDidAppear:(BOOL)animated {
}

- (void) showErrorAlertWithResponse:(DConnectResponseMessage *)response {
    
    dispatch_async(dispatch_get_main_queue(), ^{
        int code = [response integerForKey:DConnectMessageErrorCode];
        NSString *message = [response stringForKey:DConnectMessageErrorMessage];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"エラーが発生しました"
                                                        message:[NSString stringWithFormat:@"#%d : %@", code, message]
                                                       delegate:nil
                                              cancelButtonTitle:nil
                                              otherButtonTitles:@"閉じる", nil];
        [alert show];
    });
}


- (void) showNoResultAlert {
    
    dispatch_async(dispatch_get_main_queue(), ^{
        
        [_devices removeAllObjects];
        [_tableView reloadData];
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"検索結果 0件"
                                                        message:@"検索結果がありません。"
                                                       delegate:nil
                                              cancelButtonTitle:nil
                                              otherButtonTitles:@"閉じる", nil];
        [alert show];
    });
}

- (IBAction)toggled:(id)sender {
    [self searchBtnDidPushed:sender];
}

- (IBAction)searchBtnDidPushed:(id)sender {
    
    _indView.hidden = NO;
    [_indView startAnimating];
    
    DConnectRequestMessage *req = [DConnectRequestMessage message];
    
    _searchedType = _searchType.selectedSegmentIndex;
    
    if (_searchedType == 0) {
        req.action = DConnectMessageActionTypeGet;
        req.profile = DConnectNetworkServiceDiscoveryProfileName;
        req.attribute = DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices;
    } else {
        req.action = DConnectMessageActionTypeGet;
        req.profile = DConnectSystemProfileName;
    }
    
    __block ViewController *_self = self;
    
    [[DConnectManager sharedManager] sendRequest:req callback:^(DConnectResponseMessage *response) {
        
        if (response.result == DConnectMessageResultTypeError) {
            [_self showErrorAlertWithResponse:response];
            return;
        }
        
        if (_searchedType == 0) {
            DConnectArray *services = [response arrayForKey:DConnectNetworkServiceDiscoveryProfileParamServices];
            if (services && services.count > 0) {
                [_self.devices removeAllObjects];
                for (int i = 0; i < services.count; i++) {
                    DConnectMessage *service = [services messageAtIndex:i];
                    if (service
                        && [service hasKey:DConnectNetworkServiceDiscoveryProfileParamId]
                        && [service hasKey:DConnectNetworkServiceDiscoveryProfileParamName])
                    {
                        [_self.devices addObject:service];
                    }
                }
            } else {
                [_self showNoResultAlert];
            }
        } else {
            DConnectArray *plugins = [response arrayForKey:DConnectSystemProfileParamPlugins];
            if (plugins && plugins.count > 0) {
                [_self.devices removeAllObjects];
                for (int i = 0; i < plugins.count; i++) {
                    DConnectMessage *plugin = [plugins messageAtIndex:i];
                    if (plugin
                        && [plugin hasKey:DConnectSystemProfileParamId]
                        && [plugin hasKey:DConnectSystemProfileParamName])
                    {
                        [_self.devices addObject:plugin];
                    }
                }
            } else {
                [_self showNoResultAlert];
            }
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [_self.indView stopAnimating];
            _self.indView.hidden = YES;
            [_self.tableView reloadData];
        });
    }];
    
}



#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _devices.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellID = @"DPCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellID];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CellID];
    }
    
    if (_searchedType == 0) {
        DConnectMessage *service = [_devices objectAtIndex:indexPath.row];
        if (service) {
            cell.textLabel.text = [service stringForKey:DConnectNetworkServiceDiscoveryProfileParamName];
        }
    } else {
        DConnectMessage *plugin = [_devices objectAtIndex:indexPath.row];
        if (plugin) {
            cell.textLabel.text = [plugin stringForKey:DConnectSystemProfileParamName];
        }
    }
    
    return cell;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    if (_searchedType == 0) {
        _selectedIndex = indexPath.row;
        [self performSegueWithIdentifier:@"RequestSegue" sender:self];
    } else {
        
        DConnectMessage *plugin = [_devices objectAtIndex:indexPath.row];
        
        DConnectRequestMessage *req = [DConnectRequestMessage message];
        req.action = DConnectMessageActionTypePut;
        req.profile = DConnectSystemProfileName;
        req.interface = DConnectSystemProfileInterfaceDevice;
        req.attribute = DConnectSystemProfileAttrWakeUp;
        [req setString:[plugin stringForKey:DConnectSystemProfileParamId]
                forKey:DConnectSystemProfileParamPluginId];
        [tableView deselectRowAtIndexPath:indexPath animated:NO];
        
        [[DConnectManager sharedManager] sendRequest:req callback:nil];
    }
}

#pragma mark - UIViewController Override

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ([segue.identifier isEqualToString:@"RequestSegue"]
        && _selectedIndex != -1)
    {
        RequestViewController *controller = (RequestViewController *) [segue destinationViewController];
        controller.service = [_devices objectAtIndex:_selectedIndex];
        [_tableView deselectRowAtIndexPath:[_tableView indexPathForSelectedRow] animated:NO];
        _selectedIndex = -1;
        
    }
}

@end
