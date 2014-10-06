//
//  LocalOAuthScopeUtil.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "DConnectDevicePlugin.h"

@interface LocalOAuthScopeUtil : NSObject

/*!
 表示用スコープ名取得.
 @param scope[in] スコープ名.
 @param devicePlugin[in] デバイスプラグインオブジェクト(無ければnilを渡す)
 @return 標準名称またはデバイスプラグインが提供する名称、取れなければそのまま返す。
 */
+ (NSString *) displayScope: (NSString *)scope
               devicePlugin: (DConnectDevicePlugin *)devicePlugin;

@end
