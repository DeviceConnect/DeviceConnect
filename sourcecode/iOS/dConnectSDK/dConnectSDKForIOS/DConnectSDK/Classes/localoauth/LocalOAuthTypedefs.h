//
//  LocalOAuthTypedefs.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthAccessTokenData.h"
#import <pthread.h>

typedef void (^ReceiveAccessTokenCallback)(LocalOAuthAccessTokenData *accessTokenData);
typedef void (^ReceiveExceptionCallback)(NSString *exceptionMessage);

typedef mach_port_t LocalOAuthThreadId;

