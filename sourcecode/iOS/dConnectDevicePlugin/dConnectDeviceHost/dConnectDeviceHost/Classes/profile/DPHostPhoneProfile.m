//
//  DPHostPhoneProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>
#import <CoreTelephony/CTCallCenter.h>
#import <CoreTelephony/CTCall.h>
#import <CoreTelephony/CTCarrier.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>

#import "DPHostDevicePlugin.h"
#import "DPHostPhoneProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"

@interface DPHostPhoneProfile()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

// 通話イベントを処理するオブジェクト
@property CTCallCenter *callCenter;

//　通話中の番号
@property (nonatomic) NSString *callingNumber;

@end

@implementation DPHostPhoneProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        if (![[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"tel://"]]) {
            // 通話がサポートされていない
            return nil;
        }
        
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];

        // 通話イベントのハンドラを準備
        _callCenter = [CTCallCenter new];
        __weak typeof(self) weakSelf = self;
        _callCenter.callEventHandler = ^(CTCall *call) {
            DConnectPhoneProfileCallState callState;
            if (call.callState == CTCallStateDialing) {
                // NSLog(@"Dialing");
                return;
            }
            else if(call.callState == CTCallStateIncoming) {
                // NSLog(@"Incoming");
                return;
            }
            else if(call.callState == CTCallStateConnected) {
                // NSLog(@"Connected");
                callState = DConnectPhoneProfileCallStateStart;
            }
            else if(call.callState == CTCallStateDisconnected) {
                // NSLog(@"Disconnected");
                callState = DConnectPhoneProfileCallStateFinished;
            }
            else {
                // NSLog(@"Unknown");
                return;
            }
            
            // イベントの取得
            NSArray *evts = [weakSelf.eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                                    profile:DConnectPhoneProfileName
                                                  attribute:DConnectPhoneProfileAttrOnConnect];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                DConnectMessage *phoneStatus = [DConnectMessage message];
                [DConnectPhoneProfile setPhoneNumber:weakSelf.callingNumber target:phoneStatus];
                [DConnectPhoneProfile setState:callState target:phoneStatus];
                [DConnectPhoneProfile setPhoneStatus:phoneStatus target:eventMsg];
                
                [SELF_PLUGIN sendEvent:eventMsg];
            }
        };
    }
    return self;
}

- (void)dealloc
{
    _callCenter.callEventHandler = nil;
}

#pragma mark - Post Methods

- (BOOL)          profile:(DConnectPhoneProfile *)profile
didReceivePostCallRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
              phoneNumber:(NSString *)phoneNumber
{
    if (!phoneNumber) {
        [response setErrorToInvalidRequestParameterWithMessage:@"phoneNumber must be specified."];
        return YES;
    }
    
    // 電話をかける内部的な準備が整っているかのチェック（電波の強さ等外部的な要因はチェックする範疇じゃない）
    CTTelephonyNetworkInfo *netInfo = [CTTelephonyNetworkInfo new];
    CTCarrier *carrier = [netInfo subscriberCellularProvider];
    NSString *mnc = [carrier mobileNetworkCode];
    if (([mnc length] == 0) || ([mnc isEqualToString:@"65535"])) {
        // 移動体通信事業者の情報取得で不備あり；電話をかける事ができない。
        [response setErrorToIllegalDeviceStateWithMessage:@"Mobile Network Code is invalid; check your SIM card or signal reception."];
        return YES;
    }
    
    // telpromptスキームが公式なのか、ずっと存続するのかよくわからないので、使えない場合はtelスキームを使う。
    // telprompt: 電話をかける前にユーザ確認ダイアログが表示される。了承して初めて電話をかける。
    // tel: 問答無用で電話をいきなりかける。
    // MARK: telスキームもUIAlertView/UIAlertControllerと組み合わせればtelpromptスキームを実現できると思った。
    NSArray *telSchemeArr = @[@"telprompt", @"tel"];
    for (NSString *telScheme in telSchemeArr) {
        NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@:%@", telScheme, phoneNumber]];
        UIApplication *app = [UIApplication sharedApplication];
        if ([app canOpenURL:url]) {
            if ([app openURL:url]) {
                _callingNumber = phoneNumber;
                [response setResult:DConnectMessageResultTypeOk];
                return YES;
            }
        }
    }
    
    [response setErrorToUnknownWithMessage:@"Failed to make a phone call."];
    return YES;
}


#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL)              profile:(DConnectPhoneProfile *)profile
didReceivePutOnConnectRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
                   sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL)                 profile:(DConnectPhoneProfile *)profile
didReceiveDeleteOnConnectRequest:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response
                        deviceId:(NSString *)deviceId
                      sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}


@end
