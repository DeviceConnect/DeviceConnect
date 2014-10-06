//
//  LocalOAuthTokenManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthClient.h"
#import "LocalOAuthToken.h"
#import "LocalOAuthAuthSession.h"

/** TokenManagerプロトコル定義 */
@protocol LocalOAuthTokenManagerProtocol <NSObject>

@required

/*!
    Generate a new token for the client and the username. If the token has
    already issued for the client and the username, the token will be
    replaced or updated. If username is null, token will issued for the
    client itself.
 
    @param[in] client
    @param[in] username
    @param[in] scope     (Scope *)型の配列
    @param[in] applicationName アプリケーション名(追加)
    @return トークン
 */
- (LocalOAuthToken *) generateToken: (LocalOAuthClient *)client
                username:(NSString *)username
                    scope:(NSArray *)scope
          applicationName:(NSString *)applicationName;

/*!
    Call
    {@link #generateToken(org.restlet.ext.oauth.internal.Client, java.lang.String, java.lang.String[], java.lang.String)}
    with username=null.
 
    @param[in] client
    @param[in] scope (Scope *)型の配列
    @return トークン
 */
- (LocalOAuthToken *)generateToken: (LocalOAuthClient *)client
                   scope:(NSArray *)scope
         applicationName:(NSString *)applicationName;


- (NSString *) storeSession: (LocalOAuthAuthSession *)session;

- (LocalOAuthAuthSession *)restoreSession: (NSString *) code;

/*!
    Find a token issued for the client and the username. For those tokens
    issued for 'client_credentials' grant type, username must be null.
 
    @param[in] client
                the client that bound to token.
    @param[in] username
                the username that bound to token. null if the token was issued
                for the client itself.
    @return null if not found.
 */
- (LocalOAuthToken *) findToken: (LocalOAuthClient *)client username:(NSString *)username;

/*!
    Call
    {@link #findToken(org.restlet.ext.oauth.internal.Client, java.lang.String)}
    with username=null.
 
    @param[in] client
                the client that bound to token.
    @return null if not found.
 */
- (LocalOAuthToken *) findToken: (LocalOAuthClient *) client;

/*!
    Find all tokens bound to the username.
 
    @param username[in]
                the username that bound to tokens.
    @return 0 length if not found.(Token *)型の配列。
 */
- (NSArray *) findTokens: (NSString *) username;

/*!
    Find all tokens bound to the client.
 
    @param[in] client
                the client that bound to tokens.
    @return 0 length if not found.(Token *)型の配列。
 */
- (NSArray *) findTokensWithClient: (LocalOAuthClient *) client;

/*!
    Revoke a token issued for the client and the username. For those tokens
    issued for 'client_credentials' grant type, username must be null.
 
    @param[in] client
                the client that bound to token.
    @param[in] username
                the username that bound to token. null if the token was issued
                for the client itself.
 */
- (void) revokeToken: (LocalOAuthClient *)client username:(NSString *) username;

/*!
    アクセストークンが一致するトークンを探す.
    @param[in] accessToken	アクセストークン
    @return	not null: アクセストークンが一致するトークンデータ / null: アクセストークンが一致するトークンデータが見つからない
 */
- (LocalOAuthToken *) findTokenByAccessToken: (NSString *)accessToken;

/*!
    Call
    {@link #revokeToken(org.restlet.ext.oauth.internal.Client, java.lang.String)}
    with username=null.
 
    @param[in] client
 *            the client that bound to token.
 */
- (void) revokeToken: (LocalOAuthClient *)client;

/*!
    Revoke all tokens bound to the username.
 
    @param[in] username
                the username that bound to tokens.
    @return 0 length if not found.
 */
- (void) revokeAllTokens: (NSString *)username;

/*!
    Revoke all tokens bound to the client.
 
    @param[in] client
 *            the client that bound to tokens.
 * @return 0 length if not found.
 */
- (void) revokeAllTokensWithClient: (LocalOAuthClient *)client;


@optional


@end

/** TokenManager */
@interface LocalOAuthTokenManager : NSObject
@property(nonatomic, assign) id <LocalOAuthTokenManagerProtocol, NSObject> delegate;

- (LocalOAuthToken *) generateToken: (LocalOAuthClient *)client
                 username:(NSString *)username
                    scope:(NSArray *)scope
          applicationName:(NSString *)applicationName;
- (LocalOAuthToken *)generateToken: (LocalOAuthClient *)client
                   scope:(NSArray *)scope
         applicationName:(NSString *)applicationName;
- (NSString *) storeSession: (LocalOAuthAuthSession *)session;
- (LocalOAuthAuthSession *)restoreSession: (NSString *) code;
- (LocalOAuthToken *) findToken: (LocalOAuthClient *)client username:(NSString *)username;
- (LocalOAuthToken *) findToken: (LocalOAuthClient *) client;
- (NSArray *) findTokens: (NSString *) username;
- (NSArray *) findTokensWithClient: (LocalOAuthClient *) client;
- (void) revokeToken: (LocalOAuthClient *)client username:(NSString *) username;
- (LocalOAuthToken *) findTokenByAccessToken: (NSString *)accessToken;
- (void) revokeToken: (LocalOAuthClient *)client;
- (void) revokeAllTokens: (NSString *)username;
- (void) revokeAllTokensWithClient: (LocalOAuthClient *)client;



@end


