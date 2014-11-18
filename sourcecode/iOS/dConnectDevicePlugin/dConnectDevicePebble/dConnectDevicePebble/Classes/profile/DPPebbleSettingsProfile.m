//
//  DPPebbleSettingsProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleSettingsProfile.h"
#import "DPPebbleManager.h"
#import "DPPebbleProfileUtil.h"

@interface DPPebbleSettingsProfile ()
@end


@implementation DPPebbleSettingsProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
	}
	return self;
	
}


#pragma mark - DConnectSettingsProfileDelegate

- (BOOL)         profile:(DConnectSettingsProfile *)profile
didReceiveGetDateRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
	[[DPPebbleManager sharedManager] fetchDate:deviceId callback:^(NSString *date, NSError *error) {
		
		// エラーチェック
		if ([DPPebbleProfileUtil handleError:error response:response]) {
			if (date) {
				[DConnectSettingsProfile setDate:date target:response];
				[response setResult:DConnectMessageResultTypeOk];
			} else {
				[response setErrorToUnknown];
			}
		}
		
		// レスポンスを返却
		[[DConnectManager sharedManager] sendResponse:response];
	}];
	return NO;
}

@end
