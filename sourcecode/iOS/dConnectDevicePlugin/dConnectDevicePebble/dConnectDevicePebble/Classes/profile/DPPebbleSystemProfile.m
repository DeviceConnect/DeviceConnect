//
//  DPPebbleSystemProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleSystemProfile.h"
#import "PebbleViewController.h"

@interface DPPebbleSystemProfile ()

/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;
@property DConnectEventManager *eventMgr;

@end

@implementation DPPebbleSystemProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
        self.dataSource = self;
        
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
        
        
    }
    return self;
}

#pragma mark - DConnectSystemProfileDelegate

#pragma mark - DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return @"1.0";
}

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

- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
    NSMutableDictionary *dic = [NSMutableDictionary dictionary];
    dic[@(KEY_PROFILE)] = @(PROFILE_SYSTEM);
    dic[@(KEY_ATTRIBUTE)] = @(SYSTEM_ATTRIBUTE_EVENTS);
    dic[@(KEY_ACTION)] = @(ACTION_DELETE);
    [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
        if (dic) {
            if ([_eventMgr removeEventsForSessionKey:sessionKey]) {
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToUnknownWithMessage:
                 @"Failed to remove events associated with the specified session key."];
            }
        }
        else{
            [response setErrorToUnknown];
        }
        [[DConnectManager sharedManager] sendResponse:response];
        
    }     ];
    
    return NO;
}


@end
