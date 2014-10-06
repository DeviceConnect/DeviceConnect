//
//  DPIRKitPageViewController.h
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DPIRKitPageViewController : UIViewController

@property (nonatomic) NSUInteger index;
@property (nonatomic, weak) UIViewController *root;

- (void) setScrollEnable:(BOOL)enable;
- (void) setScrollEnable:(BOOL)enable closeBtn:(BOOL)closeEnable;

@end
