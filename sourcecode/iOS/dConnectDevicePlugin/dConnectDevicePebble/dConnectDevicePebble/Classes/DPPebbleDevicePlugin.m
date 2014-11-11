//
//  DPPebbleDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleDevicePlugin.h"
#import "DPPebbleNetworkServiceDiscoveryProfile.h"
#import "DPPebbleSystemProfile.h"
#import "DPPebbleBatteryProfile.h"
#import "DPPebbleFileProfile.h"
#import "DPPebbleVibrationProfile.h"
#import "DPPebbleSettingsProfile.h"
#import "DPPebbleDeviceOrientationProfile.h"
#import "DPPebbleNotificationProfile.h"
#import "PebbleViewController.h"
#import "DPPebbleManager.h"


@interface DPPebbleDevicePlugin ()
@end


@implementation DPPebbleDevicePlugin

- (instancetype) init
{
	self = [super init];
	if (self) {
		// プラグイン名を設定
		self.pluginName = [NSString stringWithFormat:@"Pebble 1.0"];
		
		// EventManagerの初期化
		Class key = [self class];
		[[DConnectEventManager sharedManagerForClass:key] setController:[DConnectDBCacheController controllerWithClass:key]];
		
		// 各プロファイルの追加
		[self addProfile:[DPPebbleNetworkServiceDiscoveryProfile new]];
		[self addProfile:[DPPebbleNotificationProfile new]];
		[self addProfile:[DPPebbleSystemProfile new]];
		[self addProfile:[DPPebbleBatteryProfile new]];
		[self addProfile:[DPPebbleFileProfile new]];
		[self addProfile:[DPPebbleSettingsProfile new]];
		[self addProfile:[DPPebbleVibrationProfile new]];
		[self addProfile:[DPPebbleDeviceOrientationProfile new]];
		
		dispatch_async(dispatch_get_main_queue(), ^{
			NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
			UIApplication *application = [UIApplication sharedApplication];
			
			[nc addObserver:self selector:@selector(enterForeground)
					   name:UIApplicationWillEnterForegroundNotification
					 object:application];
			
			[nc addObserver:self selector:@selector(enterBackground)
					   name:UIApplicationDidEnterBackgroundNotification
					 object:application];
		});

	}
	return self;
}

- (void) dealloc
{
	NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
	UIApplication *application = [UIApplication sharedApplication];
	[nc removeObserver:self name:UIApplicationDidBecomeActiveNotification object:application];
	[nc removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:application];
}

- (void)enterBackground
{
	[[DPPebbleManager sharedManager] applicationDidEnterBackground];
}

- (void)enterForeground
{
	[[DPPebbleManager sharedManager] applicationWillEnterForeground];
}

@end
