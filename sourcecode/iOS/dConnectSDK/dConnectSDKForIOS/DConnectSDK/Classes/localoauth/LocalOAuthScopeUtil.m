//
//  LocalOAuthScopeUtil.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthScopeUtil.h"
#import "DConnectDevicePlugin.h"
#import "DConnectProfile.h"

static NSString *const displayScopeKey = @"display_scope_%@";

@implementation LocalOAuthScopeUtil

+ (NSString *) displayScope: (NSString *)scope
               devicePlugin: (DConnectDevicePlugin *)devicePlugin {
    
    /* 標準プロファイル名が取得できる */
    NSString *key = [NSString stringWithFormat: displayScopeKey, scope];
    NSBundle *bundle = DCBundle();
    NSString *localizedName = DCLocalizedString(bundle, key);
    if (localizedName && ![key isEqualToString: localizedName]) {
        return localizedName;
    }
    
    /* デバイスプラグインから名称が取得できる */
    if (devicePlugin != nil) {
        DConnectProfile *profile = [(devicePlugin) profileWithName: scope];
        if (profile && profile.displayName != nil) {
            return profile.displayName;
        }
    }
    
    /* ここまで取得できなければそのまま表示する */
    return scope;
}

@end
