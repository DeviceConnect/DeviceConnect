//
//  GHHeaderView.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHHeaderView.h"
@interface GHHeaderView()
{
    ///ローディング中か判断フラグ
    BOOL isLoading;
}
@end

@implementation GHHeaderView

#define HEIGHT 44

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        //nibをはりつけ
        [self setup];
    }
    return self;
}

- (void)dealloc
{
    
}


//--------------------------------------------------------------//
#pragma mark - UI
//--------------------------------------------------------------//
- (void)setup
{
    isLoading = NO;
    
    NSString *myClassName = NSStringFromClass([self class]);
    NSArray *nibObjects = [[NSBundle mainBundle] loadNibNamed:myClassName owner:self options:nil];
    if ([nibObjects count] > 0) {
        UIView *myView = [nibObjects objectAtIndex:0];
        myView.frame = CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        [self addSubview:myView];
        
        myView.backgroundColor = [UIColor clearColor];
        self.backgroundColor   = [UIColor clearColor];
    }

    
    
    //画像角丸
    CALayer *layer = self.urlLabel.layer;
    layer.masksToBounds = YES;
    layer.cornerRadius = 5.0f;
    
    self.urlLabel.backgroundColor = [UIColor colorWithWhite:0.8 alpha:0.3];
    
    _searchBar.placeholder = @"Web検索/サイト名を入力";
    _searchBar.keyboardType = UIKeyboardTypeDefault;
    _searchBar.delegate = self;
    _searchBar.searchBarStyle = UISearchBarStyleMinimal;
    _searchBar.autoresizingMask = UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
    
    [self setSearchShow:YES];
    
    //タップセット
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]initWithTarget:self action:@selector(didTaped:)];
    tap.numberOfTapsRequired = 1;
    [self.urlLabel addGestureRecognizer:tap];
}



- (void)updateURL:(NSString*)urlStr
{
    if (![urlStr isEqualToString:@"about:blank"]) {
        self.urlLabel.text  = urlStr;
        self.searchBar.text = urlStr;
        [self setSearchShow:NO];
    }
}


- (void)setSearchShow:(BOOL)isShow
{
    if (isShow) {
        self.urlLabel.hidden   = YES;
        self.reloadbtn.hidden  = YES;
        self.searchBar.hidden  = NO;
        self.reloadbtn.enabled = NO;
    }else{
        self.urlLabel.hidden   = NO;
        self.reloadbtn.hidden  = NO;
        self.searchBar.hidden  = YES;
        self.reloadbtn.enabled = YES;
    }
}



//--------------------------------------------------------------//
#pragma mark - 更新ボタンまたはキャンセル
//--------------------------------------------------------------//
- (IBAction)reload:(UIButton*)sender
{
    LOG_METHOD
    if (isLoading) {
        if ([self.delegate respondsToSelector:@selector(cancelLoading)]) {
            [self.delegate cancelLoading];
            [self setReloadBtn:YES];
        }
    }else{
        if ([self.delegate respondsToSelector:@selector(reload)]) {
            [self.delegate reload];
            [self setReloadBtn:NO];
        }
    }
    
    
}


//--------------------------------------------------------------//
#pragma mark - ボタンの変更
//--------------------------------------------------------------//
- (void)setReloadBtn:(BOOL)isReload
{
    UIImage *btn;
    if (isReload) {
        isLoading = NO;
        btn = [UIImage imageNamed:@"reload"];
    }else{
        isLoading = YES;
        btn = [UIImage imageNamed:@"cancel"];
    }
    
    [self.reloadbtn setImage:btn forState:UIControlStateNormal];
}



- (void)didTaped:(UIGestureRecognizer*)gest
{
    [self setSearchShow:YES];
    [self.searchBar becomeFirstResponder];
}

//--------------------------------------------------------------//
#pragma mark - searchBar delegate
//--------------------------------------------------------------//
- (BOOL)searchBarShouldBeginEditing:(UISearchBar *)searchBar
{
    searchBar.showsCancelButton = YES;
    searchBar.text = self.urlLabel.text;
    return YES;
}
- (void)searchBarTextDidBeginEditing:(UISearchBar *)searchBar
{
    
}

- (BOOL)searchBarShouldEndEditing:(UISearchBar *)searchBar
{
    searchBar.showsCancelButton = NO;
    return YES;
}

- (void)searchBarTextDidEndEditing:(UISearchBar *)searchBar
{
    
}


- (void)searchBarCancelButtonClicked:(UISearchBar *) searchBar
{
    searchBar.showsCancelButton = NO;
    [searchBar resignFirstResponder];
    [self setSearchShow:NO];
}

///文字列からURLか検索キーワードか判別してwebviewに表示する
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
    if (searchBar.text.length > 0) {
        if ([self.delegate respondsToSelector:@selector(urlUpadated:)]) {
            [self.delegate urlUpadated:searchBar.text];
        }
        
    }
    
    searchBar.showsCancelButton = NO;
    [searchBar resignFirstResponder];
}

@end
