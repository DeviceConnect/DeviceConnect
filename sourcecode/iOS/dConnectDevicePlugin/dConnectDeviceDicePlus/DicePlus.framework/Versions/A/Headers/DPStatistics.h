//
//  DPStatistics.h
//  DicePlus
//
//  Created by Janusz Bossy on 28.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPStatistics class contains a single measurement of die's statistics values.
 
 Your applications received an DPStatistics object after calling the readStatistics method of
 the DPDie object.
 */
@interface DPStatistics : NSObject

/**
 Total numbers of rolls.
 */
@property uint32_t totalRolls;

/**
 Number of valid rolls.
 */
@property uint32_t validRolls;

/**
 Number of valid rolls per face.
 */
@property (retain) NSArray* rolls;

/**
 Number of seconds DICE+ was connected to a game.
 */
@property uint32_t connectedTime;

/**
 Number of times DICE+ was authenticated.
 */
@property uint32_t timesAuthenticated;

/**
 Number of milliseconds DICE+ was rolling.
 */
@property uint32_t rollTime;

/**
 Number of times DICE+ was being charged.
 */
@property uint32_t chargingCycles;

/**
 Number of seconds DICE+ was being charged.
 */
@property uint32_t chargingTime;

/**
 Number of times DICE+ has waken up.
 */
@property uint32_t wakeupCount;

@end
