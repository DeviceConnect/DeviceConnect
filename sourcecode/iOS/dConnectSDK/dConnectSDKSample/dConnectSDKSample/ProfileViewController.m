//
//  ProfileViewController.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "ProfileViewController.h"
#import <DConnectSDK/DConnectSDK.h>
#import "ResultViewController.h"
#import "Utils.h"

@interface ProfileViewController ()<UIPickerViewDataSource, UIPickerViewDelegate>
{
    UIPickerView *_picker;
    BOOL _pickerShowing;
    BOOL _pickerAnimating;
    NSArray *_actions;
    int _actionIndex;
    NSString *_result;
}

- (void) close;


@property (weak, nonatomic) IBOutlet UITextField *interfaceText;
@property (weak, nonatomic) IBOutlet UITextField *attrText;
@property (weak, nonatomic) IBOutlet UITextView *paramText;
@property (weak, nonatomic) IBOutlet UIButton *selectBtn;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *indView;
@property (weak, nonatomic) IBOutlet UITextView *eventErea;

- (DConnectRequestMessage *) createRequest;

@end

@implementation ProfileViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    for (id view in self.view.subviews) {
        if ([view isKindOfClass:[UIButton class]]) {
            UIButton *btn = (UIButton *) view;
            btn.layer.borderColor = [UIColor grayColor].CGColor;
            btn.layer.borderWidth = 1.0f;
            btn.layer.cornerRadius = 7.5f;
        }
    }
    
    _actionIndex = 0;
    _pickerAnimating = NO;
    _pickerShowing = NO;
    _picker = [[UIPickerView alloc] init];
    _picker.frame = CGRectMake(0, self.view.bounds.size.height + 216, self.view.bounds.size.width, 216);
    _picker.showsSelectionIndicator = YES;
    _picker.backgroundColor = [UIColor grayColor];
    _picker.delegate = self;
    _picker.dataSource = self;
    [self.view addSubview:_picker];
    
    _eventErea.layer.borderWidth = 1;
    _eventErea.layer.borderColor = [[UIColor blackColor] CGColor];
    
    self.title = _profile;
    _indView.hidden = YES;
    _paramText.layer.borderWidth = 1;
    _paramText.layer.borderColor = [[UIColor blackColor] CGColor];
    _paramText.text = [NSString stringWithFormat:@"deviceId=%@", _deviceId];
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *clientId = [ud stringForKey:UD_KEY_CLIENT_ID];
    
    if (clientId) {
        _paramText.text = [_paramText.text stringByAppendingString:
                           [NSString stringWithFormat:@"&sessionKey=%@", clientId]];
    }
    
    UITapGestureRecognizer *gestureRecognizer = [[UITapGestureRecognizer alloc] initWithTarget:self
                                                                                        action:@selector(close)];
    gestureRecognizer.cancelsTouchesInView = NO;
    [self.view addGestureRecognizer:gestureRecognizer];
    
    _actions = [NSArray arrayWithObjects:@"GET", @"POST", @"PUT", @"DELETE", nil];
    
    [_selectBtn setTitle:[_actions objectAtIndex:0] forState:UIControlStateNormal];
    
}

- (IBAction)selectDidPushed:(id)sender {
    
    if (_pickerAnimating || _pickerShowing) {
        return;
    }
    
    _pickerAnimating = YES;
    [UIView animateWithDuration:0.3 animations:^{
        _picker.frame = CGRectMake(0, self.view.bounds.size.height - 216, self.view.bounds.size.width, 216);
    } completion:^(BOOL finished) {
        _pickerAnimating = NO;
        _pickerShowing = YES;
    }];
    
}

- (DConnectRequestMessage *) createRequest {
    NSString *interface = _interfaceText.text;
    NSString *attribute = _attrText.text;
    NSString *params = _paramText.text;
    
    DConnectRequestMessage *req = [DConnectRequestMessage new];
    req.profile = _profile;
    req.interface = interface;
    req.attribute = attribute;
    req.action = _actionIndex;
    
    if (params && params.length > 0) {
        NSArray *paramSets = [params componentsSeparatedByString:@"&"];
        if (paramSets) {
            for (NSString *paramSet in paramSets) {
                NSArray *keyValue = [paramSet componentsSeparatedByString:@"="];
                if (keyValue.count == 2) {
                    NSString *key = [keyValue objectAtIndex:0];
                    NSString *value = [keyValue objectAtIndex:1];
                    [req setString:value forKey:key];
                }
            }
        }
    }
    
    return req;
}

