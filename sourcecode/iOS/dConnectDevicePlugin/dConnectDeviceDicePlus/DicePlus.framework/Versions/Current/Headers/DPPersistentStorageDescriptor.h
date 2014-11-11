//
//  DPPersistentStorageDescriptor.h
//  DicePlus
//
//  Created by Janusz Bossy on 08.03.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Enums.h"

/**
 An instance of DPPersistentStorageDescriptor class contains values describing a persistent storage variable.
 
 Your application receives an array of DPPersistentStorageDescriptor objects after calling the
 [DPDie initializePersistentStorageCommunication] method.
 */
@interface DPPersistentStorageDescriptor : NSObject {
    int _handle;
    DPPersistentStorageValueType _type;
    int _flags;
    NSString* _name;
    NSString* _description;
    NSString* _unit;
    int _minValue;
    int _maxValue;
    id _defaultValue;
}

/**
 The unique handle for this persistent storage variable.
 */
@property (readonly) int handle;

/**
 Bitwise flags for this variable indicating whether it's writable, readable and does it have max, min and default
 values. See DPPersistentStorageDescriptorFlag for available values.
 */
@property (readonly) int flags;

/**
 The type of this variable.
 
 Available types are:
 1. Integer - a signed 32 bit integer value.
 2. String - a 32 character long string value.
 3. Vector - an array of 3 signed 32 bit integer values.
 */
@property (readonly) DPPersistentStorageValueType type;

/**
 Indicates whether this persistent storage variable is writable.
 */
@property (readonly) BOOL isWritable;

/**
 Indicates whether this persistent storage variable is readable.
 */
@property (readonly) BOOL isReadable;

/**
 Indicates whether this persistent storage variable has default value.
 */
@property (readonly) BOOL hasDefaultValue;

/**
 Indicates whether this persistent storage variable has a maximum allowed value.
 
 Maximum value is applicable only to integer and vector variable types. In case of a vector type variable
 the maximum value is applied to each integer in the array.
 */
@property (readonly) BOOL hasMaxValue;

/**
 Indicates whether this persistent storage variable has a minimum allowed value.

 Minimum value is applicable only to integer and vector variable types. In case of a vector type variable
 the minimum value is applied to each integer in the array.
 */
@property (readonly) BOOL hasMinValue;

/**
 The name of this persistent storage variable.
 */
@property (readonly) NSString* name;

/**
 The description of this persistent storage variable.
 */
@property (readonly) NSString* description;

/**
 The unit of this persistent storage variables.
 */
@property (readonly) NSString* unit;

/**
 The maximum value allowed for this persistent storage variable.
 
 Applicable only if [DPPersistentStorageDescriptor hasMaxValue] is set to true.
 */
@property (readonly) int maxValue;

/**
 The minimum value allowed for this persistent storage variable.
 
 Applicable only if [DPPersistentStorageDescriptor hasMinValue] is set to true.
 */
@property (readonly) int minValue;

/**
 The default value for this persistent storage variable.
 
 Applicable only if [DPPersistentStorageDescriptor hasDefaultValue] is set to true.
 */
@property (readonly) id defaultValue;

@end
