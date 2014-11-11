//
//  DConnectDevicePluginManager.m
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectDevicePluginManager.h"
#import "DConnectDevicePlugin+Private.h"
#import "DConnectManager+Private.h"
#import "DConnectNetworkServiceDiscoveryProfile.h"
#import <objc/runtime.h>
#import <stdio.h>

@interface DConnectDevicePluginManager ()

/**
 * デバイスを格納するマップ.
 */
@property (nonatomic) NSMutableDictionary *mDeviceMap;

/**
 * デバイスプラグインを追加する.
 * @param[in] plugin 追加するデバイスプラグイン
 */
- (void) addDevicePlugin:(DConnectDevicePlugin *)plugin;

@end

@implementation DConnectDevicePluginManager

- (id) init {
    self = [super init];
    if (self) {
        self.mDeviceMap = [NSMutableDictionary dictionary];
    }
    return self;
}

- (DConnectDevicePlugin *) devicePluginForDeviceId:(NSString *)deviceId {
    if (deviceId) {
        // TODO: .dconnectの部分が変化したときの処理がが必要
        NSArray *domains = [deviceId componentsSeparatedByString:@"."];
        if (domains == nil || [domains count] < 2) {
            return nil;
        } else if ([domains count] == 2) {
            return [self.mDeviceMap objectForKey:[domains objectAtIndex:0]];
        } else if ([domains count] == 3) {
            return [self.mDeviceMap objectForKey:[domains objectAtIndex:1]];
        } else {
            return [self.mDeviceMap objectForKey:[domains objectAtIndex:[domains count] - 2]];
        }
    }
    return nil;
}

- (DConnectDevicePlugin *) devicePluginForPluginId:(NSString *)pluginId {
    return [self.mDeviceMap objectForKey:pluginId];
}

- (NSArray *) devicePluginList {
    NSMutableArray *list = [NSMutableArray array];
    for (id key in [self.mDeviceMap allKeys]) {
        [list addObject:[self.mDeviceMap objectForKey:key]];
    }
    return list;
}

- (NSString *) deviceIdByAppedingPluginIdWithDevicePlugin:(DConnectDevicePlugin *)plugin deviceId:(NSString *)deviceId
{
    return [NSString stringWithFormat:@"%@.%@", deviceId, [plugin pluginId]];
}

- (NSString *) spliteDeviceId:(NSString *)deviceId byDevicePlugin:(DConnectDevicePlugin *)plugin {
    if (deviceId) {
        NSRange range = [deviceId rangeOfString:NSStringFromClass([plugin class])];
        if (range.location != NSNotFound) {
            if (range.location == 0) {
                return @"";
            } else {
                return [deviceId substringToIndex:range.location - 1];
            }
        } else {
            return deviceId;
        }
    }
    return nil;
}

#pragma mark - Private Methods -

- (void) addDevicePlugin:(DConnectDevicePlugin *)plugin {
    if (plugin) {
        [self.mDeviceMap setObject:plugin forKey:NSStringFromClass([plugin class])];
        
        // Network Service Discoveryのイベント登録
        // デバイスプラグインが見つかった時点で登録を行う
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            DConnectRequestMessage *request = [DConnectRequestMessage message];
            [request setAction:DConnectMessageActionTypePut];
            [request setProfile:DConnectNetworkServiceDiscoveryProfileName];
            [request setAttribute:DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange];
            [request setSessionKey:NSStringFromClass([plugin class])];
            
            DConnectResponseMessage *response = [DConnectResponseMessage message];
            [plugin didReceiveRequest:request response:response];
        });
    }
}

- (void) searchDevicePlugin {
    
    // リフレクトを使用して、クラス一覧を取得する
    // 取得したクラス一覧からデバイスプラグインを探し出して登録する。
    int numClasses = objc_getClassList(NULL, 0);
    if (numClasses > 0 ) {
        Class *classes = (Class *) malloc(sizeof(Class) * numClasses);
        if (classes) {
            numClasses = objc_getClassList(classes, numClasses);
            // MARK: [2014/05/21 福井] 並列処理させる？addDevicePlugin:部分は@synchronizedで排他にする事。
            for (int i = 0; i < numClasses; i++) {
                Class parent = class_getSuperclass(classes[i]);
                if (strncmp("DConnectDevicePlugin", class_getName(parent), 20) == 0) {
                    [self addDevicePlugin:[classes[i] new]];
                    DCLogD(@"\"%s\" has been registered.", class_getName(classes[i]));
                }
            }
            free(classes);
        }
#ifdef DEBUG
        else {
            // メモリ不足
            DCLogW(@"Out of memory.");
        }
#endif
    }
}

@end
