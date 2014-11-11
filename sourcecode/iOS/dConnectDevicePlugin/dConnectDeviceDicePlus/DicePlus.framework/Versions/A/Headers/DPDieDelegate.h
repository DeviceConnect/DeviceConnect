//
//  DPDieDelegate.h
//  DicePlus
//
//  Created by Janusz Bossy on 04.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DPDie.h"
#import "DPAcceleration.h"
#import "DPMagnetometer.h"
#import "DPOrientation.h"
#import "DPTemperature.h"
#import "DPProximity.h"
#import "DPTouch.h"
#import "DPStatistics.h"
#import "DPLedState.h"
#import "DPPowerMode.h"
#import "DPTap.h"
#import "DPFaceChange.h"
#import "DPBattery.h"

/**
 The DPDieDelegate protocol defines methods used to receive update notifications and events from
 DPDie object.
 */
@protocol DPDieDelegate <NSObject>
@optional
#pragma mark - Connection state updates
/** @name Connection state upates */
/**
 Invoked when the die is connected and ready to accept messages.
 
 @param die The die object initiating the callback.
 */
- (void)dieConnected:(DPDie*)die;

#pragma mark - Notification state change updates
/** @name Notification state change updates */
/**
 Invoked after subscribing to sensor updates for a die.
 
 @param die The die object initiating the callback.
 @param sensor The sensor which changed the norification state.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateNotificationStateForSensor:(DPSensor)sensor withError:(NSError*)error;

#pragma mark - Notification callbacks
/** @name Notification callbacks */
/**
 Invoked when charging state changes.
 
 @param die The die object initiating the callback.
 @param isCharging Set to "true" when die is charging; "false" otherwise.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didChangeChargingState:(bool)isCharging error:(NSError*)error;

/**
 Invoked when LED state changes.
 
 @param die The die object initiating the callback.
 @param status The current LED status.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didChangeLedState:(DPLedState*)status error:(NSError*)error;

/**
 Invoked when die was rolled.
 
 @param die The die object initiating the callback.
 @param roll The current roll result.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didRoll:(DPRoll*)roll error:(NSError*)error;

/**
 Invoked when accelerometer values change.
 
 This method is called based on the requested frequency.
 
 @param die The die object initiating the callback.
 @param acceleration The accelerator values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didAccelerate:(DPAcceleration*)acceleration error:(NSError*)error;

/**
 Invoked when gyroscope values change.
 
 This method is called based on the requested frequency.
 
 @param die The die object initiating the callback.
 @param data The gyroscope values.
 @param error If unsuccessful, set with the encountered failure.
 */
//- (void)die:(DPDie*)die didUpdateGyro:(DPGyroData*)data error:(NSError*)error;

/**
 Invoked when magnetometer values change.
 
 This method is called based on the requested frequency.
 
 @param die The die object initiating the callback.
 @param data The magnetometer values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateMagnetometer:(DPMagnetometer*)data error:(NSError*)error;

/**
 Invoked when orientation values change.
 
 This method is called based on the requested frequency.
 
 @param die The die object initiating the callback.
 @param orientation The orientation values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateOrientation:(DPOrientation*)orientation error:(NSError*)error;

/**
 Invoked when temperature changes.
 
 @param die The die object initiating the callback.
 @param temperature The temperature values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateTemperature:(DPTemperature*)temperature error:(NSError*)error;

/**
 Invoked when proximity values change.
 
 This method is called based on the requested frequency.
 
 @param die The die object initiating the callback.
 @param proximity The proximity values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateProximity:(DPProximity*)proximity error:(NSError*)error;

/**
 Invoked when touch values change.
 
 @param die The die object initiating the callback.
 @param touch The touch values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateTouches:(DPTouch*)touch error:(NSError*)error;

/**
 Invoked after requesting die's statistics.
 
 @param die The die object initiating the callback.
 @param statistics The statistics values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didReceiveStatistics:(DPStatistics*)statistics error:(NSError*)error;

/**
 Invoked when die's power mode changes.
 
 @param die The die object initiating the callback.
 @param mode The current power mode.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdatePowerMode:(DPPowerMode*)mode error:(NSError*)error;

/**
 Invoked when battery level or status changes.
 
 @param die The die object initiating the callback.
 @param status The current status of the battery.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didUpdateBatteryStatus:(DPBattery*)status error:(NSError*)error;

/**
 Invoked when the die is tapped.
 
 @param die The die object initiating the callback.
 @param tap The current tap values.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didTap:(DPTap*)tap error:(NSError*)error;

/**
 Invoked when die's face is changed.
 
 @param die The die object initiating the callback.
 @param faceChange The current face value.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didChangeFace:(DPFaceChange*)faceChange error:(NSError*)error;

//#pragma mark - Accessing sensor configuration
/* @name Accessing sensor configuration */
/**
 Invoked after the accelerometer config is successfully read.
 
 @param die The die object initiating the callback.
 @param frequency The set frequency.
 @param filter The set filter.
 */
