

#import <UIKit/UIKit.h>
#import "HueViewController.h"

@class HueSettingViewControllerBase;
@class DPHueDevicePlugin;

@interface HueModelController : NSObject <UIPageViewControllerDataSource>

@property (atomic) HueViewController *hueViewController;

/*!
 指定されたインデックスに対応するViewControllerを返却する。
 
 @param[in] index ページ数
 @param[in] storyboard ViewControllerが格納されたストーリーボード
 
 @retval ViewControllerのインスタンス
 @retval nil ページが存在しない場合
 */
- (HueSettingViewControllerBase *)viewControllerAtIndex:(NSUInteger)index storyboard:(UIStoryboard *)storyboard;

/*!
 brief 指定されたViewControllerのページ数を取得する。
 
 @param[in] viewController ページ数が知りたいViewController
 
 @return ページ数
 */
- (NSUInteger)indexOfViewController:(HueSettingViewControllerBase *)viewController;

@end
