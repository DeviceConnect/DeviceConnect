//
//  DPIRKitModelController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPIRKitModelController.h"
#import "DPIRKitPageViewController.h"

@interface DPIRKitModelController()
{
    NSArray *_pages;
}

@property (nonatomic, weak) UIViewController *root;

@end

@implementation DPIRKitModelController

- (id) initWithRootViewController:(UIViewController *)root {
    self = [super init];
    
    if (self) {
        _pages = @[@"PowerGuide", @"WiFi", @"WiFiSelectionGuide", @"ConnectionGuide"];
        _root = root;
    }
    
    return self;
}

- (UIViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard
{
    
    if (_pages.count == 0 || index >= _pages.count) {
        return nil;
    }

    DPIRKitPageViewController *controller = [storyboard instantiateViewControllerWithIdentifier:[_pages objectAtIndex:index]];
    controller.index = index;
    controller.root = _root;
    
    return controller;
}

- (NSUInteger)indexOfViewController:(UIViewController *)viewController
{
    
    if ([viewController isKindOfClass:[DPIRKitPageViewController class]]) {
        return ((DPIRKitPageViewController *) viewController).index;
    }
    
    return NSNotFound;
}

#pragma mark - UIPageViewControllerDataSource

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    
    NSUInteger index = [self indexOfViewController:viewController];
    
    if (index == 0 || index == NSNotFound) {
        return nil;
    }
    
    index--;
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    
    NSUInteger index = [self indexOfViewController:viewController];
    if (index == NSNotFound) {
        return nil;
    }
    
    index++;
    if (index == _pages.count) {
        return nil;
    }
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController
{
    return _pages.count;
}

- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    return 0;
}

@end
