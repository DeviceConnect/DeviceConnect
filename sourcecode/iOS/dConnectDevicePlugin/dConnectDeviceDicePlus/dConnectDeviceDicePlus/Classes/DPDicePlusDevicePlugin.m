//
//  DPDicePlusDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusDevicePlugin.h"
#import "DPDicePlusNetworkServiceDiscoveryProfile.h"
#import "DPDicePlusDeviceOrientationProfile.h"
#import "DPDicePlusDiceProfile.h"
#import "DPDicePlusSystemProfile.h"
#import "DPDicePlusProximityProfile.h"
#import "DPDicePlusBatteryProfile.h"
#import "DPDicePlusTemperatureProfile.h"
#import "DPDicePlusLightProfile.h"
#import "DPDicePlusManager.h"


@interface DPDicePlusDevicePlugin()
@property (nonatomic, strong) NSMutableArray *diceListTemp;
@property (nonatomic, strong) NSMutableDictionary *diceDatasTemp;

@end
@implementation DPDicePlusDevicePlugin

- (id) init {
    self = [super init];
    if (self) {
        self.pluginName = @"DICE+ 1.0.1";

        // Eventの初期化
        Class key = [self class];
        [[DConnectEventManager sharedManagerForClass:key] setController:[DConnectDBCacheController controllerWithClass:key]];
        
        // 各プロファイルの初期化
        DPDicePlusNetworkServiceDiscoveryProfile *networkProfile = [DPDicePlusNetworkServiceDiscoveryProfile new];
        DPDicePlusSystemProfile *systemProfile = [DPDicePlusSystemProfile new];
        DPDicePlusDeviceOrientationProfile *orientationProfile = [DPDicePlusDeviceOrientationProfile new];
        DPDicePlusDiceProfile *diceProfile = [DPDicePlusDiceProfile new];
        DPDicePlusProximityProfile *proximityProfile = [DPDicePlusProximityProfile new];
        DPDicePlusBatteryProfile *batteryProfile = [DPDicePlusBatteryProfile new];
        DPDicePlusTemperatureProfile *temperatureProfile = [DPDicePlusTemperatureProfile new];
        DPDicePlusLightProfile *lightProfile = [DPDicePlusLightProfile new];
        
        [self addProfile:batteryProfile];
        [self addProfile:diceProfile];
        [self addProfile:lightProfile];
        [self addProfile:networkProfile];
        [self addProfile:orientationProfile];
        [self addProfile:proximityProfile];
        [self addProfile:systemProfile];
        [self addProfile:temperatureProfile];
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
    _diceListTemp = [[DPDicePlusManager sharedManager].diceList mutableCopy];
    _diceDatasTemp = [[DPDicePlusManager sharedManager].diceDatas mutableCopy];
}
/*!
 @brief フォアグラウンドに戻ったときの処理
 */
- (void) enterForeground {
    [DPDicePlusManager sharedManager].diceList = [_diceListTemp mutableCopy];
    [DPDicePlusManager sharedManager].diceDatas = [_diceDatasTemp mutableCopy];
    [[DPDicePlusManager sharedManager] startReconnectDicePlus];
}

@end
