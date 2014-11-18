//
//
//  DPSpheroManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPSpheroManager.h"
#import <RobotUIKit/RobotUIKit.h>
#import <RobotKit/RobotKit.h>
#import <RobotKit/RKGetUserRGBLEDColorCommand.h>


// センサー監視間隔（400Hz/kSensorDivisor）
#define kSensorDivisor 40


@interface DPSpheroManager () {
    NSDate *_prevDate;
    RKDataStreamingMask _streamingMask;
    BOOL _startedCollisionSensor;
}
@end

@implementation DPSpheroManager

// 共有インスタンス
+ (instancetype)sharedManager
{
    static id sharedInstance;
    static dispatch_once_t onceSpheroToken;
    dispatch_once(&onceSpheroToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

// 初期化
- (instancetype)init
{
    self = [super init];
    if (self) {
        // 初期状態で有効化済み
        _isActivated = YES;
    }
    return self;
}

// アプリがバックグラウンドに入った
- (void)applicationDidEnterBackground
{
    // センサーのマスクを保持
    _streamingMask = [RKSetDataStreamingCommand currentMask];
}

// アプリがフォアグラウンドに入った
- (void)applicationWillEnterForeground
{
    // すぐは復帰できないので。
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        // センサーを復帰
        [self startSensor:_streamingMask divisor:kSensorDivisor];
        if (_startedCollisionSensor) {
            [self startSensorCollision];
        }
    });
}

// 有効化
- (BOOL)activate
{
    if ([[[RKRobotProvider sharedRobotProvider] robots] count] > 0) {
        _isActivated = YES;
    } else {
        _isActivated = NO;
    }
    return _isActivated;
}

// 無効化
- (void)deactivate
{
    _isActivated = NO;
}

// 接続中のデバイスID取得
- (NSString*)currentDeviceID
{
    if (!_isActivated) return nil;
    
    return [[[RKRobotProvider sharedRobotProvider] robot] bluetoothAddress];
}

// デバイスに接続
- (BOOL)connectDeviceWithID:(NSString*)deviceID
{
    if (!_isActivated) return NO;
    
    RKRobotProvider *provider = [RKRobotProvider sharedRobotProvider];
    NSString *oldID = [provider robot].bluetoothAddress;
    // FIXME: これをやると動きがおかしくなる
    // 接続済みチェック
    if ([oldID isEqualToString:deviceID]) {
        [provider openRobotConnection];
        return YES;
    }
    
    // 検索して接続
    [provider closeRobotConnection];
    NSArray *robots = [provider robots];
    for (int i=0; i<[robots count]; i++) {
        RKRobot *robo = robots[i];
        if ([robo.bluetoothAddress isEqualToString:deviceID]) {
            if ([provider controlRobotAtIndex:i]) {
                
                // 現在設定されているLED色を取得
                if (![oldID isEqualToString:deviceID]) {
                    [[RKDeviceMessenger sharedMessenger] addResponseObserver:self selector:@selector(handleResponse:)];
                    [RKGetUserRGBLEDColorCommand sendCommand];
                }
                // キャリブレーションLEDの明るさをリセット
                // FIXME: キャリブレーションLEDの明るさを取得する命令がないので、LEDを付けたまま接続するとズレが生じる。
                _calibrationLightBright = 0;
                _streamingMask = 0;
                _startedCollisionSensor = NO;

                return YES;
            }
        }
    }
    return NO;
}

// 接続可能なデバイスリスト取得
- (NSArray*)deviceList
{
    if (!_isActivated) return nil;
    
    NSMutableArray *array = [NSMutableArray array];
    for (RKRobot *robo in [[RKRobotProvider sharedRobotProvider] robots]) {
        //NSLog(@"%@", robo);
        //NSLog(@"%@", robo.accessory.name);
        //NSLog(@"%@", robo.bluetoothAddress);
        [array addObject:@{@"name": robo.accessory.name, @"id": robo.bluetoothAddress}];
    }
    return array;
}


#pragma mark - Observer

// レスポンスハンドラ
- (void)handleResponse:(RKDeviceResponse *)response
{
    if (!_isActivated) return;
    
    //NSLog(@"handleResponse:%@", response);
    // LEDライトの色を取得
    if ([NSStringFromClass([response class]) isEqualToString:@"RKGetUserRGBLEDColorResponse"]) {
        Byte r, g, b;
        [response.responseData getBytes:&r range:NSMakeRange(0, 1)];
        [response.responseData getBytes:&g range:NSMakeRange(1, 1)];
        [response.responseData getBytes:&b range:NSMakeRange(2, 1)];
        _LEDLightColor = [UIColor colorWithRed:r/255. green:g/255. blue:b/255. alpha:1.0];
        //NSLog(@"*rgb:%d, %d, %d", r, g, b);
    }
}

