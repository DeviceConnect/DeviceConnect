//
//  DPPebbleProfileUtil.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleProfileUtil.h"
#import "DPPebbleDevicePlugin.h"

@implementation DPPebbleProfileUtil

// 共通エラーチェック
+ (BOOL)handleError:(NSError*)error response:(DConnectResponseMessage *)response
{
	if (error) {
		if ([error code] == 403) {
			// 端末が見つからない
			[response setErrorToNotFoundDevice];
		} else {
			// 不明なエラー
			[response setErrorToUnknown];
		}
		return NO;
	}
	return YES;
}

// 通常のエラーチェック
+ (void)handleErrorNormal:(NSError*)error response:(DConnectResponseMessage *)response
{
	// エラーチェック
	if ([DPPebbleProfileUtil handleError:error response:response]) {
		// 正常
		[response setResult:DConnectMessageResultTypeOk];
	}
	
	// レスポンスを返却
	[[DConnectManager sharedManager] sendResponse:response];
}


// 共通イベントリクエスト処理
+ (void)handleRequest:(DConnectRequestMessage *)request
			 response:(DConnectResponseMessage *)response
			 isRemove:(BOOL)isRemove
			 callback:(void(^)())callback
{
	DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
	DConnectEventError error;
	if (isRemove) {
		error = [mgr removeEventForRequest:request];
	} else {
		error = [mgr addEventForRequest:request];
	}
	switch (error) {
		case DConnectEventErrorNone:
			[response setResult:DConnectMessageResultTypeOk];
			callback();
			break;
		case DConnectEventErrorInvalidParameter:
			[response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
			break;
		case DConnectEventErrorFailed:
		case DConnectEventErrorNotFound:
		default:
			[response setErrorToUnknown];
			break;
	}
}

// 共通イベントメッセージ送信
+ (void)sendMessageWithProvider:(id)provider
						profile:(NSString *)profile
					  attribute:(NSString *)attribute
					   deviceID:(NSString*)deviceID
				messageCallback:(void(^)(DConnectMessage *eventMsg))messageCallback
				 deleteCallback:(void(^)())deleteCallback
{
	DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
	NSArray *events  = [mgr eventListForDeviceId:deviceID
										 profile:profile
									   attribute:attribute];
	if (events == 0) {
		deleteCallback();
	}
	for (DConnectEvent *event in events) {
		DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:event];
		messageCallback(eventMsg);
		DConnectDevicePlugin *plugin = (DConnectDevicePlugin *)provider;
		[plugin sendEvent:eventMsg];
	}
}

@end
