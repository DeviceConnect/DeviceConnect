//
//  DPSoftwareVersion.h
//  Mac OS X SDK
//
//  Created by Janusz Bossy on 29.05.2013.
//  Copyright (c) 2013 Janusz Bossy. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 An instance of DPSoftwareVersion class contains information about the software version of DICE+.
 */
@interface DPSoftwareVersion : NSObject

- (id)initWithMajor:(unsigned int)major minor:(unsigned int)minor andBuild:(unsigned int)build;

/**
 The major version of the software.
 */
@property (readonly) unsigned int major;

/**
 The minor version of the software.
 */
@property (readonly) unsigned int minor;

/**
 The build version of the software.
 */
@property (readonly) unsigned int build;

@end
