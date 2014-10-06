//
//  GHFolderCreateController.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHFolderCreateController.h"
#import "GHURLManager.h"
#import "GHFoldersListController.h"
#import "GHFolderTitleCell.h"

@interface GHFolderCreateController ()
@end

@implementation GHFolderCreateController



//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)initWithCoder:(NSCoder *)aDecoder
{
    self = [super initWithCoder:aDecoder];
    if (self) {
        self.title = @"フォルダを編集";
    }
    return self;
}

- (void)dealloc
{
}

//--------------------------------------------------------------//
#pragma mark - view cycle
//--------------------------------------------------------------//
- (void)viewDidLoad
{
    LOG_METHOD
    [super viewDidLoad];
    
    //セルの登録
    [self.tableView registerNib:[UINib nibWithNibName:@"GHFolderTitleCell" bundle:nil] forCellReuseIdentifier:CELL_TITLE];
}


- (void)setDirectory
{
    if (!self.directory) {
        
        //初期位置はブックマーク
        NSArray* bookmark = [[GHDataManager shareManager]getModelDataByPredicate:[NSPredicate predicateWithFormat:@"type = %@", TYPE_BOOKMARK_FOLDER] withEntityName:@"Page" context:nil];
        
        if ([bookmark count] > 0) {
            self.directory = [bookmark firstObject];
        }
    }
}


//--------------------------------------------------------------//
#pragma mark - ボタン処理
//--------------------------------------------------------------//
///キャンセルボタン
- (void)cancel
{
    [self.navigationController popViewControllerAnimated:YES];
}

/*
 完了ボタン
 DBへの保存処理
 */
- (void)done
{
    GHFolderTitleCell *cell = (GHFolderTitleCell*)[self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    
    if (cell && cell.titleField.text.length > 0) {
        //DBへ保存処理
        [GHURLManager addFolder:cell.titleField.text parent:self.directory];
    }
    
    [self.navigationController popViewControllerAnimated:YES];
}

//--------------------------------------------------------------//
#pragma mark - Table view data source
//--------------------------------------------------------------//


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    
    if (indexPath.section == 1) {
        //フォルダ選択
        __weak GHFolderCreateController *_self = self;
        GHFoldersListController *folder = [[GHFoldersListController alloc]init];
        
        //フォルダ選択後のコールバック
        [folder setSelectFolder:^(Page* folder){
            [_self updateFolder:folder];
        }];
        
        //初期フォルダセット
        [folder setInitialSelection:self.directory];
        
        [self.navigationController pushViewController:folder animated:YES];
    }
}


- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 44;
}


- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == 1) {
        LOG(@"directory %@", self.directory.title);
        cell.textLabel.text = [[self.datasource objectAtIndex:indexPath.section]objectAtIndex:indexPath.row];
        cell.detailTextLabel.text = self.directory.title;
        cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    }
}

@end
