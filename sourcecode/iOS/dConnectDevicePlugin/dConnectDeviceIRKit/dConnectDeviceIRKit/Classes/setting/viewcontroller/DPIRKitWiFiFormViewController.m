//
//  DPIRKitWiFiFormViewController.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/23.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitWiFiFormViewController.h"
#import "DPIRKit_irkit.h"
#import "DPIRKitConst.h"
#import "DPIRKitWiFiUtil.h"

@interface DPIRKitWiFiFormViewController ()

@property (weak, nonatomic) IBOutlet UITextField *ssidField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;
@property (weak, nonatomic) IBOutlet UISegmentedControl *secTypeFiled;

@end

@implementation DPIRKitWiFiFormViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    NSString *currentSSID = [DPIRKitWiFiUtil currentSSID];
    NSString *defaultSSID = (currentSSID != nil) ? currentSSID : @"";
    
    // 保存してある値を設定
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    [ud registerDefaults:@{
                           DPIRKitUDKeySecType: @(DPIRKitWiFiSecurityTypeWPA2),
                           DPIRKitUDKeySSID: defaultSSID
                           }
     ];
    
    _ssidField.text = [ud stringForKey:DPIRKitUDKeySSID];
    _passwordField.text = [ud stringForKey:DPIRKitUDKeyPassword];
    switch ([ud integerForKey:DPIRKitUDKeySecType]) {
        case DPIRKitWiFiSecurityTypeNone:
            _secTypeFiled.selectedSegmentIndex = 0;
            break;
        case DPIRKitWiFiSecurityTypeWEP:
            _secTypeFiled.selectedSegmentIndex = 1;
            break;
        default:
            _secTypeFiled.selectedSegmentIndex = 2;
            break;
    }
}

- (void) viewWillDisappear:(BOOL)animated
{
    // 値を保存
    NSString *ssid = _ssidField.text ? _ssidField.text : @"";
    NSString *password = _passwordField.text ? _passwordField.text : @"";
    DPIRKitWiFiSecurityType type;
    switch (_secTypeFiled.selectedSegmentIndex) {
        case 2:
            type = DPIRKitWiFiSecurityTypeWPA2;
            break;
        case 1:
            type = DPIRKitWiFiSecurityTypeWEP;
            break;
        case 0:
        default:
            type = DPIRKitWiFiSecurityTypeNone;
            break;
    }

    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    [ud setObject:ssid forKey:DPIRKitUDKeySSID];
    [ud setInteger:type forKey:DPIRKitUDKeySecType];
    [ud setObject:password forKey:DPIRKitUDKeyPassword];
    [ud synchronize];
}


-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

@end
