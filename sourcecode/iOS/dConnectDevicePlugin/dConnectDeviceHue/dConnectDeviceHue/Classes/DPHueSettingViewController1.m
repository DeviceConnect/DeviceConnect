//
//  DPHueSettingViewController1.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueSettingViewController1.h"
#import <HueSDK_iOS/HueSDK.h>
#import "DPHueItemBridge.h"

@interface DPHueSettingViewController1 ()
@property (nonatomic) NSMutableArray *bridgeItems;
@property (weak, nonatomic) IBOutlet UITableView *bridgeListTableView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *searchingBridgeIndicator;
@property (weak, nonatomic) IBOutlet UIView *searchingView;
@property (weak, nonatomic) IBOutlet UILabel *processingLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeIconYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeIconXConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *searchButtonYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *searchButtonXConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeListYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeListXConstraint;

@end

@implementation DPHueSettingViewController1

- (void)awakeFromNib
{
    [super awakeFromNib];
    [self initItems];
    
}

- (void)initItems
{
    _bridgeItems = nil;
    _bridgeItems = [[NSMutableArray alloc] init];
    
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {

    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    _bridgeListTableView.delegate = self;
    _bridgeListTableView.dataSource = self;

    [self searchBridge];

}

- (IBAction)searchHueBridge:(id)sender {
    [self searchBridge];
}

- (void)searchBridge
{
    _processingLabel.text = DPHueLocalizedString(_bundle, @"HueBridgeSearch");
    [self startIndicator];
    
    [self initSelectedItemBridge];
    [self initItems];
    [_bridgeListTableView reloadData];

    [manager searchBridgeWithCompletion:^(NSDictionary *bridgesFound) {
        // Check for results
        if (bridgesFound.count > 0) {
            for (id key in [bridgesFound keyEnumerator]) {
                [self addItem:[bridgesFound valueForKey:key] macAdress:key];
            }
        }
        [self stopIndicator];
    }];
}

//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
    //iPadの時だけ回転時座標調整する
    if (self.isIpad) {
    
        _bridgeIconXConstraint.constant = 128;
        _bridgeIconYConstraint.constant = 110;
        
        _searchButtonXConstraint.constant = 128;
        _searchButtonYConstraint.constant = 2;
        
        _bridgeListXConstraint.constant = 184;
        _bridgeListYConstraint.constant = 89;

        if (self.isIpadMini) {
            _bridgeListYConstraint.constant = _bridgeListYConstraint.constant- 50;
        }
    }else{
        
        _bridgeIconXConstraint.constant = 32;
        
        _searchButtonXConstraint.constant = 32;
        
        _bridgeListXConstraint.constant = 44;
        _bridgeListYConstraint.constant = 7;
        
        if ([self isIphoneLong]) {
            _bridgeListYConstraint.constant = _bridgeListYConstraint.constant + 50;
        }
    }

}

//横向き座標調整
- (void)setLayoutConstraintLandscape
{
    if (self.isIpad) {

        _bridgeIconXConstraint.constant = 50;
        _bridgeIconYConstraint.constant = 150;

        _searchButtonXConstraint.constant = 50;
        _searchButtonYConstraint.constant = 80;

        _bridgeListXConstraint.constant = 60;
        _bridgeListYConstraint.constant = 200;

    }else{

        _bridgeIconXConstraint.constant = 0;

        _searchButtonXConstraint.constant = 0;

        _bridgeListXConstraint.constant = 20;

        _bridgeListYConstraint.constant = 30;

        if ([self isIphoneLong]) {
            _bridgeIconXConstraint.constant = _bridgeIconXConstraint.constant + 25;
            _searchButtonXConstraint.constant = _searchButtonXConstraint.constant + 25;

            _bridgeListXConstraint.constant = _bridgeListXConstraint.constant + 10;

        }

    }
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section
{
    return self.bridgeItems.count;
}

// セルの生成と設定
- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Storyboard で設定したidentifier
    static NSString *CellIdentifier = @"cellBridge";
    // 再利用セルを取得する。
    // 再利用可能なセルがない場合には新しく生成されたインスタンスが返される。
    UITableViewCell *cell =
    [tableView dequeueReusableCellWithIdentifier:CellIdentifier
                                    forIndexPath:indexPath];
    // セルを設定する
    DPHueItemBridge *item = self.bridgeItems[indexPath.row];
    
    cell.textLabel.text = [NSString stringWithFormat:@"%@\n ( %@ )",item.macAddress ,item.ipAddress];
    return cell;
}

// セルの編集可否を指定
- (BOOL)    tableView:(UITableView *)tableView
canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    return YES;
}


#pragma mark Table view delegate
// セル選択時の処理
- (void)        tableView:(UITableView *)tableView
  didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    DPHueItemBridge *item = self.bridgeItems[indexPath.row];
    [self selectNextPage:item];
}


#pragma mark - actions
- (void)addItem:(NSString*)ipAdress
      macAdress:(NSString*)macAdress {
    DPHueItemBridge *newItem = [[DPHueItemBridge alloc] init];
    
    newItem.ipAddress = ipAdress;
    newItem.macAddress = macAdress;
    
    NSIndexPath *indexPathToInsert =
    [NSIndexPath indexPathForRow:self.bridgeItems.count inSection:0];

    [self.bridgeItems addObject:newItem];
    // テーブルビューの更新
    [_bridgeListTableView insertRowsAtIndexPaths:@[indexPathToInsert]
                          withRowAnimation:UITableViewRowAnimationAutomatic];
}

- (void)selectNextPage:(DPHueItemBridge*)selectedItemBridge {

    [self setSelectedItemBridge:selectedItemBridge];
    _processingLabel.text = DPHueLocalizedString(_bundle, @"HueBridgeConnecting");
    [self startIndicator];
    [manager startAuthenticateBridgeWithIpAddress:selectedItemBridge.ipAddress
                                       macAddress:selectedItemBridge.macAddress
                                        receiver:self
                   localConnectionSuccessSelector:@selector(didLocalConnectionSuccess)
                                noLocalConnection:@selector(didNoLocalConnection)
                                 notAuthenticated:@selector(didNotAuthenticated)
    ];
}


-(void)didLocalConnectionSuccess {
    [self stopIndicator];
    //接続できたのでライト検索へ飛ぶ
    [self showLightSearchPage];
}

- (void)didNoLocalConnection {
    [self stopIndicator];
    [self showAleart:DPHueLocalizedString(_bundle, @"HueNotConnectingBridge")];
}

-(void)didNotAuthenticated {
    [self stopIndicator];
    [self showAuthPage];
}

-(void)startIndicator
{
    [_searchingBridgeIndicator startAnimating];
    _searchingView.hidden = NO;
}

-(void)stopIndicator
{
    [_searchingBridgeIndicator stopAnimating];
    _searchingView.hidden = YES;
}

@end