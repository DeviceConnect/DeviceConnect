//
//  DPProximity.h
//  DicePlus
//
//  Created by Janusz Bossy on 10.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPProximity class contains a single measurement of die's proximity values.
 
 Your application receives DPProximity objects when their values change after calling the
 startProximityUpdates method of the DPDie object.
 */
@interface DPProximity : NSObject

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 Array of proximity sensor readouts for each of the die's walls.
 */
@property (retain) NSArray* values;

@end
