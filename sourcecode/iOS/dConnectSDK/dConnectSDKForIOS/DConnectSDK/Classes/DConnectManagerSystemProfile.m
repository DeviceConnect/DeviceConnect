//
//  DConnectManagerSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectManagerSystemProfile.h"
#import "DConnectManager+Private.h"

@implementation DConnectManagerSystemProfile

- (BOOL) didReceiveRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    NSUInteger action = [request integerForKey:DConnectMessageAction];
    BOOL send = NO;
    
    if (action == DConnectMessageActionTypeGet) {
        send = [self didReceiveGetRequest:request response:response];
    } else if (action == DConnectMessageActionTypePut) {
        send = [self didReceivePutRequest:request response:response];
    } else if (action == DConnectMessageActionTypePost) {
        send = [self didReceivePostRequest:request response:response];
    } else if (action == DConnectMessageActionTypeDelete) {
        send = [self didReceiveDeleteRequest:request response:response];
    }
    
    return send;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    
    BOOL send = NO;
    if (attribute == nil && interface == nil) {
        send = [self didReceiveGetSystemRequest:request response:response];
    } else if ([attribute isEqualToString:DConnectSystemProfileAttrKeyword]) {
        [response setErrorToNotSupportAction];
        send = YES;
    } else if ([attribute isEqualToString:DConnectSystemProfileAttrEvents]) {
        [response setErrorToNotSupportAction];
        send = YES;
    }
    return send;
}


- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    if (interface == nil && attribute == nil) {
        [response setErrorToNotSupportAction];
        return YES;
    } else if ([attribute isEqualToString:DConnectSystemProfileAttrKeyword]) {
        [response setErrorToNotSupportAction];
        return YES;
    } else  if ([attribute isEqualToString:DConnectSystemProfileAttrEvents]) {
        [response setErrorToNotSupportAction];
        return YES;
    }
    // 属性やインターフェースが存在する場合には、未処理扱いにして各デバイスプラグインに配送する
    return NO;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    if (interface == nil && attribute == nil) {
        [response setErrorToNotSupportAction];
        return YES;
    } else if ([attribute isEqualToString:DConnectSystemProfileAttrKeyword]) {
        [response setErrorToNotSupportAction];
        return YES;
    } else  if ([attribute isEqualToString:DConnectSystemProfileAttrEvents]) {
        [response setErrorToNotSupportAction];
        return YES;
    }
    // 属性やインターフェースが存在する場合には、未処理扱いにして各デバイスプラグインに配送する
    return NO;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response
{
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    if (interface == nil && attribute == nil) {
        [response setErrorToNotSupportAction];
        return YES;
    } else if ([attribute isEqualToString:DConnectSystemProfileAttrKeyword]) {
        [response setErrorToNotSupportAction];
        return YES;
    } else  if ([attribute isEqualToString:DConnectSystemProfileAttrEvents]) {
        NSString *sessionKey = [request sessionKey];
        return [self profile:self didReceiveDeleteEventsRequest:request response:response sessionKey:sessionKey];
    }
    // 属性やインターフェースが存在する場合には、未処理扱いにして各デバイスプラグインに配送する
    return NO;
}

- (BOOL) didReceiveGetSystemRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
{
    // DConnectManagerのシステムプロファイルを作成
    DConnectManager *mgr = (DConnectManager *) self.provider;
    DConnectDevicePluginManager *pluginMgr = mgr.mDeviceManager;
    
    NSArray *profiles = [mgr profiles];
    NSArray *deviceplugins = [pluginMgr devicePluginList];
    
    // サポートするプロファイル一覧
    DConnectArray *supports = [DConnectArray array];
    for (DConnectProfile *p in profiles) {
        [supports addString:[p profileName]];
    }
    
    // デバイスプラグイン一覧
    DConnectArray *plugins = [DConnectArray array];
    for (DConnectDevicePlugin *p in deviceplugins) {
        DConnectMessage *m = [DConnectMessage new];
        NSString *className = NSStringFromClass([p class]);
        NSString *pluginId = [NSString stringWithFormat:@"%@.dconnect", className];
        NSString *pluginName = [p pluginName];
        [m setString:pluginId forKey:DConnectSystemProfileParamId];
        [m setString:pluginName forKey:DConnectSystemProfileParamName];
        [plugins addMessage:m];
    }
    
    [response setResult:DConnectMessageResultTypeOk];
    [DConnectSystemProfile setVersion:@"1.0" target:response];
    [DConnectSystemProfile setSupports:supports target:response];
    [DConnectSystemProfile setPlugins:plugins target:response];
    return YES;
}


- (BOOL)              profile:(DConnectSystemProfile *)profile
didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                   sessionKey:(NSString *)sessionKey
{
    if (sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey is nil."];
    } else {
        DConnectManager *mgr = (DConnectManager *) self.provider;
        DConnectDevicePluginManager *pluginMgr = mgr.mDeviceManager;
        
        NSArray *deviceplugins = [pluginMgr devicePluginList];
        for (DConnectDevicePlugin *p in deviceplugins) {
            DConnectRequestMessage *copyRequest = [request copy];
            DConnectResponseMessage *dummyResponse = [DConnectResponseMessage message];
            
            // sessionkeyのコンバート
            NSMutableString *s = [NSMutableString stringWithString:sessionKey];
            [s appendString:@"."];
            [s appendString:NSStringFromClass([p class])];
            [copyRequest setString:s forKey:DConnectMessageSessionKey];
            
            // デバイスプラグインに配送
            [p didReceiveRequest:copyRequest response:dummyResponse];
        }
        
        [response setResult:DConnectMessageResultTypeOk];
    }
    return YES;
}


@end
