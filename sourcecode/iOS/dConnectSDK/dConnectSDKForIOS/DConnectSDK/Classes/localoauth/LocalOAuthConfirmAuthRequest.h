//
//  LocalOAuthConfirmAuthRequest.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthTypedefs.h"
#import "LocalOAuthConfirmAuthParams.h"

@interface LocalOAuthConfirmAuthRequest : NSObject

- (id)initWithParameter: (LocalOAuthThreadId) threadId
                 params: (LocalOAuthConfirmAuthParams *)params
receiveAccessTokenCallback: (ReceiveAccessTokenCallback)receiveAccessTokenCallback
receiveExceptionCallback: (ReceiveExceptionCallback)receiveExceptionCallback
            currentTime: (NSDate *)currentTime
          displayScopes: (NSArray *)displayScopes;

- (LocalOAuthThreadId) threadId;
- (LocalOAuthConfirmAuthParams *)params;
- (ReceiveAccessTokenCallback) receiveAccessTokenCallback;
- (ReceiveExceptionCallback) receiveExceptionCallback;
- (NSDate *)currentTime;
- (NSArray *)displayScopes;

@end
