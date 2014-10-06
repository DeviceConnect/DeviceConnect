//
//  WebViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "WebViewController.h"

@interface WebViewController ()<UISearchBarDelegate, UIWebViewDelegate>
{
    UISearchBar *_searchBar;
}

@property (weak, nonatomic) IBOutlet UIWebView *webView;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *forwardBtn;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *backBtn;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *refreshBtn;

- (void) updateControllersWithForceDisable:(BOOL)disable;

@end

@implementation WebViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    _searchBar = [[UISearchBar alloc] init];
    _searchBar.delegate = self;
    self.navigationItem.titleView = _searchBar;
    self.navigationItem.titleView.frame = CGRectMake(0, 0, 320, 44);
    
    _webView.opaque = NO;
    _webView.backgroundColor = [UIColor clearColor];
    _webView.scalesPageToFit = YES;
    _webView.delegate = self;
    
    [self updateControllersWithForceDisable:YES];
}

- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar {
    NSString *url = searchBar.text;
    NSString *pattern = @"^https?://.+$";
    NSRegularExpression *re = [NSRegularExpression regularExpressionWithPattern:pattern options:0 error:nil];
    NSTextCheckingResult *result = [re firstMatchInString:url options:0 range:NSMakeRange(0, url.length)];
    
    if (result.numberOfRanges > 0) {
        NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        req.cachePolicy = NSURLRequestReloadIgnoringLocalCacheData;
        [_webView loadRequest:req];
    }
    
    [_searchBar endEditing:YES];
}

- (void) updateControllersWithForceDisable:(BOOL)disable {
    
    if (disable) {
        _forwardBtn.enabled = NO;
        _backBtn.enabled = NO;
        _refreshBtn.enabled = NO;
    } else {
        _forwardBtn.enabled = _webView.canGoForward;
        _backBtn.enabled = _webView.canGoBack;
        _refreshBtn.enabled = YES;
    }

}

- (IBAction)back:(id)sender {
    if (_webView.canGoBack) {
        [_webView goBack];
    }
}

- (IBAction)forward:(id)sender {
    if (_webView.canGoForward) {
        [_webView goForward];
    }
}

- (IBAction)reload:(id)sender {
    [_webView reload];
}

#pragma mark - UIWebView Delegate

- (void)webViewDidStartLoad:(UIWebView *)webView
{
    [self updateControllersWithForceDisable:YES];
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    [self updateControllersWithForceDisable:NO];
}

- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error
{
    
}


@end
