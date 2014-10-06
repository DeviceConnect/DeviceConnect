//
//  DConnectConnectProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectConnectProfile.h"

// Profile Name
NSString *const DConnectConnectProfileName = @"connect";

// Attribute
NSString *const DConnectConnectProfileAttrWifi              = @"wifi";
NSString *const DConnectConnectProfileAttrBluetooth         = @"bluetooth";
NSString *const DConnectConnectProfileAttrDiscoverable      = @"discoverable";
NSString *const DConnectConnectProfileAttrBLE               = @"ble";
NSString *const DConnectConnectProfileAttrNFC               = @"nfc";
NSString *const DConnectConnectProfileAttrOnWifiChange      = @"onwifichange";
NSString *const DConnectConnectProfileAttrOnBluetoothChange = @"onbluetoothchange";
NSString *const DConnectConnectProfileAttrOnBLEChange       = @"onblechange";
NSString *const DConnectConnectProfileAttrOnNFCChange       = @"onnfcchange";

// Parameter
NSString *const DConnectConnectProfileParamEnable        = @"enable";
NSString *const DConnectConnectProfileParamConnectStatus = @"connectStatus";

// Interface
NSString *const DConnectConnectProfileInterfaceBluetooth = @"bluetooth";

@interface DConnectConnectProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectConnectProfile

#pragma mark - DConnectProfile Methods

- (NSString *) profileName {
    return DConnectConnectProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if ([DConnectConnectProfileAttrWifi isEqualToString:attribute]) {
        if ([self hasMethod:@selector(profile:didReceiveGetWifiRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetWifiRequest:request
                             response:response deviceId:deviceId];
        }
    } else if ([DConnectConnectProfileAttrBluetooth isEqualToString:attribute]) {
        if ([self hasMethod:@selector(profile:didReceiveGetBluetoothRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetBluetoothRequest:request
                             response:response deviceId:deviceId];
        }
    } else if ([DConnectConnectProfileAttrNFC isEqualToString:attribute]) {
        if ([self hasMethod:@selector(profile:didReceiveGetNFCRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetNFCRequest:request
                             response:response deviceId:deviceId];
        }
    } else if ([DConnectConnectProfileAttrBLE isEqualToString:attribute]) {
        if ([self hasMethod:@selector(profile:didReceivePutBLERequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetBLERequest:request
                             response:response deviceId:deviceId];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    
    if (!interface) {
        if (attribute) {
            
            NSString *deviceId = [request deviceId];
            NSString *sessionKey = [request sessionKey];
            
            if ([attribute isEqualToString:DConnectConnectProfileAttrWifi]) {
                if ([self hasMethod:@selector(profile:didReceivePutWiFiRequest:response:deviceId:) response:response])
                {
                    send = [_delegate profile:self didReceivePutWiFiRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrBluetooth]) {
                if ([self hasMethod:@selector(profile:didReceivePutBluetoothRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutBluetoothRequest:request
                                     response:response deviceId:deviceId];
                }
                
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrNFC]) {
                if ([self hasMethod:@selector(profile:didReceivePutNFCRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutNFCRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrBLE]) {
                if ([self hasMethod:@selector(profile:didReceivePutBLERequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutBLERequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnWifiChange]) {
                if ([self hasMethod:@selector(profile:didReceivePutWiFiRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutOnWifiChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnBluetoothChange]) {
                if ([self hasMethod:@selector(profile:didReceivePutOnBluetoothChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutOnBluetoothChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnNFCChange]) {
                if ([self hasMethod:@selector(profile:didReceivePutOnNFCChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutOnNFCChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnBLEChange]) {
                if ([self hasMethod:@selector(profile:didReceivePutOnBLEChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceivePutOnBLEChangeRequest:request response:response
                                     deviceId:deviceId sessionKey:sessionKey];
                }
            } else {
                [response setErrorToUnknownAttribute];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else if ([interface isEqualToString:DConnectConnectProfileInterfaceBluetooth]
               && [DConnectConnectProfileAttrDiscoverable isEqualToString:attribute])
    {
        if ([self hasMethod:@selector(profile:didReceivePutBluetoothDiscoverableRequest:response:deviceId:)
            response:response])
        {
            send = [_delegate profile:self didReceivePutBluetoothDiscoverableRequest:request
                             response:response deviceId:[request deviceId]];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if (!interface) {
        if (attribute) {
            
            NSString *sessionKey = [request sessionKey];
            
            if ([attribute isEqualToString:DConnectConnectProfileAttrWifi]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteWiFiRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteWiFiRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrBluetooth]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteBluetoothRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteBluetoothRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrNFC]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteNFCRequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteNFCRequest:request
                                     response:response deviceId:deviceId];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrBLE]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteBLERequest:response:deviceId:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteBLERequest:request
                                     response:response deviceId:deviceId];
                }
                
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnWifiChange]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteOnWifiChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteOnWifiChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnBluetoothChange]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteOnBluetoothChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteOnBluetoothChangeRequest:request
                                     response:response deviceId:deviceId
                                   sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnNFCChange]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteOnNFCChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteOnNFCChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else if ([attribute isEqualToString:DConnectConnectProfileAttrOnBLEChange]) {
                if ([self hasMethod:@selector(profile:didReceiveDeleteOnBLEChangeRequest:response:deviceId:sessionKey:)
                           response:response])
                {
                    send = [_delegate profile:self didReceiveDeleteOnBLEChangeRequest:request
                                     response:response deviceId:deviceId sessionKey:sessionKey];
                }
            } else {
                [response setErrorToUnknownAttribute];
            }
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else if ([interface isEqualToString:DConnectConnectProfileInterfaceBluetooth]
               && [attribute isEqualToString:DConnectConnectProfileAttrDiscoverable])
    {
        if ([self hasMethod:@selector(profile:didReceiveDeleteBluetoothDiscoverableRequest:response:deviceId:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteBluetoothDiscoverableRequest:request
                             response:response deviceId:deviceId];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter

+ (void) setEnable:(BOOL)enable target:(DConnectMessage *)message {
    if (!message) {
        @throw @"Message must not be nil.";
    } else {
        [message setBool:enable forKey:DConnectConnectProfileParamEnable];
    }
}

+ (void) setConnectStatus:(DConnectMessage *)connectStatus target:(DConnectMessage *)message {
    if (!connectStatus) {
        @throw @"ConnectStatus must not be nil.";
    } else if (!message) {
        @throw @"Message must not be nil.";
    } else {
        [message setMessage:connectStatus forKey:DConnectConnectProfileParamConnectStatus];
    }
}

#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

@end
