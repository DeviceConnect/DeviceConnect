//
//  DPChromecastDevicePlugin.m
//  dConnectChromecast
//
//  Created by Ryuya Takahashi on 2014/09/03.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPChromecastDevicePlugin.h"
#import "DPChromecastSystemProfile.h"
#import "DPChromecastNetworkServiceDiscoveryProfile.h"
#import "DPChromecastNotificationProfile.h"
#import "DPChromecastMediaPlayerProfile.h"
#import "DPChromecastManager.h"


@implementation DPChromecastDevicePlugin

// 初期化
- (id) init {
    self = [super init];
    if (self) {
        self.pluginName = @"Chromecast 1.0";
        
        // 起動時の通知
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationdidFinishLaunching) name:UIApplicationDidFinishLaunchingNotification object:nil];

        // イベントマネージャの準備
        Class key = [self class];
        [[DConnectEventManager sharedManagerForClass:key] setController:[DConnectDBCacheController controllerWithClass:key]];

        // プロファイルを追加
        [self addProfile:[DPChromecastNetworkServiceDiscoveryProfile new]];
        [self addProfile:[DPChromecastSystemProfile new]];
        [self addProfile:[DPChromecastNotificationProfile new]];
        [self addProfile:[DPChromecastMediaPlayerProfile new]];
    }
    
    return self;
}

// 後始末
- (void)dealloc
{
    // 通知削除
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    // スキャン停止
    [[DPChromecastManager sharedManager] stopScan];
}

// 起動時
- (void)applicationdidFinishLaunching
{
    // スキャン開始
    [[DPChromecastManager sharedManager] startScan];
}

@end

