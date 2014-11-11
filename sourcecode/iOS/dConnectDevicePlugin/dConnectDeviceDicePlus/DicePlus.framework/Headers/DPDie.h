//
//  DPDie.h
//  DicePlus
//
//  Created by Janusz Bossy on 31.12.2012.
//  Copyright (c) 2012 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DPRoll.h"
#import "DPAcceleration.h"
#import "DPSoftwareVersion.h"
#import "DPHardwareVersion.h"
#import "Enums.h"

#if TARGET_OS_IPHONE
#import <CoreBluetooth/CoreBluetooth.h>
#elif TARGET_OS_MAC
#import <IOBluetooth/IOBluetooth.h>
#endif

@protocol DPDieDelegate;

/**
 A DPDie object is a wrapper for the Dice+ CBPeripheral instance. It allows the developer to
 easily access the Die attributes as well as read the current sesor values and subscribe to
 notifications about the sensor changes.
 
 DPDie instances are returned by the shared DPDiceManager instance when scanning for available
 dice. DPDie should never be instantiated by the developer.
 
 Using the DPDie instance the developer can access the basic infor about the die: name, RSSI,
 UUID, faceCount, ledCount etc. After the die is connected it's possible to read and subscribe
 to the sensor changes: roll results, accelerometer, magnetometer etc.
 */
@interface DPDie : NSObject<CBPeripheralDelegate
#if !TARGET_OS_IPHONE
                            , IOBluetoothRFCOMMChannelDelegate
#endif
                            > {
    NSMutableArray* _persistentStorageDescriptiors;
}

#pragma mark - Properties
/** @name Accessing the delegate object */
/**
  The delegate object to receive update events.
 */
@property (assign) id<DPDieDelegate> delegate;

/**
 Array containing descriptors for available persistent storage variables. It contains instances
 of DPPersistentStorageDescriptor class.
 
 Please note that this property is available only after calling the 
 [DPDie initializePersistentStorageCommunication] method.
 */
@property (retain, readonly) NSArray* persistentStorageDescriptors;
@property (retain) CBPeripheral* peripheral;
@property (readonly) NSNumber* systemStatus;

#if !TARGET_OS_IPHONE
@property (retain) IOBluetoothDevice* device;
@property (retain) IOBluetoothRFCOMMChannel* channel;
- (id)initWithDevice:(IOBluetoothDevice*)device;
#endif

/**
 The unique identifier of this DICE+.
 */
@property (readonly) NSString* UID;

/**
 The name of the connected die.
 */
@property (readonly) NSString* name;

/**
 The current RSSI from the iOS device to the die.
 */
@property (retain) NSNumber* RSSI;

/**
 The UUID of the die. This ID might be changed every time the die is discovered.
 */
@property (readonly) CBUUID* UUID;

/**
 The current version of software installed on the die.
 
 This property's value is available after being connected to the die.
 */
@property (readonly) DPSoftwareVersion* softwareVersion;

/**
 The hardware version of the die.

 This property's value is available after being connected to the die.
 */
@property (readonly) DPHardwareVersion* hardwareVersion;

/**
 The model number of the die
 
 This property's value is available after being connected to the die.
 */
@property (readonly) uint32_t modelNumber;

/**
 The number of faces this die has.
 
 This property's value is available after being connected to the die.
 */
@property (readonly) NSNumber* faceCount;

/**
 The number of faces that are LED enabled.
 
 This property's value is available after being connected to the die.
 */
@property (readonly) NSNumber* ledCount;

/**
 Checks if the die is currently connected over Bluetooth.
 
 @return "True" if connected, "false" otherwise.
 */
@property (readonly) BOOL isConnected;

//- (void)startNyanAnimation;
- (id)initWithPeripheral:(CBPeripheral*)peripheral;
- (void)dieDisconnected;

#pragma mark - Modifying die behaviour
/** @name Modifying die behaviour */
/**
 Set the die operation mode.
 
 This method allows the developer to turn off the automatic throw notifications.
 
 @param mode The desired operation mode. See DPDieMode for available values.
 */
