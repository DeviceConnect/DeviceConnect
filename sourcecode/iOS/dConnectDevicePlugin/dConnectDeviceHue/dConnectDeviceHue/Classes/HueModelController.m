
#import "HueModelController.h"
#import "HueSettingViewControllerBase.h"
#import "Hue_device_plugin_defines.h"
#import "HueViewController.h"
#import "DCLogger.h"

@interface HueModelController()
@property (readonly, strong, nonatomic) NSArray *pageData;

@end

//======================================================================
@implementation HueModelController

DCLogger *mlog;

//======================================================================
- (id)init
{
    self = [super init];
    if (self) {
        // Create the data model.
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        _pageData = [[dateFormatter monthSymbols] copy];
        
    }
    
    mlog = [[DCLogger alloc]initWithSourceClass:self];
    [mlog entering:@"init" param:nil];

    return self;
}

//======================================================================
//ページのインスタンスを生成する。ページ移動時に呼ばれる
- (HueSettingViewControllerBase *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard
{
 
    [mlog entering:@"viewControllerAtIndex" param:nil];

    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        // Return the data view controller for the given index.
        if (([self.pageData count] == 0) || (index >= SETTING_PAGE_COUNT_IPHNE)) {
            return nil;
        }
    } else {
        // Return the data view controller for the given index.
        if (([self.pageData count] == 0) || (index >= SETTING_PAGE_COUNT_IPAD)) {
            return nil;
        }
    }
    
    NSString *viewId = [NSString stringWithFormat:@"HueSettingViewController%lu", (unsigned long)(index + 1)];
    
    // Create a new view controller and pass suitable data.
    HueSettingViewControllerBase *vc = [storyboard instantiateViewControllerWithIdentifier:viewId];
    
    vc.objectIndex = index;
    
    vc.hueViewController = self.hueViewController;

    return vc;
}

//======================================================================
//引数で渡されたページが何ページか返す
- (NSUInteger)indexOfViewController:(HueSettingViewControllerBase *)viewController
{
    [mlog entering:@"indexOfViewController" param:nil];

    return viewController.objectIndex;
}

//======================================================================
//ページ移動を無効にする　warning対策で実装
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerBeforeViewController:(UIViewController *)viewController
{
    return nil;
}

//======================================================================
//ページ移動を無効にする　warning対策で実装
- (UIViewController *)pageViewController:(UIPageViewController *)pageViewController viewControllerAfterViewController:(UIViewController *)viewController
{
    return nil;
}

//======================================================================
#pragma mark - Page View Controller Data Source

//======================================================================
//全ページ数取得イベント
- (NSInteger)presentationCountForPageViewController:(UIPageViewController *)pageViewController
{
    [mlog entering:@"presentationCountForPageViewController" param:nil];

    return 3;
}

//======================================================================
- (NSInteger)presentationIndexForPageViewController:(UIPageViewController *)pageViewController
{
    [mlog entering:@"presentationIndexForPageViewController" param:nil];

    NSUInteger pageNo = [self indexOfViewController:
                         [pageViewController.viewControllers objectAtIndex:0]];

    return pageNo;
}

@end
