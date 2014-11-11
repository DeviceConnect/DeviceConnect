//
//  DPDicePlusSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusSystemProfile.h"
#import "DPDicePlusDevicePlugin.h"

@implementation DPDicePlusSystemProfile
- (id)init {
    self = [super init];
    if (self) {
        self.dataSource = self;
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectSystemProfileDataSource delegate

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return @"1.0.1";
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request {
    
    // 設定画面用のViewControllerをStoryboardから生成する
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceDicePlus_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    
    UIStoryboard *sb;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        sb = [UIStoryboard storyboardWithName:@"Storyboard_iPhone" bundle:bundle];
    } else{
        sb = [UIStoryboard storyboardWithName:@"Storyboard_iPad" bundle:bundle];
    }
    return [sb instantiateInitialViewController];
}
//イベントの削除
- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
    DConnectEventManager *eventMgr = [DConnectEventManager sharedManagerForClass:[DPDicePlusDevicePlugin class]];
    if ([eventMgr removeEventsForSessionKey:sessionKey]) {
        [response setResult:DConnectMessageResultTypeOk];
    } else {
        [response setErrorToUnknownWithMessage:
         @"Failed to remove events associated with the specified session key."];
    }
    
    return YES;
}

@end
