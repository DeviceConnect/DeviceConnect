//
//  LocalOAuthConfirmAuthParamsBuilder.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthConfirmAuthParams.h"

@interface LocalOAuthConfirmAuthParamsBuilder : NSObject

- (LocalOAuthConfirmAuthParamsBuilder *) applicationName: (NSString *)applicationName;
- (LocalOAuthConfirmAuthParamsBuilder *) clientId: (NSString *)clientId;
- (LocalOAuthConfirmAuthParamsBuilder *) grantType: (NSString *)grantType;
- (LocalOAuthConfirmAuthParamsBuilder *) deviceId: (NSString *)deviceId;
- (LocalOAuthConfirmAuthParamsBuilder *) scope: (NSArray *)scope;
- (LocalOAuthConfirmAuthParamsBuilder *) isForDevicePlugin: (BOOL) isForDevicePlugin;


- (LocalOAuthConfirmAuthParams *)build;

@end
