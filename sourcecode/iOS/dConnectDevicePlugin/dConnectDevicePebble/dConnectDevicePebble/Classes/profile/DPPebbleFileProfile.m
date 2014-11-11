//
//  DPPebbleFileProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleFileProfile.h"
#import "DPPebbleManager.h"
#import "DPPebbleImage.h"
#import "DPPebbleProfileUtil.h"

@interface DPPebbleFileProfile ()
@end

@implementation DPPebbleFileProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
	}
	return self;
}


#pragma mark - DConnectFileProfileDelegate

// ファイル送信リクエストを受け取った
- (BOOL) profile:(DConnectFileProfile *)profile didReceivePostSendRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
            path:(NSString *)path
        mimeType:(NSString *)mimeType
            data:(NSData *)data
{
	// パラメータチェック
	if (data == nil) {
		[response setErrorToInvalidRequestParameterWithMessage:@"data is not specied to update a file."];
		return YES;
	}
	
	// 画像変換
	NSData *imgdata = [DPPebbleImage convertImage:data];
	if (!imgdata) {
		[response setErrorToUnknown];
		return YES;
	}
	
	[[DPPebbleManager sharedManager] sendImage:deviceId data:imgdata callback:^(NSError *error) {
		// エラーチェック
		[DPPebbleProfileUtil handleErrorNormal:error response:response];
	}];
	return NO;
}

@end
