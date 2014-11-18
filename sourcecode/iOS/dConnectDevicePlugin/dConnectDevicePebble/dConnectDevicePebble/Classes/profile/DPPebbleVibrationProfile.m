//
//  DPPebbleVibrationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleVibrationProfile.h"
#import "DPPebbleManager.h"
#import "DPPebbleProfileUtil.h"


@interface DPPebbleVibrationProfile ()
@end


@implementation DPPebbleVibrationProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
	}
	return self;
	
}


#pragma mark - DConnectVibrationProfileDelegate

// バイブ鳴動開始リクエストを受け取った
- (BOOL)            profile:(DConnectVibrationProfile *)profile
didReceivePutVibrateRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                    pattern:(NSArray *) pattern
{
	// Pebbleに通知
	[[DPPebbleManager sharedManager] startVibration:deviceId
											pattern:pattern
										   callback:^(NSError *error)
	{
		// エラーチェック
		[DPPebbleProfileUtil handleErrorNormal:error response:response];
	}];
	return NO;
}

// バイブ鳴動停止リクエストを受け取った
- (BOOL)               profile:(DConnectVibrationProfile *)profile
didReceiveDeleteVibrateRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
{
	// Pebbleに通知
	[[DPPebbleManager sharedManager] stopVibration:deviceId
										  callback:^(NSError *error)
	 {
		 // エラーチェック
		 [DPPebbleProfileUtil handleErrorNormal:error response:response];
	 }];
	return NO;
}

@end
