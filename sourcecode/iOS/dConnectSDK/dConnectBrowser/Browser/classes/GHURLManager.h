//
//  GHURLManage.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "GHDataManager.h"

typedef enum{
    webview_NavigationType_click = 0,
    webview_NavigationType_bookmark,
    webview_NavigationType_history,
    webview_NavigationType_directInput,//3
    webview_NavigationType_goback,     //4
    webview_NavigationType_goforward,  //5
    webview_NavigationType_goback_jump, //6
    webview_NavigationType_goforward_jump,//7
    webview_NavigationType_undefined,
}NavigationType;


@interface GHURLManager : NSObject

@property (nonatomic, strong) NSMutableArray *histroyBack;
@property (nonatomic, readonly) NSInteger currentIndex;
@property (nonatomic) NavigationType navigationType;

- (NSString*)isURLString:(NSString*)str;
- (NSString*)htmlTitle:(UIWebView*)webview;
- (NSString*)htmlURL:(UIWebView *)webview;
- (NSInteger)historyLength:(UIWebView *)webview;
- (NSString*)createSearchURL:(NSString*)str;
- (Page *)latestPage;

- (void)finishLoading:(UIWebView*)webview;

- (void)addPage:(UIWebView*)webview request:(NSURLRequest*)request;
+ (void)addBookMark:(NSString*)title
                url:(NSString*)url
           parent:(Page*)directory;

+ (void)addFolder:(NSString*)title
           parent:(Page*)directory;

- (NSPredicate*)backHistory;
- (NSPredicate*)forwardHistory;
- (NSString*)nextPageURL:(NavigationType)type;
- (NSString*)jumpHistory:(Page*)page index:(NSInteger)index;

- (void)clear;

- (void)navigationTypeDebug;

@end