- (void)setMode:(DPDieMode)mode;

/**
 Puts the die to sleep.
 */
- (void)sleep;

#pragma mark - Controlling LEDs
/** @name Controlling LEDs */
/**
 Start a blinking animation on the die walls.
 
 @param ledMask Binary mask of the die walls to be enabled.
 @param priority Animation's priority. Lower values indicate more important
 animations
 @param r Color red component in range from 0 to 255.
 @param g Color green component in range from 0 to 255.
 @param b Color blue component in range from 0 to 255.
 @param onPeriod Duration of the LED's ON state in ms.
 @param cyclePeriod Duration of the LED blinks in ms.
 @param blinkCount Number of blinks.
 */
- (void)startBlinkAnimationWithMask:(int)ledMask
                           priority:(int)priority
                                  r:(int)r
                                  g:(int)g
                                  b:(int)b
                           onPeriod:(int)onPeriod
                        cyclePeriod:(int)cyclePeriod
                         blinkCount:(int)blinkCount;

/**
 Start a fade animation on the die walls.
 
 @param ledMask Binary mask of the die walls to be enabled.
 @param priority Animation's priority. Lower values indicate more important
 animations
 @param r Color red component in range from 0 to 255.
 @param g Color green component in range from 0 to 255.
 @param b Color blue component in range from 0 to 255.
 @param fadeTime The duration of LED's fade in and fade out.
 @param pauseTime The duration the LEDs stay at full brightness before fading
 out.
 */
- (void)startFadeAnimationWithMask:(int)ledMask
                          priority:(int)priority
                                 r:(int)r
                                 g:(int)g
                                 b:(int)b
                          fadeTime:(int)fadeTime
                         pauseTime:(int)pauseTime;

/**
 Start a fade animation on the die walls.
 
 @param ledMask Binary mask of the die walls to be enabled.
 @param priority Animation's priority. Lower values indicate more important
 animations
 @param r Color red component in range from 0 to 255.
 @param g Color green component in range from 0 to 255.
 @param b Color blue component in range from 0 to 255.
 @param fadeTime The duration of LED's fade in and fade out.
 @param pauseTime The duration the LEDs stay at full brightness before fading
 out.
 @param repeatCount The number of times the die will fade in and out.
 */
- (void)startFadeAnimationWithMask:(int)ledMask
                          priority:(int)priority
                                 r:(int)r
                                 g:(int)g
                                 b:(int)b
                          fadeTime:(int)fadeTime
                         pauseTime:(int)pauseTime
                       repeatCount:(int)repeatCount;

/**
 Start a builtin animation on the die walls.
 
 @param ledMask Binary mask of the die walls to be enabled.
 @param priority Animation's priority. Lower values indicate more important
 animations
 @param animation Builtin animation's ID. See DPDieAnimation for details.
 */
- (void)startStandardAnimationWithMask:(int)ledMask
                              priority:(int)priority
                             animation:(DPDieAnimation)animation;

/**
 Send a one-time LED state request to the die.
 
 LED state allows the developer to check which of the die walls are still
 running any animations.
 
 See [DPDieDelegate die:didChangeLedState:error:] for appropriate callback method.
 */
- (void)readLedState;

/**
 Subscribe for LED state notitifcations.
 
 LED state allows the developer to check which of the die walls are still
 running any animations.
 
 See [DPDieDelegate die:didChangeLedState:error:] for appropriate callback method.
 */
- (void)startLedStateUpdates;

/**
 Unsubscribe from LED state notifications.
 
 LED state allows the developer to check which of the die walls are still
 running any animations.
 */
- (void)stopLedStateUpdates;

#pragma mark - Accessing roll results
/** @name Accessing roll results */
/**
 Subscribe for roll result notifications
 
 See [DPDieDelegate die:didRoll:error:] for appropriate callback method.
 */
