//
//  DPRoll.h
//  DicePlus
//
//  Created by Janusz Bossy on 02.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPRoll class contains a single measurement of die's roll value.
 
 Your application received DPRoll objects at regular intervals after calling the
 startRollUpdates method of the DPDie object.
 */
@interface DPRoll : NSObject

/** @name Getting the roll values */

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds
 from the die's startup.
 */
@property int timestamp;

/**
 The duration of the roll.
 */
@property int duration;

/**
 The flags of the roll. See DPRollFlag for available values.
 */
@property int flags;

/**
 The result of the roll. Value "0" indicates an error.
 */
@property int result;

@end
