//
//  GHAppDelegate.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHAppDelegate.h"
#import "GHDataManager.h"

@implementation GHAppDelegate

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.redirectURL = nil;
    }
    return self;
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    
    //DBの初期値を設定
    //起動時にキャッシュ画像も一旦削除
    [[GHDataManager shareManager]initPrefs];
    
    //Cookieの初期設定を更新
    NSUserDefaults *def = [NSUserDefaults standardUserDefaults];
    [def setObject:@([GHUtils isCookieAccept]) forKey:IS_COOKIE_ACCEPT];
    return YES;
}
							
- (void)applicationWillResignActive:(UIApplication *)application
{

}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    [[GHDataManager shareManager]save];
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
   
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    [[GHDataManager shareManager]save];
}

- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    if (![url.scheme isEqualToString:@"dconnect"]) {
        return NO;
    }
    
    NSString *directURLStr = [url.resourceSpecifier stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSURL *redirectURL = [NSURL URLWithString:directURLStr];

    if (_URLLoadingCallback && redirectURL) {
        // UIApplicationWillEnterForegroundNotification通知オブザベーションによりコールバックが呼ばれた場合、
        // NSURLを引数に取るコールバックが保持される。その上で「dconnect」URLスキーム経由でリダイレクト先URLが飛んできたのなら、
        // このコールバックにコールバックURLを渡す。

        _URLLoadingCallback(redirectURL);
        _URLLoadingCallback = nil;
        return YES;
    } else {
        return (_redirectURL = redirectURL) != nil;
    }
}

// HostデバイスプラグインのNotificationProfileのイベントは、各アプリでこのような処理を追加しなければイベントの通知が正常に行われない
- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)localNotification
{
    if(application.applicationState == UIApplicationStateInactive) {
        [[NSNotificationCenter defaultCenter] postNotification:[NSNotification notificationWithName:@"UIApplicationDidReceiveLocalNotification"
                                                                                             object:nil
                                                                                           userInfo:localNotification.userInfo]];
    }
}

@end
