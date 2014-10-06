//
//  HueSettingViewController1.m
//  dConnectDeviceHue
//
//  Created by DConnect05 on 2014/09/04.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "HueSettingViewController1.h"
#import "DCLogger.h"
#import <HueSDK_iOS/HueSDK.h>
#import "ItemBridge.h"

@interface HueSettingViewController1 ()
@property (weak, nonatomic) IBOutlet UITableView *listBridge;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *Indicator;
@property (weak, nonatomic) IBOutlet UIView *SearchingView;
@property (weak, nonatomic) IBOutlet UILabel *lblSyorityu;

@property (nonatomic) NSMutableArray *items;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIcomY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIconX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListX;

@end

//======================================================================
@implementation HueSettingViewController1

@synthesize listBridge;
@synthesize Indicator;
@synthesize SearchingView;
@synthesize lblSyorityu;

@synthesize lcIcomY;
@synthesize lcIconX;
@synthesize lcSearchY;
@synthesize lcSearchX;
@synthesize lcListY;
@synthesize lcListX;

//======================================================================
- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self initItems];
    
}

//======================================================================
- (void)initItems
{
    _items = nil;
    _items = [[NSMutableArray alloc] init];
    
}

//======================================================================
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {

    }

    mlog = [[DCLogger alloc]initWithSourceClass:self];

    return self;
}

//======================================================================
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [mlog entering:@"viewDidLoad" param:nil];

    listBridge.delegate = self;
    listBridge.dataSource = self;
    
    [self searchBridge];

}

//======================================================================
- (IBAction)btnBridgeSearch:(id)sender {
    
    [self searchBridge];
    
}

//======================================================================
- (void)searchBridge
{

    [mlog entering:@"searchBridge" param:nil];
    
    lblSyorityu.text = @"hueを検索中";
    [self startIndicator];
    
    [self initSelectedItemBridge];
    [self initItems];
    
    // テーブルビューの更新
//    NSIndexPath *indexPathToInsert = [NSIndexPath indexPathForRow:0 inSection:0];
    [listBridge reloadData];
    
    //HueSDK初期化
    [self initHueSdk:nil macAdr:nil isAuth:NO];
    
    [self disableLocalHeartbeat];
    
    //ブリッジ検索
    [bridgeSearching startSearchWithCompletionHandler:^(NSDictionary *bridgesFound)
     {
         
         // Check for results
         if (bridgesFound.count > 0) {
             
             for (id key in [bridgesFound keyEnumerator]) {
                 
                 [self addItem:[bridgesFound valueForKey:key] macAdress:key];
                 
             }
             
         }

         [self disableHeartBeat];
         
         [self stopIndicator];

     }];
    
}

//======================================================================
//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
    
    //iPadの時だけ回転時座標調整する
    if (self.isIpad) {
    
        lcIconX.constant = 128;
        lcIcomY.constant = 110;
        
        lcSearchX.constant = 128;
        lcSearchY.constant = 2;
        
        lcListX.constant = 184;
        lcListY.constant = 89;

        if (self.isIPadMini) {
            lcListY.constant = lcListY.constant- 50;
        }
    }else{
        
        lcIconX.constant = 32;
        
        lcSearchX.constant = 32;
        
        lcListX.constant = 44;
        lcListY.constant = 7;
        
        if ([self isIPhoneLong]) {
            lcListY.constant = lcListY.constant + 50;
        }
    }

}

//======================================================================
//横向き座標調整
- (void)setLayoutConstraintLandscape
{
    if (self.isIpad) {

        lcIconX.constant = 50;
        lcIcomY.constant = 150;

        lcSearchX.constant = 50;
        lcSearchY.constant = 80;

        lcListX.constant = 60;
        lcListY.constant = 200;

    }else{

        lcIconX.constant = 0;

        lcSearchX.constant = 0;

        lcListX.constant = 20;

        lcListY.constant = 30;

        if ([self isIPhoneLong]) {
            lcIconX.constant = lcIconX.constant + 25;
            lcSearchX.constant = lcSearchX.constant + 25;

            lcListX.constant = lcListX.constant + 10;

        }

    }
}

