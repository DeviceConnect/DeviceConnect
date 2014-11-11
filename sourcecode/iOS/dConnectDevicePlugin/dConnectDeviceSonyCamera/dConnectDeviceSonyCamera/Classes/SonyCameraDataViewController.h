//
//  SonyCameraDataViewController.h
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
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
