//
//  GHMainViewController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

#import "GHMainViewController.h"
#import "GHURLManager.h"
#import "NJKWebViewProgressView.h"
#import "GHAddBookmarkActivity.h"
#import "GHAddBookmarkController.h"
#import "UZMultipleLayeredPopoverController.h"
#import "GHBookmarkTopController.h"
#import "GHSettingController.h"
#import "GHPrintActivity.h"
#import "APLPrintPageRenderer.h"
#import "GHHistoryViewController.h"
#import "GHAppDelegate.h"

typedef enum {
    GHToolViewScrollStatusInit = 0,
    GHToolViewScrollStatusAnimation ,
}GHToolViewScrollStatus;

typedef enum{
    SwipeDirection_back = 0,
    SwipeDirection_next,
}SwipeDirection;


@interface GHMainViewController ()
{
    NJKWebViewProgressView *_progressView;
    NJKWebViewProgress *_progressProxy;
    UIBarButtonItem *activityBtn;
    UIBarButtonItem *backBtn;
    UIBarButtonItem *nextBtn;
    UIBarButtonItem *settingBtn;
    BOOL isLaunched;
    CGFloat beginTouchPt;
    
    BOOL isLongPressAccept; //ロングプレス時は連続で呼ばれる
    int loadingCount;
    BOOL isSwiping;
    int webViewLoads;
}

@property (nonatomic, strong) GHURLManager *manager;
@property (nonatomic, strong) NSURLRequest *myRequest;
@property (nonatomic, strong) NSURLRequest *firstRequest;
@property (nonatomic, strong) NSString     *requestUrl;
@property (nonatomic, strong) GHHeaderView *headerView;
@property (nonatomic) CGFloat beginScrollOffsetY;
@property (nonatomic) GHToolViewScrollStatus toolViewScrollStatus;
@property (nonatomic, strong) UIView          *swipeView;
@property (nonatomic, strong) UIImageView     *swipeBGView;
@property (nonatomic, strong) GHConnectionManager *connection;

@end

@implementation GHMainViewController

#define TIMEOUT 60
#define SEARCH_TAG 1000

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
       
    }
    return self;
}

- (void)dealloc
{
    self.manager    = nil;
    self.myRequest  = nil;
    self.requestUrl = nil;
    self.headerView = nil;
    self.firstRequest = nil;
    
    [self.webview stopLoading];
    [self.webview loadHTMLString:@"<html><body></body></html>" baseURL:[NSURL URLWithString:@"http://localhost"]];
    self.webview.delegate = nil;
    self.webview = nil;
    
    self.connection = nil;
    
    self.swipeView = nil;
    self.swipeBGView = nil;
    
    _progressView  = nil;
    _progressProxy = nil;
    
    [[NSURLCache sharedURLCache] removeAllCachedResponses];
    [[NSNotificationCenter defaultCenter]removeObserver:self];
}

- (void) enterForeground:(NSNotification *)notification
{
    // foregroundに来た事を検知した時点では、このアプリを起動したカスタムURLを取得できない。
    // なので、カスタムURLを取得するGHAppDelegateにカスタムURLを引数に取って処理を行うコールバックを渡しておく。
    
    id<UIApplicationDelegate> appDelegate = [UIApplication sharedApplication].delegate;
    if ([appDelegate isKindOfClass:[GHAppDelegate class]]) {
        [(GHAppDelegate *)appDelegate setURLLoadingCallback:^(NSURL* redirectURL){
            if (redirectURL) {
                [self loadHtml:redirectURL.absoluteString];
                
                //URLを表示
                NSURLRequest *req = [NSURLRequest requestWithURL:redirectURL
                                                     cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                 timeoutInterval:TIMEOUT];
                
                [self updateDisplayURL:req];
            }
        }];
    }
}

