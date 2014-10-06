//
//  ResultViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "ResultViewController.h"

@interface ResultViewController ()
@property (weak, nonatomic) IBOutlet UITextView *resultView;

@end

@implementation ResultViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.edgesForExtendedLayout = UIRectEdgeNone;
    _resultView.text = _json;
}

@end
