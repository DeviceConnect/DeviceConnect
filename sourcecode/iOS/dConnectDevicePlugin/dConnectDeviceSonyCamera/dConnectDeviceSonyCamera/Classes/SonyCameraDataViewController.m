//
//  SonyCameraDataViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "SonyCameraDataViewController.h"

@interface SonyCameraDataViewController ()
@end

@implementation SonyCameraDataViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.

    UIScrollView *sv = (UIScrollView *)self.mainView;
    sv.contentInset = UIEdgeInsetsMake(64, 0.0, 0.0, 0);
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