//--------------------------------------------------------------//
#pragma mark - UIセットアップ
//--------------------------------------------------------------//
- (void)setup
{
    loadingCount = 0;
    webViewLoads = 0;
    isLaunched   = NO;
    isSwiping    = NO;
    CGFloat barW = 320;
    
    //iPadの場合はナビゲーションにボタンを置く
    if ([GHUtils isiPad]) {
        [self iPadSetup];
        barW = 480;
    }
    
    CGRect frame = CGRectMake(0, 0, barW, 44);
    self.headerView = [[GHHeaderView alloc]initWithFrame:frame];
    self.headerView.delegate = self;
    self.headerView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
    self.navigationItem.titleView = self.headerView;
    self.navigationItem.titleView.frame = frame;
    
    [self refleshWebview];
    
    //マネージャー
    self.manager = [[GHURLManager alloc]init];
    self.connection = [[GHConnectionManager alloc]init];
    self.connection.delegate = self;
    
    //プログレスバー
    _progressProxy = [[NJKWebViewProgress alloc] init];
    self.webview.delegate = _progressProxy;
    _progressProxy.webViewProxyDelegate = self;
    _progressProxy.progressDelegate = self;
    
    CGFloat progressBarHeight = 2.f;
    CGRect navigaitonBarBounds = self.navigationController.navigationBar.bounds;
    CGRect barFrame = CGRectMake(0, navigaitonBarBounds.size.height - progressBarHeight, navigaitonBarBounds.size.width, progressBarHeight);
    _progressView = [[NJKWebViewProgressView alloc] initWithFrame:barFrame];
    _progressView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    
    
    //ブックマーク追加通知
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(addBookmark)
                                                name:ADD_BOOKMARK object:nil];
    
    //ブックマークのweb表示通知
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(showWebPage:)
                                                name:SHOW_WEBPAGE object:nil];
    
    //プリント通知
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(showPrint)
                                                name:SHOW_PRINT object:nil];
    
    //PopOverを閉じたとき
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(closePopup)
                                                name:UZMultipleLayeredPopoverDidDismissNotification
                                              object:nil];
    
    //履歴全削除
    [[NSNotificationCenter defaultCenter]addObserver:self
                                            selector:@selector(clearBackForwardList)
                                                name:CLEAR_HISTORY
                                              object:nil];
}


- (void)refleshWebview
{
    if (self.webview) {
        [self.webview removeFromSuperview];
        [self.webview stopLoading];
        [self.webview loadHTMLString:@"<html><body></body></html>" baseURL:[NSURL URLWithString:@"http://localhost"]];
        self.webview.delegate = nil;
        self.webview = nil;
    }
    
    self.webview = [[UIWebView alloc]initWithFrame:CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height)];
    self.webview.delegate = self;
    self.webview.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    self.webview.scalesPageToFit = YES;
    self.webview.scrollView.delegate = self;
    self.webview.dataDetectorTypes = UIDataDetectorTypeNone;
    [self.view insertSubview:self.webview atIndex:0];
    
    if (![GHUtils isiPad]) {
        self.toolView.webview = self.webview;
    }
    
    [self setAutomaticallyAdjustsScrollViewInsets:YES];
}


///iPad用にボタンを設置
- (void)iPadSetup
{
    backBtn = [[UIBarButtonItem alloc]
               initWithImage:[UIImage imageNamed:@"arrow-back"]
               style:UIBarButtonItemStylePlain
               target:self
               action:@selector(btnAction:)];
    backBtn.tag = kMenuTag_backbtn;
    
    
    nextBtn = [[UIBarButtonItem alloc]
               initWithImage:[UIImage imageNamed:@"arrow-next"]
               style:UIBarButtonItemStylePlain
               target:self
               action:@selector(btnAction:)];
    
    nextBtn.tag = kMenuTag_nextbtn;
    
    self.navigationController.navigationBar.topItem.leftBarButtonItems = @[backBtn, nextBtn];
    
    activityBtn = [[UIBarButtonItem alloc]
                   initWithImage:[UIImage imageNamed:@"upload"]
                   style:UIBarButtonItemStylePlain
                   target:self
                   action:@selector(btnAction:)];
    activityBtn.tag = kMenuTag_addbtn;
    
    UIBarButtonItem *item4 = [[UIBarButtonItem alloc]
                              initWithImage:[UIImage imageNamed:@"bookmark"]
                              style:UIBarButtonItemStylePlain
                              target:self
                              action:@selector(btnAction:)];
    item4.tag = kMenuTag_bookmarkbtn;
    
    
    settingBtn = [[UIBarButtonItem alloc]
                  initWithImage:[UIImage imageNamed:@"setting"]
                  style:UIBarButtonItemStylePlain
                  target:self
                  action:@selector(btnAction:)];
    settingBtn.tag = kMenuTag_settingbtn;
    
    
    self.navigationController.navigationBar.topItem.rightBarButtonItems = @[settingBtn, activityBtn, item4];
}



//--------------------------------------------------------------//
#pragma mark - UI表示メソッド
//--------------------------------------------------------------//

///初回起動時に最後に表示したページを表示(履歴のデータ）
- (void)showLastPage
{
    Page *page = [self.manager latestPage];
    NSString* url = page.url;
    if (url) {
        [self loadHtml:url];
        
        //URLを表示
        NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                                             cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                         timeoutInterval:TIMEOUT];
        
        [self updateDisplayURL:req];
    }
}


///レイアウトの調整
- (void)updateLayout
{
    if ([GHUtils isiPad]) {
        [self updateBtn];
    }else{
        [self.toolView updateBtn];
    }
    
}

///iPad用 戻る・進むボタンの制御
- (void)updateBtn
{
    if (self.webview.canGoBack) {
        backBtn.enabled = YES;
    }else{
        backBtn.enabled = NO;
    }
    
    if (self.webview.canGoForward) {
        nextBtn.enabled = YES;
    }else{
        nextBtn.enabled = NO;
    }
}


