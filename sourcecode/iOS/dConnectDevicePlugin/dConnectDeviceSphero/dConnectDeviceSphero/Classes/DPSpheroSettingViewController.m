//
//  DPSpheroSettingViewController.m
//  dConnectDeviceSphero
//
//  Created by Takashi Tsuchiya on 2014/09/11.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPSpheroSettingViewController.h"

@interface DPSpheroSettingViewController () <UIPageViewControllerDataSource>{
    NSArray *_pages;
}

@end

@implementation DPSpheroSettingViewController

// Viewロード時
- (void)viewDidLoad
{

    // 背景白
    self.view.backgroundColor = [UIColor whiteColor];
    // 閉じるボタン追加
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithTitle:@"＜CLOSE" style:UIBarButtonItemStylePlain target:self action:@selector(closeButtonAction:) ];
    self.navigationItem.leftBarButtonItem.tintColor = [UIColor whiteColor];
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceSphero_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    NSString *settingsTitle = [bundle localizedStringForKey:@"SpheroSettingsTitle" value:@"Settings" table:@"Localizable"];
    UILabel *title = [[UILabel alloc] initWithFrame:CGRectZero];
    title.font = [UIFont boldSystemFontOfSize:16.0];
    title.textColor = [UIColor whiteColor];
    title.text = settingsTitle;
    [title sizeToFit];
    self.navigationItem.titleView = title;
    self.navigationController.navigationBar.barTintColor = [UIColor colorWithRed:0.00 green:0.63 blue:0.91 alpha:1.0]; ;  // バー背景色

    // 下のドットの色
    UIPageControl *pageControl = [UIPageControl appearanceWhenContainedIn:[self class], nil];
    pageControl.pageIndicatorTintColor = [UIColor grayColor];
    pageControl.currentPageIndicatorTintColor = [UIColor blackColor];
    pageControl.backgroundColor = [UIColor whiteColor];

    // ページ準備
//    _pages = @[@"PowerGuide", @"ConnectionGuide", @"ActivateGuide"];
    _pages = @[@"PowerGuide", @"ConnectionGuide"];
    UIViewController *startingViewController = [self viewControllerAtIndex:0 storyboard:self.storyboard];
    NSArray *viewControllers = @[startingViewController];
    [self setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    self.dataSource = self;
}

// indexに対応するViewControllerを取得
- (UIViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard
{
    // ページ数チェック
    if (([_pages count] == 0) || (index >= [_pages count])) {
        return nil;
    }
    // ViewControllerを生成
    UIViewController *dataViewController = [storyboard instantiateViewControllerWithIdentifier:_pages[index]];
    // Viewのtabにindexを保持
    dataViewController.view.tag = index;
    return dataViewController;
}

// ViewControllerに対するindexを取得
- (NSUInteger)indexOfViewController:(UIViewController *)viewController
{
    // Viewのtabにindexが保持されている
    return viewController.view.tag;
}

// 閉じるボタンイベント
- (void)closeButtonAction:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}


#pragma mark - Page View Controller Data Source

// 前のページ
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSUInteger index = [self indexOfViewController:viewController];
    if ((index == 0) || (index == NSNotFound)) {
        return nil;
    }
    
    index--;
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

// 次のページ
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    NSUInteger index = [self indexOfViewController:viewController];
    if (index == NSNotFound) {
        return nil;
    }
    
    index++;
    if (index == [_pages count]) {
        return nil;
    }
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

// 最大ページ数
- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController
{
    return _pages.count;
}

// 現在のページ数
- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    return [self indexOfViewController:pageViewController];
}

@end
