//
//  DConnectDevicePlugin+Private.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectDevicePlugin.h"

@interface DConnectDevicePlugin (Private)

/**
 @brief プラグインのIDを取得する。
 
 [deviceplugin].dconnect
 */
- (NSString *) pluginId;

@end
