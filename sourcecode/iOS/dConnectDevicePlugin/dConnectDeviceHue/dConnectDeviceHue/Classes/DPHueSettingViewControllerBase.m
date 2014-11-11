//
//  DPHueSettingViewControllerBase.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import "DPHueSettingViewControllerBase.h"

@implementation DPHueSettingViewControllerBase

static DPHueItemBridge *mSelectedItemBridge;

- (void)viewDidLoad
{
    [super viewDidLoad];
    manager = [DPHueManager sharedManager];
    [manager initHue];
    _bundle = DPHueBundle();
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    //起動時の位置合わせ
    [self setLayoutConstraint];

}

- (void)willAnimateRotationToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
                                         duration:(NSTimeInterval)duration
{
 
    [self setLayoutConstraint];
    
}

- (void)setLayoutConstraint
{
    if ([UIApplication sharedApplication].statusBarOrientation == UIInterfaceOrientationPortrait){
        [self setLayoutConstraintPortrait];
    } else {
        [self setLayoutConstraintLandscape];
    }
}

//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
    // nop
}

//横向き座標調整
- (void)setLayoutConstraintLandscape
{
    // nop
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];

    [manager deallocHueSDK];
}

- (void)showAleart:(NSString*)msg
{
    UIAlertView *alert = [[UIAlertView alloc]
                          initWithTitle:@"hue"
                          message:msg delegate:self
                          cancelButtonTitle:@"OK"
                          otherButtonTitles:nil];
    
    [alert show];
}

- (BOOL)isIpad
{
    return (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad);
}

- (BOOL)isIphone
{
    return (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone);
}

- (BOOL)isIpadMini
{
    if (!self.isIpad) {
        return false;
    }
    
    CGRect rect = [[UIScreen mainScreen] bounds];
    
    return ((int)rect.size.height <= 1024);
}

- (BOOL)isIphoneLong
{
    if (!self.isIphone) {
        return false;
    }
    
    CGRect rect = [[UIScreen mainScreen] bounds];

    return ((int)rect.size.height > 480);
    
}


- (void)setSelectedItemBridge:(DPHueItemBridge*)itemBridge
{
    
    mSelectedItemBridge = itemBridge.copy;
    
}


- (void)initSelectedItemBridge
{
    mSelectedItemBridge = nil;
    mSelectedItemBridge = [[DPHueItemBridge alloc] init];
}

- (DPHueItemBridge*)getSelectedItemBridge
{
    if (mSelectedItemBridge == nil) {
        [self initSelectedItemBridge];
    }
    return mSelectedItemBridge;
}

- (BOOL)isSelectedItemBridge
{
    if (mSelectedItemBridge == nil
        || mSelectedItemBridge.ipAddress.length < 7
        || mSelectedItemBridge.macAddress.length < 17) {
        return NO;
    }
    return YES;
}

- (void)showPage:(NSUInteger)jumpIndex
{
    [self.hueViewController showPage:jumpIndex];
}

//ブリッジ検索ページを開く
- (void)showBridgeListPage
{
    [self showPage:0];
}

//アプリ登録ページを開く
- (void)showAuthPage
{
    [self showPage:1];
}

//ライト検索ページを開く
- (void)showLightSearchPage
{
    [self showPage:2];
}

@end
