//
//  DPHueSettingViewController2.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueSettingViewController2.h"

@interface DPHueSettingViewController2 ()
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *pushlinkWaitIndicator;
@property (weak, nonatomic) IBOutlet UIView *searchingView;
@property (weak, nonatomic) IBOutlet UIImageView *pushlinkIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *selectedMacAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *selectedIpAddressLabel;
@property (weak, nonatomic) IBOutlet UILabel *authorizeStateLabel;
@property (weak, nonatomic) IBOutlet UILabel *settingMsgLabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *pushlinkIcomYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *pushlinkIconXConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *searchButtonYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *searchButtonXConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeInfoYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *bridgeInfoXConstraint;

@end

@implementation DPHueSettingViewController2


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
    [manager deallocPHNotificationManagerWithReceiver:self];
    [self startHueAuthentication];
}

- (IBAction)registerAppNameForHueBridge:(id)sender
{
    [self startHueAuthentication];
}

- (void)startHueAuthentication
{
    if (![self isSelectedItemBridge]) {
        return;
    }
    [self startIndicator];
    DPHueItemBridge *item = [self getSelectedItemBridge];
    _selectedIpAddressLabel.text = item.ipAddress;
    _selectedMacAddressLabel.text = item.macAddress;
    _authorizeStateLabel.text = @"---";
    [manager            startPushlinkWithReceiver:self
            pushlinkAuthenticationSuccessSelector:@selector(didPushlinkAuthenticationSuccess)
             pushlinkAuthenticationFailedSelector:@selector(didPushlinkAuthenticationFailed)
                pushlinkNoLocalConnectionSelector:@selector(didPushlinkNoLocalConnection)
                    pushlinkNoLocalBridgeSelector:@selector(didPushlinkNoLocalConnection)
                 pushlinkButtonNotPressedSelector:@selector(didPushlinkButtonNotPressed)];
}


- (void)didPushlinkAuthenticationSuccess
{
    [self stopIndicator];
    [manager searchLightWithCompletion:nil];
    _authorizeStateLabel.text = DPHueLocalizedString(_bundle, @"HueBridgeAuthorized");
    [manager disableHeartbeat];
    [self stopIndicator];
    [self showAleart:DPHueLocalizedString(_bundle, @"HueRegisterApp")];

    [self showLightSearchPage];

}

- (void)didPushlinkAuthenticationFailed
{
    [manager disableHeartbeat];
    [self stopIndicator];

    _authorizeStateLabel.text = DPHueLocalizedString(_bundle, @"HueFailAuthorize");
    [self showAleart:DPHueLocalizedString(_bundle, @"HueFailRegisterApp")];

}

- (void)didPushlinkNoLocalConnection
{
    [manager disableHeartbeat];
    [self stopIndicator];

    _authorizeStateLabel.text = DPHueLocalizedString(_bundle, @"HueFailAuthorize");;
    [self showAleart:DPHueLocalizedString(_bundle, @"HueNotConnectingBridge")];

}

- (void)didPushlinkNoLocalBridge
{
    [manager disableHeartbeat];
    [self stopIndicator];

    _authorizeStateLabel.text = DPHueLocalizedString(_bundle, @"HueFailAuthorize");
    [self showAleart:DPHueLocalizedString(_bundle, @"HueNotFoundBridge")];

}
- (void)didPushlinkButtonNotPressed
{
    //nop
}

//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
    //iPadの時だけ回転時座標調整する
    if (self.isIpad) {
        
        _pushlinkIconXConstraint.constant = 128;
        _pushlinkIcomYConstraint.constant = 110;
        
        _searchButtonXConstraint.constant = 128;
        _searchButtonYConstraint.constant = 2;
        
        _bridgeInfoXConstraint.constant = 55;
        _bridgeInfoYConstraint.constant = 184;

        if (self.isIpadMini) {
            _bridgeInfoYConstraint.constant = _bridgeInfoYConstraint.constant- 50;
        }

    }else{
        
        _pushlinkIconXConstraint.constant = 32;
        
        _searchButtonXConstraint.constant = 32;
        
        _bridgeInfoXConstraint.constant = 44;
        _bridgeInfoYConstraint.constant = 7;
        
        if ([self isIphoneLong]) {
            _bridgeInfoYConstraint.constant = _bridgeInfoYConstraint.constant + 50;
        }

        
    }

}

//横向き座標調整
- (void)setLayoutConstraintLandscape
{
    if (self.isIpad) {
        
        _pushlinkIconXConstraint.constant = 50;
        _pushlinkIcomYConstraint.constant = 150;
        
        _searchButtonXConstraint.constant = 50;
        _searchButtonYConstraint.constant = 80;
        
        _bridgeInfoXConstraint.constant = 160;
        _bridgeInfoYConstraint.constant = 150;
        
    }else{
        
        _pushlinkIconXConstraint.constant = 0;
        
        _searchButtonXConstraint.constant = 0;
        
        _bridgeInfoXConstraint.constant = 20;
        _bridgeInfoYConstraint.constant = 30;
        
        if ([self isIphoneLong]) {
            _pushlinkIconXConstraint.constant = _pushlinkIconXConstraint.constant + 25;
            _searchButtonXConstraint.constant = _searchButtonXConstraint.constant + 25;
            
            _bridgeInfoXConstraint.constant = _bridgeInfoXConstraint.constant + 10;

        }

    }

}
-(void)startIndicator
{
    [_pushlinkWaitIndicator startAnimating];
    
    _searchingView.hidden = NO;
}

-(void)stopIndicator
{
    [_pushlinkWaitIndicator stopAnimating];
    _searchingView.hidden = YES;
}

@end
