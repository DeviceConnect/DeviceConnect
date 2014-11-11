//
//  DPOrientation.h
//  DicePlus
//
//  Created by Janusz Bossy on 04.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPOrientation class contains a single measurement of die's orientation values.
 
 Your application received DPOrientation objects at regular intervals after calling the
 startOrientationUpdates method of the DPDie object.
 */
@interface DPOrientation : NSObject

/** @name Getting the orientation values */
/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 The roll value expressed in degrees.
 */
@property int roll;

/**
 The pitch value expressed in degrees.
 */
@property int pitch;

/**
 The yaw value expressed in degrees.
 */
@property int yaw;

@end
