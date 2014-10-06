//
//  SonyCameraDataViewController.m
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import "SonyCameraDataViewController.h"

@interface SonyCameraDataViewController ()
- (void) calcBarHeight;
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
