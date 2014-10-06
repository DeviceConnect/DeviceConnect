//
//  GHToolView.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>

@protocol GHToolViewDelegate <NSObject>

@optional
- (void)showActivityView;
- (void)showSetting;
- (void)showBookmark;
- (void)showHistroy;
@end

enum {
    kMenuTag_nextbtn = 100,
    kMenuTag_backbtn,
    kMenuTag_addbtn,
    kMenuTag_bookmarkbtn,
    kMenuTag_settingbtn,
};

@interface GHToolView : UIToolbar<UIGestureRecognizerDelegate>

@property (nonatomic, weak) IBOutlet UIBarButtonItem* nextbtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem* backbtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem* bookmarkbtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem* addbtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem* settingbtn;
@property (nonatomic, weak) IBOutlet UIWebView*       webview;

//- (IBAction)btnAction:(UIBarButtonItem*)sender;
- (void)setBtnEnabled:(BOOL)isEnabled;
- (void)updateBtn;

- (IBAction)longpressBack:(UIGestureRecognizer*)gest;
- (IBAction)longpressNext:(UIGestureRecognizer*)gest;
@end