// ストリーミングデータハンドラ
- (void)handleDataStreaming:(RKDeviceAsyncData *)asyncData
{
    if (!_isActivated) return;
    
    if ([asyncData isKindOfClass:[RKDeviceSensorsAsyncData class]]) {
        // 計測間隔
        int interval = [[NSDate date] timeIntervalSinceDate:_prevDate] * 1000;
        
        // Received sensor data
        RKDeviceSensorsAsyncData *sensorsAsyncData = (RKDeviceSensorsAsyncData *)asyncData;
        RKDeviceSensorsData *sensorsData = [sensorsAsyncData.dataFrames lastObject];

        // Orientation
        RKAccelerometerData *accelerometerData = sensorsData.accelerometerData;
        RKAttitudeData *attitudeData = sensorsData.attitudeData;
        if (accelerometerData || attitudeData) {
            if ([_orientationDelegate respondsToSelector:@selector(spheroManagerStreamingOrientation:accel:interval:)]) {
                DPAttitude attitude;
                attitude.yaw = attitudeData.yaw;
                attitude.roll = attitudeData.roll;
                attitude.pitch = attitudeData.pitch;
                DPPoint3D accel;
                accel.x = accelerometerData.acceleration.x;
                accel.y = accelerometerData.acceleration.y;
                accel.z = accelerometerData.acceleration.z;
                [_orientationDelegate spheroManagerStreamingOrientation:attitude accel:accel interval:interval];
            }
        }
        // Quaternion
        RKQuaternionData *quaternionData = sensorsData.quaternionData;
        if (quaternionData) {
            if ([_sensorDelegate respondsToSelector:@selector(spheroManagerStreamingQuaternion:interval:)]) {
                DPQuaternion qt;
                qt.q0 = quaternionData.quaternions.q0;
                qt.q1 = quaternionData.quaternions.q1;
                qt.q2 = quaternionData.quaternions.q2;
                qt.q3 = quaternionData.quaternions.q3;
                [_sensorDelegate spheroManagerStreamingQuaternion:qt interval:interval];
                //NSLog(@"qt:%f,%f,%f,%f", qt.q0, qt.q1, qt.q2, qt.q3);
            }
        }
        // Locator
        RKLocatorData *locatorData = sensorsData.locatorData;
        if (locatorData) {
            if ([_sensorDelegate respondsToSelector:@selector(spheroManagerStreamingLocatorPos:velocity:interval:)]) {
                CGPoint pos = CGPointMake(locatorData.position.x, locatorData.position.y);
                CGPoint vel = CGPointMake(locatorData.velocity.x, locatorData.velocity.y);
                [_sensorDelegate spheroManagerStreamingLocatorPos:pos velocity:vel interval:interval];
            }
        }
        
        _prevDate = [NSDate date];
        
    } else if ([asyncData isKindOfClass:[RKCollisionDetectedAsyncData class]]) {
        // Collision
        RKCollisionDetectedAsyncData *collisionData = (RKCollisionDetectedAsyncData *)asyncData;
        if (collisionData) {
            if ([_sensorDelegate respondsToSelector:@selector(spheroManagerStreamingCollisionImpactAcceleration:axis:power:speed:time:)]) {
                DPPoint3D accel;
                accel.x = collisionData.impactAcceleration.x;
                accel.y = collisionData.impactAcceleration.y;
                accel.z = collisionData.impactAcceleration.z;
                CGPoint axis = CGPointMake(collisionData.impactAxis.x, collisionData.impactAxis.y);
                CGPoint power = CGPointMake(collisionData.impactPower.x, collisionData.impactPower.y);
                float speed = collisionData.impactSpeed;
                NSTimeInterval time = collisionData.timeStamp;
                [_sensorDelegate spheroManagerStreamingCollisionImpactAcceleration:accel axis:axis power:power speed:speed time:time];
            }
        }
    }
}


#pragma mark - Light

// キャリブレーションライトの点灯
- (void)setCalibrationLightBright:(float)calibrationLightBright
{
    if (!_isActivated) return;
    
    _calibrationLightBright = calibrationLightBright;
    [RKBackLEDOutputCommand sendCommandWithBrightness:_calibrationLightBright];
}

