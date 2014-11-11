//
//  DPMagnetometer.h
//  DicePlus
//
//  Created by Janusz Bossy on 04.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Enums.h"

/**
 An instance of DPMagnetometer class contains a single measurement of the magnetic field captured
 by the die's magnetometer.
 
 Your application receives DPMagnetometer objects at regular intervals after calling the
 startMagnetometerUpdates method of the DPDie shared object.

 */
@interface DPMagnetometer : NSObject

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 The filter type for this magnetometer readout.
 */
@property DPMagnetometerFilter filter;

/**
 The X-axis value in micro Teslas.
 */
@property int x;

/**
 The Y-axis value in micro Teslas.
 */
@property int y;

/**
 The Z-asix value in micro Teslas.
 */
@property int z;

@end
