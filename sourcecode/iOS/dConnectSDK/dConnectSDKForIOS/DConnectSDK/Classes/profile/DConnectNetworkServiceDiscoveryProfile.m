//
//  DConnectNetworkServiceDiscoveryProfile.m
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectNetworkServiceDiscoveryProfile.h"

NSString *const DConnectNetworkServiceDiscoveryProfileName = @"network_service_discovery";
NSString *const DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices = @"getnetworkservices";
NSString *const DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange = @"onservicechange";
NSString *const DConnectNetworkServiceDiscoveryProfileParamNetworkService = @"networkService";
NSString *const DConnectNetworkServiceDiscoveryProfileParamServices = @"services";
NSString *const DConnectNetworkServiceDiscoveryProfileParamState = @"state";
NSString *const DConnectNetworkServiceDiscoveryProfileParamId = @"id";
NSString *const DConnectNetworkServiceDiscoveryProfileParamName = @"name";
NSString *const DConnectNetworkServiceDiscoveryProfileParamType = @"type";
NSString *const DConnectNetworkServiceDiscoveryProfileParamOnline = @"online";
NSString *const DConnectNetworkServiceDiscoveryProfileParamConfig = @"config";

NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeUnknown = @"Unknown";
NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi = @"WiFi";
NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeBluetooth = @"Bluetooth";
NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeNFC = @"NFC";
NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeBLE = @"BLE";

@interface DConnectNetworkServiceDiscoveryProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectNetworkServiceDiscoveryProfile

#pragma mark - DConnectProfile Methods -

- (NSString *) profileName {
    return DConnectNetworkServiceDiscoveryProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    if ([attribute isEqualToString:DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices]) {
        if ([self hasMethod:@selector(profile:didReceiveGetGetNetworkServicesRequest:response:) response:response]) {
            send = [_delegate profile:self didReceiveGetGetNetworkServicesRequest:request response:response];
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
    
    NSString *attribute = [request attribute];
    if ([attribute isEqualToString:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnServiceChangeRequest:response:deviceId:sessionKey:) response:response]) {
            send = [_delegate profile:self didReceivePutOnServiceChangeRequest:request response:response
                             deviceId:[request deviceId] sessionKey:[request sessionKey]];
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
    
    NSString *attribute = [request attribute];
    if ([attribute isEqualToString:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnServiceChangeRequest:response:deviceId:sessionKey:) response:response]) {
            send = [_delegate profile:self didReceiveDeleteOnServiceChangeRequest:request response:response
                             deviceId:[request deviceId] sessionKey:[request sessionKey]];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter
+ (void) setServices:(DConnectArray *)services target:(DConnectMessage *)message {
    [message setArray:services forKey:DConnectNetworkServiceDiscoveryProfileParamServices];
}

+ (void) setNetworkService:(DConnectMessage *)networkService target:(DConnectMessage *)message {
    [message setMessage:networkService forKey:DConnectNetworkServiceDiscoveryProfileParamNetworkService];
}

+ (void) setId:(NSString *)id target:(DConnectMessage *)message {
    [message setString:id forKey:DConnectNetworkServiceDiscoveryProfileParamId];
}

+ (void) setName:(NSString *)name target:(DConnectMessage *)message {
    [message setString:name forKey:DConnectNetworkServiceDiscoveryProfileParamName];
}

+ (void) setType:(NSString *)type target:(DConnectMessage *)message {
    [message setString:type forKey:DConnectNetworkServiceDiscoveryProfileParamType];
}

+ (void) setOnline:(BOOL)online target:(DConnectMessage *)message {
    [message setBool:online forKey:DConnectNetworkServiceDiscoveryProfileParamOnline];
}

+ (void) setConfig:(NSString *)config target:(DConnectMessage *)message {
    [message setString:config forKey:DConnectNetworkServiceDiscoveryProfileParamConfig];
}

+ (void) setState:(BOOL)state target:(DConnectMessage *)message {
    [message setBool:state forKey:DConnectNetworkServiceDiscoveryProfileParamState];
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
