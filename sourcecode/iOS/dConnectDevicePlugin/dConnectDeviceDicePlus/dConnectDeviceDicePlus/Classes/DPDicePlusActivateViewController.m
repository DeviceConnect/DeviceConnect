//
//  DPDicePlusActivateViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusActivateViewController.h"

@interface DPDicePlusActivateViewController () {
    NSTimer *timeoutTimer;
    NSMutableArray *nowConnectingSwitch;
}
@property (weak, nonatomic) IBOutlet UITableView *foundDieListView;
@property UIActivityIndicatorView *indicator;
@property (nonatomic, strong) UIView *loadingView;
- (IBAction)reloadButton:(id)sender;
#pragma mark - iPhone's Constraint
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *searchBtnBottomConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *descBottomConstraint;
#pragma mark - iPad's Constraint
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *imageLeftConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *descHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *descWidthConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tableHeightConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tableWidthConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *tableBottomConstraint;


@end

@implementation DPDicePlusActivateViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

// View表示時
- (void)viewWillAppear:(BOOL)animated
{
	[super viewWillAppear:animated];
    nowConnectingSwitch = [NSMutableArray array];
    self.view.multipleTouchEnabled = NO;
    self.view.exclusiveTouch = YES;
    _foundDieListView.multipleTouchEnabled = NO;
    _foundDieListView.exclusiveTouch = YES;
    [self rotateOrientation:[[UIApplication sharedApplication] statusBarOrientation]];
    [self loadDice];
}

// View回転時
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    [super willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
    [self rotateOrientation:toInterfaceOrientation];
    [self didRotateView];
}

// 位置合わせ
- (void)rotateOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
    
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        CGRect r = [[UIScreen mainScreen] bounds];
        if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
            toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
            if (r.size.height == IPHONE4_H) {
            } else {
                _searchBtnBottomConstraint.constant = 20;
                _descBottomConstraint.constant = 180;
            }
        } else {
            if (r.size.height == IPHONE4_H) {
                //対応しない
            } else {
                _searchBtnBottomConstraint.constant = -5;
                _descBottomConstraint.constant = 125;
            }
        }
    } else {
        if (toInterfaceOrientation == UIInterfaceOrientationPortrait |
            toInterfaceOrientation == UIInterfaceOrientationPortraitUpsideDown) {
            _imageLeftConstraint.constant = 188;
            _searchBtnBottomConstraint.constant = 40;
            _descBottomConstraint.constant = 280;
            _descWidthConstraint.constant = 649;
            _descHeightConstraint.constant = 149;
            _tableWidthConstraint.constant = 599;
            _tableHeightConstraint.constant = 164;
            _tableBottomConstraint.constant = 100;
        } else {
            _imageLeftConstraint.constant = 94;
            _searchBtnBottomConstraint.constant = 50;
            _descBottomConstraint.constant = 316;
            _descWidthConstraint.constant = 400;
            _descHeightConstraint.constant = 300;
            _tableWidthConstraint.constant = 400;
            _tableHeightConstraint.constant = 300;
            _tableBottomConstraint.constant = 50;

        }
    }
    [_foundDieListView reloadData];
}

#pragma mark - Table view data source

// 行数を指定（self.items の要素数）
- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section
{
    return [DPDicePlusManager sharedManager].foundDiceList.count;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    [tableView deselectRowAtIndexPath:indexPath animated:YES]; // 選択状態の解除
}

// セルの生成と設定
- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Storyboard で設定したidentifier
    static NSString *CellIdentifier = @"cellDice";
    // 再利用セルを取得する。
    // 再利用可能なセルがない場合には新しく生成されたインスタンスが返される。
    UITableViewCell *cell =
    [tableView dequeueReusableCellWithIdentifier:CellIdentifier
                                    forIndexPath:indexPath];
    UISwitch *sw = [[UISwitch alloc] initWithFrame:CGRectZero];
    [sw addTarget:self action:@selector(tapSwitch:) forControlEvents:UIControlEventTouchUpInside];
    sw.tag = indexPath.row;
    DPDie *die = [[DPDicePlusManager sharedManager].foundDiceList objectAtIndex:indexPath.row];
    // accessoryViewに代入するときれいに動作します。
    sw.on = [die isConnected];
    cell.accessoryView = sw;
    cell.textLabel.numberOfLines = 3;
    cell.exclusiveTouch = YES;
    cell.accessoryView.exclusiveTouch = YES;
    sw.exclusiveTouch = YES;
    NSString *uid = [NSString stringWithFormat:@"%@", die.UUID];
    cell.textLabel.text = [NSString stringWithFormat:@"DICE+ ( %@ )", [uid substringToIndex:8]];
    CGRect r = [[UIScreen mainScreen] bounds];
    if (r.size.height == IPHONE4_H
        && ([[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationLandscapeLeft
        || [[UIApplication sharedApplication] statusBarOrientation] == UIInterfaceOrientationLandscapeRight)) {
        cell.textLabel.font = [UIFont fontWithName:@"AppleGothic" size:8];
    } else if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone){
        cell.textLabel.font = [UIFont fontWithName:@"AppleGothic" size:12];
    } else {
        cell.textLabel.font = [UIFont fontWithName:@"AppleGothic" size:28];
    }
    [nowConnectingSwitch addObject:sw];

    return cell;
}
- (void)tapSwitch:(id)sender {
    if (!timeoutTimer) {
        UISwitch *sw = (UISwitch *)sender;
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr.foundDiceList objectAtIndex:sw.tag];
        [self startIndicator];
        if (![die isConnected]) {
            [mgr startConnectDicePlusByDPDie:die];
        } else {
            [mgr startDisonnectDicePlusByDPDie:die];
        }
        timeoutTimer = [NSTimer
            scheduledTimerWithTimeInterval:15.0f
            target:self
            selector:@selector(timeoutConnectDice)
            userInfo:nil
            repeats:NO
        ];
    }
}

