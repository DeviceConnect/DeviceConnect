//
//  RequestViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "RequestViewController.h"
#import "ProfileViewController.h"

@interface RequestViewController ()<UITableViewDataSource, UITableViewDelegate>
{
    NSMutableArray *_profiles;
    int _selectedIndex;
}

@property (weak, nonatomic) IBOutlet UITableView *tableView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *indView;

@end

@implementation RequestViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = [_service objectForKey:DConnectNetworkServiceDiscoveryProfileParamName];
    
    _selectedIndex = -1;
    _profiles = [NSMutableArray array];
    _indView.hidden = NO;
    
    [_indView startAnimating];
    
    _tableView.delegate = self;
    _tableView.dataSource = self;
    
    DConnectRequestMessage *req = [DConnectRequestMessage new];
    req.action = DConnectMessageActionTypeGet;
    req.profile = DConnectSystemProfileName;
    req.attribute = DConnectSystemProfileAttrDevice;
    req.deviceId = [_service objectForKey:DConnectNetworkServiceDiscoveryProfileParamId];
    
    __block RequestViewController *_self = self;
    
    [[DConnectManager sharedManager] sendRequest:req callback:^(DConnectResponseMessage *response) {
        
        if (response.result == DConnectMessageResultTypeError) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [_self.indView stopAnimating];
                _self.indView.hidden = YES;
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error"
                                                                message:@"Could not get supported profiles."
                                                               delegate:nil
                                                      cancelButtonTitle:nil
                                                      otherButtonTitles:@"Close", nil];
                [alert show];
            });
        } else {
            DConnectArray *supports = [response arrayForKey:DConnectSystemProfileParamSupports];
            if (supports) {
                for (int i = 0; i < supports.count; i++) {
                    NSString *profile = [supports stringAtIndex:i];
                    [_profiles addObject:profile];
                }
                
                if (_profiles.count > 0) {
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [_self.indView stopAnimating];
                        _self.indView.hidden = YES;
                        [_self.tableView reloadData];
                    });
                }
            }
        }
    }];

}

#pragma mark - UITableViewDataSource

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _profiles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellID = @"ProfileCell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellID];
    
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:CellID];
    }
    
    NSString *profileName = [_profiles objectAtIndex:indexPath.row];
    cell.textLabel.text = profileName;
    
    return cell;
}

#pragma mark - UITableViewDelegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    _selectedIndex = indexPath.row;
    [self performSegueWithIdentifier:@"ProfileSegue" sender:self];
}

#pragma mark - UIViewController Override

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ([segue.identifier isEqualToString:@"ProfileSegue"]
        && _selectedIndex != -1)
    {
        ProfileViewController *controller = (ProfileViewController *) [segue destinationViewController];
        controller.deviceId = [_service objectForKey:DConnectNetworkServiceDiscoveryProfileParamId];
        controller.profile = [_profiles objectAtIndex:_selectedIndex];
        [_tableView deselectRowAtIndexPath:[_tableView indexPathForSelectedRow] animated:NO];
        _selectedIndex = -1;
    }
}

@end
