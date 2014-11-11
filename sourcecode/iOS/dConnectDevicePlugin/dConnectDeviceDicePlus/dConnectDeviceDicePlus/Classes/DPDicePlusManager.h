//
//  DPDicePlusManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import <DicePlus/DicePlus.h>
#import "DPDicePlusData.h"

@class DPDicePlusManager;

/*!
 @brief DicePlusの接続するデリゲート。
 */
@protocol DPDicePlusConnectDelegate <NSObject>

/*!
 @brief Dice+に接続完了したことを通知する。
 
 @param[in] manager 管理クラス
 @param[in] die 接続されたDice+
 */
- (void)manager:(DPDicePlusManager *)manager didConnectDie:(DPDie *)die;

/*!
 @brief Dice+が切断されたことを通知する。
 
 @param[in] manager 管理クラス
 @param[in] die 切断されたDice+
 */
- (void)manager:(DPDicePlusManager *)manager didDisconnectDie:(DPDie *)die;

/*!
 @brief Dice+の接続が失敗したことを通知する。
 
 @param[in] manager 管理クラス
 @param[in] die 失敗されたDice+
 */
- (void)manager:(DPDicePlusManager *)manager didFailConnecttDie:(DPDie *)die;

@optional

/*!
 @brief スキャンが完了したことを通知する。
 
 @param[in] manager 管理クラス
 @param[in] diceList スキャンされたDice+リスト
 */
- (void)manager:(DPDicePlusManager *)manager didFinishedScan:(NSArray *)diceList;

@end


/*!
 @brief Dice+管理クラス。
 */
@interface DPDicePlusManager : NSObject <DPDiceManagerDelegate, DPDieDelegate>

/*!
 @brief Dice+管理クラス。
 */
@property (nonatomic) DPDiceManager* diceManager;

/*!
 @brief Diceリスト。
 */
@property (nonatomic) NSMutableArray *diceList;
/*!
 @brief 見つかったDiceのリスト。
 */
@property (nonatomic) NSMutableArray *foundDiceList;

/*!
 @brief 接続通知デリゲート。
 */
@property (nonatomic) NSMutableArray *connectDelegateList;
/*!
 @brief Dice管理オブジェクト
 */
@property NSMutableDictionary *diceDatas;
/*!
 @brief 管理クラスを取得する。
 */
+ (DPDicePlusManager *) sharedManager;

/*!
 @brief Dice+との接続を開始する。
 */
- (void)startConnectDicePlus;
/*!
 @brief Dice+を指定して接続する。
 */
- (void)startConnectDicePlusByDPDie:(DPDie *)die;
/*!
 @brief Dice+を指定して切断する。
 */
- (void)startDisonnectDicePlusByDPDie:(DPDie *)die;

/*!
 @brief Dice+との再接続を開始する。
 */
- (void)startReconnectDicePlus;


/*!
 @brief Dice+との接続をすべて切断する。
 */
- (void)startDisconnectDicePlus;

/*!
 @brief 指定されたUIDのDice+を取得する。
 
 @param[in] uid ID
 
 @retval DPDie 指定されたUIDのDice+
 @retval nil 指定されたUIDに対応するDice+がない場合
 */
- (DPDie *)getDieByUID:(NSString *)uid;



- (void)getBatteryOfDie:(DPDie *)die block:(DPDicePlusBatteryBlock)block;
- (void)addBatteryStateOfDie:(DPDie *)die block:(DPDicePlusBatteryStateBlock)block;
- (void)removeBatteryStateOfDie:(DPDie *)die;
- (void)addBatteryChargingOfDie:(DPDie *)die block:(DPDicePlusBatteryChargingBlock)block;
- (void)removeBatteryChargingOfDie:(DPDie *)die;



/*!
 @brief Rollイベントを通知するためのブロックを追加する。
 
 @param[in] die 目を通知するDice+
 @param[in] block 通知するブロック
 */
- (void)addRollOfDie:(DPDie *)die block:(DPDicePlusRollBlock)block;

/*!
 @brief Rollイベントを削除する。
 @param[in] die 目を通知するDice+
 */
- (void)removeRollOfDie:(DPDie *)die;

/*!
 @brief 温度情報を取得する。
 
 @param[in] die 温度情報を取得するDice+
 @param[in] block 温度情報を通知するブロック
 */
- (void)getTemperatureOfDie:(DPDie *)die block:(DPDicePlusTemperatureBlock)block;

/*!
 @brief Dice+のライトを付ける。
 
 @param[in] die Dice+
 @param[in] lightId ライトID
 @param[in] red 赤色(0〜255)
 @param[in] green 緑色(0〜255)
 @param[in] blue 青色(0〜255)
 */
- (void)turnOnLightOfDie:(DPDie *)die lightId:(int)lightId red:(int)r green:(int)g blue:(int)b;

/*!
 @brief Dice+のライトを消灯する。
 
 @param[in] die Dice+
 @param[in] lightId ライトID
 */
- (void)turnOffLightOfDie:(DPDie *)die lightId:(int)lightId;

/*!
 @brief 加速度センサーのイベントを追加する。
 
 @param[in] die Dice+
 @param[in] block イベントを通知するブロック
 */
- (void)addOrientationOfDie:(DPDie *)die block:(DPDicePlusOrientationBlock)block;

/*!
 @brief 加速度センサーのイベントを削除する。
 
 @param[in] die Dice+
 */
- (void)removeOrientationOfDie:(DPDie *)die;


- (void)addMagnetometerOfDie:(DPDie *)die block:(DPDicePlusMagnetometerBlock)block;
- (void)removeMagnetometerOfDie:(DPDie *)die;

- (void)addProximityOfDie:(DPDie *)die block:(DPDicePlusProximityBlock)block;
- (void)removeProximityOfDie:(DPDie *)die;

@end