//スイッチの確認処理
- (void)isSwitches {
    NSMutableArray *dies = [DPDicePlusManager sharedManager].foundDiceList;
    for (int i = 0; i < nowConnectingSwitch.count; i++) {
        UISwitch *sw = [nowConnectingSwitch objectAtIndex:i];
        if (dies.count > i) {
            DPDie *die = [dies objectAtIndex:i];
            if ([die isConnected]) {
                sw.on = YES;
            } else {
                sw.on = NO;
            }
        }
    }
}

- (void) timeoutConnectDice{
    [self manager:nil didDisconnectDie:nil];
}

//リロード
- (void)loadDice {
    [nowConnectingSwitch removeAllObjects];
    _foundDieListView.delegate = self;
    _foundDieListView.dataSource = self;
    [_foundDieListView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cellDice"];
    [self startIndicator];
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    [mgr.connectDelegateList removeAllObjects];
    [mgr.connectDelegateList addObject:self];
    [mgr startConnectDicePlus];
}

// セルの編集可否を指定
- (BOOL)tableView:(UITableView *)tableView
canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return NO;
}

#pragma mark - DPDicePlusManager delegate

- (void)manager:(DPDicePlusManager *)manager didConnectDie:(DPDie *)die {
    [self showAlertWithTitleKey:@"DiceConnectMessage" messageKey:@"DiceConnectedMessage"];
}

- (void)manager:(DPDicePlusManager *)manager didDisconnectDie:(DPDie *)die {
   [self showAlertWithTitleKey:@"DiceDisconnectMessage" messageKey:@"DiceDisconnectedMessage"];
}

- (void)manager:(DPDicePlusManager *)manager didFailConnecttDie:(DPDie *)die {
    [self showAlertWithTitleKey:@"DiceDisconnectMessage" messageKey:@"DiceFailConnectFirmwareErrorMessage"];
}




- (void)manager:(DPDicePlusManager *)manager didFinishedScan:(NSArray *)diceList {

    [nowConnectingSwitch removeAllObjects];
    [_foundDieListView reloadData];
    if (_indicator) {
        [_indicator stopAnimating];
        [_indicator removeFromSuperview];
        [self.loadingView removeFromSuperview];
        _indicator = nil;
        _loadingView = nil;
    }
    if ([DPDicePlusManager sharedManager].foundDiceList.count == 0) {
        [self showAlertWithTitleKey:@"DiceDisconnectMessage" messageKey:@"DiceFailMessage"];
        
    }
}
#pragma mark - private state notice method
- (void) startIndicator {
    if (!_loadingView) {
        UIViewController *rootView = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (rootView.presentedViewController) {
            rootView = rootView.presentedViewController;
        }
        _loadingView = [[UIView alloc] initWithFrame:rootView.view.bounds];
        _loadingView.backgroundColor = [UIColor blackColor];
        [_loadingView setAlpha:0.5];
        _indicator = [[UIActivityIndicatorView alloc] init];
        _indicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyleWhiteLarge;
        _indicator.frame = CGRectMake(0, 0, 20, 20);
        float width = rootView.view.bounds.size.width / (float) 2;
        float height = rootView.view.bounds.size.height / (float) 2;
        CGPoint center = CGPointMake(width, height);
        
        _indicator.center = center;
        _indicator.hidesWhenStopped = YES;
        
        [_indicator startAnimating];
        [_loadingView addSubview:_indicator];
        
        [rootView.view addSubview:_loadingView];
    }
}

//ダイアログのレイアウト修正
-(void) didRotateView {
    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *rootView = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (rootView.presentedViewController) {
            rootView = rootView.presentedViewController;
        }
        if (_indicator) {
            _loadingView.frame = rootView.view.bounds;
            
            float width = rootView.view.bounds.size.width / (float) 2;
            float height = rootView.view.bounds.size.height / (float) 2;
            CGPoint center = CGPointMake(width, height);
            _indicator.center = center;
            
        }
    });
}
// メッセージ表示
- (void)showAlertWithTitleKey:(NSString*)titleKey messageKey:(NSString*)messageKey
{
    
    dispatch_async(dispatch_get_main_queue(), ^{
        if (_indicator) {
            if (timeoutTimer) {
                [timeoutTimer invalidate];
                timeoutTimer = nil;
            }
            [self isSwitches];
            
            [_indicator stopAnimating];
            [_indicator removeFromSuperview];
            [self.loadingView removeFromSuperview];
            _indicator = nil;
            _loadingView = nil;
        }

        NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceDicePlus_resources" ofType:@"bundle"];
        NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
        NSString* disconnectTitle = [bundle localizedStringForKey:titleKey value:nil table:nil];
        NSString* failMessage = [bundle localizedStringForKey:messageKey value:nil table:nil];
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:disconnectTitle
                                                        message:failMessage
                                                       delegate:nil
                                              cancelButtonTitle:nil
                                              otherButtonTitles:@"OK", nil];
        [alert show];
    });
}

- (IBAction)reloadButton:(id)sender {
    UIButton *reloadBtn = (UIButton*) sender;
    reloadBtn.exclusiveTouch = YES;
    [self loadDice];
}
@end