/**
 * ヘッダーのリンクテキストフィールドにアドレスを表示する
 * @param request 表示するwebのリクエスト
 */
- (void)updateDisplayURL:(NSURLRequest *)request
{
    NSString* url = [self.manager htmlURL:self.webview];
    if (url.length == 0 || [@"about:blank" isEqualToString:url]) {
        url = [[request URL]absoluteString];
    }
    [self.headerView updateURL:url];
}


///現在表示しているページのURL
- (NSString*)currentPageURL
{
    return [self.manager htmlURL:self.webview];
}


//--------------------------------------------------------------//
#pragma mark - URLのセット
//--------------------------------------------------------------//
/**
 * webにリクエストを投げる
 * @param url 表示するurl
 */
- (void)loadHtml:(NSString*)url
{
    
    //直前のwebviewキャプチャ
    [self createPreview:self.webview];
    
    self.requestUrl = url;
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:url]
                                         cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                     timeoutInterval:TIMEOUT];
    
    self.manager.navigationType = webview_NavigationType_undefined;
    
    //Basic認証対応
    [self.connection startConnection:req];
}



//--------------------------------------------------------------//
#pragma mark - ボタン制御
//--------------------------------------------------------------//
- (IBAction)btnAction:(UIBarButtonItem*)item
{
    int tag = (int)item.tag;
    
    if (tag == kMenuTag_nextbtn) {
        [self goForward];
    }
    
    if (tag == kMenuTag_backbtn) {
        [self goBack];
    }
    
    if (tag == kMenuTag_addbtn) {
        [self showActivityView];
    }
    
    if (tag == kMenuTag_bookmarkbtn) {
        [self showBookmark:item];
    }
    
    if (tag == kMenuTag_settingbtn) {
        [self showSetting];
    }
}



//--------------------------------------------------------------//
#pragma mark - ナビゲーション
//--------------------------------------------------------------//

- (void)goBack
{
    //直前のwebviewキャプチャ
    if (!isSwiping) {
        [self createPreview:self.webview];
    }
    
    isSwiping = NO;
    
    self.manager.navigationType = webview_NavigationType_goback;
    loadingCount = 0;
    [self.webview goBack];
}

- (void)goForward
{
    //直前のwebviewキャプチャ
    if (!isSwiping) {
        [self createPreview:self.webview];
    }
    
    isSwiping = NO;
    
    self.manager.navigationType = webview_NavigationType_goforward;
    loadingCount = 0;
    [self.webview goForward];
}


///設定画面表示
- (void)showSetting
{
    GHSettingController *setting = [[GHSettingController alloc]initWithStyle:UITableViewStyleGrouped];
    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:setting];
    
    if ([GHUtils isiPad]) {
        [self showPopup:nav button:settingBtn];
    }else{
        [self presentViewController:nav animated:YES completion:nil];
    }
}



/**
 * ブックマークのアドレスをwebで表示する
 * 表示するurlはNSNotificationのモデル、PAGE_URLキーに入っている
 * @param notif 通知モデル
 */
- (void)showWebPage:(NSNotification*)notif
{
    NSDictionary *dict = notif.userInfo;
    NSString* url = [dict objectForKey:PAGE_URL];
    
    if (url) {
        self.manager.navigationType = webview_NavigationType_bookmark;
        [self loadHtml:url];
    }
    
    if ([GHUtils isiPad]) {
        [self closePopup];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    
    [self updateLayout];
}


//--------------------------------------------------------------//
#pragma mark - ブックマーク
//--------------------------------------------------------------//
///ブックマーク追加通知受信
- (void)addBookmark
{
    //現在表示されているwebviewからデータを持ってくる
    GHPageModel *model = [[GHPageModel alloc]init];
    model.title = [self.manager htmlTitle:self.webview];
    model.url   = [self.manager htmlURL:self.webview];
    model.type  = TYPE_BOOKMARK;
    
    if (model) {
        //ディレイさせないとモーダルが出ない
        [self performSelector:@selector(showAddBookMarkController:) withObject:model afterDelay:0.75];
    }
}


/**
 * ブックマークの追加controllerを表示
 * @param page 最新の履歴から持ってくる
 */
- (void)showAddBookMarkController:(GHPageModel*)model
{
    GHAddBookmarkController * addbook = [[GHAddBookmarkController alloc]initWithPage:model];
    UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:addbook];
    
    if ([GHUtils isiPad]) {
        [self showPopup:nav button:activityBtn];
    }else{
        [self presentViewController:nav animated:YES completion:nil];
    }
}

/**
 * ブックマークを表示
 * @param item 押されたボタン
 */
- (void)showBookmark:(UIBarButtonItem*)item
{
    //ストーリーボードから取得
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Popup" bundle:[NSBundle mainBundle]];
    UIViewController *bookmark = [storyboard instantiateInitialViewController];
    
    
    if ([GHUtils isiPad]) {
        [self showPopup:bookmark button:item];
    }else{
        
        [self presentViewController:bookmark animated:YES completion:nil];
    }
}


