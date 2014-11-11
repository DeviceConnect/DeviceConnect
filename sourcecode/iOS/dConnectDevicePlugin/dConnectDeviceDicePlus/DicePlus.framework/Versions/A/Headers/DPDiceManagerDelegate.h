//
//  DPDiceManagerDelegate.h
//  DicePlus
//
//  Created by Janusz Bossy on 04.01.2013.
//  Copyright (c) 2013 DicePlus. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DPDiceManager.h"

/**
 The DPDiceManagerDelegate protocol defines methods used to receive scan and connection events from
 DPDiceManager object.
 */
@protocol DPDiceManagerDelegate <NSObject>

#pragma mark - Informing the user about Bluetooth status
/** @name Informing the user about Bluetooth status */

/**
 Invoked when the CoreBluetooth central manager's state is updated.
 
 DPDiceManager uses CoreBluetooth framework to manage connections with Dice+ peripherals. This method
 allows you to inform the user that the device's Bluetooth adapter has to be turned on.
 
 @param state The current state of CoreBluetooth central manager.
 */
- (void)centralManagerDidUpdateState:(CBCentralManagerState)state;

#pragma mark - Managing dice connections
/** @name Managing dice connections */

/**
 Invoked when a Dice+ peripheral is found. The DPDie object must be retained if any commands are to
 be performed on it.
 
 In order to save battery life the DPDie object's description values (faceCount, ledCount, etc.) are
 not yet filled in.
 
 @param manager The shared dice manager instance.
 @param die The DPDie object assigned individually to this die peripheral.
 */
- (void)diceManager:(DPDiceManager*)manager didDiscoverDie:(DPDie*)die;

/**
 Invoked when a Dice+ peripheral is connected and properly initialized.
 
 At this point in time the DPDie object's description values (faceCount, ledCount, etc.) are filled in.
 
 @param manager The shared dice manager instance.
 @param die The DPDie object assigned individually to this die peripheral.
 */
- (void)diceManager:(DPDiceManager*)manager didConnectDie:(DPDie*)die;

/**
 Invoked when a pending or active connection to the die is closed.
 
 @param manager The shared dice manager instance.
 @param die The DPDie object assigned individually to this die peripheral.
 @param error The error object containing the reason die disconnected. This object is passed by CoreBluetooth
 framework.
 */
- (void)diceManager:(DPDiceManager*)manager didDisconnectDie:(DPDie*)die error:(NSError*)error;

/**
 Invoked when an error occurs while trying to connect a new die.
 
 @param manager The shared dice manager instance.
 @param die The DPDie object assigned individually to this die peripheral.
 @param error The error object containing the reason die didn't connect.
 */
- (void)diceManager:(DPDiceManager*)manager failedConnectingDie:(DPDie*)die error:(NSError*)error;

/**
 Invoked when the dice manager stops scanning for dice.
 
 @param manager The shared dice manager instance.
 */
- (void)diceManagerStoppedScan:(DPDiceManager *)manager;

@end
