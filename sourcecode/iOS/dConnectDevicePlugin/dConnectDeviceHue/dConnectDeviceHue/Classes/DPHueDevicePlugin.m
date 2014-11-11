//
//  dConnectDeviceHue.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueDevicePlugin.h"
#import "DPHueNetworkServiceDiscoveryProfile.h"
#import "DPHueSystemProfile.h"
#import "DPHueLightProfile.h"
#import "DPHueManager.h"
#import "DPHueConst.h"


NSString *const DPHueBundleName = @"dConnectDeviceHue_resources";

@interface DPHueDevicePlugin()

@property (nonatomic, strong) NSMutableDictionary *hueBridgeListTemp;
@end
@implementation DPHueDevicePlugin

- (id) init {
    
    self = [super init];
    
    if (self) {
        
        self.pluginName = [NSString stringWithFormat:@"hue 1.0.1"];
        
        // Network Service Discovery Profileの追加
        DPHueNetworkServiceDiscoveryProfile *networkProfile = [DPHueNetworkServiceDiscoveryProfile new];
        
        // System Profileの追加
        DPHueSystemProfile *systemProfile = [DPHueSystemProfile new];
        
        // Hue Profileの追加
        DPHueLightProfile *hueProfile = [DPHueLightProfile new];
        
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:hueProfile];
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
            UIApplication *application = [UIApplication sharedApplication];
            
            [nc addObserver:_self selector:@selector(enterForeground)
                       name:UIApplicationWillEnterForegroundNotification
                     object:application];
            
            [nc addObserver:_self selector:@selector(enterBackground)
                       name:UIApplicationDidEnterBackgroundNotification
                     object:application];
        });
    }
    
    return self;
}

- (void) dealloc {
    
    NSNotificationCenter *nc = [NSNotificationCenter defaultCenter];
    UIApplication *application = [UIApplication sharedApplication];
    
    [nc removeObserver:self name:UIApplicationDidBecomeActiveNotification object:application];
    [nc removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:application];
}
/*!
 @brief バックグラウンドに回ったときの処理
 */
- (void) enterBackground {
    [[DPHueManager sharedManager] saveBridgeList];
}
/*!
 @brief フォアグラウンドに戻ったときの処理
 */
- (void) enterForeground {
    [[DPHueManager sharedManager] readBridgeList];
}

@end