- (void)startRollUpdates;

/**
 Unsubscribe from roll result notifications.
 */
- (void)stopRollUpdates;

/*
 Send a one-time roll result request to the die.
 
 See [DPDieDelegate die:didRoll:error:] for appropriate callback method.
 */
//- (void)readRoll;

#pragma mark - Accessing magnetometer data
/** @name Accessing magnetometer data */
/**
 Subscribe to magnetometer data notifications with 10Hz frequency and raw filter.
 
 The configuration can be set using setMangetometerUpdateFrequency:andFilter:.
 
 See [DPDieDelegate die:didUpdateMagnetometer:error:] for appropriate callback method.
 */
- (void)startMagnetometerUpdates;

/**
 Subscribe to magnetometer data notifications with custom frequency and filter.
 
 See [DPDieDelegate die:didUpdateMagnetometer:error:] for appropriate callback method.
 
 @param frequency The desired frequency in the range from 1 to 30 in Hz.
 @param filter The desired filter for magnetometer data. See
 DPMagnetometerFilter for available options.
 */
- (void)startMagnetometerUpdatesWithFrequency:(uint8_t)frequency andFilter:(unsigned char)filter;

/**
 Unsubscribe from magnetometer data notification.
 */
- (void)stopMagnetometerUpdates;

/*
 Send a one-time magnetometer data request.
 
 See [DPDieDelegate die:didUpdateMagnetometer:error:] for appropriate callback method.
 */
//- (void)readMagnetometer;

/*
 Set the desired frequency and filter for magnetometer updates.
 
 @param frequency The desired frequency in the range from 1 to 60 in Hz.
 @param filter The desired filter for magnetometer data. See
 DPMagnetometerFilter for available options.
 */
//- (void)setMagnetometerUpdateFrequency:(uint8_t)frequency andFilter:(DPMagnetometerFilter)filter;

#pragma mark - Accessing accelerometer data
/** @name Accessing accelerometer data */
/**
 Subscribe to accelerometer data notifications with 10Hz frequency and raw filter.
 
 The configuration can be set using setAccelerometerUpdateFrequency:andFilter:.
 
 See [DPDieDelegate die:didAccelerate:error:] for appropriate callback method.
 */
- (void)startAccelerometerUpdates;

/**
 Subscribe to accelerometer data notifications with custom frequency and filter.
 
 See [DPDieDelegate die:didAccelerate:error:] for appropriate callback method.
 
 @param frequency The desired frequency in the range from 1 to 60 Hz.
 @param filter The desired filter for accelerometer data. See DPAccelerometerFilter for available options.
 */
- (void)startAccelerometerUpdatesWithFrequency:(uint8_t)frequency andFilter:(unsigned char)filter;

/**
 Unsubscribe from accelerometer data notifications.
 */
- (void)stopAccelerometerUpdates;

/*
 Send a one-time accelerometer data request.
 
 See [DPDieDelegate die:didAccelerate:error:] for appropriate callback method.
 */
//- (void)readAccelerometer;

/*
 Set the desired frequency and filter for accelerometer updates.
 
 @param frequency The desired frequency in the range from 1 to 60 Hz.
 @param filter The desired filter for accelerometer data. See DPAccelerometerFilter for available options.
 */
//- (void)setAccelerometerUpdateFrequency:(uint8_t)frequency andFilter:(DPAccelerometerFilter)filter;

#pragma mark - Accessing thermometer data
/** @name Accessing thermometer data */
/**
 Subscribe for temperature updates.
 
 See [DPDieDelegate die:didUpdateTemperature:error:] for appropriate callback method. The
 callback method is called when temperature changes.
 */
- (void)startThermometerUpdates;

/**
 Unsubscribe from temperature updates.
 */
- (void)stopThermometerUpdates;

/*
 Send a one-time temperature request.
 
 See [DPDieDelegate die:didUpdateTemperature:error:] for appropraite callback method.
 */
