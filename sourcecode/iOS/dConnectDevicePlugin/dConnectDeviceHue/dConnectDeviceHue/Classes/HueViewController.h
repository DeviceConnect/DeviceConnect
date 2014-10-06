//
//  RootViewController.h
//  pageview
//
//  Created by *** on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import <UIKit/UIKit.h>


/*!
 @brief Hue デバイスプラグインの設定画面を表示するためのViewController。
 */
@interface HueViewController : UIViewController <UIPageViewControllerDelegate>

/*!
 @brief 各ページをコントロールするためのクラス。
 */
@property (strong, nonatomic) UIPageViewController *pageViewController;

/*!
 @brief 閉じるボタン。
 */
@property (strong, nonatomic) IBOutlet UIBarButtonItem *closeBtn;

/*!
 @brief 設定画面を閉じるボタンのアクション。
 */
- (IBAction)closeBtnDidPushed:(id)sender;

//指定画面を表示
- (void)showPage:(NSUInteger)jumpIndex;

@end
