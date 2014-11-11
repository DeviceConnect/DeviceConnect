//
//  DPAcceleration.h
//  DicePlus
//
//  Created by Janusz Bossy on 03.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Enums.h"

/**
 An instance of DPAcceleration class contains a single measurement of die's accelerometer values.
 
 Your application receives DPAcceleration objects at regular intervals after calling the
 startAccelerometerUpdates method of the DPDie object.
 */
@interface DPAcceleration : NSObject

/** @name Getting the accelerometer values */

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 The filter type for this accelerometer readout.
 */
@property DPAccelerometerFilter filter;

/**
 The X-axis acceleration rate in milli Gs.
 */
@property int x;

/**
 The Y-axis acceleration rate in milli Gs.
 */
@property int y;


/**
 The Z-axis acceleration rate in milli Gs.
 */
@property int z;

@end
