//
//  DPHueSettingViewControllerBase.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! @file
 @brief 設定画面のベースとなる機能を持つ。
 @author NTT DOCOMO
 @date 作成日(2014.7.15)
 */
#import <UIKit/UIKit.h>
#import <HueSDK_iOS/HueSDK.h>
#import "DPHueItemBridge.h"
#import "DPHueViewController.h"
#import "DPHueManager.h"
#include "DPHueConst.h"
/*!
 @class DPHueSettingViewControllerBase
 @brief 設定画面の親クラス。
 */
@interface DPHueSettingViewControllerBase : UIViewController{
 
@protected
     /*!
      @brief Hueとの設定を行うためのインスタンス。
      */
    DPHueManager *manager;
    NSBundle *_bundle;
}
/*!
 @brief 設定画面を表示するViewController。
 */
@property (atomic) DPHueViewController *hueViewController;

/*!
 @brief ViewControllerのページ数。
 */
@property (nonatomic) NSUInteger objectIndex;

#pragma mark Protected Methods

/*!
 @brief アラートを表示する。
 @param[in] msg メッセージ。
 */
- (void)showAleart:(NSString*)msg;

/*!
 @brief 端末がiPadかどうか。
 @retval YES iPad。
 @retval NO それ以外。
 */
- (BOOL)isIpad;

/*!
 @brief 端末がiPadMiniかどうか。
 @retval YES iPadMini。
 @retval NO それ以外。
 */
- (BOOL)isIpadMini;

/*!
 @brief 端末がiPhone4/4Sかどうか。
 @retval YES iPhone4/4S。
 @retval NO それ以外。
 */
- (BOOL)isIphone;

/*!
 @brief 端末がiPhone5以降かどうか。
 @retval YES iPhone5以降。
 @retval NO それ以外。
 */
- (BOOL)isIphoneLong;

/*!
 @brief 縦画面のときのConstraintの設定。
 */
- (void)setLayoutConstraintPortrait;

/*!
 @brief 横画面のときのConstraintの設定。
 */
- (void)setLayoutConstraintLandscape;

/*!
 @brief 選択されたテーブルビューのRowを初期化する。
 */
- (void)initSelectedItemBridge;

/*!
 @brief 選択されたテーブルビューのRowを設定する。
 @param[in] itemBridge 追加するRow。
 */
- (void)setSelectedItemBridge:(DPHueItemBridge*)itemBridge;

/*!
 @brief 選択されたテーブルビューのRowを取得する。
 @return DPHueItemBridge 選択されたRow。
 */
- (DPHueItemBridge*)getSelectedItemBridge;

/*!
 @brief 選択されたテーブルビューのRowが存在するかどうか。
 @retval YES 存在する。
 @retval NO 存在しない。
 */
- (BOOL)isSelectedItemBridge;

/*!
 @brief Hueのブリッジリストページを表示する。
 */
- (void)showBridgeListPage;

/*!
 @brief Hueのブリッジとの認証ページを表示する。
 */
- (void)showAuthPage;

/*!
 @brief Hueのライトを検索するページを表示する。
 */
- (void)showLightSearchPage;

@end