//--------------------------------------------------------------//
#pragma mark - 履歴
//--------------------------------------------------------------//

///履歴を表示
- (void)showHistroy:(NSPredicate*)pred listType:(LIST_TYPE)type
{
    isLongPressAccept = NO;
    GHHistoryViewController * history = [[GHHistoryViewController alloc]initWithStyle:UITableViewStylePlain];
    history.listType = type;
    
    __weak GHMainViewController* _self = self;
    [history setDidHistorySelected:^(NSInteger index, Page* page){
        [_self jumpHistory:page listType:type index:index];
    }];
    
    
    //戻るまたは進むの履歴検索条件をセット
    [history setPredicate:pred];
    
    if ([GHUtils isiPad]) {
        
        UIBarButtonItem* item;
        if (type == kListType_history_back){
            item = backBtn;
        }else{
            item = nextBtn;
        }
        
        [self showPopup:history button:item];
    }else{
        UINavigationController *nav = [[UINavigationController alloc]initWithRootViewController:history];
        [self presentViewController:nav animated:YES completion:nil];
    }
}


/**
 * 履歴リストから選択されたページへジャンプ
 * @param page ページモデル
 * @param type 進む・戻るタイプ
 * @param index 移動先
 */
- (void)jumpHistory:(Page*)page listType:(LIST_TYPE)type index:(NSInteger)index
{
    index ++;
    if (type == kListType_history_back) {
        index = index * -1;
    }
    
    NSString* js = [self.manager jumpHistory:page index:index];
    [self.webview stringByEvaluatingJavaScriptFromString:js];
    
    if ([GHUtils isiPad]) {
        [self closePopup];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
    
    [self updateLayout];
}



- (void)clearBackForwardList
{
    //NOTE:webviewをクリアする方法がない...
    __weak GHMainViewController *_self = self;
    NSString* url = [self.manager htmlURL:self.webview];
    
    //webviewを一旦削除
    [self refleshWebview];
    
    //ディレイする
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [_self loadHtml:url];
    });
    
}

//--------------------------------------------------------------//
#pragma mark - PopoverView
//--------------------------------------------------------------//
- (void)showPopup:(UIViewController*)cont button:(UIBarButtonItem*)item
{
    //UIBarButtonItemのサイズを取得
    UIView *barbutton= (UIView *)[item valueForKey:@"view"];
    CGRect barRect = barbutton.frame;
    barRect.origin.y = barRect.origin.y + 10;
    
    //ポップアップするviewのサイズ調整
    CGRect frame = cont.view.frame;
    frame.size.width = 320;
    
    //PopOver表示
    [self presentMultipleLayeredPopoverWithViewController:cont
                                              contentSize:frame.size
                                                 fromRect:barRect
                                                   inView:self.view
                                                direction:UZMultipleLayeredPopoverTopDirection];
}


- (void)closePopup
{
    [self dismissCurrentPopoverController];
    isLongPressAccept = YES;
}




//--------------------------------------------------------------//
#pragma mark - webview delegate
//--------------------------------------------------------------//

- (BOOL)webView:(UIWebView *)webView shouldStartLoadWithRequest:(NSURLRequest *)request
 navigationType:(UIWebViewNavigationType)navigationType
{
    
    //    LOG(@"navigationType:%d", navigationType);
    //    LOG(@"%@", [[request URL]absoluteString]);
    //    [self.manager navigationTypeDebug];
    
    //about:blankは無視
    if ([@"about:blank" isEqualToString:[[request URL]absoluteString]] ) {
        return NO;
    }
    
    
    
    if(navigationType == UIWebViewNavigationTypeOther){
        //アドレス入力した場合
        if (self.manager.navigationType == webview_NavigationType_undefined) {
            self.manager.navigationType = webview_NavigationType_directInput;
            loadingCount = 0;
        }
        
        self.myRequest = request;
        [self updateDisplayURL:request];
        
    }
    
    //リンクをクリックした場合
    //戻る・進むボタンで遷移した場合のアドレス表示変更
    if (navigationType == UIWebViewNavigationTypeLinkClicked ||
        navigationType == UIWebViewNavigationTypeBackForward) {
        
        if (navigationType == UIWebViewNavigationTypeLinkClicked) {
            self.manager.navigationType = webview_NavigationType_click;
            loadingCount = 0;
        }
        
        self.myRequest = request;
        [self updateDisplayURL:request];
        self.requestUrl = [[request URL]absoluteString];
    }
    
    
    if (loadingCount == 0) {
        //初回のリクエストの場合はrequestを保持
        self.firstRequest = request;
    }
    
    return YES;
}

