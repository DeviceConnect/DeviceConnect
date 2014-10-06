//
//  GHAddBookmarkController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHAddBookmarkController.h"
#import "GHURLManager.h"
#import "GHFoldersListController.h"
#import "UZMultipleLayeredPopoverController.h"

@interface GHAddBookmarkController ()
@property (nonatomic, weak) GHBookmarkTitleCell *titleCell;
@end

@implementation GHAddBookmarkController

//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)initWithPage:(GHPageModel*)page
{
    self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.myPage = page;
        self.title = @"追加";
    }
    return self;
}

- (void)dealloc
{
    self.datasource = nil;
    self.myPage = nil;
    self.directory = nil;
}

//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.datasource = @[@[@"アドレス"],
                        @[@"場所"]];
    
    //初期位置
    [self setDirectory];
    
    
    //セルの登録
    [self.tableView registerNib:[UINib nibWithNibName:@"GHBookmarkTitleCell" bundle:nil] forCellReuseIdentifier:CELL_TITLE];
   
    
    //ナビボタンのセット
    UIBarButtonItem* cancel = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemCancel
                                                                           target:self action:@selector(cancel)];
    self.navigationItem.leftBarButtonItem = cancel;
    
    UIBarButtonItem* done = [[UIBarButtonItem alloc]initWithBarButtonSystemItem:UIBarButtonSystemItemDone
                                                                           target:self action:@selector(done)];
    self.navigationItem.rightBarButtonItem = done;
    
}



- (void)setDirectory
{
    if (!self.directory) {
        
        //初期位置はお気に入り
        NSArray* favorites = [[GHDataManager shareManager]getModelDataByPredicate:[NSPredicate predicateWithFormat:@"type = %@", TYPE_FAVORITE] withEntityName:@"Page" context:nil];
        
        if ([favorites count] > 0) {
            self.directory = [favorites firstObject];
        }
    }
}



- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self.titleCell.titleField becomeFirstResponder];
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];

}


//--------------------------------------------------------------//
#pragma mark - ボタン処理
//--------------------------------------------------------------//
///キャンセルボタン
- (void)cancel
{
    if ([GHUtils isiPad]) {
        [GHUtils postNotification:nil withKey:UZMultipleLayeredPopoverDidDismissNotification];
    }else{
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

/*
  完了ボタン
  DBへの保存処理
 */
- (void)done
{
    //DBへ保存処理
    [GHURLManager addBookMark:self.titleCell.titleField.text url:self.myPage.url parent:self.directory];
    
    if ([GHUtils isiPad]) {
        [GHUtils postNotification:nil withKey:UZMultipleLayeredPopoverDidDismissNotification];
    }else{
       [self dismissViewControllerAnimated:YES completion:nil];
    }
}


- (void)updateFolder:(Page*)directory
{
    self.directory = directory;
    
    //セルのリロード
    [self.tableView reloadRowsAtIndexPaths:@[[NSIndexPath indexPathForRow:0 inSection:1]]
                          withRowAnimation:UITableViewRowAnimationFade];
}


//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [[self.datasource objectAtIndex:section] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell* cell;
    if (indexPath.section == 0) {
        cell = [tableView dequeueReusableCellWithIdentifier:CELL_TITLE forIndexPath:indexPath];
        self.titleCell = (GHBookmarkTitleCell*)cell;
    }else{
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:CELL_LOC];
    }
   
    [self configureCell:cell atIndexPath:indexPath];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 1) {
        //フォルダ選択
        __weak GHAddBookmarkController *_self = self;
        GHFoldersListController *folder = [[GHFoldersListController alloc]init];
        
        //フォルダ選択後のコールバック
        [folder setSelectFolder:^(Page* folder){
            [_self updateFolder:folder];
        }];
        
        [self.navigationController pushViewController:folder animated:YES];
    }
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        return 88;
    }else{
        return 44;
    }
}


- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 0) {
        GHBookmarkTitleCell* mycell = (GHBookmarkTitleCell*)cell;
        [mycell setItem:self.myPage];
    }else{
        cell.textLabel.text = [[self.datasource objectAtIndex:indexPath.section]objectAtIndex:indexPath.row];
        cell.detailTextLabel.text = self.directory.title;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
}


@end
