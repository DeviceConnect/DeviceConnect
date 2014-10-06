//
//  LocalOAuthConfirmAuthParams.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthConfirmAuthParams : NSObject

/** アプリケーション名. */
@property NSString *applicationName;

/** クライアントID. */
@property NSString *clientId;

/** グラントタイプ. */
@property NSString *grantType;

/** デバイスID(デバイスプラグイン用の場合のみ設定する). */
@property NSString *deviceId;

/** スコープ名(NSString*の配列). */
@property NSArray * scope;

/** デバイスプラグイン用の承認確認画面か.　 */
@property BOOL isForDevicePlugin;

/** DConnectAuthorizationProfileの呼び出しの際に指定されたObject(DConnectDevicePlugin *等が入る) */
@property id object;

@end
