//
//  RootViewController.h
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "SonyCameraDevicePlugin.h"

/*!
 @brief Sony Camera デバイスプラグインの設定画面を表示するためのViewController。
 */
@interface SonyCameraViewController : UIViewController <UIPageViewControllerDelegate>

/*!
 @brief 各ページをコントロールするためのクラス。
 */
@property (strong, nonatomic) UIPageViewController *pageViewController;

/*!
 @brief 閉じるボタン。
 */
@property (strong, nonatomic) IBOutlet UIBarButtonItem *closeBtn;

/*!
 @brief Sony Camera デバイスプラグインのインスタンス。
 */
@property (nonatomic) SonyCameraDevicePlugin *deviceplugin;

/*!
 @brief 設定画面を閉じるボタンのアクション。
 */
- (IBAction)closeBtnDidPushed:(id)sender;

@end
