//
//  DPIRKitConnectionGuideViewController.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/21.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitConnectionGuideViewController.h"
#import "DPIRKit_irkit.h"
#import "DPIRKitWiFiUtil.h"
#import "DPIRKitConst.h"

typedef NS_ENUM(NSUInteger, DPIRKitConnectionState) {
    DPIRKitConnectionStateIdling = 0,
    DPIRKitConnectionStateConnectingToIRKit,
    DPIRKitConnectionStateWaitingForLAN,
    DPIRKitConnectionStateConnected,
};

@interface DPIRKitConnectionGuideViewController ()<UIAlertViewDelegate>
{
    DPIRKitConnectionState _state;
    NSString *_deviceId;
    NSString *_deviceKey;
    NSString *_clientKey;
    NSBundle *_bundle;
}

@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *indView;
- (IBAction)sendButtonPressed:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *sendButton;
@property (weak, nonatomic) IBOutlet UIView *indBackView;


- (void) startLoading;
- (void) stopLoading;

- (void) showAlertWithTileKey:(NSString *)titleKey
                  messsageKey:(NSString *)messageKey
               closeButtonKey:(NSString *)closeButtonKey
                     delegate:(id<UIAlertViewDelegate>)delegate;
- (void) enterForeground;

@end

@implementation DPIRKitConnectionGuideViewController


- (void)viewDidLoad
{
    [super viewDidLoad];
    _indBackView.hidden = YES;
    _indView.hidden = YES;
    _state = DPIRKitConnectionStateIdling;
    _bundle = DPIRBundle();
}

// 送信ボタンイベント
- (IBAction)sendButtonPressed:(id)sender
{
    _sendButton.enabled = NO;
    __weak typeof(self) _self = self;
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *ssid = [ud stringForKey:DPIRKitUDKeySSID];
    DPIRKitWiFiSecurityType type = [ud integerForKey:DPIRKitUDKeySecType];
    NSString *password = [ud stringForKey:DPIRKitUDKeyPassword];
    
    @synchronized (self) {
        _state = DPIRKitConnectionStateConnectingToIRKit;
    }
    
    [self startLoading];
    [[DPIRKitManager sharedInstance] connectIRKitToWiFiWithSSID:ssid
                                                       password:password
                                                   securityType:type
                                                      deviceKey:_deviceKey
                                                     completion:
     ^(BOOL success, DPIRKitConnectionErrorCode errorCode) {
         @synchronized (_self) {
             if (success) {
                 [_self showAlertWithTileKey:@"AlertTitleConnection"
                                 messsageKey:@"AlertMessageConnectedWithWiFi"
                              closeButtonKey:@"AlertBtnClose"
                                    delegate:_self];
             } else {
                 _state = DPIRKitConnectionStateIdling;
                 [_self showAlertWithTileKey:@"AlertTitleError"
                                 messsageKey:@"AlertMessageNetworkError"
                              closeButtonKey:@"AlertBtnClose"
                                    delegate:_self];
             }
         }
     }];
    
}

- (void) viewDidAppear:(BOOL)animated {
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    _deviceId = [ud stringForKey:DPIRKitUDKeyDeviceId];
    _deviceKey = [ud stringForKey:DPIRKitUDKeyDeviceKey];
    _clientKey = [ud stringForKey:DPIRKitUDKeyClientKey];
    
    UIApplication *application = [UIApplication sharedApplication];
    [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(enterForeground)
                                                name:UIApplicationWillEnterForegroundNotification
                                              object:application];
}

- (void) viewDidDisappear:(BOOL)animated {
    UIApplication *application = [UIApplication sharedApplication];
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    [nc removeObserver:self name:UIApplicationWillEnterForegroundNotification object:application];
}

- (void) enterForeground {
    
    @synchronized (self) {
        if (_state == DPIRKitConnectionStateWaitingForLAN) {
            __weak typeof(self) _self = self;
            [self startLoading];
            [[DPIRKitManager sharedInstance]
             checkIfIRKitIsConnectedToInternetWithClientKey:_clientKey
             deviceId:_deviceId
             completion:^(BOOL isConnected) {
                 if (isConnected) {
                     _state = DPIRKitConnectionStateConnected;
                     [_self showAlertWithTileKey:@"AlertTitleConnection"
                                     messsageKey:@"AlertMessageConnectedSuccess"
                                  closeButtonKey:@"AlertBtnClose"
                                        delegate:_self];
                     
                 } else {
                     _state = DPIRKitConnectionStateIdling;
                     [_self showAlertWithTileKey:@"AlertTitleError"
                                     messsageKey:@"AlertMessageNetworkError"
                                  closeButtonKey:@"AlertBtnClose"
                                        delegate:_self];
                 }
                 
             }];
        }
    }
    
}

#pragma mark - Private Methods

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

- (void) showAlertWithTileKey:(NSString *)titleKey
                  messsageKey:(NSString *)messageKey
               closeButtonKey:(NSString *)closeButtonKey
                     delegate:(id<UIAlertViewDelegate>)delegate
{
    UIAlertView *alert
    = [[UIAlertView alloc] initWithTitle:DPIRLocalizedString(_bundle, titleKey)
                                 message:DPIRLocalizedString(_bundle, messageKey)
                                delegate:delegate
                       cancelButtonTitle:nil
                       otherButtonTitles:DPIRLocalizedString(_bundle, closeButtonKey), nil];
    
    __weak typeof(self) _self = self;
    dispatch_async(dispatch_get_main_queue(), ^{
        [_self stopLoading];
        [alert show];
    });
    
}

#pragma mark - UIAlertView Delegate

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    @synchronized (self) {
        if (_state == DPIRKitConnectionStateConnectingToIRKit) {
            _state = DPIRKitConnectionStateWaitingForLAN;
        } else if (_state == DPIRKitConnectionStateConnected) {
            [self dismissViewControllerAnimated:YES completion:^{
                [[DPIRKitManager sharedInstance] stopDetection];
                [[DPIRKitManager sharedInstance] startDetection];
            }];
        } else {
            _sendButton.enabled = YES;
        }
    }
}

@end