//- (void)readThermometer;

#pragma mark - Accessing touch data
/** @name Accessing touch data */
/**
 Subscribe for touch notifications.
 
 See [DPDieDelegate die:didUpdateTouches:error:] for appropriate callback method. The callback
 method is called whenever touch sensors change value.
 */
- (void)startTouchUpdates;

/**
 Unsubscribe from touch notifications.
 */
- (void)stopTouchUpdates;

/*
 Send a one-time touch request.
 
 See [DPDieDelegate die:didUpdateTouches:error:] for appropriate callback method.
 */
//- (void)readTouch;

#pragma mark - Accessing proximity data
/** @name Accessing proximity data */
/**
 Subscribe for proximity updates with 5Hz frequency.
 
 See [DPDieDelegate die:didUpdateProximity:error:] for appropriate callback method.
 */
- (void)startProximityUpdates;

/**
 Subscribe for proximity updates with custom frequency.
 
 See [DPDieDelegate die:didUpdateProximity:error:] for appropriate callback method.
 
 @param frequency The desired update frequency in the range from 1 to 10 Hz.
 */
- (void)startProximityUpdatesWithFrequency:(uint8_t)frequency;

/**
 Unsubscribe from proximity updates.
 */
- (void)stopProximityUpdates;

/*
 Send a one-time proximity data request.
 
 See [DPDieDelegate die:didUpdateProximity:error:] for appropriate callback method.
 */
//- (void)readProximity;

/*
 Set the desired frequency for proxmity updates.
 
 @param frequency The desired update frequency in the range from 0 to 60 Hz.
 */
//- (void)setProximityUpdateFrequency:(uint8_t)frequency;

#pragma mark - Accessing orientation data
/** @name Accessing orientation data */
/**
 Subscribe for orientation updates with 10Hz frequency.
 
 See [DPDieDelegate die:didUpdateOrientation:error:] for appropriate callback method.
 */
- (void)startOrientationUpdates;

/**
 Subscribe for orientation updates with desired frequency.
 
 See [DPDieDelegate die:didUpdateOrientation:error:] for appropriate callback method.
 
 @param frequency The desired orientation update frequency in the range from 1 to 20 Hz.
 */
- (void)startOrientationUpdatesWithFrequency:(uint8_t)frequency;

/**
 Unsubscribe from orientation updates.
 */
- (void)stopOrientationUpdates;

/*
 Send a on-time orientation request.
 
 See [DPDieDelegate die:didUpdateOrientation:error:] for appropriate callback method.
 */
//- (void)readOrientation;

#pragma mark - Accessing battery state
/** @name Accessing battery state */
/**
 Subscribe for battery level updates.
 
 See [DPDieDelegate die:didUpdateBatteryStatus:error:] for appropriate callback method. The
 callback method is called when battery level changes.
 */
- (void)startBatteryUpdates;

/**
 Unsubscribe from battery level updates.
 */
- (void)stopBatteryUpdates;

#pragma mark - Accessing power mode
/** @name Accessing power mode */
/**
 Subscribe for power mode updates.
 
 See [DPDieDelegate die:didUpdatePowerMode:error:] for appropriate callback method. The callback
 method is called when power mode changes.
 */
- (void)startPowerModeUpdates;

/**
 Unsubscribe from power mode updates.
 */
- (void)stopPowerModeUpdates;

/*
 Send a one-time power mode request.
 
 See [DPDieDelegate die:didUpdatePowerMode:error:] for appropriate callback method.
 */
//- (void)readPowerMode;

#pragma mark - Accessing tap data
/** @name Accessing tap data */
/**
 Subscribe for tap updates.
 
 See [DPDieDelegate die:didTap:error:] for appropriate callback method. The
 callback method is called when die was tapped changes.
 */
- (void)startTapUpdates;

/**
 Unsubscribe from tap updates.
 */
- (void)stopTapUpdates;

