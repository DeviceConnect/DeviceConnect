//
//  DPTouch.h
//  DicePlus
//
//  Created by Janusz Bossy on 10.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPTouch class contains a single measurement of die's touch sensor values.
 
 Your application received DPTouch objects when the sensor values change after calling
 the startTouchUpdates method of the DPDie object.
 */
@interface DPTouch : NSObject

/** @name Getting the touch values */
/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 A bit mask of the changes between this and previous state.
 */
@property unsigned int changeMask;

/**
 A bit mask of currently touched die's walls.
 */
@property unsigned int currentStateMask;

@end