- (void)webViewDidStartLoad:(UIWebView *)webView
{
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    [self.headerView setReloadBtn:NO];
    
    loadingCount++;
    webViewLoads++;
    
    //swipe中はロードを止める
    if (isSwiping) {
        [webView stopLoading];
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    LOG_METHOD
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    
    //初回のリクエストのみ履歴に保存
    if (self.firstRequest) {
        [self.manager addPage:webView request:self.firstRequest];
        self.firstRequest = nil;
    }
    
    webViewLoads--;
    
    //履歴を保存（ローディングが完了してから）
    //NOTE:about:blankやリダイレクトは無視
    if (!self.webview.isLoading || webViewLoads == 0) {
        //ディスプレイのURL更新 常に読み込んだhtmlのURLにする
        [self updateDisplayURL:[NSURLRequest requestWithURL:[NSURL URLWithString:[self currentPageURL]]]];
        
        loadingCount = 0;
        webViewLoads = 0;
        
        [self createPreview:self.webview];
        [_progressView setProgress:100 animated:YES];
        self.manager.navigationType = webview_NavigationType_undefined;
        
        [self.manager finishLoading:self.webview];
    }
    
    self.myRequest = nil;
    
    [self updateLayout];
    [self.headerView setReloadBtn:YES];
    
    //スワイプで遷移の場合、読み込み完了まで待つ
    [self removeSwipeImages];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    //    LOG(@"error[%d]:%@",(int)[error code], [error localizedDescription]);
    
    //"Frame load interrupted"は無視
    if([error code] == 102){
        return;
    }
}


//--------------------------------------------------------------//
#pragma mark - NSURLConnectionDelegate
//--------------------------------------------------------------//
- (void)didReceiveResponse:(NSString*)url
{
    //URLを戻ってきたレスポンスに差し替える
    self.requestUrl = url;
    loadingCount = 0;
    
    NSURLRequest *req = [NSURLRequest requestWithURL:[NSURL URLWithString:self.requestUrl]
                                         cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                     timeoutInterval:TIMEOUT];
    
    [self.webview loadRequest:req];
}

- (void)didFailWithError:(NSError *)error
{
    
}


//--------------------------------------------------------------//
#pragma mark - GHToolViewDelegate
//--------------------------------------------------------------//
///ブックマークへ追加のアクティビティ
- (void)showActivityView
{
    NSArray *items;
    NSString* url = [[self.webview.request URL]absoluteString];
    
    if (!url) {
        return;
    }else{
        items = @[[NSURL URLWithString:url]];
    }
    
    GHAddBookmarkActivity *activity = [[GHAddBookmarkActivity alloc] init];
    GHPrintActivity *printactivity  = [[GHPrintActivity alloc] init];
    UIActivityViewController *avc = [[UIActivityViewController alloc] initWithActivityItems:items
                                                                      applicationActivities:@[activity, printactivity]];
    
    //リーディングリストを外す
    avc.excludedActivityTypes =  @[UIActivityTypeAddToReadingList];
    
    [self presentViewController:avc animated:YES completion:nil];
    
}


//--------------------------------------------------------------//
#pragma mark - NJKWebViewProgressDelegate
//--------------------------------------------------------------//
-(void)webViewProgress:(NJKWebViewProgress *)webViewProgress updateProgress:(float)progress
{
    [_progressView setProgress:progress animated:YES];
}

//--------------------------------------------------------------//
#pragma mark - GHHeaderViewDelegate delegate
//--------------------------------------------------------------//

- (void)urlUpadated:(NSString*)urlStr
{
    //文字列がURLの場合
    NSString* url = [self.manager isURLString:urlStr];
    if (url) {
        [self loadHtml:url];
    }else{
        //URLではないので検索
        url = [self.manager createSearchURL:urlStr];
        [self loadHtml:url];
    }
    
    [self.headerView updateURL:url];
}

- (void)reload
{
    [self loadHtml:self.requestUrl];
}


- (void)cancelLoading
{
    [self.webview stopLoading];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    [_progressView setProgress:0 animated:NO]; //起動時0％にする
}

//--------------------------------------------------------------//
#pragma mark - scrollview delegate
//--------------------------------------------------------------//
- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{
    if (GHToolViewScrollStatusAnimation == self.toolViewScrollStatus || [GHUtils isiPad]) {
        return;
    }
    
    
    if (self.beginScrollOffsetY < [scrollView contentOffset].y
        && !self.toolView.hidden) {
        
        //スクロール前のオフセットよりスクロール後が多い = 下を見ようとした =>スクロールバーを隠す
        [UIView animateWithDuration:0.4 animations:^{
            self.toolViewScrollStatus = GHToolViewScrollStatusAnimation;
            
            CGRect rect = self.toolView.frame;
            rect.origin.y =  rect.origin.y + rect.size.height;
            self.toolView.frame = rect;
            
        } completion:^(BOOL finished) {
            
            self.toolView.hidden = YES;
            self.toolViewScrollStatus = GHToolViewScrollStatusInit;
        }];
    } else if ([scrollView contentOffset].y < self.beginScrollOffsetY
               && self.toolView.hidden
               && 0.0 != self.beginScrollOffsetY) {
        
        if (self.toolView.frame.origin.y < (self.view.frame.size.height)) {
            //なぜかツールバー1個分ズレることがある
            //必ずツールバーの位置をリセットする
            CGRect rect = self.toolView.frame;
            rect.origin.y =  self.view.frame.size.height;
            self.toolView.frame = rect;
        }
        
        //ツールバーを表示
        self.toolView.hidden = NO;
        [UIView animateWithDuration:0.4 animations:^{
            self.toolViewScrollStatus = GHToolViewScrollStatusAnimation;
            
            CGRect rect = self.toolView.frame;
            rect.origin.y =  rect.origin.y - rect.size.height;
            self.toolView.frame = rect;
            
        } completion:^(BOOL finished) {
            
            self.toolViewScrollStatus = GHToolViewScrollStatusInit;
            
            //必ずツールバーの位置をリセットする
            CGRect rect = self.toolView.frame;
            rect.origin.y =  self.view.frame.size.height - rect.size.height;
            self.toolView.frame = rect;
            
        }];
    }
}


//スクロールビューをドラッグし始めた際に一度実行される
- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView;
{
    self.beginScrollOffsetY = [scrollView contentOffset].y;
}

//スクロール終了でキャプチャ
- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView
{
    [self createPreview:self.webview];
}


- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate
{
    if (!decelerate) {
        [self createPreview:self.webview];
    }
}


//--------------------------------------------------------------//
#pragma mark - swipeで遷移
//--------------------------------------------------------------//
- (void)setGesture
{
    UIScreenEdgePanGestureRecognizer *swipeGest = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self
                                                                                                    action:@selector(swipeBack:)];
    swipeGest.edges = UIRectEdgeLeft;
    swipeGest.delegate = self;
    [self.view addGestureRecognizer:swipeGest];
    
    UIScreenEdgePanGestureRecognizer *swipeGest2 = [[UIScreenEdgePanGestureRecognizer alloc] initWithTarget:self
                                                                                                     action:@selector(swipeNext:)];
    swipeGest2.edges = UIRectEdgeRight;
    swipeGest2.delegate = self;
    [self.view addGestureRecognizer:swipeGest2];
    
    
    UIView *invisibleScrollPreventer = [UIView new];
    invisibleScrollPreventer.frame = CGRectMake(0, 0, 10, self.view.frame.size.height);
    invisibleScrollPreventer.tintColor = [UIColor yellowColor];
    //    invisibleScrollPreventer.autoresizingMask = UIViewAutoresizingFlexibleHeight;
    [self.view addSubview:invisibleScrollPreventer];
    
    UIView *invisibleScrollPreventer2 = [UIView new];
    invisibleScrollPreventer2.frame = CGRectMake(self.view.frame.size.width - 10, 0, 10, self.view.frame.size.height);
    //    invisibleScrollPreventer2.autoresizingMask = UIViewAutoresizingFlexibleHeight|UIViewAutoresizingFlexibleLeftMargin;
    invisibleScrollPreventer2.tintColor = [UIColor yellowColor];
    [self.view addSubview:invisibleScrollPreventer2];
    
    
    
    //長押し設定
    UILongPressGestureRecognizer *longGest = [[UILongPressGestureRecognizer alloc]initWithTarget:self
                                                                                          action:@selector(longpressBack:)];
    
    UILongPressGestureRecognizer *longGest2 = [[UILongPressGestureRecognizer alloc]initWithTarget:self
                                                                                           action:@selector(longpressNext:)];
    
    if ([GHUtils isiPad]) {
        //iPad
        [[backBtn valueForKey:@"view"] addGestureRecognizer:longGest];
        [[nextBtn valueForKey:@"view"] addGestureRecognizer:longGest2];
    }else{
        //iPhone
        [[self.toolView.backbtn valueForKey:@"view"] addGestureRecognizer:longGest];
        [[self.toolView.nextbtn valueForKey:@"view"] addGestureRecognizer:longGest2];
    }
    
}

