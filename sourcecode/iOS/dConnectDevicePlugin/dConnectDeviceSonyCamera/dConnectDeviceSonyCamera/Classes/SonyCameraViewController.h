//
//  SonyCameraViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
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
