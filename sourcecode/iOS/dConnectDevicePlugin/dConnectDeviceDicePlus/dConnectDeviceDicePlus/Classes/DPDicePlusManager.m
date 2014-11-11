//
//  DPDicePlusManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import "DPDicePlusManager.h"

@interface DPDicePlusManager ()
@property (nonatomic) BOOL discoveryDice;

/*!
 @brief Dice+の情報を取得する。
 */
- (DPDicePlusData *) getDicePlusDataByDie:(DPDie *)die;

@end

@implementation DPDicePlusManager

- (id) init {
    self = [super init];
    if (self) {
        _connectDelegateList = [NSMutableArray array];
        _diceList = [NSMutableArray array];
        _diceDatas = [NSMutableDictionary dictionary];
        _foundDiceList = [NSMutableArray array];
        _discoveryDice = NO;
        
        // Dice+の初期化
        _diceManager = [DPDiceManager sharedDiceManager];
        _diceManager.delegate = self;
        
        // Dice+のキー (ただし、このキーはDeveloperバージョン)
        uint8_t key[8] = {0x83, 0xed, 0x60, 0x0e, 0x5d, 0x31, 0x8f, 0xe7};
        [_diceManager setKey:key];
    }
    return self;
}

#pragma Public Method

+ (DPDicePlusManager *)sharedManager {
    static DPDicePlusManager *sharedDPManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedDPManager = [DPDicePlusManager new];
    });
    return sharedDPManager;
}

- (void)startConnectDicePlus {
    // カウントを初期化
    _discoveryDice = NO;
    // スキャン開始
    [_diceManager startScan];
}
- (void)startConnectDicePlusByDPDie:(DPDie *)die {
    _discoveryDice = YES;
    [_diceManager connectDie:die];
}

- (void)startDisonnectDicePlusByDPDie:(DPDie *)die {
    [_diceManager disconnectDie:die];
}

- (void)startReconnectDicePlus {
    for (DPDie *die in _diceList) {
        [_diceManager connectDie:die];
    }
    
    dispatch_time_t popTime = dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC));
    dispatch_after(popTime, dispatch_get_main_queue(), ^(void) {
        for (DPDie *die in _diceList) {
            DPDicePlusData *data = [self getDicePlusDataByDie:die];
            //イベントのBlockがあれば再登録
            if (data.rollEventBlock) {
                [die startRollUpdates];
            }
            if ([data checkBattery]) {
                [die startBatteryUpdates];
            }
            if (data.temperatureBlocks.count > 0) {
                [die startThermometerUpdates];
            }
            if (data.orientationEventBlock) {
                [die startAccelerometerUpdates];
                [die startOrientationUpdates];
            }
            if (data.magnetometorEventBlock) {
                [die startMagnetometerUpdates];
            }
            if (data.proximityEventBlock) {
                [die startProximityUpdates];
            }
        }
    });

}

- (void)startDisconnectDicePlus {
    [_diceManager disconnectAllDice];
}

- (DPDie *)getDieByUID:(NSString *)uid {
    for (DPDie *die in _diceList) {
        if ([uid isEqualToString:die.UID]) {
            return die;
        }
    }
    return nil;
}

- (DPDicePlusData *) getDicePlusDataByDie:(DPDie *)die {
    DPDicePlusData *data = [_diceDatas objectForKey:die.UID];
    if (data == nil) {
        data = [DPDicePlusData new];
        [_diceDatas setObject:data forKey:die.UID];
    }
    return data;
}


#pragma mark - Battery Methods

- (void)getBatteryOfDie:(DPDie *)die block:(DPDicePlusBatteryBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (![dice checkBattery]) {
        [die startBatteryUpdates];
    }
    [dice.batteryBlocks addObject:block];
}

- (void)addBatteryStateOfDie:(DPDie *)die block:(DPDicePlusBatteryStateBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (![dice checkBattery]) {
        [die startBatteryUpdates];
    }
    dice.batteryEventBlock = block;
}

- (void)removeBatteryStateOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
     dice.batteryEventBlock = nil;
    if (![dice checkBattery]) {
        [die stopBatteryUpdates];
    }
}

- (void)addBatteryChargingOfDie:(DPDie *)die block:(DPDicePlusBatteryChargingBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (![dice checkBattery]) {
        [die startBatteryUpdates];
    }
    dice.chargingEventBlock = block;
}