// LEDライトの色変更
- (void)setLEDLightColor:(UIColor*)color
{
    if (!_isActivated) return;
    
    _LEDLightColor = color;
    CGFloat r, g, b, a;
    [color getRed:&r green:&g blue:&b alpha:&a];
    [RKRGBLEDOutputCommand sendCommandWithRed:r*a green:g*a blue:b*a userDefault:YES];
    //NSLog(@"rgb:%f, %f, %f", r, g, b);
}


// LEDが点灯しているか
- (BOOL)isLEDOn
{
    if (!_isActivated) return NO;
    
    CGFloat r, g, b, a;
    [_LEDLightColor getRed:&r green:&g blue:&b alpha:&a];
    //NSLog(@"led:%f, %f, %f", r, g, b);
    return r>0 && g>0 && b>0 && a>0;
}

// キャリブレーションライトの点滅
- (void)flashLightWithBrightness:(float)brightness flashData:(NSArray*)flashData
{
    
    RKMacroObject *macro = [RKMacroObject new];
    for (int i=0; i<flashData.count; i++) {
        int delay = [flashData[i] intValue];
        if (i%2==0) {
            // 点灯
            [macro addCommand:[RKMCFrontLED commandWithIntensity:brightness delay:delay]];
        } else {
            // 消灯
            [macro addCommand:[RKMCFrontLED commandWithIntensity:0 delay:delay]];
        }
    }
    // 最後は現状復帰で終わる
    [macro addCommand:[RKMCFrontLED commandWithIntensity:_calibrationLightBright delay:0]];
    [macro playMacro];
}

// LEDを点滅
- (void)flashLightWithColor:(UIColor*)color flashData:(NSArray*)flashData
{
    RKMacroObject *macro = [RKMacroObject new];
    for (int i=0; i<flashData.count; i++) {
        int delay = [flashData[i] intValue];
        if (i%2==0) {
            // 点灯
            CGFloat r, g, b, a;
            [color getRed:&r green:&g blue:&b alpha:&a];
            [macro addCommand:[RKMCRGB commandWithRed:r*a green:g*a blue:b*a delay:delay]];
        } else {
            // 消灯
            [macro addCommand:[RKMCRGB commandWithRed:0 green:0 blue:0 delay:delay]];
        }
    }
    [macro playMacro];
}

#pragma mark - Move

// 移動
- (void)move:(float)angle velocity:(float)velocity
{
    if (!_isActivated) return;
    [RKRollCommand sendCommandWithHeading:angle velocity:velocity];
}

// 回転
- (void)rotate:(float)angle
{
    if (!_isActivated) return;
    [RKRollCommand sendCommandWithHeading:angle velocity:0.0];
}

// 停止
- (void)stop
{
    if (!_isActivated) return;
    
    [RKRollCommand sendStop];
    [RKAbortMacroCommand sendCommand];
}


#pragma mark - Sensor

// 姿勢センサー開始
- (void)startSensorOrientation
{
    if (!_isActivated) return;
    
    RKDataStreamingMask mask = RKDataStreamingMaskAccelerometerFilteredAll | RKDataStreamingMaskIMUAnglesFilteredAll;
    mask = [RKSetDataStreamingCommand currentMask] | mask;
    [self startSensor:mask divisor:kSensorDivisor];
}

// 姿勢センサー停止
- (void)stopSensorOrientation
{
    if (!_isActivated) return;
    
    [self stopSensor:RKDataStreamingMaskAccelerometerFilteredAll | RKDataStreamingMaskIMUAnglesFilteredAll];
}

// クォータニオンセンサー開始
- (void)startSensorQuaternion
{
    if (!_isActivated) return;
    
    RKDataStreamingMask mask = RKDataStreamingMaskQuaternionAll;
    mask = [RKSetDataStreamingCommand currentMask] | mask;
    [self startSensor:mask divisor:kSensorDivisor];
}

// クォータニオンセンサー停止
- (void)stopSensorQuaternion
{
    if (!_isActivated) return;
    
    [self stopSensor:RKDataStreamingMaskQuaternionAll];
}

// 位置センサー開始
- (void)startSensorLocator
{
    if (!_isActivated) return;
    
    RKDataStreamingMask mask = RKDataStreamingMaskLocatorAll;
    mask = [RKSetDataStreamingCommand currentMask] | mask;
    [self startSensor:mask divisor:kSensorDivisor];
    [RKConfigureLocatorCommand sendCommandForFlag:0 newX:0 newY:0 newYaw:0];
}

