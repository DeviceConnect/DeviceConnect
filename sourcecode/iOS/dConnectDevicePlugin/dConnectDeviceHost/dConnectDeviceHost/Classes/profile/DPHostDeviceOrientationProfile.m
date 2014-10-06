//
//  DPHostDeviceOrientationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <CoreMotion/CoreMotion.h>
#import <DConnectSDK/DConnectSDK.h>

#import "DPHostDevicePlugin.h"
#import "DPHostDeviceOrientationProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"

// CMDeviceMotionオブジェクトが配送されるインターバル（ミリ秒）
static const double MotionDeviceIntervalMilliSec = 100;

@interface DPHostDeviceOrientationProfile ()

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

// 加速度センサー、ジャイロセンサーからの値受領を管理するオブジェクト
@property CMMotionManager *motionManager;
// motionManagerで使うキュー
@property NSOperationQueue *deviceOrientationOpQueue;
// キューで回す処理
@property (strong) CMDeviceMotionHandler deviceOrientationOp;

- (void) sendOnDeviceOrientationEventWithMotion:(CMDeviceMotion *)motion;

@end

@implementation DPHostDeviceOrientationProfile

- (instancetype)init
{
    CMMotionManager *motionMgr = [CMMotionManager new];
    if (!motionMgr.accelerometerAvailable && !motionMgr.gyroAvailable) {
        // 加速度センサーとジャイロセンサー（角速度センサー）両方が使用不可；本プロファイルが使用不可
        return nil;
    }
    
    self = [super init];
    if (self) {
        // 加速度センサーもしくはジャイロセンサー（角速度センサー）が使用可能；本プロファイルが使用可能
        
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
        
        _motionManager = motionMgr;
        // CMDeviceMotionオブジェクトが配送されるインターバル（ミリ秒→秒）
        _motionManager.deviceMotionUpdateInterval = MotionDeviceIntervalMilliSec/1000.0;
        _deviceOrientationOpQueue = [NSOperationQueue new];
        __unsafe_unretained typeof(self) weakSelf = self;
        _deviceOrientationOp = ^(CMDeviceMotion *motion, NSError *error) {
            if (error) {
                NSLog(@"DPHostDeviceOrientationProfile Error:\n%@", error.description);
                [weakSelf.motionManager stopDeviceMotionUpdates];
            }
            [weakSelf sendOnDeviceOrientationEventWithMotion:motion];
        };
    }
    return self;
}

- (void)dealloc
{
    [_motionManager stopDeviceMotionUpdates];
}

- (void) sendOnDeviceOrientationEventWithMotion:(CMDeviceMotion *)motion
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectDeviceOrientationProfileName
                                          attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        
        DConnectMessage *orientation = [DConnectMessage message];
        // 加速度系
        if (_motionManager.accelerometerAvailable) {
            DConnectMessage *acceleration = [DConnectMessage message];
            [DConnectDeviceOrientationProfile setX:motion.userAcceleration.x target:acceleration];
            [DConnectDeviceOrientationProfile setY:motion.userAcceleration.y target:acceleration];
            [DConnectDeviceOrientationProfile setZ:motion.userAcceleration.z target:acceleration];
            [DConnectDeviceOrientationProfile setAcceleration:acceleration target:orientation];
            
            DConnectMessage *accelerationIncludingGravity = [DConnectMessage message];
            [DConnectDeviceOrientationProfile setX:motion.userAcceleration.x+motion.gravity.x target:accelerationIncludingGravity];
            [DConnectDeviceOrientationProfile setY:motion.userAcceleration.y+motion.gravity.y target:accelerationIncludingGravity];
            [DConnectDeviceOrientationProfile setZ:motion.userAcceleration.z+motion.gravity.z target:accelerationIncludingGravity];
            [DConnectDeviceOrientationProfile setAccelerationIncludingGravity:accelerationIncludingGravity target:orientation];
        }
        // 角速度系
        if (_motionManager.gyroAvailable) {
            DConnectMessage *rotationRate = [DConnectMessage message];
            [DConnectDeviceOrientationProfile setAlpha:motion.rotationRate.x target:rotationRate];
            [DConnectDeviceOrientationProfile setBeta:motion.rotationRate.y  target:rotationRate];
            [DConnectDeviceOrientationProfile setGamma:motion.rotationRate.z target:rotationRate];
            [DConnectDeviceOrientationProfile setRotationRate:rotationRate target:orientation];
        }
        // インターバル（ミリ秒）
        [DConnectDeviceOrientationProfile setInterval:MotionDeviceIntervalMilliSec target:orientation];
        [DConnectDeviceOrientationProfile setOrientation:orientation target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL)                        profile:(DConnectDeviceOrientationProfile *)profile
didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
    NSArray *evts = [_eventMgr eventListForDeviceId:deviceId
                                            profile:DConnectDeviceOrientationProfileName
                                          attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
    if (evts.count == 0) {
        // CMMotionDeviceの配送処理が開始されていないのなら、開始する。
        [_motionManager startDeviceMotionUpdatesToQueue:_deviceOrientationOpQueue
                                            withHandler:_deviceOrientationOp];
    }
    
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - Delete Methods
#pragma mark Event Unregistration

- (BOOL)                           profile:(DConnectDeviceOrientationProfile *)profile
didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                                  response:(DConnectResponseMessage *)response
                                  deviceId:(NSString *)deviceId
                                sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    NSArray *evts = [_eventMgr eventListForDeviceId:deviceId
                                            profile:DConnectDeviceOrientationProfileName
                                          attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
    if (evts.count == 0) {
        // イベント受領先が存在しないなら、UIDeviceBatteryStateDidChangeNotification通知の配送処理を停止する。
        [[NSNotificationCenter defaultCenter]
         removeObserver:self name:UIDeviceBatteryStateDidChangeNotification object:nil];
    }
    
    return YES;
}

@end
