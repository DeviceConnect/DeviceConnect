//
//  DPLedStatus.h
//  DicePlus
//
//  Created by Janusz Bossy on 28.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPLedState class contains a single measurement of die's LED states.
 
 Your application received DPLedState objects when their values change after calling the
 startLedUpdates method of the DPDie object.
 */
@interface DPLedState : NSObject

/**
 The timestamp at which the measurement was taken. Expressed by milliseconds from the die's
 startup.
 */
@property unsigned int timestamp;

/**
 ID of the animation this event is related to.
 */
@property unsigned int animationId;

/**
 Bit mask indicating which die faces have finished led animation.
 */
@property uint32_t mask;

/**
 The type property indicating if this is the start or end of an animation and if it's a
 user or system animation
 */
@property uint8_t type;

/**
 If true the event means that the animation has finished. Otherwise - it's started.
 */
@property (readonly) bool isFinished;

/**
 If true the event is corresponding to your application's animation. Otherwise - it's an
 internal DICE+ animation.
 */
@property (readonly) bool isUserAnimation;

@end
