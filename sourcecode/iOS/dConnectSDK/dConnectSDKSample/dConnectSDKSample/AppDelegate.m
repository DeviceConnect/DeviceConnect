//
//  AppDelegate.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "AppDelegate.h"
#import <DConnectSDK/DConnectSDK.h>

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    [[DConnectManager sharedManager] start];
    return YES;
}

@end
