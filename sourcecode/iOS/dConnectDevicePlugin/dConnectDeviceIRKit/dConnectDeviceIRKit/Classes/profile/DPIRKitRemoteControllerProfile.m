//
//  DPIRKitRemoteControllerProfile.m
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/20.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DPIRKitRemoteControllerProfile.h"
#import "DPIRKit.h"
#import "DPIRKitDevicePlugin.h"
#import "DPIRKit_irkit.h"

NSString *const DPIRKitRemoteControllerProfileName = @"remote_controller";
NSString *const DPIRKitRemoteControllerProfileParamMessage = @"message";

@interface DPIRKitRemoteControllerProfile()
{
    DPIRKitManager *_irkit;
}
@property (nonatomic, weak) DPIRKitDevicePlugin *plugin;

- (void) setMessage:(NSString *)message target:(DConnectMessage *)target;
- (NSString *) messageFromRequest:(DConnectRequestMessage *)request;

@end

@implementation DPIRKitRemoteControllerProfile

#pragma mark - Initialization

- (id) initWithDevicePlugin:(DPIRKitDevicePlugin *)plugin {
    
    self = [super init];
    
    if (self) {
        self.plugin = plugin;
        _irkit = [DPIRKitManager sharedInstance];
    }
    
    return self;
}

#pragma mark - Setter

- (void) setMessage:(NSString *)message target:(DConnectMessage *)target {
    [target setString:message forKey:DPIRKitRemoteControllerProfileParamMessage];
}

#pragma mark - Getter

- (NSString *) messageFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DPIRKitRemoteControllerProfileParamMessage];
}

#pragma mark - DConnectProfile Override

- (NSString *) profileName {
    return DPIRKitRemoteControllerProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    if ([request.attribute length] != 0) {
        [response setErrorToUnknownAttribute];
    } else if (_plugin) {
        
        __weak typeof(self) _self = self;
        DPIRKitDevice *device = [_plugin deviceForDeviceId:request.deviceId];
        if (device) {
            send = NO;
            [_irkit fetchMessageWithHostName:device.hostName completion:^(NSString *message) {
                
                if (message) {
                    [_self setMessage:message target:response];
                    response.result = DConnectMessageResultTypeOk;
                } else {
                    [response setErrorToUnknown];
                }
                
                [[DConnectManager sharedManager] sendResponse:response];
            }];
        } else {
            [response setErrorToNotFoundDevice];
        }
        
    } else {
        // デバイスプラグインがnilになっているという状態は通常ありえないので不明なエラーを返しておく。
        [response setErrorToUnknown];
    }
    
    return send;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    if ([request.attribute length] != 0) {
        [response setErrorToUnknownAttribute];
    } else if (_plugin) {
        
        DPIRKitDevice *device = [_plugin deviceForDeviceId:request.deviceId];
        if (device) {
            
            NSString *message = [self messageFromRequest:request];
            if (!message) {
                [response setErrorToInvalidRequestParameter];
            } else {
                send = NO;
                [_irkit sendMessage:message withHostName:device.hostName completion:^(BOOL success) {
                    if (success) {
                        response.result = DConnectMessageResultTypeOk;
                    } else {
                        [response setErrorToUnknown];
                    }
                    
                    [[DConnectManager sharedManager] sendResponse:response];
                }];
            }
        } else {
            [response setErrorToNotFoundDevice];
        }
        
    } else {
        // デバイスプラグインがnilになっているという状態は通常ありえないので不明なエラーを返しておく。
        [response setErrorToUnknown];
    }
    
    return send;
}

@end
