//
//  SuccessTestDevicePlugin.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>
#import "TestDevicePlugin.h"

NSString *const TestDevicePluginAppName = @"Test Device Plugin v0.1";

// Network Service Discoveryの定数

NSString *const TestDevicePluginName = @"TestDevicePlugin";
NSString *const TestDevicePluginId = @"0";
NSString *const TestDevicePluginType = @"TEST";

// System Profileの定数

NSString *const TestDevicePluginSystemVersion = @"1.0";


/** デバイスプラグインのバッテリーチャージフラグを定義. */
const BOOL TestDevicePluginBatteryCharging = YES;
/** デバイスプラグインのバッテリーチャージ時間を定義. */
const long TestDevicePluginBatteryChargingTime = 50000;
/** デバイスプラグインのバッテリー放電時間を定義. */
const long TestDevicePluginBatteryDischargingTime = 10000;
/** デバイスプラグインのバッテリーレベルを定義. */
const float TestDevicePluginBatteryLevel = 0.5;


@interface TestDevicePlugin : DConnectDevicePlugin <DConnectNetworkServiceDiscoveryProfileDelegate, DConnectSystemProfileDelegate, DConnectBatteryProfileDelegate, DConnectSystemProfileDataSource>

/*!
 * デバイスIDの正当性をチェックする.
 * @param deviceId デバイスID
 * @return 正常な場合はtrue、それ以外はfalse
 */
- (BOOL) checkDeviceId:(NSString *)deviceId;

@end

@implementation TestDevicePlugin

- (id) init {
    self = [super init];
    if (self) {
        // プラグインの名前を設定
        self.pluginName = TestDevicePluginAppName;
        
        // イベント管理クラス
        [DConnectEventManager sharedManagerForClass:[self class]];
        
        // Network Service Discovery Profileの追加
        DConnectNetworkServiceDiscoveryProfile *networkProfile = [DConnectNetworkServiceDiscoveryProfile new];
        networkProfile.delegate = self;
        
        // System Profileの追加
        DConnectSystemProfile *systemProfile = [DConnectSystemProfile new];
        systemProfile.delegate = self;
        systemProfile.dataSource = self;
        
        // Battery Profileの追加
        DConnectBatteryProfile *batteryProfile = [DConnectBatteryProfile new];
        batteryProfile.delegate = self;
        
        // 各プロファイルの追加
        [self addProfile:networkProfile];
        [self addProfile:systemProfile];
        [self addProfile:batteryProfile];
    }
    return self;
}

#pragma mark - Public Methods -

- (BOOL) checkDeviceId:(NSString *)deviceId {
    return [TestDevicePluginId isEqualToString:deviceId];
}


#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    DConnectArray *services = [DConnectArray array];
    
    DConnectMessage *service = [DConnectMessage message];
    [DConnectNetworkServiceDiscoveryProfile setId:TestDevicePluginId target:service];
    [DConnectNetworkServiceDiscoveryProfile setName:TestDevicePluginName target:service];
    [DConnectNetworkServiceDiscoveryProfile setType:TestDevicePluginType
                                             target:service];
    [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
    [services addMessage:service];
    
    [response setInteger:DConnectMessageResultTypeOk forKey:DConnectMessageResult];
    [response setArray:services forKey:DConnectNetworkServiceDiscoveryProfileParamServices];
    
    return YES;
}

#pragma mark - DConnectSystemProfileDelegate


#pragma mark - DConnectSystemProfileDataSource

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return TestDevicePluginSystemVersion;
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
    return nil;
}

#pragma mark - DConnectBatteryProfileDelegate

- (BOOL)        profile:(DConnectBatteryProfile *)profile
didReceiveGetAllRequest:(DConnectRequestMessage *)request
               response:(DConnectResponseMessage *)response
               deviceId:(NSString *)deviceId
{
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
        [DConnectBatteryProfile setCharging:TestDevicePluginBatteryCharging target:response];
        [DConnectBatteryProfile setChargingTime:TestDevicePluginBatteryChargingTime target:response];
        [DConnectBatteryProfile setDischargingTime:TestDevicePluginBatteryDischargingTime target:response];
        [DConnectBatteryProfile setLevel:TestDevicePluginBatteryLevel target:response];
    }
    return YES;
}

- (BOOL)          profile:(DConnectBatteryProfile *)profile
didReceiveGetLevelRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
{
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
        [DConnectBatteryProfile setLevel:TestDevicePluginBatteryLevel target:response];
    }
    return YES;
}

- (BOOL)             profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
{
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
        [DConnectBatteryProfile setCharging:TestDevicePluginBatteryCharging target:response];
    }
    return YES;
}

- (BOOL)                 profile:(DConnectBatteryProfile *)profile
didReceiveGetChargingTimeRequest:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response
                        deviceId:(NSString *)deviceId
{
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
        [DConnectBatteryProfile setChargingTime:TestDevicePluginBatteryChargingTime target:response];
    }
    return YES;
}

- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceiveGetDischargingTimeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
{
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        [response setResult:DConnectMessageResultTypeOk];
        [DConnectBatteryProfile setDischargingTime:TestDevicePluginBatteryDischargingTime target:response];
    }
    return YES;
}

#pragma mark DConnectBatteryProfileDelegate Event Registration

- (BOOL)                     profile:(DConnectBatteryProfile *)profile
didReceivePutOnChargingChangeRequest:(DConnectRequestMessage *)request
                            response:(DConnectResponseMessage *)response
                            deviceId:(NSString *)deviceId
                          sessionKey:(NSString *)sessionKey
{
    return YES;
}

- (BOOL)                    profile:(DConnectBatteryProfile *)profile
didReceivePutOnBatteryChangeRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                         sessionKey:(NSString *)sessionKey
{
    return YES;
}

#pragma mark DConnectBatteryProfileDelegate Event Unregistration

- (BOOL)                        profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnChargingChangeRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
    return YES;
}

- (BOOL)                       profile:(DConnectBatteryProfile *)profile
didReceiveDeleteOnBatteryChangeRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
                              deviceId:(NSString *)deviceId
                            sessionKey:(NSString *)sessionKey
{
    return YES;
}

@end
