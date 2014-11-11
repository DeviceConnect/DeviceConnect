//
//  DPTap.h
//  iOS SDK
//
//  Created by Janusz Bossy on 05.06.2013.
//  Copyright (c) 2013 Janusz Bossy. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPTap class contains a single measurement of die's taps.
 
 Your application receives DPTap objects at regular intervals after calling the
 startTapUpdates method of the DPDie object.
 */

@interface DPTap : NSObject

/** @name Getting the tap values */

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 The X-axis value of the tap's magnitude, expressed in milli Gs.
 */
@property int16_t x;

/**
 The Y-axis value of the tap's magnitude, expressed in milli Gs.
 */
@property int16_t y;

/**
 The Z-axis value of the tap's magnitude, expressed in milli Gs.
 */
@property int16_t z;

@end
