//
//  SonyCameraViewController.m
//  pageview
//
//  Created by 小林伸郎 on 2014/08/07.
//  Copyright (c) 2014年 ___FULLUSERNAME___. All rights reserved.
//

#import "SonyCameraViewController.h"
#import "SonyCameraModelController.h"
#import "SonyCameraDataViewController.h"

@interface SonyCameraViewController ()
@property (readonly, strong, nonatomic) SonyCameraModelController *sonyCameraModelController;
@end

@implementation SonyCameraViewController

@synthesize sonyCameraModelController = _sonyCameraModelController;

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    // Configure the page view controller and add it as a child view controller.
    self.pageViewController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    self.pageViewController.delegate = self;
    self.SonyCameraModelController.deviceplugin = self.deviceplugin;

    SonyCameraDataViewController *startingViewController = [self.SonyCameraModelController viewControllerAtIndex:0 storyboard:self.storyboard];
    NSArray *viewControllers = @[startingViewController];
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];

    self.pageViewController.dataSource = self.SonyCameraModelController;

    [self addChildViewController:self.pageViewController];
    [self.view addSubview:self.pageViewController.view];
    
    NSArray *subviews = self.pageViewController.view.subviews;
    UIPageControl *thisControl = nil;
    for (int i=0; i<[subviews count]; i++) {
        if ([[subviews objectAtIndex:i] isKindOfClass:[UIPageControl class]]) {
            thisControl = (UIPageControl *)[subviews objectAtIndex:i];
        }
    }
    thisControl.currentPageIndicatorTintColor = [UIColor colorWithRed:0.1 green:0.5 blue:1.0 alpha:1.0];
    thisControl.pageIndicatorTintColor =[UIColor colorWithRed:0.7 green:0.7 blue:0.7 alpha:1.0];
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        thisControl.numberOfPages = 4;
    } else {
        thisControl.numberOfPages = 3;
    }

    // バー背景色
    self.navigationController.navigationBar.barTintColor =
    [UIColor colorWithRed:0.00 green:0.63 blue:0.91 alpha:1.0];
    
    //Title文字色指定
    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName: [UIColor whiteColor]};
    
    //Closeボタンの色設定
    self.closeBtn.tintColor = [UIColor whiteColor];
    
    [self.pageViewController didMoveToParentViewController:self];

    // Add the page view controller's gesture recognizers to the book view controller's view so that the gestures are started more easily.
    self.view.gestureRecognizers = self.pageViewController.gestureRecognizers;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (SonyCameraModelController *)SonyCameraModelController
{
     // Return the model controller object, creating it if necessary.
     // In more complex implementations, the model controller may be passed to the view controller.
    if (!_sonyCameraModelController) {
        _sonyCameraModelController = [[SonyCameraModelController alloc] init];
    }
    return _sonyCameraModelController;
}

#pragma mark - UIPageViewController delegate methods

/*
- (void)pageViewController:(UIPageViewController *)pageViewController didFinishAnimating:(BOOL)finished previousViewControllers:(NSArray *)previousViewControllers transitionCompleted:(BOOL)completed
{
    
}
 */

- (UIPageViewControllerSpineLocation)pageViewController:(UIPageViewController *)pageViewController spineLocationForInterfaceOrientation:(UIInterfaceOrientation)orientation
{
    if (UIInterfaceOrientationIsPortrait(orientation) || ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone)) {
        // In portrait orientation or on iPhone: Set the spine position to "min" and the page view controller's view controllers array to contain just one view controller. Setting the spine position to 'UIPageViewControllerSpineLocationMid' in landscape orientation sets the doubleSided property to YES, so set it to NO here.
        
        UIViewController *currentViewController = self.pageViewController.viewControllers[0];
        NSArray *viewControllers = @[currentViewController];
        [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
        
        self.pageViewController.doubleSided = NO;
        return UIPageViewControllerSpineLocationMin;
    }

    // In landscape orientation: Set set the spine location to "mid" and the page view controller's view controllers array to contain two view controllers. If the current page is even, set it to contain the current and next view controllers; if it is odd, set the array to contain the previous and current view controllers.
    SonyCameraDataViewController *currentViewController = self.pageViewController.viewControllers[0];
    NSArray *viewControllers = nil;

    NSUInteger indexOfCurrentViewController = [self.SonyCameraModelController indexOfViewController:currentViewController];
    if (indexOfCurrentViewController == 0 || indexOfCurrentViewController % 2 == 0) {
        UIViewController *nextViewController = [self.SonyCameraModelController pageViewController:self.pageViewController viewControllerAfterViewController:currentViewController];
        viewControllers = @[currentViewController, nextViewController];
    } else {
        UIViewController *previousViewController = [self.SonyCameraModelController pageViewController:self.pageViewController viewControllerBeforeViewController:currentViewController];
        viewControllers = @[previousViewController, currentViewController];
    }
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];

    return UIPageViewControllerSpineLocationMid;
}

#pragma mark - action methods

- (IBAction)closeBtnDidPushed:(id)sender {
    // 完了ボタンを押したときに閉じるように設定
    [self dismissViewControllerAnimated:YES completion:nil];
}

@end
