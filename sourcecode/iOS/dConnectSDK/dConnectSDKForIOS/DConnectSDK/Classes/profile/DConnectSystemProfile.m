//
//  DConnectSystemProfile.m
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSystemProfile.h"
#import "DConnectProfileProvider.h"
#import "DConnectManager+Private.h"

NSString *const DConnectSystemProfileName = @"system";

NSString *const DConnectSystemProfileAttrDevice = @"device";
NSString *const DConnectSystemProfileInterfaceDevice = @"device";
NSString *const DConnectSystemProfileAttrWakeUp = @"wakeup";
NSString *const DConnectSystemProfileAttrKeyword = @"keyword";
NSString *const DConnectSystemProfileAttrEvents = @"events";

NSString *const DConnectSystemProfileParamSupports = @"supports";
NSString *const DConnectSystemProfileParamPlugins = @"plugins";
NSString *const DConnectSystemProfileParamPluginId = @"pluginId";
NSString *const DConnectSystemProfileParamId = @"id";
NSString *const DConnectSystemProfileParamName = @"name";

NSString *const DConnectSystemProfileParamVersion = @"version";
NSString *const DConnectSystemProfileParamConnect = @"connect";
NSString *const DConnectSystemProfileParamWiFi = @"wifi";
NSString *const DConnectSystemProfileParamBluetooth = @"bluetooth";
NSString *const DConnectSystemProfileParamNFC = @"nfc";
NSString *const DConnectSystemProfileParamBLE = @"ble";

@interface DConnectSystemProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;
+ (void) message:(DConnectMessage *)message setConnectionState:(DConnectSystemProfileConnectState)state
          forKey:(NSString *)aKey;
@end

@implementation DConnectSystemProfile

- (NSString *) profileName {
    return DConnectSystemProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if (attribute && [attribute isEqualToString:DConnectSystemProfileAttrDevice]) {
        if ([_delegate respondsToSelector:@selector(profile:didReceiveGetDeviceRequest:response:deviceId:)])
        {
            send = [_delegate profile:self didReceiveGetDeviceRequest:request response:response deviceId:deviceId];
        } else if (_dataSource) {
            
            DConnectMessage *connect = [DConnectMessage message];
            if ([_dataSource respondsToSelector:@selector(profile:wifiStateForDeviceId:)]) {
                [DConnectSystemProfile setWiFiState:[_dataSource profile:self wifiStateForDeviceId:deviceId]
                                             target:connect];
            }
            if ([_dataSource respondsToSelector:@selector(profile:bleStateForDeviceId:)]) {
                [DConnectSystemProfile setBLEState:[_dataSource profile:self bleStateForDeviceId:deviceId]
                                            target:connect];
            }
            if ([_dataSource respondsToSelector:@selector(profile:bluetoothStateForDeviceId:)]) {
                [DConnectSystemProfile setBluetoothState:[_dataSource profile:self bluetoothStateForDeviceId:deviceId]
                                                  target:connect];
            }
            if ([_dataSource respondsToSelector:@selector(profile:nfcStateForDeviceId:)]) {
                [DConnectSystemProfile setNFCState:[_dataSource profile:self nfcStateForDeviceId:deviceId]
                                            target:connect];
            }
            
            [DConnectSystemProfile setConnect:connect target:response];
            [DConnectSystemProfile setVersion:[_dataSource versionOfSystemProfile:self] target:response];
            
            DConnectArray *supports = [DConnectArray array];
            NSArray *profiles = [self.provider profiles];
            
            for (DConnectProfile *profile in profiles) {
                [supports addString:[profile profileName]];
            }
            
            [DConnectSystemProfile setSupports:supports target:response];
            [response setResult:DConnectMessageResultTypeOk];
        } else {
            [response setErrorToNotSupportAction];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    
    if (interface && attribute && [attribute isEqualToString:DConnectSystemProfileAttrWakeUp] && _dataSource)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            UIViewController *viewController = [_dataSource profile:self settingPageForRequest:request];
            if (viewController) {
                UIViewController *rootView;
                DCPutPresentedViewController(rootView);
                
                [rootView presentViewController:viewController animated:YES completion:nil];
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToNotSupportAttribute];
            }
            
            [[DConnectManager sharedManager] sendResponse:response];
        });
        send = NO;
    } else if (attribute && [attribute isEqualToString:DConnectSystemProfileAttrKeyword]) {
        if (_delegate && [_delegate respondsToSelector:@selector(profile:didReceivePutKeywordRequest:response:)])
        {
            send = [_delegate profile:self didReceivePutKeywordRequest:request response:response];
        } else {
            [response setErrorToNotSupportAttribute];
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
    if ([DConnectSystemProfileAttrEvents isEqualToString:attribute]) {
        if ([_delegate respondsToSelector:@selector(profile:didReceiveDeleteEventsRequest:response:sessionKey:)]) {
            [_delegate profile:self didReceiveDeleteEventsRequest:request response:response sessionKey:[request sessionKey]];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter

+ (void) setVersion:(NSString *)version target:(DConnectMessage *)message {
    [message setString:version forKey:DConnectSystemProfileParamVersion];
}

+ (void) setSupports:(DConnectArray *)supports target:(DConnectMessage *)message {
    [message setArray:supports forKey:DConnectSystemProfileParamSupports];
}

+ (void) setPlugins:(DConnectArray *)plugins target:(DConnectMessage *)message {
    [message setArray:plugins forKey:DConnectSystemProfileParamPlugins];
}

+ (void) setConnect:(DConnectMessage *)connect target:(DConnectMessage *)message {
    [message setMessage:connect forKey:DConnectSystemProfileParamConnect];
}

+ (void) setWiFiState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message {
    [DConnectSystemProfile message:message setConnectionState:state forKey:DConnectSystemProfileParamWiFi];
}

+ (void) setBluetoothState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message {
    [DConnectSystemProfile message:message setConnectionState:state forKey:DConnectSystemProfileParamBluetooth];
}

+ (void) setNFCState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message {
    [DConnectSystemProfile message:message setConnectionState:state forKey:DConnectSystemProfileParamNFC];
}

+ (void) setBLEState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message {
    [DConnectSystemProfile message:message setConnectionState:state forKey:DConnectSystemProfileParamBLE];
}

+ (void) setId:(NSString *)pluginId target:(DConnectMessage *)message {
    [message setString:pluginId forKey:DConnectSystemProfileParamId];
}

+ (void) setName:(NSString *)name target:(DConnectMessage *)message {
    [message setString:name forKey:DConnectSystemProfileParamName];
}

#pragma mark - Getter Methods

+ (NSString *) pluginIdFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectSystemProfileParamPluginId];
}

#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

+ (void) message:(DConnectMessage *)message setConnectionState:(DConnectSystemProfileConnectState)state
          forKey:(NSString *)aKey
{
    switch (state) {
        case DConnectSystemProfileConnectStateOn:
            [message setBool:YES forKey:aKey];
            break;
        case DConnectSystemProfileConnectStateOff:
            [message setBool:NO forKey:aKey];
            break;
        default:
            break;
    }
}

@end