- (void)removeBatteryChargingOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.chargingEventBlock = nil;
    if (![dice checkBattery]) {
        [die stopBatteryUpdates];
    }
}


#pragma mark - Roll

- (void)addRollOfDie:(DPDie *)die block:(DPDicePlusRollBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (dice.rollEventBlock == nil) {
        [die startRollUpdates];
    }
    dice.rollEventBlock = block;
}

- (void)removeRollOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.rollEventBlock = nil;
    [die stopRollUpdates];
}

#pragma mark - Temperature

- (void)getTemperatureOfDie:(DPDie *)die block:(DPDicePlusTemperatureBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (![dice.temperatureBlocks containsObject:block]) {
        [dice.temperatureBlocks addObject:block];
    }
    if (dice.temperatureBlocks.count > 0) {
        [die startThermometerUpdates];
    }
}

#pragma mark - Orientation

- (void)addOrientationOfDie:(DPDie *)die block:(DPDicePlusOrientationBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (!dice.orientationEventBlock) {
        [die startAccelerometerUpdates];
        [die startOrientationUpdates];
    }
    dice.orientationEventBlock = block;
}

- (void)removeOrientationOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.orientationEventBlock = nil;
    [die stopAccelerometerUpdates];
    [die stopOrientationUpdates];
}

#pragma mark - Magnetometer

- (void)addMagnetometerOfDie:(DPDie *)die block:(DPDicePlusMagnetometerBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (!dice.magnetometorEventBlock) {
        [die startMagnetometerUpdates];
    }
    dice.magnetometorEventBlock = block;
}

- (void)removeMagnetometerOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.magnetometorEventBlock = nil;
    [die stopMagnetometerUpdates];
}

#pragma mark - Proximity

- (void)addProximityOfDie:(DPDie *)die block:(DPDicePlusProximityBlock)block {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    if (!dice.proximityEventBlock) {
        [die startProximityUpdates];
    }
    dice.proximityEventBlock = block;
}

- (void)removeProximityOfDie:(DPDie *)die {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.proximityEventBlock = nil;
    [die stopProximityUpdates];
}


#pragma mark - DPDiceManagerDelegate

- (void)diceManager:(DPDiceManager *)manager didDiscoverDie:(DPDie *)die {
    die.delegate = self;
    if (![_foundDiceList containsObject:die]) {
        [_foundDiceList addObject:die];
    }
}

-(void)diceManagerStoppedScan:(DPDiceManager *)manager {
    if (!_discoveryDice) {
        for (id delegate in _connectDelegateList) {
            if ([delegate respondsToSelector:@selector(manager:didFinishedScan:)]) {
                [delegate manager:self didFinishedScan:_foundDiceList];
            }
        }
    }
}

-(void)centralManagerDidUpdateState:(CBCentralManagerState)state {
    //DICE+がONであることを見つけられているが、表示されないとき対策のため、このときにもう一度スキャンしている。
    if (state == CBCentralManagerStatePoweredOn) {
        [_diceManager startScan];
    }
}

-(void)diceManager:(DPDiceManager *)manager didConnectDie:(DPDie *)die {
    // 接続完了したDice+を追加

    if (![_diceList containsObject:die]) {
        [_diceList addObject:die];
    }
    // 発見されたことを通知
    for (id delegate in _connectDelegateList) {
        [delegate manager:self didConnectDie:die];
    }
    
    // 次のDice+を検索
    _discoveryDice = NO;
    [_diceManager startScan];
}

-(void)diceManager:(DPDiceManager *)manager didDisconnectDie:(DPDie *)die error:(NSError *)error {
    [_diceList removeObject:die];
    if (die.UID) {
        [_diceDatas removeObjectForKey:die.UID];
    }
    for (id delegate in _connectDelegateList) {
        [delegate manager:self didDisconnectDie:die];
    }
}

-(void)diceManager:(DPDiceManager *)manager failedConnectingDie:(DPDie *)die error:(NSError *)error {
    if ([error.description rangeOfString:@"Invalid SDK key"].location != NSNotFound) {  //DiceがDeveloperのファームウェアではない場合
        for (id delegate in _connectDelegateList) {
            [delegate manager:self didFailConnecttDie:die];
        }
        
        _discoveryDice = NO;
        [_diceManager startScan];
    }
}

#pragma mark - DPDieDelegate
#pragma mark - Roll

