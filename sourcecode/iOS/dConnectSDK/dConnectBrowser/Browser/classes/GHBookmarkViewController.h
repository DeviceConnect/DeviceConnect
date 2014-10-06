//
//  GHBookmarkViewController.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>
#import "GHDataManager.h"

/*
 ブックマーク詳細controller（フォルダ、お気に入り階層で使用）
 */

///一覧のタイプ 3種　フォルダ、お気に入り、履歴
typedef enum{
    kListType_folder = 100,
    kListType_favorite,
    kListType_history,
    kListType_history_back,
    kListType_history_forward,
    kListType_undefined,
}LIST_TYPE;


@interface GHBookmarkViewController : UITableViewController<NSFetchedResultsControllerDelegate>

@property (nonatomic, weak) IBOutlet UIBarButtonItem *editBtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem *folderBtn;
@property (nonatomic, weak) IBOutlet UIBarButtonItem *doneBtn;


///LIST_TYPE
@property (nonatomic) LIST_TYPE listType;

///使用するresuseIdentifier
@property (nonatomic, strong) NSString* cellID;


@property (nonatomic, strong) Page *parent;

///閉じる
- (IBAction)close:(id)sender;

///編集ボタン
- (IBAction)edit:(id)sender;

/**
 * セルの表示内容をセット
 * @param cell 対象のセル
 * @param indexPath indexPath
 * @param controller NSFetchedResultsController
 */
- (void)configureCell:(UITableViewCell *)cell atIndexPath:(NSIndexPath *)indexPath
           controller:(NSFetchedResultsController *)controller;


@end
