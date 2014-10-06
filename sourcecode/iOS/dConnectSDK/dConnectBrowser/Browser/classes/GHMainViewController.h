//
//  GHMainViewController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "NJKWebViewProgress.h"
#import "GHToolView.h"
#import "GHHeaderView.h"
#import "GHConnectionManager.h"

/*
 Webviewを表示大元のcontroller
 */

@interface GHMainViewController : UIViewController<UIWebViewDelegate, UIScrollViewDelegate,
NJKWebViewProgressDelegate, UIToolbarDelegate, GHHeaderViewDelegate, GHConnectionManagerDelegate,
UIGestureRecognizerDelegate>

///ツールバー
@property (nonatomic, weak) IBOutlet GHToolView* toolView;

///webview
@property (nonatomic, strong) UIWebView *webview;

- (IBAction)btnAction:(UIBarButtonItem*)item;

@end