//前へ
-(void)swipeBack:(UIScreenEdgePanGestureRecognizer *)recognizer
{
    //遷移できない場合は無視
    if (!self.webview.canGoBack) {
        return;
    }
    
    CGPoint location = [recognizer locationInView:self.view];
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        if (self.webview.canGoBack) {
            [self.webview stopLoading];
            
            beginTouchPt = location.x;
            isSwiping = YES;
            
            //スワイプ用の画像を配置
            [self setSwipeImages:SwipeDirection_back];
            self.webview.hidden = YES;
        }
    }else if(recognizer.state == UIGestureRecognizerStateChanged){
        CGPoint pt = CGPointMake(self.webview.center.x - (beginTouchPt - location.x), self.swipeView.center.y);
        self.swipeView.center = pt;
        
    }else if (recognizer.state == UIGestureRecognizerStateEnded) {
        if (self.webview.canGoBack) {
            
            //画面幅の半分以上移動で成立
            if (self.view.frame.size.width/2 < location.x) {
                [self goBack];
                
                [UIView animateWithDuration:0.3
                                 animations:^{
                                     CGRect frame = self.swipeView.frame;
                                     frame.origin.x = self.view.frame.size.width + 10;
                                     self.swipeView.frame = frame;
                                 } completion:^(BOOL finished) {
                                     //
                                 }];
                
            }else{
                
                [UIView animateWithDuration:0.15
                                 animations:^{
                                     CGRect frame = self.swipeView.frame;
                                     frame.origin.x = 0;
                                     self.swipeView.frame = frame;
                                 } completion:^(BOOL finished) {
                                     self.webview.hidden = NO;
                                     [self removeSwipeImages];
                                 }];
            }
            
            
            
        }else{
            self.webview.hidden = NO;
        }
        
    }else if (recognizer.state == UIGestureRecognizerStateCancelled){
        LOG(@"======== CANCELED ==========");
    }
}


