//
//  SonyCameraSettingView04Controller.h
//  dConnectDeviceSonyCamera
//
//  Created by 小林伸郎 on 2014/08/08.
//  Copyright (c) 2014年 小林 伸郎. All rights reserved.
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
