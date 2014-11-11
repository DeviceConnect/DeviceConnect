//
//  DPFaceChange.h
//  iOS SDK
//
//  Created by Janusz Bossy on 05.06.2013.
//  Copyright (c) 2013 Janusz Bossy. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPFaceChange class contains a single measurement of die's accelerometer values.
 
 Your application receives DPFaceChange objects at regular intervals after calling the
 startFaceChangeUpdates method of the DPDie object.
 */

@interface DPFaceChange : NSObject

/** @name Getting the accelerometer values */

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 Number of the face currently pointing up.
 */
@property uint8_t face;

@end