//次へ
-(void)swipeNext:(UIScreenEdgePanGestureRecognizer *)recognizer
{
    //遷移できない場合は無視
    if (!self.webview.canGoForward) {
        return;
    }
    
    CGPoint location = [recognizer locationInView:self.view];
    if (recognizer.state == UIGestureRecognizerStateBegan) {
        if (self.webview.canGoForward) {
            [self.webview stopLoading];
            
            beginTouchPt = location.x;
            isSwiping = YES;
            
            //スワイプ用の画像を配置
            [self setSwipeImages:SwipeDirection_next];
            self.webview.hidden = YES;
        }
    }else if(recognizer.state == UIGestureRecognizerStateChanged){
        CGPoint pt = CGPointMake(self.webview.center.x - (beginTouchPt - location.x), self.swipeView.center.y);
        self.swipeView.center = pt;
        
    }else if (recognizer.state == UIGestureRecognizerStateEnded) {
        if (self.webview.canGoForward) {
            
            //画面幅の半分以上移動で成立
            if (self.view.frame.size.width/2 > location.x) {
                [self goForward];
                
                //残りをアニメーションする
                [UIView animateWithDuration:0.3
                                 animations:^{
                                     CGRect frame = self.swipeView.frame;
                                     frame.origin.x = - (self.view.frame.size.width + 10);
                                     self.swipeView.frame = frame;
                                 } completion:^(BOOL finished) {
                                 }];
                
                
            }else{
                //戻す
                [UIView animateWithDuration:0.15
                                 animations:^{
                                     CGRect frame = self.swipeView.frame;
                                     frame.origin.x = 0;
                                     self.swipeView.frame = frame;
                                 } completion:^(BOOL finished) {
                                     self.webview.hidden = NO;
                                     [self removeSwipeImages];
                                 }];
                
            }
            
            
        }else{
            self.webview.hidden = NO;
        }
    }else if (recognizer.state == UIGestureRecognizerStateCancelled){
        LOG(@"======== CANCELED ==========");
    }
}



//プレビュー画像を保存
- (void)createPreview:(UIWebView*)web
{
    [GHUtils saveImage:web identifier:[self currentPageURL]];
}


- (void)setSwipeImages:(SwipeDirection)direction
{
    [self removeImages];
    
    NSString* url;
    //履歴から過去の画像を表示
    if (direction == SwipeDirection_back) {
        //戻る
        url = [self.manager nextPageURL:webview_NavigationType_goback];
    }else{
        //進む
        url = [self.manager nextPageURL:webview_NavigationType_goforward];
    }
    
    //履歴が見つかった場合
    if (url) {
        UIImage* img = [GHUtils previewImage:url];
        //画面サイズが違う場合はセットしない
        
        CGFloat scale = [UIScreen mainScreen].scale;
        CGSize size = self.webview.frame.size;
        
        if (img && (img.size.width == size.width * scale)) {
            self.swipeBGView = [[UIImageView alloc]initWithImage:img];
            self.swipeBGView.frame = CGRectMake(0, self.webview.frame.origin.y, img.size.width / scale, img.size.height/scale);
            [self.view insertSubview:self.swipeBGView aboveSubview:self.webview];
        }
    }
    
    
    //表示中のviewをキャプチャ
    self.swipeView = [self.webview snapshotViewAfterScreenUpdates:YES];
    
    if ([GHUtils isiPad]) {
        [self.view addSubview:self.swipeView];
    }else{
        [self.view insertSubview:self.swipeView belowSubview:self.toolView];
    }
    
    //影
    self.swipeView.layer.shadowRadius = 5;
    self.swipeView.layer.shadowOffset = CGSizeMake(0, 0);
    self.swipeView.layer.shadowOpacity = 0.5;
}


