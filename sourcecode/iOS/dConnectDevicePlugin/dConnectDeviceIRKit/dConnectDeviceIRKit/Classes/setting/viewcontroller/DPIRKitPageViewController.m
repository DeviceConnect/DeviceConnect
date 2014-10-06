//
//  DPIRKitPageViewController.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitPageViewController.h"
#import "DPIRKitConst.h"

@interface DPIRKitPageViewController (){
    NSLayoutConstraint *_space;
}
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *leadingAlignment;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *verticalSpace;

@end

@implementation DPIRKitPageViewController

- (id)init {
    
    self = [super init];
    
    if (self) {
        _index = NSNotFound;
    }
    
    return self;
}

- (id) initWithCoder:(NSCoder *)aDecoder {
    
    self = [super initWithCoder:aDecoder];
    
    if (self) {
        _index = NSNotFound;
    }
    
    return self;
}

- (id) initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    
    if (self) {
        _index = NSNotFound;
    }
    
    return self;
}

// View読み込み
- (void)viewDidLoad
{
    _space = _verticalSpace;
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
    if (!_leadingAlignment) return;
    
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
            toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown)
        {
            // 上下のスペーサー復活
            if (![[self.view constraints] containsObject:_space]) {
                [self.view addConstraint:_space];
            }
            // 下の位置
            _leadingAlignment.constant = 0;
        } else {
            // 上下のスペーサー除去
            [self.view removeConstraint:_space];
            // 右の位置
            _leadingAlignment.constant = 270;
        }
        [self.view setNeedsUpdateConstraints];
    }
}

- (void) setScrollEnable:(BOOL)enable {
    [self setScrollEnable:enable closeBtn:enable];
}

- (void) setScrollEnable:(BOOL)enable closeBtn:(BOOL)closeEnable {
    self.root.view.userInteractionEnabled = enable;
    self.root.navigationItem.leftBarButtonItem.enabled = closeEnable;
}

@end
