

#import <UIKit/UIKit.h>

@class DPPebbleDevicePlugin;

/*!
 各ページの親クラス。
 */
@interface PebbleDataViewController : UIViewController

/*!
 @brief メインのView。
 */
@property (strong, nonatomic) IBOutlet UIView *mainView;

/*!
 @brief ViewControllerのページ数。
 */
@property (nonatomic) NSUInteger objectIndex;

/*!
 @brief Pebble デバイスプラグインのインスタンス。
 */
@property (nonatomic) DPPebbleDevicePlugin *deviceplugin;

@end