- (void)die:(DPDie *)die didRoll:(DPRoll *)roll error:(NSError *)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    DPDicePlusRollBlock block = dice.rollEventBlock;
    dice.rollNowPip = roll.result;
    if (block) {
        block(roll.result);
    }
}

#pragma mark - Battery

- (void)die:(DPDie *)die didUpdateBatteryStatus:(DPBattery *)status error:(NSError*)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    
    BOOL charging = status.isCharging;
    double level = status.level / (double) 100.0f;
    
    // 前回のバッテリー状態から充電状態が変わったか判断する
    BOOL isChargingEvent = false;
    DPBattery *old = dice.batteryState;
    if (old && old.isCharging != status.isCharging) {
        isChargingEvent = true;
    }
    dice.batteryState = status;
    
    // イベント振り分け
    if (isChargingEvent) {
        DPDicePlusBatteryChargingBlock block = dice.chargingEventBlock;
        if (block) {
            block(charging);
        }
    } else {
        DPDicePlusBatteryStateBlock block = dice.batteryEventBlock;
        if (block) {
            block(level);
        }
    }
    
    NSArray *blocks = dice.batteryBlocks;
    if (blocks) {
        for (DPDicePlusBatteryBlock block in blocks) {
            block(charging, level);
        }
        [dice.batteryBlocks removeAllObjects];
    }
    
    // バッテリイベントの登録がなくなった場合には停止
    if (![dice checkBattery]) {
        [die stopBatteryUpdates];
    }
}

#pragma mark - Temperature

- (void)die:(DPDie *)die didUpdateTemperature:(DPTemperature *)temperature error:(NSError *)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];

    NSArray *blocks = dice.temperatureBlocks;
    if (blocks) {
        for (DPDicePlusTemperatureBlock block in blocks) {
            block(temperature.temperature);
        }
        [dice.temperatureBlocks removeAllObjects];
    }

    [die stopThermometerUpdates];
}

#pragma mark - Accelerate

- (void) die:(DPDie *)die didAccelerate:(DPAcceleration *)acceleration error:(NSError *)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    dice.accel = acceleration;
}

- (void) die:(DPDie *)die didUpdateOrientation:(DPOrientation *)orientation error:(NSError *)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    
    int interval = 0;
    DPOrientation *old = dice.orien;
    if (old) {
        interval = (int) (orientation.timestamp - old.timestamp);
    }
    dice.orien = orientation;
    
    
    DPDicePlusOrientationBlock block = dice.orientationEventBlock;
    if (block) {
        DPAcceleration *accel = dice.accel;
        if (accel) {
            double ax = accel.x / (double) 1000 * 9.81;
            double ay = accel.y / (double) 1000 * 9.81;
            double az = accel.z / (double) 1000 * 9.81;
            double yaw = orientation.yaw;
            double pitch = orientation.pitch;
            double roll = orientation.roll;
            block(ax, ay, az, roll, pitch, yaw, interval);
        }
    }
}

#pragma mark - Magneto

- (void)die:(DPDie *)die didUpdateMagnetometer:(DPMagnetometer *)data error:(NSError *)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    
    int interval = 0;
    DPMagnetometer *old = dice.magnetometer;
    if (old) {
        interval = data.timestamp - old.timestamp;
    }
    dice.magnetometer = data;
    
    DPDicePlusMagnetometerBlock block = dice.magnetometorEventBlock;
    if (block) {
        block(data.x, data.y, data.z, interval, data.filter);
    }
}

#pragma mark - Proximity

- (void)die:(DPDie*)die didUpdateProximity:(DPProximity*)proximity error:(NSError*)error {
    DPDicePlusData *dice = [self getDicePlusDataByDie:die];
    DPDicePlusProximityBlock block = dice.proximityEventBlock;
    float proximityMainValue = [[proximity.values objectAtIndex:0] floatValue];
    if (block) {
        block(proximityMainValue, 0, 100, 0);
    }
}



#pragma mark - Light

- (void)turnOnLightOfDie:(DPDie *)die lightId:(int)lightId red:(int)r green:(int)g blue:(int)b {
    [die startBlinkAnimationWithMask:lightId priority:255 r:r g:g b:b onPeriod:65535 cyclePeriod: 0 blinkCount:1];
}

- (void)turnOffLightOfDie:(DPDie *)die lightId:(int)lightId {
    [die startBlinkAnimationWithMask:lightId priority:255 r:0 g:0 b:0 onPeriod:1 cyclePeriod: 0 blinkCount:1];
}

@end
