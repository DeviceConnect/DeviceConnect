//
//  GHFoldersController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHFoldersListController.h"
#import "GHURLManager.h"
#import "RATreeView.h"
#import "GHDirectory.h"
#import "GHFolderCell.h"

@interface GHFoldersListController ()<RATreeViewDelegate, RATreeViewDataSource>
@property (weak, nonatomic) RATreeView *treeView;
@property (strong, nonatomic) id expanded;
@property (strong, nonatomic) NSArray *data;
@property (nonatomic, strong) GHPageModel* selectedModel;
@end

@implementation GHFoldersListController

#define CELL_ID @"folder"

#define UIColorFromRGB(rgbValue) [UIColor colorWithRed:((float)((rgbValue & 0xFF0000) >> 16))/255.0 green:((float)((rgbValue & 0xFF00) >> 8))/255.0 blue:((float)(rgbValue & 0xFF))/255.0 alpha:1.0]


//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//

- (void)dealloc
{
    LOG_METHOD
    self.data = nil;
    _selectFolder = nil;
}

//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//
- (void)viewDidLoad
{
    [super viewDidLoad];
    self.title = @"フォルダを選択";
    [self setup];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.navigationController.toolbarHidden = YES;
    self.treeView.frame = self.view.bounds;
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}


//--------------------------------------------------------------//
#pragma mark - UI
//--------------------------------------------------------------//
- (void)setup
{
    [self listFolders];
    
    RATreeView *treeView = [[RATreeView alloc] initWithFrame:self.view.frame];
    
    treeView.delegate = self;
    treeView.dataSource = self;
    treeView.separatorStyle = RATreeViewCellSeparatorStyleSingleLine;
    [treeView reloadData];
    [treeView setBackgroundColor:UIColorFromRGB(0xF7F7F7)];
    
    [treeView registerClass:[GHFolderCell class] forCellReuseIdentifier:CELL_ID];
    
    self.treeView = treeView;
    [self.view addSubview:treeView];
}



- (void)listFolders
{
    //お気に入り
    Page* fav = (Page*)[[GHDataManager shareManager]getModelDataByType:TYPE_FAVORITE context:nil];
    GHPageModel* favs = [GHDirectory checkDirectory:fav];
    
    //ブックマーク
    Page* bookmark = (Page*)[[GHDataManager shareManager]getModelDataByType:TYPE_BOOKMARK_FOLDER context:nil];
    GHPageModel* books = [GHDirectory checkDirectory:bookmark];
    
    self.data = @[favs, books];
    
}


- (void)setInitialSelection:(Page*)page
{
    self.myPage = page;
    self.selectedModel = [GHDirectory copyPageModel:page];
}


//--------------------------------------------------------------//
#pragma mark - TreeView Delegate methods
//--------------------------------------------------------------//
- (CGFloat)treeView:(RATreeView *)treeView heightForRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    return 44;
}

- (NSInteger)treeView:(RATreeView *)treeView indentationLevelForRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    return 2 * treeNodeInfo.treeDepthLevel;
}

- (BOOL)treeView:(RATreeView *)treeView shouldExpandItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    return YES;
}

- (BOOL)treeView:(RATreeView *)treeView shouldItemBeExpandedAfterDataReload:(id)item treeDepthLevel:(NSInteger)treeDepthLevel
{
    return YES;
}

- (BOOL)treeView:(RATreeView *)treeView shouldCollapaseRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    return NO;
}


- (void)treeView:(RATreeView *)treeView didSelectRowForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    if (_selectFolder) {
        
        //Pageモデルに変換
        GHPageModel *data = item;
        Page* page = (Page*)[[GHDataManager shareManager]getModelData:data.identifier
                                                withEntityName:@"Page"
                                                       context:nil];
        _selectFolder(page);
    }
    
    [self.navigationController popViewControllerAnimated:YES];
}

//--------------------------------------------------------------//
#pragma mark - TreeView Data Source
//--------------------------------------------------------------//

- (UITableViewCell *)treeView:(RATreeView *)treeView cellForItem:(id)item treeNodeInfo:(RATreeNodeInfo *)treeNodeInfo
{
    UITableViewCell *cell = [treeView dequeueReusableCellWithIdentifier:CELL_ID];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CELL_ID];
    }
    
    [self configureCell:cell cellForItem:item];
    return cell;
}

- (NSInteger)treeView:(RATreeView *)treeView numberOfChildrenOfItem:(id)item
{
    if (item == nil) {
        return [self.data count];
    }
    
    GHPageModel *data = item;
    return [data.children count];
}

- (id)treeView:(RATreeView *)treeView child:(NSInteger)index ofItem:(id)item
{
    GHPageModel *data = item;
    if (item == nil) {
        return [self.data objectAtIndex:index];
    }
    
    return [data.children objectAtIndex:index];
}


//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//

/**
 * セルの表示内容をセット
 * @param cell 対象のセル
 * @param indexPath indexPath
 */
- (void)configureCell:(UITableViewCell *)cell cellForItem:(GHPageModel*)page
{
    cell.textLabel.text = page.title;
    
    if ([page.type isEqualToString:TYPE_FAVORITE]) {
        //お気に入り
        cell.imageView.image = [UIImage imageNamed:@"star"];
        
    }else if ([page.type isEqualToString:TYPE_HISTORY]){
        //履歴
        cell.imageView.image = [UIImage imageNamed:@"history"];
        
    }else{
        //フォルダ
        cell.imageView.image = [UIImage imageNamed:@"folder"];
    }
    
    //選択状態
    if ([page.identifier isEqualToString:self.selectedModel.identifier]) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    
}

@end
