//
//  DPHostSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

#import "DPHostDevicePlugin.h"
#import "DPHostSystemProfile.h"

@interface DPHostSystemProfile ()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

@end

@implementation DPHostSystemProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        self.dataSource = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
    }
    return self;
}

#pragma mark - DConnectSystemProfileDelegate

#pragma mark - Put Methods

//- (BOOL)            profile:(DConnectSystemProfile *)profile
//didReceivePutKeywordRequest:(DConnectRequestMessage *)request
//                   response:(DConnectResponseMessage *)response
//{
//    DPHostDevicePlugin *devicePlugin = SELF_PLUGIN;
//    NSString *title = [NSString stringWithFormat:@"Keyword for %@", devicePlugin.pluginName];
//    UIAlertView *alertView =
//    [[UIAlertView alloc] initWithTitle:title message:devicePlugin.keyword
//                              delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
//    [alertView show];
//    return YES;
//}

#pragma mark - Delete Methods

- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
    if ([_eventMgr removeEventsForSessionKey:sessionKey]) {
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToUnknownWithMessage:
         @"Failed to remove events associated with the specified session key."];
    }
    
    return YES;
}

#pragma mark - DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return @"1.0";
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
    // 設定画面無し；nilを返す。
    return nil;
}

@end
