//
//  DPTemperature.h
//  DicePlus
//
//  Created by Janusz Bossy on 10.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPTemperature class contains a single measurement of die's temperature.
 
 Your application received DPTemperature objects at regular intervals after calling the
 startThermometerUpdates method of DPDie object.
 */
@interface DPTemperature : NSObject

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 Current temperature expressed in Celsius.
 */
@property float temperature;

@end
