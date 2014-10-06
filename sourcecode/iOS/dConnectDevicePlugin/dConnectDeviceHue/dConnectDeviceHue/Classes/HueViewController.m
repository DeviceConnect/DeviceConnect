#import "HueViewController.h"
#import "HueModelController.h"
#import "HueSettingViewControllerBase.h"
#import "Hue_device_plugin_defines.h"
#import "DCLogger.h"

@interface HueViewController ()
@property (readonly, strong, nonatomic) HueModelController *hueModelController;
@end

//======================================================================
@implementation HueViewController

DCLogger *mlog;

@synthesize hueModelController = _hueModelController;

//======================================================================
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    mlog = [[DCLogger alloc]initWithSourceClass:self];

    [mlog entering:@"viewDidLoad" param:nil];

    //pageViewController生成
    self.pageViewController = [[UIPageViewController alloc] initWithTransitionStyle:UIPageViewControllerTransitionStyleScroll navigationOrientation:UIPageViewControllerNavigationOrientationHorizontal options:nil];
    self.pageViewController.delegate = self;
    
    //先頭ページ
    HueSettingViewControllerBase *startingViewController = [self.HueModelController viewControllerAtIndex:0 storyboard:self.storyboard];

    //コントローラーCollectionに先頭ページを設定
    NSArray *viewControllers = @[startingViewController];

    //pageViewControllerにコントローラーCollectionを設定（先頭ページ）
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:NO completion:nil];
    
    //HueModelControllerをpageViewControllerのdataSourceへ設定
    self.pageViewController.dataSource = self.HueModelController;
    
    //本画面にpageViewControllerを追加
    [self addChildViewController:self.pageViewController];
    [self.view addSubview:self.pageViewController.view];
    

    //TODO:なんで２回やってるの？
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
        thisControl.numberOfPages = SETTING_PAGE_COUNT_IPHNE;
    } else {
        thisControl.numberOfPages = SETTING_PAGE_COUNT_IPAD;
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

//======================================================================
- (HueModelController *)HueModelController
{
    
    [mlog entering:@"HueModelController" param:nil];

    if (!_hueModelController) {
        _hueModelController = [[HueModelController alloc] init];
    }
    
    _hueModelController.hueViewController = self;
    
    return _hueModelController;
}

//======================================================================
#pragma mark - UIPageViewController delegate methods
//======================================================================
- (UIPageViewControllerSpineLocation)pageViewController:(UIPageViewController *)pageViewController spineLocationForInterfaceOrientation:(UIInterfaceOrientation)orientation
{
    
    [mlog entering:@"pageViewController" param:nil];

    if (UIInterfaceOrientationIsPortrait(orientation) || ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone)) {
        
        UIViewController *currentViewController = self.pageViewController.viewControllers[0];
        NSArray *viewControllers = @[currentViewController];
        [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
        
        self.pageViewController.doubleSided = NO;
        return UIPageViewControllerSpineLocationMin;
    }
    
    HueSettingViewControllerBase *currentViewController = self.pageViewController.viewControllers[0];
    NSArray *viewControllers = nil;
    
    NSUInteger indexOfCurrentViewController = [self.HueModelController indexOfViewController:currentViewController];
    
    if (indexOfCurrentViewController == 0 || indexOfCurrentViewController % 2 == 0) {
        UIViewController *nextViewController = [self.HueModelController pageViewController:self.pageViewController viewControllerAfterViewController:currentViewController];
        viewControllers = @[currentViewController, nextViewController];
        
    } else {
        UIViewController *previousViewController = [self.HueModelController pageViewController:self.pageViewController viewControllerBeforeViewController:currentViewController];
        viewControllers = @[previousViewController, currentViewController];
    }
    
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    
    return UIPageViewControllerSpineLocationMid;
}

//======================================================================
-(void)rotationView:(int )rotationMode{
 
    [mlog entering:@"rotationView" param:nil];

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

//======================================================================
//指定ページへジャンプ(jumpIndex:0〜)
- (void)showPage:(NSUInteger)jumpIndex
{
    HueSettingViewControllerBase *viewController =
        [self.HueModelController
            viewControllerAtIndex:jumpIndex storyboard:self.storyboard];

    //コントローラーCollectionにページを設定
    NSArray *viewControllers = @[viewController];
    
    //コントローラーCollectionをページコントローラに設定
    [self.pageViewController setViewControllers:viewControllers direction:UIPageViewControllerNavigationDirectionForward animated:YES completion:nil];
    
}

//======================================================================
//現在表示中のページ数を取得
- (NSUInteger)getSelectPageIndex
{
    
    NSUInteger pageNo = [self.hueModelController indexOfViewController:
                         [self.pageViewController.viewControllers objectAtIndex:0]];
    
    return pageNo;
    
}

//======================================================================
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//======================================================================
#pragma mark - action methods

//======================================================================
- (IBAction)closeBtnDidPushed:(id)sender {
    
    [mlog entering:@"closeBtnDidPushed" param:nil];
    
    if ([self getSelectPageIndex] == 1) {
        //先頭の画面に戻る
        [self showPage:0];

    }else{
        // 完了ボタンを押したときに閉じるように設定
        [self dismissViewControllerAnimated:YES completion:nil];

    }
}

@end
