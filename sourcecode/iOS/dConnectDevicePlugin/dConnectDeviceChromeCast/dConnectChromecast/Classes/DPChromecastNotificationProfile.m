//
//  DPChromecastNotificationProfile.m
//  dConnectChromecast
//
//  Created by Ryuya Takahashi on 2014/09/08.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPChromecastDevicePlugin.h"
#import "DPChromecastNotificationProfile.h"
#import "DPChromecastManager.h"



@interface DPChromecastNotificationProfile ()
@end


@implementation DPChromecastNotificationProfile

// 初期化
- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

// 共通リクエスト処理
- (BOOL)handleRequest:(DConnectRequestMessage *)request
             response:(DConnectResponseMessage *)response
             deviceId:(NSString *)deviceId
             callback:(void(^)())callback
{
    // パラメータチェック
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }
    
    // 接続＆メッセージクリア
    DPChromecastManager *mgr = [DPChromecastManager sharedManager];
    [mgr connectToDeviceWithID:deviceId completion:^(BOOL success, NSString *error) {
        if (success) {
            callback();
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            // エラー
            //NSLog(@"error:%@", error);
            [response setErrorToNotFoundDevice];
        }
        [[DConnectManager sharedManager] sendResponse:response];
    }];
    return NO;
}



#pragma mark - Post Methods

// ノーティフィケーションの表示リクエストを受け取った
- (BOOL)            profile:(DConnectNotificationProfile *)profile
didReceivePostNotifyRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                       type:(NSNumber *)type
                        dir:(NSString *)dir
                       lang:(NSString *)lang
                       body:(NSString *)body
                        tag:(NSString *)tag
                       icon:(NSData *)icon
{
    // パラメータチェック
    if (!type || type<0 || 2<type) {
        [response setErrorToInvalidRequestParameterWithMessage:@"type is null or invalid"];
//        [response setString:@"DevicePlugin" forKey:@"debug"];
        return YES;
    }
    if (!body) {
        [response setErrorToInvalidRequestParameterWithMessage:@"body is null"];
//        [response setString:@"DevicePlugin" forKey:@"debug"];
        return YES;
    }
    
    // リクエスト処理
    return [self handleRequest:request
                      response:response
                      deviceId:deviceId
                      callback:
            ^{
                // メッセージ送信
                DPChromecastManager *mgr = [DPChromecastManager sharedManager];
                [mgr sendMessage:body type:[type intValue]];
            }];
}


#pragma mark - Delete Methods

// ノーティフィケーションの削除リクエストを受け取った
- (BOOL)              profile:(DConnectNotificationProfile *)profile
didReceiveDeleteNotifyRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
               notificationId:(NSString *)notificationId
{
    // リクエスト処理
    return [self handleRequest:request
                      response:response
                      deviceId:deviceId
                      callback:
            ^{
                // メッセージクリア
                DPChromecastManager *mgr = [DPChromecastManager sharedManager];
                [mgr clearMessage];
            }];
}

@end

