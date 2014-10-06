//
//  DeviceList.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>
#import "DeviceInfo.h"

@interface DeviceList : NSObject

+ (void) addDevice : (DeviceInfo*) device;

+ (void) selectDeviceAt:(long) position;

+ (DeviceInfo*) getDeviceAt:(long) position;

+ (DeviceInfo*) getSelectedDevice;

+ (long) getSize;

+ (void) reset;

@end
