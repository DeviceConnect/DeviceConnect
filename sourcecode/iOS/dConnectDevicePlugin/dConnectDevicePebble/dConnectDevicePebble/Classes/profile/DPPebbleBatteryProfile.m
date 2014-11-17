//
//  DPPebbleBatteryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleBatteryProfile.h"
#import "DPPebbleDevicePlugin.h"
#import "DPPebbleManager.h"
#import "DPPebbleProfileUtil.h"

@interface DPPebbleBatteryProfile ()
@end

@implementation DPPebbleBatteryProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
	}
	return self;
	
}


#pragma mark - DConnectBatteryProfileDelegate

// 全アトリビュート取得リクエストを受け取った
- (BOOL)        profile:(DConnectBatteryProfile *)profile
didReceiveGetAllRequest:(DConnectRequestMessage *)request
               response:(DConnectResponseMessage *)response
               deviceId:(NSString *)deviceId
{
	[[DPPebbleManager sharedManager] fetchBatteryInfo:deviceId callback:^(float level, BOOL isCharging, NSError *error) {
		
		// エラーチェック
		if ([DPPebbleProfileUtil handleError:error response:response]) {
			// レベルとステータスを設定
			[DConnectBatteryProfile setLevel:level target:response];
			[DConnectBatteryProfile setCharging:isCharging target:response];
			// 正常
			[response setResult:DConnectMessageResultTypeOk];
		}
		
		// レスポンスを返却
		[[DConnectManager sharedManager] sendResponse:response];
	}];
	return NO;
}

// level取得リクエストを受け取った
- (BOOL)          profile:(DConnectBatteryProfile *)profile
didReceiveGetLevelRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
	[[DPPebbleManager sharedManager] fetchBatteryLevel:deviceId callback:^(float level, NSError *error) {
		
		// エラーチェック
		if ([DPPebbleProfileUtil handleError:error response:response]) {
			// レベルを設定
			[DConnectBatteryProfile setLevel:level target:response];
			// 正常
			[response setResult:DConnectMessageResultTypeOk];
		}
		
		// レスポンスを返却
		[[DConnectManager sharedManager] sendResponse:response];
	}];
	return NO;
}

// charging取得リクエストを受け取った
- (BOOL)             profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
{
	[[DPPebbleManager sharedManager] fetchBatteryCharging:deviceId callback:^(BOOL isCharging, NSError *error) {
		
		// エラーチェック
		if ([DPPebbleProfileUtil handleError:error response:response]) {
			// ステータスを設定
			[DConnectBatteryProfile setCharging:isCharging target:response];
			// 正常
			[response setResult:DConnectMessageResultTypeOk];
		}
		
		// レスポンスを返却
		[[DConnectManager sharedManager] sendResponse:response];
	}];
	return NO;
}


#pragma mark - Put Methods

// onchargingchangeイベント登録リクエストを受け取った
- (BOOL)                     profile:(DConnectBatteryProfile *)profile
didReceivePutOnChargingChangeRequest:(DConnectRequestMessage *)request
                            response:(DConnectResponseMessage *)response
                            deviceId:(NSString *)deviceId
                          sessionKey:(NSString *)sessionKey
{
	__block BOOL responseFlg = YES;
	// イベント登録
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:NO callback:^{
		
		// Pebbleに登録
		[[DPPebbleManager sharedManager] registChargingChangeEvent:deviceId callback:^(NSError *error) {
			// 登録成功
			// エラーチェック
			[DPPebbleProfileUtil handleErrorNormal:error response:response];
			
		} eventCallback:^(BOOL isCharging) {
			// イベントコールバック
			// DConnectイベント作成
			DConnectMessage *message = [DConnectMessage message];
			[DConnectBatteryProfile setCharging:isCharging target:message];
			
			// DConnectにイベント送信
			[DPPebbleProfileUtil sendMessageWithProvider:self.provider
												 profile:DConnectBatteryProfileName
											   attribute:DConnectBatteryProfileAttrOnChargingChange
												deviceID:deviceId
										 messageCallback:^(DConnectMessage *eventMsg)
			 {
				 // イベントにメッセージ追加
				 [DConnectBatteryProfile setBattery:message target:eventMsg];
				 
			 } deleteCallback:^
			 {
				 // Pebbleのイベント削除
				 [[DPPebbleManager sharedManager] deleteChargingChangeEvent:deviceId callback:^(NSError *error) {
					 if (error) NSLog(@"Error:%@", error);
				 }];
			 }];
		}];
		
		responseFlg = NO;
	}];
	
	return responseFlg;
}

// onbatterychangeイベント登録リクエストを受け取った
- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
	__block BOOL responseFlg = YES;
	// イベント登録
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:NO callback:^{
		
		// Pebbleに登録
		[[DPPebbleManager sharedManager] registBatteryLevelChangeEvent:deviceId callback:^(NSError *error) {
			// 登録成功
			// エラーチェック
			[DPPebbleProfileUtil handleErrorNormal:error response:response];
			
		} eventCallback:^(float level) {
			// イベントコールバック
			// DConnectイベント作成
			DConnectMessage *message = [DConnectMessage message];
			[DConnectBatteryProfile setLevel:level target:message];
			
			// DConnectにイベント送信
			[DPPebbleProfileUtil sendMessageWithProvider:self.provider
												 profile:DConnectBatteryProfileName
											   attribute:DConnectBatteryProfileAttrOnBatteryChange
												deviceID:deviceId
										 messageCallback:^(DConnectMessage *eventMsg)
			 {
				 // イベントにメッセージ追加
				 [DConnectBatteryProfile setBattery:message target:eventMsg];
				 
			 } deleteCallback:^
			 {
				 // Pebbleのイベント削除
				 [[DPPebbleManager sharedManager] deleteBatteryLevelChangeEvent:deviceId callback:^(NSError *error) {
					 if (error) NSLog(@"Error:%@", error);
				 }];
			 }];
		}];
		
		responseFlg = NO;
	}];
	
	return responseFlg;
}

#pragma mark - Delete Methods

// onchargingchangeイベント解除リクエストを受け取った
- (BOOL)                        profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
	// DConnectイベント削除
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:YES callback:^{
		// Pebbleのイベント削除
		[[DPPebbleManager sharedManager] deleteChargingChangeEvent:deviceId callback:^(NSError *error) {
			if (error) NSLog(@"Error:%@", error);
		}];
	}];
	return YES;
}

// onbatterychangeイベント解除リクエストを受け取った
- (BOOL)                       profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
	// DConnectイベント削除
	[DPPebbleProfileUtil handleRequest:request response:response isRemove:YES callback:^{
		// Pebbleのイベント削除
		[[DPPebbleManager sharedManager] deleteBatteryLevelChangeEvent:deviceId callback:^(NSError *error) {
			if (error) NSLog(@"Error:%@", error);
		}];
	}];
	return YES;
}

@end
