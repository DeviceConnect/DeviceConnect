//
//  SonyCameraModelController.h
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SonyCameraDataViewController;
@class SonyCameraDevicePlugin;

@interface SonyCameraModelController : NSObject <UIPageViewControllerDataSource>

/*!
 @brief Sone Cameraデバイスプラグインのインスタンス。
 */
@property (nonatomic) SonyCameraDevicePlugin *deviceplugin;

/*!
 指定されたインデックスに対応するViewControllerを返却する。
 
 @param[in] index ページ数
 @param[in] storyboard ViewControllerが格納されたストーリーボード
 
 @retval ViewControllerのインスタンス
 @retval nil ページが存在しない場合
 */
- (SonyCameraDataViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard;

/*!
 brief 指定されたViewControllerのページ数を取得する。
 
 @param[in] viewController ページ数が知りたいViewController
 
 @return ページ数
 */
- (NSUInteger)indexOfViewController:(SonyCameraDataViewController *)viewController;

@end
