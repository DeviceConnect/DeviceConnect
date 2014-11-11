//
//  DPPebbleNotificationProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleNotificationProfile.h"

@interface DPPebbleNotificationProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;
@end


@implementation DPPebbleNotificationProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
    }
    return self;
}



#pragma mark - DConnectNotificationProfileDelegate

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
    NSString* mDeviceId = [self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
    } else if (type == nil) {
        [response setError:10 message:@"Type : UNKNOWN" ];
        return true;
    } else {
        // UILocalNotificationクラスのインスタンスを作成します。
        UILocalNotification *localNotif = [[UILocalNotification alloc] init];
        
        if (localNotif == nil){
            [response setErrorToUnknown];
            return YES;
        }
        if(body == nil||body.length == 0){
            body = @" ";
        }
        // 通知メッセージの本文を指定します。
        localNotif.alertBody = [NSString stringWithFormat:@"%@",body];
        // 通知メッセージアラートのボタンに表示される文字を指定します。
        //        localNotif.alertAction = @"OK";
        
        // 通知を受け取るときに送付される NSDictionary を作成します。
        //    NSDictionary *infoDict = [NSDictionary dictionaryWithObject:notificationMsg forKey:@"EventKey"];
        //    localNotif.userInfo = infoDict;
        
        // 作成した通知イベント情報をアプリケーションに登録します。
        [[UIApplication sharedApplication] scheduleLocalNotification:localNotif];
        
        //IDは取得できないので、処理のしようがない
        [DConnectNotificationProfile setNotificationId:@"0" target:response];
        [response setResult:DConnectMessageResultTypeOk];
    }
    return YES;
}

@end
