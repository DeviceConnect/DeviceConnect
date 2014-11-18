//
//  DPHueViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import "DPHueViewController.h"
#import "DPHueModelController.h"
#import "DPHueSettingViewControllerBase.h"
#import "DPHueConst.h"
#import "DPHueManager.h"

@interface DPHueViewController ()
@property (readonly, strong, nonatomic) DPHueModelController *hueModelController;
@end

@implementation DPHueViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.pageViewController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    self.pageViewController.delegate = self;
    
    //先頭ページ
    DPHueSettingViewControllerBase *startingViewController = [self.DPHueModelController viewControllerAtIndex:0 storyboard:self.storyboard];

    //コントローラーCollectionに先頭ページを設定
    NSArray *viewControllers = @[startingViewController];

    //pageViewControllerにコントローラーCollectionを設定（先頭ページ）
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    
    //DPHueModelControllerをpageViewControllerのdataSourceへ設定
    self.pageViewController.dataSource = self.DPHueModelController;
    
    //本画面にpageViewControllerを追加
    [self addChildViewController:self.pageViewController];
    [self.view addSubview:self.pageViewController.view];
    
    //pageViewControllerのsubviewコレクションを取得
    NSArray *subviews = self.pageViewController.view.subviews;
    
    //UIPageControlを取得
    UIPageControl *thisControl = nil;
    for (int i=0; i<[subviews count]; i++) {
        if ([[subviews objectAtIndex:i] isKindOfClass:[UIPageControl class]]) {
            thisControl = (UIPageControl *)[subviews objectAtIndex:i];
        }
    }
    
    //インジケータの色設定
    thisControl.currentPageIndicatorTintColor = [UIColor colorWithRed:0.1 green:0.5 blue:1.0 alpha:1.0];
    thisControl.pageIndicatorTintColor =[UIColor colorWithRed:0.7 green:0.7 blue:0.7 alpha:1.0];
    
    //ページ数最大を設定
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        thisControl.numberOfPages = DPHUE_SETTING_PAGE_COUNT_IPHONE;
    } else {
        thisControl.numberOfPages = DPHUE_SETTING_PAGE_COUNT_IPAD;
    }

    // バー背景色
    self.navigationController.navigationBar.barTintColor =
        [UIColor colorWithRed:0.00 green:0.63 blue:0.91 alpha:1.0];

    //Title文字色指定
    self.navigationController.navigationBar.titleTextAttributes = @{NSForegroundColorAttributeName: [UIColor whiteColor]};

    //Closeボタンの色設定
    self.closeBtn.tintColor = [UIColor whiteColor];
    
    //手動でViewControllerを追加したので追加完了を知らせる
    [self.pageViewController didMoveToParentViewController:self];
    
    //ジェスチャーを伝える
    self.view.gestureRecognizers = self.pageViewController.gestureRecognizers;
}

- (DPHueModelController *)DPHueModelController
{
    if (!_hueModelController) {
        _hueModelController = [[DPHueModelController alloc] init];
    }
    
    _hueModelController.hueViewController = self;
    
    return _hueModelController;
}

#pragma mark - UIPageViewController delegate methods

- (UIPageViewControllerSpineLocation)pageViewController:(UIPageViewController *)pageViewController
                   spineLocationForInterfaceOrientation:(UIInterfaceOrientation)orientation
{
    if (UIInterfaceOrientationIsPortrait(orientation) || ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone)) {
        
        UIViewController *currentViewController = self.pageViewController.viewControllers[0];
        NSArray *viewControllers = @[currentViewController];
        [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
        
        self.pageViewController.doubleSided = NO;
        return UIPageViewControllerSpineLocationMin;
    }
    
    DPHueSettingViewControllerBase *currentViewController = self.pageViewController.viewControllers[0];
    NSArray *viewControllers = nil;
    
    NSUInteger indexOfCurrentViewController = [self.DPHueModelController indexOfViewController:currentViewController];
    
    if (indexOfCurrentViewController == 0 || indexOfCurrentViewController % 2 == 0) {
        UIViewController *nextViewController = [self.DPHueModelController pageViewController:self.pageViewController viewControllerAfterViewController:currentViewController];
        viewControllers = @[currentViewController, nextViewController];
        
    } else {
        UIViewController *previousViewController = [self.DPHueModelController pageViewController:self.pageViewController viewControllerBeforeViewController:currentViewController];
        viewControllers = @[previousViewController, currentViewController];
    }
    
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    
    return UIPageViewControllerSpineLocationMid;
}

-(void)rotationView:(int )rotationMode
{
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceHue_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    
    UIStoryboard *sb;
  
    if(rotationMode==0){
        sb = [UIStoryboard storyboardWithName:@"dConnectDeviceHue_iPhone" bundle:bundle];
    }else{
        sb = [UIStoryboard storyboardWithName:@"dConnectDeviceHue_iPhone_Landscape" bundle:bundle];
    }
    
    UIViewController * nextViewController = (UIViewController *)[sb instantiateInitialViewController];
    
    [self presentViewController: (UIViewController *)nextViewController animated:NO completion: nil];
    
}

//指定ページへジャンプ(jumpIndex:0〜)
- (void)showPage:(NSUInteger)jumpIndex
{
    DPHueSettingViewControllerBase *viewController =
        [self.DPHueModelController
            viewControllerAtIndex:jumpIndex storyboard:self.storyboard];

    //コントローラーCollectionにページを設定
    NSArray *viewControllers = @[viewController];
    
    //コントローラーCollectionをページコントローラに設定
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    
}

//現在表示中のページ数を取得
- (NSUInteger)getSelectPageIndex
{
    
    NSUInteger pageNo = [self.hueModelController indexOfViewController:
                         [self.pageViewController.viewControllers objectAtIndex:0]];
    
    return pageNo;
    
}

#pragma mark - action methods
- (IBAction)closeBtnDidPushed:(id)sender {
    if ([self getSelectPageIndex] == 1) {
        //先頭の画面に戻る
        [self showPage:0];

    }else{
        // 完了ボタンを押したときに閉じるように設定
        [self dismissViewControllerAnimated:YES completion:nil];
        [[DPHueManager sharedManager] deallocHueSDK];
    }
}

@end
