//
//  DPIRKitModelController.h
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface DPIRKitModelController : NSObject<UIPageViewControllerDataSource>

- (id) initWithRootViewController:(UIViewController *)root;

- (UIViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard;
- (NSUInteger)indexOfViewController:(UIViewController *)viewController;

@end
