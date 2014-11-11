//
//  DPHardwareVersion.h
//  Mac OS X SDK
//
//  Created by Janusz Bossy on 29.05.2013.
//  Copyright (c) 2013 Janusz Bossy. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPHardwareVersion class contains information about the hardware version of DICE+.
 */
@interface DPHardwareVersion : NSObject

- (id)initWithMajor:(unsigned int)major andMinor:(unsigned int)minor;

/**
 The major version of the hardware.
 */
@property (readonly) unsigned int major;

/**
 The minor version of the hardware.
 */
@property (readonly) unsigned int minor;

@end