- (IBAction)sendEvent:(id)sender {
    
    DConnectRequestMessage *req = [self createRequest];
    
    __block typeof(self) _self = self;
    void (^exeReq)(void) = ^() {
        if (req.action == DConnectMessageActionTypePut) {
            [[DConnectEventHelper sharedHelper] registerEventWithRequest:req
                                                         responseHandler:^(DConnectResponseMessage *response)
             {
                 
                 dispatch_async(dispatch_get_main_queue(), ^{
                     if (response.result == DConnectMessageResultTypeError) {
                         _result = [response convertToJSONString];
                         [_self performSegueWithIdentifier:@"ResultSegue" sender:nil];
                     } else {
                         _self.eventErea.text =
                         [_self.eventErea.text stringByAppendingString:@"\n ** Event Registered ** "];
                     }
                 });
             } messageHandler:^(DConnectMessage *message) {
                 dispatch_async(dispatch_get_main_queue(), ^{
                     NSMutableString *text = [NSMutableString stringWithString:@"\n============================\n"];
                     [text appendString:[message convertToJSONString]];
                     _self.eventErea.text = [_self.eventErea.text stringByAppendingString:text];
                 });
             }];
        } else if (req.action == DConnectMessageActionTypeDelete) {
            [[DConnectEventHelper sharedHelper] unregisterEventWithRequest:req
                                                           responseHandler:^(DConnectResponseMessage *response)
             {
                 _result = [response convertToJSONString];
                 dispatch_async(dispatch_get_main_queue(), ^{
                     [_self performSegueWithIdentifier:@"ResultSegue" sender:nil];
                 });
             }];
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"エラー"
                                                            message:@"イベントはPUT、DELETEのみ有効です。"
                                                           delegate:nil
                                                  cancelButtonTitle:nil
                                                  otherButtonTitles:@"閉じる", nil];
            [alert show];
        }
    };
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [ud stringForKey:UD_KEY_ACCESS_TOKEN];
    
    if (!accessToken) {
        [Utils authorizeOrRefreshTokenWithForceRefresh:YES success:^(NSString *clientId, NSString *clientSecret, NSString *accessToken) {
            dispatch_async(dispatch_get_main_queue(), ^{
                req.accessToken = accessToken;
                exeReq();
            });
        } error:^(DConnectMessageErrorCodeType errorCode) {
            dispatch_async(dispatch_get_main_queue(), ^{
                int code = errorCode;
                
                if (code == DConnectMessageErrorCodeTimeout) {
                    [_self dismissViewControllerAnimated:YES completion:nil];
                }
                
                NSString *message = @"認証に失敗しました";
                
                UIAlertView *alert = [[UIAlertView alloc]
                                      initWithTitle:@"エラーが発生しました"
                                      message:[NSString stringWithFormat:@"#%d : %@", code, message]
                                      delegate:nil
                                      cancelButtonTitle:nil
                                      otherButtonTitles:@"閉じる", nil];
                [alert show];
            });
            
        }];
        
    } else {
        req.accessToken = accessToken;
        exeReq();
    }
}

- (IBAction)sendDidPushed:(id)sender {
    
    __block typeof(self) _self = self;
    DConnectRequestMessage *req = [self createRequest];
    
    void (^exeReq)(void) = ^() {
        [[DConnectManager sharedManager] sendRequest:req
                                            callback:^(DConnectResponseMessage *response)
         {
             _result = [response convertToJSONString];
             dispatch_async(dispatch_get_main_queue(), ^{
                 [_self performSegueWithIdentifier:@"ResultSegue" sender:nil];
             });
         }];
        
    };
    
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [ud stringForKey:UD_KEY_ACCESS_TOKEN];
    
    if (!accessToken) {
        [Utils authorizeOrRefreshTokenWithForceRefresh:YES success:^(NSString *clientId, NSString *clientSecret, NSString *accessToken) {
            dispatch_async(dispatch_get_main_queue(), ^{
                req.accessToken = accessToken;
                exeReq();
            });
        } error:^(DConnectMessageErrorCodeType errorCode) {
            dispatch_async(dispatch_get_main_queue(), ^{
                int code = errorCode;
                
                if (code == DConnectMessageErrorCodeTimeout) {
                    [_self dismissViewControllerAnimated:YES completion:nil];
                }
                
                NSString *message = @"認証に失敗しました";
                
                UIAlertView *alert = [[UIAlertView alloc]
                                      initWithTitle:@"エラーが発生しました"
                                      message:[NSString stringWithFormat:@"#%d : %@", code, message]
                                      delegate:nil
                                      cancelButtonTitle:nil
                                      otherButtonTitles:@"閉じる", nil];
                [alert show];
            });
            
        }];
        
    } else {
        req.accessToken = accessToken;
        exeReq();
    }
}

// キーボードを隠す処理
- (void) close {
    
    if (_pickerAnimating) {
        return;
    }
    
    if (_pickerShowing) {
        _pickerShowing = NO;
        _pickerAnimating = YES;
        int index = [_picker selectedRowInComponent:0];
        _actionIndex = index;
        [_selectBtn setTitle:[_actions objectAtIndex:index] forState:UIControlStateNormal];
        [UIView animateWithDuration:0.3 animations:^{
            _picker.frame = CGRectMake(0, self.view.bounds.size.height + 216, self.view.bounds.size.width, 216);
        } completion:^(BOOL finished) {
            _pickerAnimating = NO;
        }];
    }
    
    [self.view endEditing:YES];
}

- (void) dealloc {
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    NSString *accessToken = [ud stringForKey:UD_KEY_ACCESS_TOKEN];
    [[DConnectEventHelper sharedHelper] unregisterAllEventsWithAccessToken:accessToken];
}

#pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return 4;
}

#pragma mark - UIPickerViewDelegate

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    return [_actions objectAtIndex:row];
}

#pragma mark - UIViewController Override

- (void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ([segue.identifier isEqualToString:@"ResultSegue"]) {
        ResultViewController *controller = (ResultViewController *) [segue destinationViewController];
        controller.json = _result;
    }
}


@end
