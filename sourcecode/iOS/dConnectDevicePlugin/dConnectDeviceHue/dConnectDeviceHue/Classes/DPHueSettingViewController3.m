//
//  DPHueSettingViewController3.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import "DPHueSettingViewController3.h"
@interface DPHueSettingViewController3 ()

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *lightSearchingIndicator;
@property (weak, nonatomic) IBOutlet UIImageView *lightOffIconImageView;
@property (weak, nonatomic) IBOutlet UIImageView *lightOnIconImageView;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightIcomYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightIconXConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightSearchButtonYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightSearchButtonXConstraint;

@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightSearchDescriptionYConstraint;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *lightSearchDescriptionXConstraint;

@end

@implementation DPHueSettingViewController3

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

}

- (IBAction)searchLight:(id)sender
{
    
    if (![self isSelectedItemBridge]) {
        return;
    }

    [self startIndicator];
    [self searchLight];
    
}

//ライト検索
- (void)searchLight
{
    [self startIndicator];
    [manager searchLightWithCompletion:^(NSArray *errors) {
        
        [self stopIndicator];
        
        if (!errors) {
            _lightOnIconImageView.hidden = NO;
            _lightOffIconImageView.hidden = YES;
            
            [self showAleart:DPHueLocalizedString(_bundle, @"HueSearchLight")];
        }
        else {
            [self showAleart:DPHueLocalizedString(_bundle, @"HueSearchLightError")];
        }
    }];
}

//縦向き座標調整
- (void)setLayoutConstraintPortrait
{
    //iPadの時だけ回転時座標調整する
    if (self.isIpad) {
        _lightIconXConstraint.constant = 128;
        _lightIcomYConstraint.constant = 110;
        _lightSearchButtonXConstraint.constant = 128;
        _lightSearchButtonYConstraint.constant = 2;
        _lightSearchDescriptionXConstraint.constant = 184;
        _lightSearchDescriptionYConstraint.constant = 55;
        if (self.isIpadMini) {
            _lightSearchDescriptionYConstraint.constant = _lightSearchDescriptionYConstraint.constant- 50;
        }
    }else{
        
        _lightIconXConstraint.constant = 32;
        
        _lightSearchButtonXConstraint.constant = 32;
        
        _lightSearchDescriptionXConstraint.constant = 44;
        _lightSearchDescriptionYConstraint.constant = 7;
        
        if ([self isIphoneLong]) {
            _lightSearchDescriptionYConstraint.constant = _lightSearchDescriptionYConstraint.constant + 50;
        }
    }

}

//横向き座標調整
- (void)setLayoutConstraintLandscape
{
    if (self.isIpad) {
        
        _lightIconXConstraint.constant = 50;
        _lightIcomYConstraint.constant = 150;
        
        _lightSearchButtonXConstraint.constant = 50;
        _lightSearchButtonYConstraint.constant = 80;
        
        _lightSearchDescriptionXConstraint.constant = 100;
        _lightSearchDescriptionYConstraint.constant = 150;

    }else{
        
        _lightIconXConstraint.constant = 0;
        
        _lightSearchButtonXConstraint.constant = 0;
        
        _lightSearchDescriptionXConstraint.constant = 20;
        _lightSearchDescriptionYConstraint.constant = 30;
        
        if ([self isIphoneLong]) {
            _lightIconXConstraint.constant = _lightIconXConstraint.constant + 25;
            _lightSearchButtonXConstraint.constant = _lightSearchButtonXConstraint.constant + 25;
            _lightSearchDescriptionXConstraint.constant = _lightSearchDescriptionXConstraint.constant + 10;

        }

    }
}

-(void)startIndicator
{
    [_lightSearchingIndicator startAnimating];
    _lightSearchingIndicator.hidden = NO;
}

-(void)stopIndicator
{
    [_lightSearchingIndicator stopAnimating];
    _lightSearchingIndicator.hidden = YES;
}
@end
