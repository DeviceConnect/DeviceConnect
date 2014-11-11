//
//  PebbleViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import <UIKit/UIKit.h>

#import "DPPebbleDevicePlugin.h"

/*!
 @brief Pebble デバイスプラグインの設定画面を表示するためのViewController。
 */
@interface PebbleViewController : UIViewController <UIPageViewControllerDelegate>

/*!
 @brief 各ページをコントロールするためのクラス。
 */
@property (strong, nonatomic) UIPageViewController *pageViewController;


/*!
 @brief 設定画面を閉じるボタンのアクション。
 */
- (IBAction)closeBtnDidPushed:(id)sender;

@end
