//
//  GHHeaderView.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHURLLabel.h"

@protocol GHHeaderViewDelegate <NSObject>
@optional

///リロード
- (void)reload;

///ロードキャンセル
- (void)cancelLoading;

/**
 * 入力されたURLを渡す
 * @param urlStr 文字列
 */
- (void)urlUpadated:(NSString*)urlStr;

@end

@interface GHHeaderView : UIView<UISearchBarDelegate, UIGestureRecognizerDelegate>

@property (nonatomic, weak) IBOutlet GHURLLabel       *urlLabel;
@property (nonatomic, weak) IBOutlet UISearchBar      *searchBar;
@property (nonatomic, weak) IBOutlet UIButton         *reloadbtn;
@property (nonatomic, weak) id<GHHeaderViewDelegate>  delegate;


/**
 * リロードボタンまたはキャンセルボタンタップ
 * @param sender
 */
- (IBAction)reload:(UIButton*)sender;

/**
 * リロードボタンの画像を変更およびフラグ変更
 * @param isReload リロードボタンか
 */
- (void)setReloadBtn:(BOOL)isReload;

/**
 * URLを表示する
 * @param urlStr 表示する文字列
 */
- (void)updateURL:(NSString*)urlStr;
@end