//- (void)die:(DPDie*)die didReadAccelerometerConfigWithFrequency:(int)frequency andFilter:(DPAccelerometerFilter)filter;

/**
 Invoked after the gyroscope config is successfully read.
 
 @param die The die object initiating the callback.
 @param frequency The set frequency.
 */
//- (void)die:(DPDie*)die didReadGyroscopeConfigWithFrequency:(int)frequency;

/**
 Invoked after the magnetometer config us successfyly read.
 
 @param die The die object initiating the callback.
 @param frequency The set frequency.
 @param filter The set filter.
 */
//- (void)die:(DPDie*)die didReadMagnetometerConfigWithFrequency:(int)frequency andFilter:(DPMagnetometerFilter)filter;

/**
 Invoked after the orientation config is successfully read.
 
 @param die The die object initiating the callback.
 @param frequency The set frequency.
 */
//- (void)die:(DPDie*)die didReadOrientationConfigWithFrequency:(int)frequency;

/**
 Invoked after the proximity config is successfully read.
 
 @param die The die object initiating the callback.
 @param frequency The set frequency.
 */
//- (void)die:(DPDie*)die didReadProximityConfigWithFrequency:(int)frequency;

#pragma mark - Error handling
/** @name Error handling */
/**
 Invoked when start animation fails.
 
 @param die The die object initiating the callback.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die startedAnimation:(NSError*)error;

/**
 Invoked when subscribing for sensor updates fails.
 
 @param die The die object initiating the callback.
 @param sensor The sensor which failed to start sending notifications.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die failedToStartUpdatesForSensor:(DPSensor)sensor withError:(NSError*)error;

/**
 Invoked when unsubscribing for sensor updates fails.
 
 @param die The die object initiating the callback.
 @param sensor The sensor which failed to stop sending notifications.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die failedToStopUpdatesForSensor:(DPSensor)sensor withError:(NSError*)error;

/**
 Invoked when sleep request fails.
 
 @param die The die object initiating the callback.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die didFinishSleepRequest:(NSError*)error;

/**
 Invoked when set operation mode request fails.
 
 @param die The die object initiating the callback.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die didFinishSetModeRequest:(NSError*)error;

#pragma mark - Persistent storage callbacks
/* @name Persistent storage callbacks */
/**
 Invoked after the persistent storage communication has been initialized.
 
 @param die The die object initiating the callback.
 @param descriptors Contains all available persistent storage variable descriptors.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didInitializePersistenStorage:(NSArray*)descriptors error:(NSError*)error;

/** @name Persistent storage variables callbacks */
/**
 Invoked after resetting the persistent storage to default values.
 
 @param die The die object initiating the callback.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)dieDidResetPersistentStorage:(DPDie*)die error:(NSError*)error;

/**
 Invoked after reading the value for a persistent storage variable.
 
 @param die The die object initiating the call.
 @param value The current value of the variable. Depending on the type of the persistent storage variable
 it's either an NSNumber for integer variables, NSArray for vector variables or NSString for string variables.
 @param handle The handle for the persistent storage variable which value was read.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didReadPersistentValue:(id)value forHandle:(int)handle error:(NSError*)error;

/**
 Invoked after setting value for a persistent storage variable.
 
 @param die The die object initiating the callback.
 @param handle The handle of the persistent storage variable which value was set.
 @param error If unsuccessful, set with the encountered failure.
 */
- (void)die:(DPDie*)die didWritePersistentValueForHandle:(int)handle error:(NSError*)error;

/**
 Invoked when an error occurs outside the die communication process, i.e. wrong paramters passed to set value.
 
 @param die The die object initiating the callback.
 @param error The error object wrapping encountered failure.
 */
- (void)die:(DPDie*)die didFinishPersistentStorageOperation:(NSError*)error;

@end
