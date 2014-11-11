//
//  SonyCameraSettingView04Controller.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "SonyCameraDataViewController.h"
#import "SonyCameraDevicePlugin.h"

/*!
 @brief 4ページ目のViewController。
 */
@interface SonyCameraSettingView04Controller : SonyCameraDataViewController <SonyCameraDevicePluginDelegate>

/*!
 @brief プログレスバーを表示するView。
 */
@property (strong, nonatomic) IBOutlet UIView *progressView;

/*!
 @brief デバイス検索中を示すインジケーター。
 */
@property (strong, nonatomic) IBOutlet UIActivityIndicatorView *indicator;

/*!
 @brief SSIDを表示するためのラベル。
 */
@property (strong, nonatomic) IBOutlet UILabel *ssidLabel;

/*!
 @brief 検索ボタン。
 */
@property (strong, nonatomic) IBOutlet UIButton *searchBtn;

/*!
 @brief Sony Camera 検索ボタンのアクション。
 */
- (IBAction) searchBtnDidPushed:(id)sender;

@end
