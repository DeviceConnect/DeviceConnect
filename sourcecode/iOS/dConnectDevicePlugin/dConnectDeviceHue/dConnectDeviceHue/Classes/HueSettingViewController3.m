//
//  HueSettingViewController3.m
//  dConnectDeviceHue
//
//  Created by DConnect05 on 2014/09/04.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "HueSettingViewController3.h"
#import "DCLogger.h"

@interface HueSettingViewController3 ()

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *Indicator;
@property (weak, nonatomic) IBOutlet UIImageView *iconImage;
@property (weak, nonatomic) IBOutlet UIImageView *iconImage2;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIcomY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcIconX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcSearchX;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListY;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lcListX;

@end

//======================================================================
@implementation HueSettingViewController3

@synthesize Indicator;
@synthesize iconImage;
@synthesize iconImage2;

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

}

//======================================================================
- (IBAction)btnSearchLight:(id)sender {
    
    if (![self isSelectedItemBridge]) {
        return;
    }

    [self startIndicator];
    
    ItemBridge *item = [self getSelectedItemBridge];
    
    [self initHueSdk:item.ipAdress macAdr:item.macAdress isAuth:NO];

    [self searchLight];
    
}

//======================================================================
//ライト検索
- (void)searchLight
{
    
    [mlog entering:@"searchLight" param:nil];
    
    [self startIndicator];
    
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    PHBridgeSendErrorArrayCompletionHandler completionHandler = ^(NSArray *errors) {

        [self stopIndicator];
        
        if (!errors) {

            [mlog fine:@"searchLight" param:@"ライト検索開始しました。３０秒お待ちください"];

            iconImage2.hidden = NO;
            iconImage.hidden = YES;
            
            [self showAleart:@"ライト検索開始しました。\n３０秒お待ちください。"];
        }
        else {

            [mlog fine:@"searchLight" param:@"ライト検索でエラーが発生しました。"];

            [self showAleart:@"ライト検索でエラーが発生しました。"];
        }
    };
    
    [bridgeSendAPI searchForNewLights:completionHandler];
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
        lcListY.constant = 55;
        
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
        
        lcListX.constant = 100;
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
    Indicator.hidden = false;
    
}

//======================================================================
-(void)stopIndicator
{
    Indicator.hidden = true;
    [Indicator stopAnimating];

}

//======================================================================
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