#pragma mark - Accessing face change data
/** @name Accessing face change data */
/**
 Subscribe for face change updates.
 
 See [DPDieDelegate die:didChangeFace:error:] for appropriate callback method. The
 callback method is called when die changed the rolled face changes.
 */
- (void)startFaceChangeUpdates;

/**
 Unsubscribe from face change updates.
 */
- (void)stopFaceChangeUpdates;

#pragma mark - Accessing die variables
/** @name Accessing die variables */
/**
 Send a one-time request for die statistics.
 
 See DPDieDelegate for appropriate callback method.
 */
- (void)readStatistics;

/*
 Send a one-time request for accelerometer config.
 
 See DPDieDelegate for appropriate callback method.
 */
//- (void)readAccelerometerConfig;

/*
 Send a one-time request for gyroscope config.
 
 See DPDieDelegate for appropriate callback method.
 */
//- (void)readGyroscopeConfig;

/*
 Send a one-time request for magnetometer config.
 
 See DPDieDelegate for appropriate callback method.
 */
//- (void)readMagnetometerConfig;

/*
 Send a one-time request for proximity config.
 
 See DPDieDelegate for appropriate callback method.
 */
//- (void)readProximityConfig;

/*
 Send a one-time request for orientation config.
 
 See DPDieDelegate for appropriate callback method.
 */
//- (void)readOrientationConfig;

#pragma mark - Accessing persistent storage
/** @name Accessing persistent storage variables */
/**
 Initializes the persistent storage communication module.
 
 Calling this method allows you to check what variables are available on the die.
 
 See [DPDieDelegate die:didInitializePersistenStorage:error:] for appropriate callback method.
 */
- (void)initializePersistentStorageCommunication;

/**
 Resets the values stored in persistent storage variables to their default values.
 
 See [DPDieDelegate dieDidResetPersistentStorage:error:] for appropriate callback method.
 */
- (void)resetPersistentStorage;

/**
 Sends a one-time request for a persistent storage variable value.
 
 Please note that the persistent storage operations are using a serial queue to communicate with
 DICE+ and are not queued by the SDK itself. Therefore calling more than one persistent storage
 operation at a time will result in undefined callback results.
 
 See [DPDieDelegate die:didReadPersistentValue:forHandle:error:] for appropriate callback method.
 @param handle The handle of the persistent storage variable which value should be read.
 */
- (void)readPersistentStorageValueForHandle:(int)handle;

/**
 Sends a one-time request to write an int value for a persistent storage variable.
 
 Please note that the persistent storage operations are using a serial queue to communicate with
 DICE+ and are not queued by the SDK itself. Therefore calling more than one persistent storage
 operation at a time will result in undefined callback results.
 
 @param value The value that will be stored on the die.
 @param handle The handle of the persistent storage variable which value should be written.
 */
- (void)writePersistentIntValue:(int32_t)value forHandle:(int)handle;

/**
 Sends a one-time request to write a string value for a persistent storage variable.
 
 Please note that the persistent storage operations are using a serial queue to communicate with
 DICE+ and are not queued by the SDK itself. Therefore calling more than one persistent storage
 operation at a time will result in undefined callback results.
 
 @param value The value that will be stored on the die.
 @param handle The handle of the persistent storage variable which value should be written.
 */
- (void)writePersistentStringValue:(NSString*)value forHandle:(int)handle;

/**
 Sends a one-time request to write a vector ([x, y, z]) value for a persistent storage variable.
 
 Please note that the persistent storage operations are using a serial queue to communicate with
 DICE+ and are not queued by the SDK itself. Therefore calling more than one persistent storage
 operation at a time will result in undefined callback results.
 
 @param x The X value that will be stored on the die.
 @param y The Y value that will be stored on the die.
 @param z The Z value that will be stored on the die.
 @param handle The handle of the persistent storage variable which value should be written.
 */
- (void)writePersistentVectorValueWithX:(int32_t)x y:(int32_t)y andZ:(int32_t)z forHandle:(int)handle;

@end