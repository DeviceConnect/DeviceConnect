//
//  DPScanner.h
//  DicePlus
//
//  Created by Janusz Bossy on 31.12.2012.
//  Copyright (c) 2012 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DPDie.h"
#import "DPRoll.h"
#import "DPAcceleration.h"
#import "Enums.h"

#if TARGET_OS_IPHONE
#import <CoreBluetooth/CoreBluetooth.h>
#elif TARGET_OS_MAC
#import <IOBluetooth/IOBluetooth.h>
#endif

@protocol DPDiceManagerDelegate;

/**
 A DPDcieManager object is the entry point to communicating with Dice+. It allows the developer
 to scan for available dice, initiating a connection to a die and disconnecting at appropriate
 time.
 
 After obtaining an instance of DPDiceManager, the app can use it to: scan for available dice
 and connect to a specified die. DPDiceManager is designed to be a singleton instance, creating
 additional instances of DPDiceManager can affect the rate at which data is received from dice
 and is not encouraged.
 */
@interface DPDiceManager : NSObject<CBCentralManagerDelegate
#if !TARGET_OS_IPHONE
                                    , IOBluetoothDeviceInquiryDelegate
#endif
                                    > {
@private
    CBCentralManager* centralManager;
    NSMutableDictionary* connectedDice;
    NSMutableDictionary* discoveredDice;
    uint8_t* _key;
}

/** @name Getting the Shared DPDiceManager instance */

/**
 Returns the shared DPDiceManager instance.
 
 @return The shared dice manager object.
 */
+ (id)sharedDiceManager;

/** @name Accessing the Delegate */

/**
 The delegate object to receive update events.
 
 See DPDieDelegate protocol for available callback methods.
 */
@property (assign, nonatomic) id<DPDiceManagerDelegate> delegate;

/** @name Configuring the dice manager */

/**
 Configures the DPDiceManager object with your SDK key. This should be the first method you
 call after obtaining the shared dice manager. Since the dice manager object is shared you only
 need to set the key once.
 
 Example:
 
    diceManager = [DPDiceManager sharedDiceManager];
    uint8_t key[8] = {0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77};
    [diceManager setKey:key];
 
 @param key The key array provided to you.
 */
- (void)setKey:(uint8_t*)key;

/** @name Scanning for dice */

/**
 Initiates the scan for available dice.
 
 This method returns immediately. Calling this method causes the dice manager to start searching
 for available dice. When a die is found the appropriate delegate method is called.
 
 Calling this method more than once in succession does not automatically result in new events being
 triggered.
 
 The scan process is stopped automatically after 12 seconds.
 */
- (void)startScan;

/**
 Stops the current scanning process.
 
 If no scan process is not currently running, this method has no effect.
 */
- (void)stopScan;

/** @name Managing connections with Dice+ */

/**
 Establish a connection to the die.
 
 This never times out. Use disconnectDie: to cancel a pending or ongoing connection.
 @param die The die.
 */
- (void)connectDie:(DPDie*)die;

/**
 Cancels a pending or ongoing connection to the die.
 
 @param die The die.
 */
- (void)disconnectDie:(DPDie*)die;

/**
 Cancels all pending and ongoing connections to dice.
 */
- (void)disconnectAllDice;

@end
