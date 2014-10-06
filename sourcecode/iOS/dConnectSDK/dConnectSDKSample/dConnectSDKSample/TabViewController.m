//
//  TabViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/28.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TabViewController.h"

@interface TabViewController ()

@end

@implementation TabViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    ((UITabBarItem *)self.tabBar.items[0]).title = @"デバッガ";
    ((UITabBarItem *)self.tabBar.items[1]).title = @"ブラウザ";
}

@end
