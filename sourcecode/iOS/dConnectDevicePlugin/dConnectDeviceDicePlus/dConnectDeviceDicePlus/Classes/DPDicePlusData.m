//
//  DPDicePlusData.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusData.h"

@implementation DPDicePlusData

- (id) init {
    self = [super init];
    if (self) {
        _batteryBlocks = [NSMutableArray array];
        _batteryEventBlock = nil;
        _chargingEventBlock = nil;
        
        _temperatureBlocks = [NSMutableArray array];
        
        _rollEventBlock = nil;
        _rollNowPip = 0; //1の目を初期値とする
        
        _orientationEventBlock = nil;
        _accel = nil;
        _orien = nil;
        
        _magnetometorEventBlock = nil;
        
        _proximityEventBlock = nil;
    }
    return self;
}

- (BOOL) checkBattery {
    return _batteryBlocks.count > 0 || _batteryEventBlock || _chargingEventBlock;
}

@end
