//
//  LocalOAuthTokenManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthTokenManager.h"

@implementation LocalOAuthTokenManager
@synthesize delegate = _delegate;

- (LocalOAuthToken *) generateToken: (LocalOAuthClient *)client
                 username:(NSString *)username
                    scope:(NSArray *)scope
          applicationName:(NSString *)applicationName {
    LocalOAuthToken *token = [_delegate generateToken:client username:username scope:scope applicationName:applicationName];
    return token;
}

- (LocalOAuthToken *)generateToken: (LocalOAuthClient *)client
                   scope:(NSArray *)scope
         applicationName:(NSString *)applicationName {
    LocalOAuthToken *token = [_delegate generateToken:client scope:scope applicationName:applicationName];
    return token;
}

- (NSString *) storeSession: (LocalOAuthAuthSession *)session {
    NSString *sessionId = [_delegate storeSession: session];
    return sessionId;
}

- (LocalOAuthAuthSession *)restoreSession: (NSString *) code {
    LocalOAuthAuthSession *session = [_delegate restoreSession: code];
    return session;
}

- (LocalOAuthToken *) findToken: (LocalOAuthClient *)client username:(NSString *)username {
    LocalOAuthToken *token = [_delegate findToken:client username:username];
    return token;
}

- (LocalOAuthToken *) findToken: (LocalOAuthClient *) client {
    LocalOAuthToken *token = [_delegate findToken:client];
    return token;
}

- (NSArray *) findTokens: (NSString *) username {
    NSArray *tokens = [_delegate findTokens:username];
    return tokens;
}

- (NSArray *) findTokensWithClient: (LocalOAuthClient *) client {
    NSArray *tokens = [_delegate findTokensWithClient: client];
    return tokens;
}

- (void) revokeToken: (LocalOAuthClient *)client username:(NSString *) username
{
    [_delegate revokeToken:client username:username];
}


- (LocalOAuthToken *) findTokenByAccessToken: (NSString *)accessToken {
    LocalOAuthToken *token = [_delegate findTokenByAccessToken: accessToken];
    return token;
}

- (void) revokeToken: (LocalOAuthClient *)client {
    [_delegate revokeToken:client];
}

- (void) revokeAllTokens: (NSString *)username {
    [_delegate revokeAllTokens: username];
}

- (void) revokeAllTokensWithClient: (LocalOAuthClient *)client {
    [_delegate revokeAllTokensWithClient: client];
    
}

@end
