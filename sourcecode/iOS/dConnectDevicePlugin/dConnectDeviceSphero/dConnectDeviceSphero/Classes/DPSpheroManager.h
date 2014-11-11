//
//  DPSpheroManager.h
//  dConnectDeviceSphero
//
//  Created by Takashi Tsuchiya on 2014/09/10.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <UIKit/UIKit.h>

// 3次元構造体
typedef struct DPPoint3D_ {
    float x, y, z;
} DPPoint3D;

// クオータニオン構造体
typedef struct DPQuaternion_ {
    float q0, q1, q2, q3;
} DPQuaternion;

// 姿勢構造体
typedef struct DPAttitude_ {
    float pitch, roll, yaw;
} DPAttitude;

// Spheroのセンサー処理用デリゲート
@protocol DPSpheroManagerSensorDelegate <NSObject>
- (void)spheroManagerStreamingQuaternion:(DPQuaternion)q interval:(int)interval;
- (void)spheroManagerStreamingLocatorPos:(CGPoint)pos velocity:(CGPoint)velocity interval:(int)interval;
- (void)spheroManagerStreamingCollisionImpactAcceleration:(DPPoint3D)accel axis:(CGPoint)axis power:(CGPoint)power speed:(float)speed time:(NSTimeInterval)time;
@end

@protocol DPSpheroManagerOrientationDelegate <NSObject>
- (void)spheroManagerStreamingOrientation:(DPAttitude)attitude accel:(DPPoint3D)accel interval:(int)interval;
@end


// Spheroの制御クラス
@interface DPSpheroManager : NSObject

@property (nonatomic, weak) id<DPSpheroManagerSensorDelegate> sensorDelegate;
@property (nonatomic, weak) id<DPSpheroManagerOrientationDelegate> orientationDelegate;

// キャリブレーションライトの明るさ
@property (nonatomic) float calibrationLightBright;
// LEDの色
@property (nonatomic) UIColor* LEDLightColor;
// LEDが付いているか
@property (nonatomic, readonly) BOOL isLEDOn;
// 接続中のデバイスID取得
@property (nonatomic, readonly) NSString *currentDeviceID;
// 接続可能なデバイスリスト取得
@property (nonatomic, readonly) NSArray *deviceList;
// アクティベート済みか
@property (nonatomic, readonly) BOOL isActivated;


// 共有インスタンス
+ (instancetype)sharedManager;

// アプリがバックグラウンドに入った
- (void)applicationDidEnterBackground ;
// アプリがフォアグラウンドに入った
- (void)applicationWillEnterForeground;

// 有効化
- (BOOL)activate;
// 無効化
- (void)deactivate;

// デバイスに接続
- (BOOL)connectDeviceWithID:(NSString*)deviceID;
// 移動
- (void)move:(float)angle velocity:(float)velocity;
// 回転
- (void)rotate:(float)angle;
// 停止
- (void)stop;

// LEDを点滅
- (void)flashLightWithColor:(UIColor*)color flashData:(NSArray*)flashData;
// キャリブレーションライトの点滅
- (void)flashLightWithBrightness:(float)brightness flashData:(NSArray*)flashData;

// 姿勢センサー
- (void)startSensorOrientation;
- (void)stopSensorOrientation;
// クォータニオンセンサー
- (void)startSensorQuaternion;
- (void)stopSensorQuaternion;
// 位置センサー
- (void)startSensorLocator;
- (void)stopSensorLocator;
// 衝突センサー
- (void)startSensorCollision;
- (void)stopSensorCollision;
// 全センサー停止
- (void)stopAllSensor;

@end
