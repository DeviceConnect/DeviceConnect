//
//  LocalOAuthToken.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthPackageInfo.h"

/** OAuthResourceDefs.TOKEN_TYPE_BEARER */
#define OAuthResourceDefs_TOKEN_TYPE_BEARER @"Bearer"

/** トークンプロトコル定義 */
@protocol LocalOAuthTokenProtocol <NSObject>

@required

/**
 * The access token issued by the authorization server. (5.1.
 * 'access_token')
 *
 * @return the actual token to be used for OAuth invocations.
 */
- (NSString *) accessToken;

/**
 * The type of the token.
 *
 * @return
 */
- (NSString *) tokenType;

/**
 * The refresh token. (5.1. 'refresh_token')
 *
 * @return null if refresh token was not issued.
 */
- (NSString *) refreshToken;

/**
 * The actual granted scope. Must not be null.
 *
 * @return (Scope *)の配列
 */
- (NSArray *) scope;


@optional


@end

/** トークンクラス */
@interface LocalOAuthToken : NSObject

/* 派生クラスのオブジェクトをdelegateに設定する */
@property(strong, nonatomic) id <LocalOAuthTokenProtocol, NSObject> delegate;

/* 基底クラスなのでdelegateのメソッドを呼び出すメソッドを用意する */

- (NSString *) accessToken;
- (NSString *) tokenType;
- (NSString *) refreshToken;
- (NSArray *) scope;

@end
