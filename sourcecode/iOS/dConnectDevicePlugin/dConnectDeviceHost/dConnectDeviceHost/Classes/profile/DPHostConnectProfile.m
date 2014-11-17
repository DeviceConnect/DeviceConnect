//
//  DConnectConnectProfile+DPHostConnectProfile.m
//  dConnectDeviceHost
//
//  Created by 星　貴之 on 2014/11/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPHostConnectProfile.h"
#import "DPHostDevicePlugin.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"
#import "DPHostReachability.h"
/*
 @brief ConnectProfileのステータスを通知するためのブロック。
 */
typedef void (^DPHostConnectStatusBlock)(BOOL status);

@interface DPHostConnectProfile()


@property (nonatomic) DConnectEventManager *eventMgr;

@property (nonatomic) DPHostReachability *wifiReachability;


@property (nonatomic) CBCentralManager *centralManager;

/*
 @brief bluetoothの状態を通知するためのブロック。
 */
@property (nonatomic) NSMutableArray *bluetoothStatusBlocks;
/*
 @brief bleの状態を通知するためのブロック。
 */
@property (nonatomic) NSMutableArray *bleStatusBlocks;

/*
 @brief wifiの状態を通知するためのブロック。
 */
@property (nonatomic) NSMutableArray *wifiStatusBlocks;

/*
 @brief bluetoothの状態をイベント通知するためのブロック。
 */
@property (nonatomic) id bluetoothEventBlock;
/*
 @brief bleの状態をイベント通知するためのブロック。
 */
@property (nonatomic) id bleEventBlock;

/*
 @brief wifiの状態を通知するためのブロック。
 */
@property (nonatomic) id wifiEventBlock;

@end

@implementation DPHostConnectProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        
        _eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
        
        _bluetoothStatusBlocks = [NSMutableArray array];
        _bleStatusBlocks = [NSMutableArray array];
        _wifiStatusBlocks = [NSMutableArray array];
        
        _bluetoothEventBlock = nil;
        _wifiEventBlock = nil;
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] addObserver:_self selector:@selector(reachabilityChanged:) name:kReachabilityChangedNotification object:nil];
        });

    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:kReachabilityChangedNotification object:nil];
}

#pragma mark - GET

- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetWifiRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId {
    [self scanForWifi];
    NetworkStatus netStatus = [_wifiReachability currentReachabilityStatus];
    if (netStatus == NotReachable) {
        [DConnectConnectProfile setEnable:NO target:response];
    } else {
        [DConnectConnectProfile setEnable:YES target:response];
    }
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}

- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetBluetoothRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId {
    __weak typeof(self) _self = self;
    DPHostConnectStatusBlock block = ^(BOOL isStatus) {
        [DConnectConnectProfile setEnable:isStatus target:response];
        [response setResult:DConnectMessageResultTypeOk];
        if (![_self checkBluetoothBlocks]) {
            [_centralManager stopScan];
        }
        [[DConnectManager sharedManager] sendResponse:response];
    };
    [_bluetoothStatusBlocks addObject:block];
    [self scanForPeripherals];

    return NO;
}

- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetBLERequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId {
    __weak typeof(self) _self = self;
    DPHostConnectStatusBlock block = ^(BOOL isStatus) {
        [DConnectConnectProfile setEnable:isStatus target:response];
        [response setResult:DConnectMessageResultTypeOk];
        if (![_self checkBluetoothBlocks]) {
            [_centralManager stopScan];
        }
        [[DConnectManager sharedManager] sendResponse:response];
    };
    [_bleStatusBlocks addObject:block];
    [self scanForPeripherals];

    return NO;
}


#pragma mark - PUT


- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnWifiChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey {
    BOOL result = [self registerEventWithRequest:request response:response];
    if (result) {
        __weak typeof(self) _this = self;
        __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
        _wifiEventBlock = ^(BOOL isStatus) {
            NSArray *evts = [_this.eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                                         profile:DConnectConnectProfileName
                                                       attribute:DConnectConnectProfileAttrOnWifiChange];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                DConnectMessage *wifi = [DConnectMessage message];
                [DConnectConnectProfile setEnable:isStatus target:wifi];
                [DConnectConnectProfile setConnectStatus:wifi target:eventMsg];
                
                [_self sendEvent:eventMsg];
            }
            
        };
        [self scanForWifi];
    }
    return YES;
}


- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnBluetoothChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey {
    BOOL result = [self registerEventWithRequest:request response:response];
    if (result) {
        __weak typeof(self) _this = self;
        __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
        _bluetoothEventBlock = ^(BOOL isStatus) {
            NSArray *evts = [_this.eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                                         profile:DConnectConnectProfileName
                                                       attribute:DConnectConnectProfileAttrOnBluetoothChange];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                DConnectMessage *bluetooth = [DConnectMessage message];
                [DConnectConnectProfile setEnable:isStatus target:bluetooth];
                [DConnectConnectProfile setConnectStatus:bluetooth target:eventMsg];
                [_self sendEvent:eventMsg];
            }
        };
        [self scanForPeripherals];
    }
    return YES;
}

- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnBLEChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey {
    BOOL result = [self registerEventWithRequest:request response:response];
    if (result) {
        __weak typeof(self) _this = self;
        __block DConnectDevicePlugin *_self = (DConnectDevicePlugin *)self.provider;
        _bleEventBlock = ^(BOOL isStatus) {
            NSArray *evts = [_this.eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                                         profile:DConnectConnectProfileName
                                                       attribute:DConnectConnectProfileAttrOnBLEChange];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                DConnectMessage *ble = [DConnectMessage message];
                [DConnectConnectProfile setEnable:isStatus target:ble];
                [DConnectConnectProfile setConnectStatus:ble target:eventMsg];

                [_self sendEvent:eventMsg];
            }
            
        };
        [self scanForPeripherals];
    }
    return YES;
}






#pragma mark - DELETE
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnWifiChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey {
    BOOL result = [self unregisterEventWithRequest:request response:response];
    if (result) {
        _wifiEventBlock = nil;
        [_wifiReachability stopNotifier];
    }
    return YES;
}

- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnBluetoothChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey {
    BOOL result = [self unregisterEventWithRequest:request response:response];
    if (result) {
        _bluetoothEventBlock = nil;
        [_centralManager stopScan];
    }
    return YES;
}

- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnBLEChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey {
    BOOL result = [self unregisterEventWithRequest:request response:response];
    if (result) {
        _bleEventBlock = nil;
        [_centralManager stopScan];
    }
    return YES;
}

#pragma mark - CoreBluetooth Delegate

- (void)centralManagerDidUpdateState:(CBCentralManager *)central
{
    BOOL isStatus = NO;
    if (central.state == CBCentralManagerStatePoweredOn) {
        isStatus = YES;
    } else {
        isStatus = NO;
    }
    
    NSArray *bluetoothBlocks = _bluetoothStatusBlocks;
    if (bluetoothBlocks) {
        for (DPHostConnectStatusBlock bluetoothBlock in bluetoothBlocks) {
            bluetoothBlock(isStatus);
        }
        [_bluetoothStatusBlocks removeAllObjects];
    }
    NSArray *bleBlocks = _bleStatusBlocks;
    if (bleBlocks) {
        for (DPHostConnectStatusBlock bleBlock in bleBlocks) {
            bleBlock(isStatus);
        }
        [_bleStatusBlocks removeAllObjects];
    }
    DPHostConnectStatusBlock bluetoothBlock = _bluetoothEventBlock;
    if (bluetoothBlock) {
        bluetoothBlock(isStatus);
    }
    DPHostConnectStatusBlock bleBlock = _bleEventBlock;
    if (bleBlock) {
        bleBlock(isStatus);
    }
    if (![self checkBluetoothBlocks]) {
        [_centralManager stopScan];
    }
}

#pragma mark - Wifi Delegate

- (void) reachabilityChanged:(NSNotification *)note
{
    DPHostReachability* curReach = [note object];
    BOOL isStatus = NO;
    if ([curReach currentReachabilityStatus] == NotReachable) {
        isStatus = NO;
    } else {
        isStatus = YES;
    }
    NSArray *wifiBlocks = _wifiStatusBlocks;
    if (wifiBlocks) {
        for (DPHostConnectStatusBlock wifiBlock in wifiBlocks) {
            wifiBlock(isStatus);
        }
        [_wifiStatusBlocks removeAllObjects];
    }
    DPHostConnectStatusBlock wifiBlock = _wifiEventBlock;
    if (wifiBlock) {
        wifiBlock(isStatus);
    }

    if (![self checkWifiBlocks]) {
        [_wifiReachability stopNotifier];
    }
    
}
#pragma mark - Private Method

- (void)scanForPeripherals
{
    _centralManager = [[CBCentralManager alloc] initWithDelegate:self queue:nil];
    _centralManager.delegate = self;

    NSArray *services = [NSArray arrayWithObjects:nil, nil];
    NSDictionary *options = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:NO]
                                                        forKey:CBCentralManagerScanOptionAllowDuplicatesKey];
    [_centralManager scanForPeripheralsWithServices:services options:options];
}

- (void)scanForWifi
{

    _wifiReachability = [DPHostReachability reachabilityForInternetConnection];
    [_wifiReachability startNotifier];

}


                   
                   
- (BOOL) checkBluetoothBlocks {
    return _bluetoothStatusBlocks.count > 0 || _bluetoothStatusBlocks || _bleStatusBlocks.count > 0 || _bleEventBlock;
}

- (BOOL) checkWifiBlocks {
    return _wifiStatusBlocks.count > 0 || _wifiStatusBlocks;
}

- (BOOL)registerEventWithRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            return YES;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            return NO;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            return NO;
    }
}

- (BOOL)unregisterEventWithRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            return YES;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            return NO;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            return NO;
    }
    return YES;
}
@end
