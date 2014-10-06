//
//  SonyCameraModelController.m
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import "SonyCameraModelController.h"

#import "SonyCameraDataViewController.h"

/*
 A controller object that manages a simple model -- a collection of month names.
 
 The controller serves as the data source for the page view controller; it therefore implements pageViewController:viewControllerBeforeViewController: and pageViewController:viewControllerAfterViewController:.
 It also implements a custom method, viewControllerAtIndex: which is useful in the implementation of the data source methods, and in the initial configuration of the application.
 
 There is no need to actually create view controllers for each page in advance -- indeed doing so incurs unnecessary overhead. Given the data model, these methods create, configure, and return a new view controller on demand.
 */

@interface SonyCameraModelController()
@property (readonly, strong, nonatomic) NSArray *pageData;
@end

@implementation SonyCameraModelController

- (id)init
{
    self = [super init];
    if (self) {
        // Create the data model.
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        _pageData = [[dateFormatter monthSymbols] copy];
    }
    return self;
}

- (SonyCameraDataViewController *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard
{
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        // Return the data view controller for the given index.
        if (([self.pageData count] == 0) || (index >= 4)) {
            return nil;
        }
    } else {
        // Return the data view controller for the given index.
        if (([self.pageData count] == 0) || (index >= 3)) {
            return nil;
        }
    }
    
    NSString *viewId = [NSString stringWithFormat:@"SonyCameraSettingView0%dController", (index + 1)];
    
    // Create a new view controller and pass suitable data.
    SonyCameraDataViewController *SonyCameraDataViewController = [storyboard instantiateViewControllerWithIdentifier:viewId];
    SonyCameraDataViewController.objectIndex = index;
    SonyCameraDataViewController.deviceplugin = self.deviceplugin;
    return SonyCameraDataViewController;
}

- (NSUInteger)indexOfViewController:(SonyCameraDataViewController *)viewController
{   
     // Return the index of the given data view controller.
     // For simplicity, this implementation uses a static array of model objects and the view controller stores the model object; you can therefore use the model object to identify the index.
    return viewController.objectIndex;
}

#pragma mark - Page View Controller Data Source

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    NSUInteger index = [self indexOfViewController:(SonyCameraDataViewController *)viewController];
    if (index == 0) {
        return nil;
    }
    
    index--;
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    NSUInteger index = [self indexOfViewController:(SonyCameraDataViewController *)viewController];
    
    index++;
    return [self viewControllerAtIndex:index storyboard:viewController.storyboard];
}

- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController
{
    return 4;
}

- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    return 0;
}

@end
