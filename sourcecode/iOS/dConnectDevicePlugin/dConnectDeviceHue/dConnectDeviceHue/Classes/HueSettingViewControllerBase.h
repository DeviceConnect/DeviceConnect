

#import <UIKit/UIKit.h>
#import "DCLogger.h"
#import <HueSDK_iOS/HueSDK.h>
#import "ItemBridge.h"
#import "HueViewController.h"

@class DPHueDevicePlugin;

/*!
 各ページの親クラス。
 */
@interface HueSettingViewControllerBase : UIViewController{
 
@protected
    DCLogger *mlog;

    PHHueSDK *phHueSDK;
    PHNotificationManager *notificationManager;

    PHBridgeSearching *bridgeSearching;

    
    
}

@property (atomic) HueViewController *hueViewController;

/*!
 @brief メインのView。
 */
@property (strong, nonatomic) IBOutlet UIView *mainView;

/*!
 @brief ViewControllerのページ数。
 */
@property (nonatomic) NSUInteger objectIndex;

#pragma mark Protected Methods
//Protectedメソッド

- (void)showAleart:(NSString*)msg;
- (BOOL)isIpad;
- (BOOL)isIPadMini;
- (BOOL)isIphone;
- (BOOL)isIPhoneLong;
- (void)setLayoutConstraintPortrait;
- (void)setLayoutConstraintLandscape;

- (void)initHueSdk:(NSString*)ipAdr macAdr:(NSString*)macAdr isAuth:(BOOL)isAuth;
- (void)enableLocalHeartbeat;
- (void)disableLocalHeartbeat;
- (void)disableHeartBeat;

- (void)pushlink_authenticationSuccess;
- (void)pushlink_authenticationFailed;
- (void)pushlink_noLocalConnection;
- (void)pushlink_noLocalBridge;
- (void)pushlink_buttonNotPressed;

- (void)localConnection;
- (void)noLocalConnection;
- (void)notAuthenticated;

- (void)after_pushlink_authenticationSuccess;
- (void)after_pushlink_authenticationFailed;
- (void)after_pushlink_noLocalConnection;
- (void)after_pushlink_noLocalBridge;
- (void)after_pushlink_buttonNotPressed;

- (void)after_localConnection;
- (void)after_noLocalConnection;
- (void)after_notAuthenticated;

- (void)initSelectedItemBridge;
- (void)setSelectedItemBridge:(ItemBridge*)itemBridge;
- (ItemBridge*)getSelectedItemBridge;
- (BOOL)isSelectedItemBridge;

- (void)showBridgeListPage;
- (void)showAuthPage;
- (void)showLightSearchPage;

- (void)startIndicator;
- (void)stopIndicator;

@end
