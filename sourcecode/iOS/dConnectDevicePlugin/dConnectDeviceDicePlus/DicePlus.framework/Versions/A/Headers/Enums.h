/**
 Set of constants for available accelerometer filter options.
 */
typedef enum {
    DPAccelerometerFilterRaw      = 1,
    DPAccelerometerFilterLowPass  = 2,
    DPAccelerometerFilterHighPass = 4
} DPAccelerometerFilter;

/**
 Set of constants for available magnetometer filter options.
 */
typedef enum {
    DPMagnetometerFilterRaw     = 1,
    DPMagnetometerFilterLowPass = 2
} DPMagnetometerFilter;

/**
 Set of constants for available operation mode options.
 */
typedef enum {
    DPDieModeLedsOn  = 0,
    DPDieModeLedsOff = 1
} DPDieMode;

/**
 Set of constants for available sensor types.
 */
typedef enum {
    DPSensorRoll          = 1,
    DPSensorMagnetometer  = 2,
    DPSensorAccelerometer = 3,
    DPSensorThermometer   = 4,
    DPSensorTouch         = 5,
    DPSensorProximity     = 6,
    DPSensorOrientation   = 9,
    DPSensorLedState      = 10,
    DPSensorPowerMode     = 11,
    DPSensorBattery       = 13,
    DPSensorFaceChange    = 15,
    DPSensorTap           = 16
} DPSensor;

/**
 Set of constants for available characteristic types.
 */
typedef enum {
    DPCharacteristicMagnetometerConfig  = 2,
    DPCharacteristicAccelerometerConfig = 3,
    DPCharacteristicProximityConfig     = 6,
    DPCharacteristicGyroscopeConfig     = 8,
    DPCharacteristicOrientationConfig   = 9,
    DPCharacteristicPowerMode           = 11,
    DPCharacteristicStatistics          = 14
} DPCharacteristic;

/**
 Set of constants for bultin animation options.
 */
typedef enum {
    DPDieAnimationClear      = 0,
    DPDieAnimationRollOK     = 4
} DPDieAnimation;

/**
 Set of constants for roll status result.
 */
typedef enum {
    DPRollFlagOK       = 0,
    DPRollFlagTilt     = 1,
    DPRollFlagTooShort = 2
} DPRollFlag;

typedef enum {
    DPPersistentStorageValueTypeInt    = 1,
    DPPersistentStorageValueTypeVector = 2,
    DPPersistentStorageValueTypeString = 3
} DPPersistentStorageValueType;

/**
 Set of constants for DPPersistentStorageDescriptor flags.
 */
typedef enum {
    DPPersistentStorageDescriptorFlagsRead         = 1,
    DPPersistentStorageDescriptorFlagsWrite        = 2,
    DPPersistentStorageDescriptorFlagsDefaultValue = 4,
    DPPersistentStorageDescriptorFlagsMinValue     = 8,
    DPPersistentStorageDescriptorFlagsMaxValue     = 16
} DPPersistentStorageDescriptorFlags;

/**
 Set of constants for DPPowerMode mode.
 */
typedef enum {
    DPModeOperating    = 0,
    DPModeGoingToSleep = 1,
    DPModeSleeping     = 2
} DPMode;

/**
 Set of constants for DPLedState type.
 */
typedef enum {
    DPLedStateTypeFinish    = 1,
    DPLedStateTypeUserSpace = 2
} DPLedStateType;