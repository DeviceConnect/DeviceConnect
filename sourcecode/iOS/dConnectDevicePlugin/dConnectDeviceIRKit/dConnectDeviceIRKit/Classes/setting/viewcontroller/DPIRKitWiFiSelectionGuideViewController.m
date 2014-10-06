//
//  DPIRKitPowerGuideViewController.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/09/20.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKit_irkit.h"
#import "DPIRKitWiFiSelectionGuideViewController.h"
#import "DPIRKitConst.h"
#import "DPIRKitReachability.h"

typedef NS_ENUM(NSUInteger, DPIRKitSelectionState) {
    DPIRKitSelectionStateIdling = 0,
    DPIRKitSelectionStateGotDeviceKey,
    DPIRKitSelectionStateWaitingIRKitSSID,
    DPIRKitSelectionStateCheckingIRKit,
};

@interface DPIRKitWiFiSelectionGuideViewController ()<UIAlertViewDelegate>
{
    DPIRKitSelectionState _state;
}

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *indView;
@property (weak, nonatomic) IBOutlet UIView *indBackView;

- (void) showAlertWithTileKey:(NSString *)titleKey
                  messsageKey:(NSString *)messageKey
               closeButtonKey:(NSString *)closeButtonKey
                     delegate:(id<UIAlertViewDelegate>)delegate;

- (void) createNewDeviceWithClientKey:(NSString *)clientKey;
- (void) showNoNetworkError;
- (void) startLoading;
- (void) stopLoading;

- (void) enterForground;

@end

@implementation DPIRKitWiFiSelectionGuideViewController

- (void) showAlertWithTileKey:(NSString *)titleKey
                  messsageKey:(NSString *)messageKey
               closeButtonKey:(NSString *)closeButtonKey
                     delegate:(id<UIAlertViewDelegate>)delegate
{
    
    __weak typeof(self) _self = self;
    NSBundle *bundle = DPIRBundle();
    
    UIAlertView *alert
    = [[UIAlertView alloc] initWithTitle:DPIRLocalizedString(bundle, titleKey)
                                 message:DPIRLocalizedString(bundle, messageKey)
                                delegate:delegate
                       cancelButtonTitle:nil
                       otherButtonTitles:DPIRLocalizedString(bundle, closeButtonKey), nil];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [_self stopLoading];
        [alert show];
    });
    
}

- (void) showNoNetworkError {
    
    [self showAlertWithTileKey:@"AlertTitleError"
                   messsageKey:@"AlertMessageNoNetworkError"
                closeButtonKey:@"AlertBtnClose"
                      delegate:self];
}

- (void) startLoading {
    _indBackView.hidden = NO;
    _indView.hidden = NO;
    [_indView startAnimating];
    [self setScrollEnable:NO];
}

- (void) stopLoading {
    _indBackView.hidden = YES;
    _indView.hidden = YES;
    [_indView stopAnimating];
    [self setScrollEnable:YES];
}

- (void) viewDidLoad {
    [super viewDidLoad];
    _state = DPIRKitSelectionStateIdling;
    _indView.hidden = YES;
    _indBackView.hidden = YES;
}

- (void) viewDidAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    UIApplication *application = [UIApplication sharedApplication];
    
    [nc addObserver:self selector:@selector(enterForground)
               name:UIApplicationWillEnterForegroundNotification
             object:application];
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *clientKey = [ud stringForKey:DPIRKitUDKeyClientKey];
    
    if (!clientKey) {
        
        DPIRKitReachability *r = [DPIRKitReachability reachabilityForInternetConnection];
        DPIRKitNetworkStatus s = [r currentReachabilityStatus];
        
        if (s == DPIRKitNotReachable) {
            [self showNoNetworkError];
        } else {
            
            [self startLoading];
            __weak typeof(self) _self = self;
            
            [[DPIRKitManager sharedInstance] fetchClientKeyWithCompletion:^(NSString *clientKey, DPIRKitConnectionErrorCode errorCode) {
                
                if (clientKey) {
                    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
                    [ud setObject:clientKey forKey:DPIRKitUDKeyClientKey];
                    [ud synchronize];
                    [_self createNewDeviceWithClientKey:clientKey];
                } else {
                    [_self showNoNetworkError];
                }
            }];
        }
    } else {
        NSString *deviceKey = [ud stringForKey:DPIRKitUDKeyDeviceKey];
        if (!deviceKey) {
            [self startLoading];
            [self createNewDeviceWithClientKey:clientKey];
        }
    }
}

- (void) createNewDeviceWithClientKey:(NSString *)clientKey {
    
    __weak typeof(self) _self = self;
    
    [[DPIRKitManager sharedInstance] createNewDeviceWithClientKey:clientKey
                                                       completion:^(NSString *deviceId, NSString *deviceKey, DPIRKitConnectionErrorCode errorCode)
     {
         if (deviceKey && deviceId) {
             NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
             [ud setValue:deviceKey forKey:DPIRKitUDKeyDeviceKey];
             [ud setValue:deviceId forKey:DPIRKitUDKeyDeviceId];
             [ud synchronize];
             
             @synchronized (_self) {
                 _state = DPIRKitSelectionStateGotDeviceKey;
             }
             
             [_self showAlertWithTileKey:@"AlertTitlePrepared"
                             messsageKey:@"AlertMessagePrepared"
                          closeButtonKey:@"AlertBtnClose"
                                delegate:_self];
             
         } else {
             [_self showNoNetworkError];
         }
     }];
}

- (void) viewDidDisappear:(BOOL)animated {
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    UIApplication *application = [UIApplication sharedApplication];
    [nc removeObserver:self name:UIApplicationWillEnterForegroundNotification object:application];
}

- (void) enterForground {
    
    @synchronized (self) {
        if (_state == DPIRKitSelectionStateWaitingIRKitSSID) {
            __weak typeof(self) _self = self;
            _state = DPIRKitSelectionStateCheckingIRKit;
            [self startLoading];
            [[DPIRKitManager sharedInstance] checkIfCurrentSSIDIsIRKitWithCompletion:
             ^(BOOL isIRKit, NSError *error)
             {
                 if (isIRKit) {
                     
                     @synchronized (_self) {
                         _state = DPIRKitSelectionStateIdling;
                     }
                     [_self showAlertWithTileKey:@"AlertTitlePrepared"
                                     messsageKey:@"AlertMessageIsIRKit"
                                  closeButtonKey:@"AlertBtnClose"
                                        delegate:nil];
                     
                 } else {
                     [_self showAlertWithTileKey:@"AlertTitleError"
                                     messsageKey:@"AlertMessageIsNotIRKit"
                                  closeButtonKey:@"AlertBtnClose"
                                        delegate:_self];
                 }
             }];
            
        }
    }
}

#pragma mark - UIAlertView Delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    
    @synchronized (self) {
        if (_state == DPIRKitSelectionStateGotDeviceKey) {
            _state = DPIRKitSelectionStateWaitingIRKitSSID;
            [self setScrollEnable:NO closeBtn:YES];
        } else if (_state == DPIRKitSelectionStateCheckingIRKit) {
            _state = DPIRKitSelectionStateWaitingIRKitSSID;
            [self setScrollEnable:NO closeBtn:YES];
        } else {
            [self dismissViewControllerAnimated:YES completion:nil];
        }
    }
}

@end
