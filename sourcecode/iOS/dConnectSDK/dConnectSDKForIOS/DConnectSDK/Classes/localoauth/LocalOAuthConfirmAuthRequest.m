//
//  LocalOAuthConfirmAuthRequest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthConfirmAuthRequest.h"


@interface LocalOAuthConfirmAuthRequest() {
    LocalOAuthThreadId _threadId;
    LocalOAuthConfirmAuthParams *_params;
    ReceiveAccessTokenCallback _receiveAccessTokenCallback;
    ReceiveExceptionCallback _receiveExceptionCallback;
    NSDate *_currentTime;
    NSArray *_displayScopes;
}

@end

@implementation LocalOAuthConfirmAuthRequest

- (id)initWithParameter: (LocalOAuthThreadId) threadId
                 params: (LocalOAuthConfirmAuthParams *)params
receiveAccessTokenCallback: (ReceiveAccessTokenCallback)receiveAccessTokenCallback
receiveExceptionCallback: (ReceiveExceptionCallback)receiveExceptionCallback
            currentTime: (NSDate *)currentTime
          displayScopes: (NSArray *)displayScopes {
    
    self = [super init];
    
    if (self) {

        _threadId = threadId;
        _params = params;
        _receiveAccessTokenCallback = receiveAccessTokenCallback;
        _receiveExceptionCallback = receiveExceptionCallback;
        _currentTime = currentTime;
        _displayScopes = displayScopes;
        
    }
    return self;
}

- (LocalOAuthThreadId) threadId {
    return _threadId;
}

- (LocalOAuthConfirmAuthParams *) params {
    return _params;
}


- (ReceiveAccessTokenCallback) receiveAccessTokenCallback {
    return _receiveAccessTokenCallback;
}

- (ReceiveExceptionCallback) receiveExceptionCallback {
    return _receiveExceptionCallback;
}

- (NSDate *)currentTime {
    return _currentTime;
}

- (NSArray *)displayScopes {
    return _displayScopes;
}


@end