- (void)removeSwipeImages
{
    isSwiping = NO;
    self.webview.hidden = NO;
    [self removeImages];
}


- (void)removeImages
{
    if (self.swipeView) {
        [self.swipeView removeFromSuperview];
        self.swipeView = nil;
    }
    
    if (self.swipeBGView) {
        [self.swipeBGView removeFromSuperview];
        self.swipeBGView = nil;
    }
}

//--------------------------------------------------------------//
#pragma mark - 長押しで履歴表示
//--------------------------------------------------------------//
- (void)longpressBack:(UILongPressGestureRecognizer*)gest
{
    if (isLongPressAccept) {
        [self showHistroy:[self.manager backHistory] listType:kListType_history_back];
    }
}

- (void)longpressNext:(UILongPressGestureRecognizer*)gest
{
    if (isLongPressAccept) {
        [self showHistroy:[self.manager forwardHistory] listType:kListType_history_forward];
    }
}


//--------------------------------------------------------------//
#pragma mark - プリント
//--------------------------------------------------------------//
- (void)showPrint
{
    UIPrintInteractionController *controller = [UIPrintInteractionController sharedPrintController];
    if(!controller){
        LOG(@"Couldn't get shared UIPrintInteractionController!");
        return;
    }
    
    UIPrintInteractionCompletionHandler completionHandler =
    ^(UIPrintInteractionController *printController, BOOL completed, NSError *error) {
        if(!completed && error){
            LOG(@"FAILED! due to error in domain %@ with error code %ld", error.domain, (long)error.code);
        }
    };
    
    
    UIPrintInfo *printInfo = [UIPrintInfo printInfo];
    printInfo.outputType = UIPrintInfoOutputGeneral;
    printInfo.jobName = [[self.webview.request URL]absoluteString];
    printInfo.duplex = UIPrintInfoDuplexLongEdge;
    controller.printInfo = printInfo;
    
    controller.showsPageRange = YES;
    
    APLPrintPageRenderer *myRenderer = [[APLPrintPageRenderer alloc] init];
    myRenderer.jobTitle = printInfo.jobName;
    UIViewPrintFormatter *viewFormatter = [self.webview viewPrintFormatter];
    [myRenderer addPrintFormatter:viewFormatter startingAtPageAtIndex:0];
    controller.printPageRenderer = myRenderer;
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        [controller presentFromBarButtonItem:activityBtn animated:YES completionHandler:completionHandler]; // iPad
    }
    else {
        [controller presentAnimated:YES completionHandler:completionHandler];  // iPhone
    }
}



//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self setup];
    
    //キャッシュさせないように
    NSURLCache* cache = [NSURLCache sharedURLCache];
    [cache removeAllCachedResponses];
    [cache setMemoryCapacity:0];
    
    [self setGesture];
    
    // dConnect Managerを初期化する。
    DConnectManager *mgr = [DConnectManager sharedManager];
    [mgr start];
    [mgr startWebsocket];
}


- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self.navigationController.navigationBar addSubview:_progressView];
    [_progressView setProgress:0 animated:NO]; //起動時0％にする
    [self updateLayout];
    
    NSNotificationCenter *center = [NSNotificationCenter defaultCenter];
    [center addObserver:self selector:@selector(enterForeground:)
                   name:UIApplicationWillEnterForegroundNotification object:nil];
    
    isLongPressAccept = YES;
}

- (void)viewDidAppear:(BOOL)animated
{
    //初回起動時のweb表示
    id<UIApplicationDelegate> appDelegate = [UIApplication sharedApplication].delegate;
    if ([appDelegate isKindOfClass:[GHAppDelegate class]]) {
        // カスタムURLによって起動された場合は、このカスタムURLによって指定されたリダイレクト先を表示する。
        
        NSURL *redirectURL = [(GHAppDelegate *)appDelegate redirectURL];
        if (redirectURL) {
            [(GHAppDelegate *)appDelegate setRedirectURL:nil];
            
            [self loadHtml:redirectURL.absoluteString];
            
            //URLを表示
            NSURLRequest *req = [NSURLRequest requestWithURL:redirectURL
                                                 cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                             timeoutInterval:TIMEOUT];
            
            [self updateDisplayURL:req];
            return;
        }
    }
    
    if (!isLaunched) {
        isLaunched = YES;
        [self showLastPage];
    }
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [_progressView removeFromSuperview];
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

//--------------------------------------------------------------//
#pragma mark - 回転
//--------------------------------------------------------------//

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    //回転後、隠れている場合は表示する
    CGRect rect = self.toolView.frame;
    rect.origin.y = self.view.frame.size.height - rect.size.height;
    self.toolView.frame = rect;
    self.toolView.hidden = NO;
    
}


@end
