//
//  DPPebbleSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleSystemProfile.h"
#import "PebbleViewController.h"
#import "DPPebbleManager.h"
#import "DPPebbleProfileUtil.h"

@interface DPPebbleSystemProfile ()
@end

@implementation DPPebbleSystemProfile

// 初期化
- (id)init
{
	self = [super init];
	if (self) {
		self.delegate = self;
		self.dataSource = self;
	}
	return self;
}


#pragma mark - DConnectSystemProfileDelegate & DataSource

// デバイスプラグインのバージョン
- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
	return @"1.0";
}

// 設定画面用のUIViewController
- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
	NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDevicePebble_resources" ofType:@"bundle"];
	NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];

	// iphoneとipadでストーリーボードを切り替える
	UIStoryboard *sb;
	if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
		sb = [UIStoryboard storyboardWithName:@"dConnectDevicePebble_iPhone" bundle:bundle];
	} else{
		sb = [UIStoryboard storyboardWithName:@"dConnectDevicePebble_iPad" bundle:bundle];
	}
	UINavigationController *vc = [sb instantiateInitialViewController];

	return vc;
}


#pragma mark - Delete Methods

// イベント一括解除リクエストを受け取った
- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
	[[DPPebbleManager sharedManager] deleteAllEvents:^(NSError *error) {
		[response setResult:DConnectMessageResultTypeOk];
		[[DConnectManager sharedManager] sendResponse:response];
	}];
	return NO;
}


@end
