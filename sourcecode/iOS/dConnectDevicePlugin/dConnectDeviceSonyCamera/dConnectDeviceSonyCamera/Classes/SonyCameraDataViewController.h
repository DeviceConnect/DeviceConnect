//
//  SonyCameraDataViewController.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>

@class SonyCameraDevicePlugin;

/*!
 各ページの親クラス。
 */
@interface SonyCameraDataViewController : UIViewController

/*!
 @brief メインのView。
 */
@property (strong, nonatomic) IBOutlet UIView *mainView;

/*!
 @brief ViewControllerのページ数。
 */
@property (nonatomic) NSUInteger objectIndex;

/*!
 @brief Sony Camera デバイスプラグインのインスタンス。
 */
@property (nonatomic) SonyCameraDevicePlugin *deviceplugin;

@end
