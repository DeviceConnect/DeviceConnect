//
//  GHURLManage.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHURLManager.h"
@interface GHURLManager()
{
    BOOL isJump;
}
@property (nonatomic, strong) NSString *lastURL;
@end

@implementation GHURLManager

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)init
{
    self = [super init];
    if (self) {
        
        //遷移履歴
        self.histroyBack    = [[NSMutableArray alloc]init];
        _currentIndex = 0;
        _navigationType = webview_NavigationType_undefined;
        isJump = NO;
    }
    return self;
}

- (void)dealloc
{
    self.histroyBack = nil;
    self.lastURL = nil;
}


//--------------------------------------------------------------//
#pragma mark - 入力された文字列がURLかチェック
//--------------------------------------------------------------//
- (NSString*)isURLString:(NSString*)str
{
    //URLか判定
    NSDataDetector *dataDetector = [NSDataDetector dataDetectorWithTypes:NSTextCheckingTypeLink error:nil];
    NSArray *resultArray = [dataDetector matchesInString:[str stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]
                                                 options:0
                                                   range:NSMakeRange(0,[str length])];
    
    for (NSTextCheckingResult *result in resultArray){
        if ([result resultType] == NSTextCheckingTypeLink){
            NSURL *url = [result URL];
            LOG(@"url:%@",[url description]);
            
            return [url description];
        }
    }
    
    return nil;
}


///google検索APIの形式にする
- (NSString*)createSearchURL:(NSString*)str
{
    NSArray *languages = [NSLocale preferredLanguages];
    NSString *lang = [languages objectAtIndex:0];
    NSString *encodedString = [str stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    return [NSString stringWithFormat:@"https://google.co.jp/search?q=%@&ie=UTF-8&oe=UTF-8&hl=%@&client=safari", encodedString , lang];
}



//--------------------------------------------------------------//
#pragma mark - JS
//--------------------------------------------------------------//

///htmlからタイトルを取得
- (NSString*)htmlTitle:(UIWebView*)webview
{
    NSString *title = [webview stringByEvaluatingJavaScriptFromString:@"document.title"];
    return title;
}

///htmlからURLを取得
- (NSString*)htmlURL:(UIWebView *)webview
{
    NSString *url = [webview stringByEvaluatingJavaScriptFromString:@"document.URL"];
    return url;
}

///htmlのhistory.lengthを取得
- (NSInteger)historyLength:(UIWebView *)webview
{
    NSString* history = [webview stringByEvaluatingJavaScriptFromString:@"window.history.length"];
    return [history integerValue];
}


- (NSString*)jumpHistory:(Page*)page index:(NSInteger)index
{
    //ジャンプ遷移の場合
    isJump = YES;
    
    _currentIndex = _currentIndex + index;
    [self debug];
    
    //履歴に追加
    [self addHistory:page.title url:page.url];
    
    return [NSString stringWithFormat:@"window.history.go(%d);", (int)index];
}


//--------------------------------------------------------------//
#pragma mark - DB処理
//--------------------------------------------------------------//
//履歴の追加
- (void)addPage:(UIWebView*)webview request:(NSURLRequest*)request
{
    //ジャンプ遷移した場合は無視
    if (isJump) {
        return;
    }
    
    NSString* url =  [self htmlURL:webview];
    
//    LOG(@"URL:%@", url);
    
    //最後に登録したURLと同じ場合は無視。
    //NOTE:ページによりリダイレクトで同じURLを再度読み込む場合がある
    if ([url isEqualToString:self.lastURL]) {
        return;
    }
    
    self.lastURL = url;
    
    GHPageModel *model = [self addHistory:[self htmlTitle:webview] url:self.lastURL];
    [self manageHistory:model];
}


- (GHPageModel*)addHistory:(NSString*)title url:(NSString*)url
{
    GHPageModel *model = [[GHPageModel alloc]init];
    model.title = title;
    model.url   = url;
    model.type  = TYPE_BOOKMARK;
    model.category = CATEGORY_HISTORY;
    
    Page* page = [[GHDataManager shareManager]addHistory:model context:nil];
    model.identifier = page.identifier;
    return model;
}



//ブックマークの追加
+ (void)addBookMark:(NSString*)title
                url:(NSString*)url
           parent:(Page*)directory
{
    GHPageModel *model = [[GHPageModel alloc]init];
    model.title = title;
    model.url   = url;
    model.type  = TYPE_BOOKMARK;
    
    //デフォルトフォルダはPRIORITY以下。
    //追加はPRIORITY+n
    NSInteger priority = [self getPriority:directory];
    if (priority < PRIORITY) {
        priority += PRIORITY;
    }
    
    model.priority = @(priority+1);
    
    [[GHDataManager shareManager]addBookmark:model parent:directory context:nil];
}


//ディレクトリの追加
+ (void)addFolder:(NSString*)title
           parent:(Page*)directory
{
    GHPageModel *model = [[GHPageModel alloc]init];
    model.title = title;
    model.type  = TYPE_FOLDER;
    
    NSInteger priority = [self getPriority:directory];
    if (priority < PRIORITY) {
        priority += PRIORITY;
    }
    
    model.priority = @(priority+1);
    
    [[GHDataManager shareManager]addBookmark:model parent:directory context:nil];
}


//一番大きいpriorityを取得
+ (NSInteger)getPriority:(Page*)parent
{
    NSInteger priority = 0;
    for (Page* page in parent.children) {
        NSInteger p = [page.priority integerValue];
        if (p > priority) {
            priority = p;
        }
    }
    
    return priority;
}


//--------------------------------------------------------------//
#pragma mark - ユーティリティ
//--------------------------------------------------------------//

- (BOOL)isSameURL:(NSString*)url
{
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"url = %@", url];
    NSArray* object = [[GHDataManager shareManager]getModelDataByPredicate:pred withEntityName:@"Page" context:nil];
    if ([object count] > 0) {
        return YES;
    }
    
    return NO;
}



