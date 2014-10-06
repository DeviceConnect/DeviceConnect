//
//  DPGuideViewController.m
//  dConnectDeviceSphero
//
//  Created by Takashi Tsuchiya on 2014/09/12.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPGuideViewController.h"
#import "DPSpheroManager.h"

@interface DPGuideViewController () {
    NSLayoutConstraint *_space;
}
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *verticalSpace;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *horizontalSpace;
@property (weak, nonatomic) IBOutlet UISwitch *activateSwitch;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topSpace;
- (IBAction)switchValueChanged:(id)sender;

@end

@implementation DPGuideViewController

// View読み込み中
- (void)viewDidLoad
{
    _space = _verticalSpace;
}

// View表示時
- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
    [self rotateOrientation:[[UIApplication sharedApplication] statusBarOrientation]];
    _activateSwitch.on = [[DPSpheroManager sharedManager] isActivated];
}

// View回転時
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    [super willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
    [self rotateOrientation:toInterfaceOrientation];
}

// 位置合わせ
- (void)rotateOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
    if (!_horizontalSpace) return;
    
	if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
        toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
        // 上下のスペーサー復活
        if (![[self.view constraints] containsObject:_space]) {
            [self.view addConstraint:_space];
        }
        // 下の位置
		_horizontalSpace.constant = 239;
	} else {
        // 上下のスペーサー除去
        [self.view removeConstraint:_space];
        // 右の位置
		_horizontalSpace.constant = -10;
	}
    // Topからの位置
    _topSpace.constant = self.navigationController.navigationBar.frame.size.height +31;
}

// スイッチ操作
- (IBAction)switchValueChanged:(id)sender
{
    if (_activateSwitch.on) {
        // 有効化
        if ([[DPSpheroManager sharedManager] activate]) {
            // 成功
            [self showAlertWithTitleKey:@"SpheroConnectMessage" messageKey:@"SpheroConnectedMessage"];

        } else {
            // 失敗
            _activateSwitch.on = NO;
            [self showAlertWithTitleKey:@"SpheroDisconnecMessage" messageKey:@"SpheroFailMessage"];

        }
    } else {
        // 無効化
        [[DPSpheroManager sharedManager] deactivate];
        [self showAlertWithTitleKey:@"SpheroDisconnecMessage" messageKey:@"SpheroDisconnectedMessage"];
    }
}

// メッセージ表示
- (void)showAlertWithTitleKey:(NSString*)titleKey messageKey:(NSString*)messageKey
{
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceSphero_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    NSString* disconnectTitle = [bundle localizedStringForKey:titleKey value:nil table:nil];
    NSString* failMessage = [bundle localizedStringForKey:messageKey value:nil table:nil];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:disconnectTitle
                                                    message:failMessage
                                                   delegate:nil
                                          cancelButtonTitle:nil
                                          otherButtonTitles:@"OK", nil];
    [alert show];
    
}

@end
