//
//  DPBattery.h
//  iOS SDK
//
//  Created by Janusz Bossy on 22.07.2013.
//  Copyright (c) 2013 Janusz Bossy. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPBattery class contains a single measurement of die's battery state.
 
 Your application receives DPBattery objects at regular intervals after calling the
 startBatteryUpdates method of the DPDie object.
 */
@interface DPBattery : NSObject

/**
 Current battery level in percents.
 */
@property int level;

/**
 Current charging state.
 */
@property bool isCharging;

/**
 Indicator if the battery level is low.
 */
@property bool isLow;

@end