- (Page *)latestPage
{
    NSPredicate *pred = [NSPredicate predicateWithFormat:@"type = %@", TYPE_BOOKMARK];
    NSSortDescriptor *sortDescriptor = [[NSSortDescriptor alloc] initWithKey:@"created_date" ascending:NO];
    NSArray* pages = [[GHDataManager shareManager]getModelDataByPredicate:pred
                                                      withSortDescriptors:@[sortDescriptor]
                                                               entityName:@"Page"
                                                                  context:nil];
    
	if ([pages count] > 0) {
        //NOTE:data:faultになる場合がある
        Page *page = [pages firstObject];
        if (page) {
            return  page;
        }else{
            return nil;
        }
		
	}
    
    return nil;

}


//--------------------------------------------------------------//
#pragma mark - 履歴管理
//--------------------------------------------------------------//
- (void)manageHistory:(GHPageModel*)model
{
    [self navigationTypeDebug];
    
    //現在位置
    if (_navigationType == webview_NavigationType_goback){
        //戻るボタン
        _currentIndex--;
    }else if(_navigationType == webview_NavigationType_goforward) {
        //進むボタン
        _currentIndex++;
    }else if(_navigationType == webview_NavigationType_goforward_jump ||
             _navigationType == webview_NavigationType_goback_jump){
        //進む・戻る履歴から遷移
        LOG(@"_currentIndex:%d", (int)_currentIndex );
    }else{
        //新しい履歴ラインになる
        //現在位置より先にある履歴は削除
        if (_currentIndex < [self.histroyBack count]) {
            LOG(@"=== 新しい履歴ライン ===");
            
            for (int i = (int)[self.histroyBack count] ; i > _currentIndex; i--) {
                [self.histroyBack removeObject:[self.histroyBack lastObject]];
            }
        }
        
        _currentIndex++;
        [self.histroyBack addObject:model];
    }
    
}

///戻る履歴一覧
- (NSPredicate*)backHistory
{
    if (![self isIndexExist:(int)_currentIndex - 1]) {
        return nil;
    }
    
    return [self findModelAtIndex:0 To:(int)(_currentIndex - 1)];
}

///進む履歴一覧
- (NSPredicate*)forwardHistory
{
    if (![self isIndexExist:(int)_currentIndex]) {
        return nil;
    }
    
    return [self findModelAtIndex:(int)_currentIndex To:(int)[self.histroyBack count]];
}


- (BOOL)isIndexExist:(int)index
{
    int total = (int)[self.histroyBack count];
    
    if (index < 0) {
        return NO;
    }
    
    return index < total ? YES : NO;
}