// 位置センサー停止
- (void)stopSensorLocator
{
    if (!_isActivated) return;
    
    [self stopSensor:RKDataStreamingMaskLocatorAll];
}

// 衝突センサー開始
- (void)startSensorCollision
{
    if (!_isActivated) return;
    
    _prevDate = [NSDate date];
    _startedCollisionSensor = YES;
    
    int xThreshold = 90;
    int yThreshold = 90;
    int xSpeedThreshold = 130;
    int ySpeedThreshold = 130;
    int deadZone = 1000;
    [RKConfigureCollisionDetectionCommand sendCommandForMethod:RKCollisionDetectionMethod1
                                                    xThreshold:xThreshold
                                               xSpeedThreshold:xSpeedThreshold
                                                    yThreshold:yThreshold
                                               ySpeedThreshold:ySpeedThreshold
                                              postTimeDeadZone:deadZone];
    ////Register for asynchronise data streaming packets
    [[RKDeviceMessenger sharedMessenger] addDataStreamingObserver:self selector:@selector(handleDataStreaming:)];
}

// 衝突センサー停止
- (void)stopSensorCollision
{
    if (!_isActivated) return;
    _startedCollisionSensor = NO;
    
    [RKConfigureCollisionDetectionCommand sendCommandToStopDetection];
    [self stopSensor:0];
}

// 全センサー停止
- (void)stopAllSensor
{
    if (!_isActivated) return;
    
    _startedCollisionSensor = NO;
    
    [RKConfigureCollisionDetectionCommand sendCommandToStopDetection];
    // Turn off data streaming
    [RKSetDataStreamingCommand sendCommandStopStreaming];
    // Unregister for async data packets
    [[RKDeviceMessenger sharedMessenger] removeDataStreamingObserver:self];
    // スタビライザーを再開
    [RKStabilizationCommand sendCommandWithState:RKStabilizationStateOn];
}

// センサー停止
- (void)stopSensor:(RKDataStreamingMask)mask
{
    if (!_isActivated) return;
    
    // 指定のセンサーを外す
    if (mask) {
        //NSLog(@"stopSensor_pre:%llx", mask);
        mask = [RKSetDataStreamingCommand currentMask] & (0xFFFFFFFFFFFFFFFF^mask);
        //NSLog(@"stopSensor_after:%llx", mask);
    }
    
    if (!(mask & RKDataStreamingMaskAccelerometerFilteredAll) &&
        !(mask & RKDataStreamingMaskQuaternionAll)) {
        // 姿勢センサーとクォータニオンセンサーが無い場合はスタビライザーを再開
        [RKStabilizationCommand sendCommandWithState:RKStabilizationStateOn];
    }

    if (mask==RKDataStreamingMaskOff) {
        if (!_startedCollisionSensor) {
            // Turn off data streaming
            [RKSetDataStreamingCommand sendCommandStopStreaming];
            // Unregister for async data packets
            [[RKDeviceMessenger sharedMessenger] removeDataStreamingObserver:self];
            // スタビライザーを再開
            [RKStabilizationCommand sendCommandWithState:RKStabilizationStateOn];
        }
    } else {
        // まだセンサーが残ってる
        [self startSensor:mask divisor:kSensorDivisor];
    }
}

// 共通センサー開始処理
- (void)startSensor:(RKDataStreamingMask)mask divisor:(uint16_t)devisor
{
    if (!_isActivated) return;
    
    _prevDate = [NSDate date];
    
    // 一旦止める
    [RKSetDataStreamingCommand sendCommandStopStreaming];
    
    if (mask & RKDataStreamingMaskAccelerometerFilteredAll ||
        mask & RKDataStreamingMaskQuaternionAll) {
        // 姿勢センサーとクォータニオンセンサーの場合はスタビライザーを停止
        [RKStabilizationCommand sendCommandWithState:RKStabilizationStateOff];
    }
    
    // Send command to Sphero
    [RKSetDataStreamingCommand sendCommandWithSampleRateDivisor:kSensorDivisor
                                                   packetFrames:1
                                                     sensorMask:mask
                                                    packetCount:0];
    
    ////Register for asynchronise data streaming packets
    [[RKDeviceMessenger sharedMessenger] addDataStreamingObserver:self selector:@selector(handleDataStreaming:)];
    
}

@end
