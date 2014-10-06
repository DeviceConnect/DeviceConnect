//
//  DPHostDevicePlugin.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectDevicePlugin.h>
#import <DConnectSDK/DConnectFileManager.h>

@interface DPHostDevicePlugin : DConnectDevicePlugin

/// このデバイスプラグイン用のファイル管理用オブジェクト
@property DConnectFileManager *fileMgr;

- (NSString *) pathByAppendingPathComponent:(NSString *)pathComponent;

@end
