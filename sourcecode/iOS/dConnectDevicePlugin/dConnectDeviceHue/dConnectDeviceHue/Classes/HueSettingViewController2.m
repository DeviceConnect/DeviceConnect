//
//  HueSettingViewController2.m
//  dConnectDeviceHue
//
//  Created by DConnect05 on 2014/09/04.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "HueSettingViewController2.h"
#import "DCLogger.h"

@interface HueSettingViewController2 ()
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *Indicator;
@property (weak, nonatomic) IBOutlet UIView *SearchingView;
@property (weak, nonatomic) IBOutlet UIImageView *iconImage;
@property (weak, nonatomic) IBOutlet UILabel *selectedMac;
@property (weak, nonatomic) IBOutlet UILabel *selectedIp;
@property (weak, nonatomic) IBOutlet UILabel *authState;
@property (weak, nonatomic) IBOutlet UILabel *settingMsg;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIcomY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIconX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListX;

@end

//======================================================================
@implementation HueSettingViewController2

@synthesize Indicator;
@synthesize SearchingView;
@synthesize iconImage;
@synthesize selectedMac;
@synthesize selectedIp;
@synthesize authState;
@synthesize settingMsg;

@synthesize lcIcomY;
@synthesize lcIconX;
@synthesize lcSearchY;
@synthesize lcSearchX;
@synthesize lcListY;
@synthesize lcListX;

DCLogger *mlog;

//======================================================================
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }

    mlog = [[DCLogger alloc]initWithSourceClass:self];

    return self;
}

//======================================================================
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [mlog entering:@"viewDidLoad" param:nil];

    [self startAuth];

}

//======================================================================
- (IBAction)btnAppTouroku:(id)sender {
    
    [mlog entering:@"btnAppTouroku" param:nil];

    [self startAuth];

}

//======================================================================
- (void)startAuth
{
    
    [mlog entering:@"startAuth" param:nil];

    if (![self isSelectedItemBridge]) {
        return;
    }
    
    [self startIndicator];
    
    ItemBridge *item = [self getSelectedItemBridge];
    
    [self initHueSdk:item.ipAdress macAdr:item.macAdress isAuth:YES];
    
    selectedIp.text = item.ipAdress;
    selectedMac.text = item.macAdress;
    
    authState.text = @"---";
    
    //登録開始
    [phHueSDK startPushlinkAuthentication];
    
    
}

//======================================================================
- (void)after_pushlink_authenticationSuccess
{
    
    //隠れてライト検索をしておく
    [self searchLight];
    
    authState.text = @"認証済み";
    [super after_pushlink_authenticationSuccess];

    [self showLightSearchPage];

}

//======================================================================
- (void)after_pushlink_authenticationFailed
{
   
    authState.text = @"認証失敗";
    [super after_pushlink_authenticationFailed];

}

//======================================================================
- (void)after_pushlink_noLocalConnection
{
    
    authState.text = @"認証失敗";
    [super after_pushlink_noLocalConnection];

}

//======================================================================
- (void)after_pushlink_noLocalBridge
{
    
    authState.text = @"認証失敗";
    [super after_pushlink_noLocalBridge];

}

//======================================================================
//ライト検索
- (void)searchLight
{
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    [bridgeSendAPI searchForNewLights:nil];

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
        
        lcListX.constant = 55;
        lcListY.constant = 184;

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
        
        lcListX.constant = 160;
        lcListY.constant = 150;
        
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
-(void)startIndicator
{
    [Indicator startAnimating];
    Indicator.hidden = NO;
    SearchingView.hidden = NO;
    
}

//======================================================================
-(void)stopIndicator
{
    Indicator.hidden = YES;
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
