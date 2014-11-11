//
//  DPPowerMode.h
//  DicePlus
//
//  Created by Natalia Czarnecka on 13.02.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPPowerMode class contains values indicating the die's current power mode.
 
 Your application receives DPPowerMode objects when the power mode changes after calling the
 startPowerModeUpdates method of the DPDie object.
 */
@interface DPPowerMode : NSObject

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 The current power mode.
 */
@property unsigned int mode;

@end