//======================================================================
#pragma mark - Table view data source

//======================================================================
// 行数を指定（self.items の要素数）
- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section
{
    [mlog entering:@"numberOfRowsInSection" param:nil];

    return self.items.count;
}

//======================================================================
// セルの生成と設定
- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    [mlog entering:@"cellForRowAtIndexPath" param:nil];

    // Storyboard で設定したidentifier
    static NSString *CellIdentifier = @"cellBridge";
    // 再利用セルを取得する。
    // 再利用可能なセルがない場合には新しく生成されたインスタンスが返される。
    UITableViewCell *cell =
    [tableView dequeueReusableCellWithIdentifier:CellIdentifier
                                    forIndexPath:indexPath];
    // セルを設定する
    ItemBridge *item = self.items[indexPath.row];
    
    cell.textLabel.text = [NSString stringWithFormat:@"%@\n ( %@ )",item.macAdress ,item.ipAdress];


    return cell;
}

//TODO:いらない？
//======================================================================
// セルの編集可否を指定
- (BOOL)tableView:(UITableView *)tableView
canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    [mlog entering:@"canEditRowAtIndexPath" param:nil];

    return YES;
}

//======================================================================
#pragma mark Table view delegate
//======================================================================
// セル選択時の処理
- (void)tableView:(UITableView *)tableView
didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [mlog entering:@"didSelectRowAtIndexPath" param:nil];

    ItemBridge *item = self.items[indexPath.row];

    [mlog fine:@"didSelectRowAtIndexPath"
     paramName:@"ipAdress" paramString:item.ipAdress];

    [mlog fine:@"didSelectRowAtIndexPath"
     paramName:@"macAdress" paramString:item.macAdress];

    [self selectNextPage:item];
}

//======================================================================
#pragma mark - actions

//======================================================================
- (void)addItem:(NSString*)ipAdress macAdress:(NSString*)macAdress
{
    [mlog entering:@"addItem" param:ipAdress];

    ItemBridge *newItem = [[ItemBridge alloc] init];
    
    newItem.ipAdress = ipAdress;
    newItem.macAdress = macAdress;
    
    NSIndexPath *indexPathToInsert =
    [NSIndexPath indexPathForRow:self.items.count inSection:0];

    [self.items addObject:newItem];


    // テーブルビューの更新
    [listBridge insertRowsAtIndexPaths:@[indexPathToInsert]
                          withRowAnimation:UITableViewRowAnimationAutomatic];
}

//======================================================================
- (void)selectNextPage:(ItemBridge*)selectedItemBridge
{
    
    [mlog entering:@"selectNextPage" param:nil];

    [self setSelectedItemBridge:selectedItemBridge];

    lblSyorityu.text = @"hueに接続中";
    [self startIndicator];
    
    //ブリッジに接続して認証済みかで判断して動作を決める
    [self initHueSdk:selectedItemBridge.ipAdress
              macAdr:selectedItemBridge.macAdress
              isAuth:NO];
    
    //ブリッジ接続を試みてリスナー側で判断して次の画面に進む
    [self enableLocalHeartbeat];
}

//======================================================================
- (void)after_localConnection
{
    //接続できたのでライト検索へ飛ぶ
    [self showLightSearchPage];

}

//======================================================================
- (void)after_notAuthenticated
{

    //未認証なのでアプリ登録へ飛ぶ
    [self showAuthPage];
    
}

//======================================================================
-(void)startIndicator
{
    [Indicator startAnimating];

    SearchingView.hidden = NO;
    
}

//======================================================================
-(void)stopIndicator
{

    [Indicator stopAnimating];
    SearchingView.hidden = YES;
    
}

//======================================================================
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
