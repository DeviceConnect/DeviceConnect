//
//  GHToolView.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHToolView.h"
#import "GHAppDelegate.h"

@interface GHToolView()
@end


@implementation GHToolView

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//

- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        
    }
    
    return self;
}


- (void)dealloc
{
    
}


- (void)awakeFromNib
{
    UILongPressGestureRecognizer *longGest = [[UILongPressGestureRecognizer alloc]initWithTarget:self
                                                                                          action:@selector(longpressBack:)];
    
    
    [[self.backbtn valueForKey:@"view"] addGestureRecognizer:longGest];
    
    UILongPressGestureRecognizer *longGest2 = [[UILongPressGestureRecognizer alloc]initWithTarget:self
                                                                                           action:@selector(longpressNext:)];
    [[self.nextbtn valueForKey:@"view"] addGestureRecognizer:longGest2];
}

//--------------------------------------------------------------//
#pragma mark - ボタン制御
//--------------------------------------------------------------//
//- (IBAction)btnAction:(UIBarButtonItem*)sender
//{
//    if ([sender isEqual:self.nextbtn]) {
//        [self.webview goForward];
//    }
//    
//    if ([sender isEqual:self.backbtn]) {
//        [self.webview goBack];
//    }
//    
//    if ([sender isEqual:self.addbtn]) {
//        if ([self.delegate respondsToSelector:@selector(showActivityView)]) {
//            [self.delegate performSelector:@selector(showActivityView) withObject:nil];
//        }
//    }
//    
//    if ([sender isEqual:self.settingbtn]) {
//        if ([self.delegate respondsToSelector:@selector(showSetting)]) {
//            [self.delegate performSelector:@selector(showSetting) withObject:nil];
//        }
//    }
//    
//    
//    if ([sender isEqual:self.bookmarkbtn]) {
//        if ([self.delegate respondsToSelector:@selector(showBookmark)]) {
//            [self.delegate performSelector:@selector(showBookmark) withObject:nil];
//        }
//    }
//}


- (void)setBtnEnabled:(BOOL)isEnabled
{
    self.bookmarkbtn.enabled = isEnabled;
    self.addbtn.enabled = isEnabled;
}



- (void)updateBtn
{
    if (self.webview.canGoBack) {
        self.backbtn.enabled = YES;
    }else{
        self.backbtn.enabled = NO;
    }
    
    if (self.webview.canGoForward) {
        self.nextbtn.enabled = YES;
    }else{
        self.nextbtn.enabled = NO;
    }
}


//--------------------------------------------------------------//
#pragma mark - 長押しで履歴表示
//--------------------------------------------------------------//
- (IBAction)longpressBack:(UIGestureRecognizer*)gest
{
    if ([self.delegate respondsToSelector:@selector(showHistroy)]) {
        [self.delegate performSelector:@selector(showHistroy) withObject:nil];
    }
}

- (IBAction)longpressNext:(UIGestureRecognizer*)gest
{
    if ([self.delegate respondsToSelector:@selector(showHistroy)]) {
        [self.delegate performSelector:@selector(showHistroy) withObject:nil];
    }
}

@end
