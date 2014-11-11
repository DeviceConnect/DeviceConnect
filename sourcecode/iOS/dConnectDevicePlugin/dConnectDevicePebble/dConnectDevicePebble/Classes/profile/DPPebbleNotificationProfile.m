//
//  DPPebbleNotificationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//


#import "DPPebbleNotificationProfile.h"

@interface DPPebbleNotificationProfile ()
@end

@implementation DPPebbleNotificationProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
		// 通知許可を得る
		UIApplication *application = [UIApplication sharedApplication];
		if ([application respondsToSelector:@selector(registerUserNotificationSettings:)]) {
			UIUserNotificationSettings *settings = [UIUserNotificationSettings settingsForTypes:UIUserNotificationTypeAlert|UIUserNotificationTypeSound categories:nil];
			[application registerUserNotificationSettings:settings];
		}
	}
	return self;
}


#pragma mark - DConnectNotificationProfileDelegate

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
	// LocalNotificationを発動させるとPebbleのNotificationに通知が行く
	// FIXME:この方式だとDeviceID無視で全デバイスに通知が行ってしまう。
	UILocalNotification *notify = [[UILocalNotification alloc] init];
	notify.fireDate = [NSDate new];
	notify.alertBody = body.length>0 ? body : @" ";
	[[UIApplication sharedApplication] scheduleLocalNotification:notify];

	//IDは取得できないので、処理のしようがない
	[DConnectNotificationProfile setNotificationId:@"0" target:response];
	[response setResult:DConnectMessageResultTypeOk];
	
    return YES;
}

@end