- (NSPredicate*)findModelAtIndex:(int)startCount To:(int)end
{
    NSMutableArray* preds = [[NSMutableArray alloc]init];
    for (int i = startCount ; i < end; i++) {
        GHPageModel* model = (GHPageModel*)[self.histroyBack objectAtIndex:i];
        NSPredicate *pred = [NSPredicate predicateWithFormat:@"identifier = %@", model.identifier];
        [preds addObject:pred];
    }
    
    NSPredicate* orPred = [NSCompoundPredicate orPredicateWithSubpredicates:preds];
    return orPred;
}


/**
 * 履歴から前後のページのURLを渡す
 * @param type
 */
- (NSString*)nextPageURL:(NavigationType)type
{
    int index = -1;
    if (type == webview_NavigationType_goback) {
        index = (int)_currentIndex - 2;
    }else if(type == webview_NavigationType_goforward){
        index = (int)_currentIndex;
    }
    
    
    LOG(@"index:%d", index);
    
    if (index < 0 && index >= [self.histroyBack count]) {
        LOG(@"indexが変");
        return nil;
    }
    
    
    GHPageModel* model = (GHPageModel*)[self.histroyBack objectAtIndex:index];
    
    if (!model) {
        return nil;
    }
    
    return model.url;
}




- (void)setNavigationType:(NavigationType)navigationType
{
    _navigationType = navigationType;
}


- (void)clear
{
    Page* currentPage = [self.histroyBack objectAtIndex:_currentIndex -1];
    [self.histroyBack removeAllObjects];
    [self.histroyBack addObject:currentPage];
    _currentIndex = 1;
    [self debug];
}



- (void)finishLoading:(UIWebView*)webview
{
    LOG_METHOD
    int length = (int)[self historyLength:webview];
    int count  = (int)[self.histroyBack count];
    
    //ずれを調整
    if (count != length) {
        int diff = count - length;
        for (int i = count - 2 ; i > count - diff - 2; i--) {
            [self.histroyBack removeObjectAtIndex:i];
        }
        
        _currentIndex = _currentIndex - diff;
    }
    
    //現在表示しているURLとcurrentIndexが指しているモデルが同じかチェック
    //NOTE:webkitでも対応できないリダイレクトがある
    //*** webkit discarded an uncaught exception in the webview didfinishloadforframe
    [self checkHistoryAndURL:[self htmlURL:webview]];
    

    isJump = NO;
    
    LOG(@"length:%d", length);
    [self debug];
}


///現在表示しているURLとcurrentIndexが指しているモデルが同じかチェック
- (void)checkHistoryAndURL:(NSString*)url
{
    
    
//    Page* currentPage = [self.histroyBack objectAtIndex:_currentIndex -1];
//    if (![currentPage.url isEqualToString:url]) {
    
        __block NSMutableArray* array = [[NSMutableArray alloc]init];
        
        //URLが違うのでモデルを探す
        [self.histroyBack enumerateObjectsWithOptions:NSSortConcurrent usingBlock:^(Page* obj, NSUInteger idx, BOOL *stop) {
            if ([obj.url isEqualToString:url]) {
                [array addObject:@(idx)];
            }
        }];
        
        //結果がある場合は一番最後を選択
        if ([array count] > 0) {
            _currentIndex = [[array lastObject] integerValue] + 1;
        }
//    }
}



//--------------------------------------------------------------//
#pragma mark - DEBUG
//--------------------------------------------------------------//
- (void)debug
{
#ifdef DEBUG
    LOG(@"--:%d", (int)_currentIndex );
    int i = 1;
    for (GHPageModel* model in self.histroyBack) {
        if (i == _currentIndex) {
            LOG(@"->[%d]:%@", i, model.title );
        }else{
            LOG(@"  [%d]:%@", i, model.title );
        }
        
        i++;
    }

#endif
}


- (void)navigationTypeDebug
{
#ifdef DEBUG
    NSString* type = @"directInput";
    switch (_navigationType) {
        case  webview_NavigationType_goback:
            type = @"goback";
            break;
            
        case webview_NavigationType_goforward:
            type = @"goforward";
            break;
            
        case webview_NavigationType_click:
            type = @"click";
            break;
            
        default:
            break;
    }
    
    LOG(@"navigationType:%@", type);
#endif
}

@end
