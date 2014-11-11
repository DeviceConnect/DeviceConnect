//
//  DPDicePlusData.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import <DicePlus/DicePlus.h>


/*!
 @brief バッテリー情報を受け取る為のブロック。
 
 @param[in] charging 充電中(YES:充電中)
 @param[in] level バッテリーレベル
 */
typedef void (^DPDicePlusBatteryBlock)(BOOL charging, double level);

/*!
 @brief チャージングイベント情報を受け取る為のブロック。
 
 @param[in] level バッテリーレベル
 */
typedef void (^DPDicePlusBatteryStateBlock)(float level);

/*!
 @brief チャージングイベント情報を受け取る為のブロック。
 
 @param[in] charging 充電中(YES:充電中)
 */
typedef void (^DPDicePlusBatteryChargingBlock)(BOOL charging);

/*!
 @brief Dice+を振ったときの目を通知するためのブロック。
 
 @param[in] pid さいころの目
 */
typedef void (^DPDicePlusRollBlock)(int pid);

/*!
 @brief Dice+の温度を通知するためのブロック。
 
 @param[in] temperature 温度
 */
typedef void (^DPDicePlusTemperatureBlock)(float temperature);

/*!
 @brief Dice+の加速度センサーを通知するためのブロック。
 */
typedef void (^DPDicePlusOrientationBlock)(double x, double y, double z, int roll, int pitch, int yaw, int interval);


typedef void (^DPDicePlusMagnetometerBlock)(int x, int y, int z, int interval, int flags);


typedef void (^DPDicePlusProximityBlock)(float value, float min, float max, float threshold);


@interface DPDicePlusData : NSObject

/*! @brief バッテリー情報を取得するためのブロックリスト。 */
@property (nonatomic) NSMutableArray *batteryBlocks;
/*! @brief バッテリーイベント情報を取得するためのブロック。 */
@property (nonatomic) id batteryEventBlock;
/*! @brief チャージングイベント情報を取得するためのブロック。 */
@property (nonatomic) id chargingEventBlock;
/*! @brief バッテリー情報。 */
@property (nonatomic) DPBattery *batteryState;


/*! @brief 温度通知用ブロックリスト。 */
@property (nonatomic) NSMutableArray *temperatureBlocks;


/*! @brief Diceの目を通知するブロック。 */
@property (nonatomic) id rollEventBlock;

/*! @brief Diceの目を保持する。*/
@property (nonatomic) int rollNowPip;


@property (nonatomic) id orientationEventBlock;
@property (nonatomic) DPAcceleration *accel;
@property (nonatomic) DPOrientation *orien;


@property (nonatomic) id magnetometorEventBlock;
@property (nonatomic) DPMagnetometer *magnetometer;


@property (nonatomic) id proximityEventBlock;


/*!
 @brief バッテリチェック。
 */
- (BOOL) checkBattery;

@end
