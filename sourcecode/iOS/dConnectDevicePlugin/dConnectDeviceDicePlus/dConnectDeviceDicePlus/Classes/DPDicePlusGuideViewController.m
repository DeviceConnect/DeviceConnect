//
//  DPDicePlusGuideViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusGuideViewController.h"
@interface DPDicePlusGuideViewController ()
#pragma mark - iPhone's Constraint
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bluetoothOnDescViewBottomConstraint;
#pragma mark - iPad's Constraint
//共通
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideImageLeftConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *descBottomConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideDescHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideDescWidthConstraint;
//2ページのみ
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideCautionWidthConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideCautionHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *guideCautionBottomConstraint;



@end
@implementation DPDicePlusGuideViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

// View表示時
- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
    
    [self rotateOrientation:[[UIApplication sharedApplication] statusBarOrientation]];
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
    CGRect r = [[UIScreen mainScreen] bounds];
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
            toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
            if (r.size.height == IPHONE4_H) {
            } else {
                _bluetoothOnDescViewBottomConstraint.constant = 150;
            }
        } else {
            if (r.size.height == IPHONE4_H) {
            } else {
                _bluetoothOnDescViewBottomConstraint.constant = 120;
            }

        }
    } else {
        if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
            toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
            _guideImageLeftConstraint.constant = 170;
            _guideDescWidthConstraint.constant = 600;
            _guideDescHeightConstraint.constant = 178;
            _guideCautionWidthConstraint.constant = 600;
            _guideCautionHeightConstraint.constant = 178;
            _guideCautionBottomConstraint.constant = 138;
            _descBottomConstraint.constant = 212;
        } else {
            _guideImageLeftConstraint.constant = 85;
            _guideDescWidthConstraint.constant = 400;
            _guideDescHeightConstraint.constant = 300;
            _guideCautionWidthConstraint.constant = 400;
            _guideCautionHeightConstraint.constant = 300;
            _guideCautionBottomConstraint.constant = 69;
            
        }
    }
}

@end
