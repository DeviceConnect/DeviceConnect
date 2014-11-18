//
//  DPPebbleDeviceOrientationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleDeviceOrientationProfile.h"
#import "DPPebbleDevicePlugin.h"
#import "DPPebbleManager.h"
#import "DPPebbleProfileUtil.h"

@interface DPPebbleDeviceOrientationProfile ()
@end

@implementation DPPebbleDeviceOrientationProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
	}
	return self;
	
}


#pragma mark - DConnectDeviceOrientationProfileDelegate

// ondeviceorientationイベント登録リクエストを受け取った
- (BOOL)                        profile:(DConnectDeviceOrientationProfile *)profile
didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
	__block BOOL responseFlg = YES;
	// イベント登録
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:NO callback:^{
		
		// Pebbleに登録
		[[DPPebbleManager sharedManager] registDeviceOrientationEvent:deviceId callback:^(NSError *error) {
			// 登録成功
			// エラーチェック
			[DPPebbleProfileUtil handleErrorNormal:error response:response];
			
		} eventCallback:^(float x, float y, float z, long long t) {
			// イベントコールバック
			// DConnectメッセージ作成
			DConnectMessage *message = [DConnectMessage message];
			DConnectMessage *accelerationIncludingGravity = [DConnectMessage message];
			[DConnectDeviceOrientationProfile setX:x target:accelerationIncludingGravity];
			[DConnectDeviceOrientationProfile setY:y target:accelerationIncludingGravity];
			[DConnectDeviceOrientationProfile setZ:z target:accelerationIncludingGravity];
			
			[DConnectDeviceOrientationProfile setAccelerationIncludingGravity:accelerationIncludingGravity target:message];
			[DConnectDeviceOrientationProfile setInterval:t target:message];
			
			// DConnectにイベント送信
			[DPPebbleProfileUtil sendMessageWithProvider:self.provider
												 profile:DConnectDeviceOrientationProfileName
											   attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation
												deviceID:deviceId
										 messageCallback:^(DConnectMessage *eventMsg)
			 {
				 // イベントにメッセージ追加
				 [DConnectDeviceOrientationProfile setOrientation:message target:eventMsg];
			 } deleteCallback:^
			 {
				 // Pebbleのイベント削除
				 [[DPPebbleManager sharedManager] deleteDeviceOrientationEvent:deviceId callback:^(NSError *error) {
					 if (error) NSLog(@"Error:%@", error);
				 }];
			 }];
		}];
		
		responseFlg = NO;
	}];
	
	return responseFlg;
}

// ondeviceorientationイベント解除リクエストを受け取った
- (BOOL)                           profile:(DConnectDeviceOrientationProfile *)profile
didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                                  response:(DConnectResponseMessage *)response
                                  deviceId:(NSString *)deviceId
                                sessionKey:(NSString *)sessionKey
{
	// DConnectイベント削除
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:YES callback:^{
		// Pebbleのイベント削除
		[[DPPebbleManager sharedManager] deleteDeviceOrientationEvent:deviceId callback:^(NSError *error) {
			if (error) NSLog(@"Error:%@", error);
		}];
	}];
	return YES;
}

@end
