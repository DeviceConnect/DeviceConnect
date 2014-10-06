//
//  LocalOAuthConfirmAuthParamsBuilder.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthConfirmAuthParamsBuilder.h"


@interface LocalOAuthConfirmAuthParamsBuilder() {
    
    /** アプリケーション名. */
    NSString *_applicationName;
    
    /** クライアントID. */
    NSString *_clientId;
    
    /** グラントタイプ. */
    NSString *_grantType;
    
    /** デバイスID(デバイスプラグイン用の場合のみ設定する). */
    NSString *_deviceId;
    
    /** スコープ名(NSString*の配列). */
    NSArray * _scope;
    
    /** デバイスプラグイン用の承認確認画面か.　 */
    BOOL _isForDevicePlugin;
    
}
@end

@implementation LocalOAuthConfirmAuthParamsBuilder

- (LocalOAuthConfirmAuthParamsBuilder *) applicationName: (NSString *)applicationName {
    _applicationName = applicationName;
    return self;
}

- (LocalOAuthConfirmAuthParamsBuilder *) clientId: (NSString *)clientId {
    _clientId = clientId;
    return self;
}

- (LocalOAuthConfirmAuthParamsBuilder *) grantType: (NSString *)grantType {
    _grantType = grantType;
    return self;
}

- (LocalOAuthConfirmAuthParamsBuilder *) deviceId: (NSString *)deviceId {
    _deviceId = deviceId;
    return self;
}

- (LocalOAuthConfirmAuthParamsBuilder *) scope: (NSArray *)scope {
    _scope = scope;
    return self;
}

- (LocalOAuthConfirmAuthParamsBuilder *) isForDevicePlugin: (BOOL) isForDevicePlugin {
    _isForDevicePlugin = isForDevicePlugin;
    return self;
}

- (LocalOAuthConfirmAuthParams *)build {
    LocalOAuthConfirmAuthParams *params = [[LocalOAuthConfirmAuthParams alloc] init];
    
    params.applicationName = _applicationName;
    params.clientId = _clientId;
    params.grantType = _grantType;
    params.deviceId = _deviceId;
    params.scope = _scope;
    params.isForDevicePlugin = _isForDevicePlugin;
    return params;
}



@end
