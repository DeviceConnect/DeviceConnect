//
//  LocalOAuthClient.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

#import "LocalOAuthPackageInfo.h"
#import "LocalOAuthResponseType.h"
#import "LocalOAuthGrantType.h"


extern NSString *const PROPERTY_APPLICATION_NAME;

extern NSString *const PROPERTY_DESCRIPTION;

extern NSString *const PROPERTY_SUPPORTED_FLOWS;

/** クライアントタイプ. */
typedef NS_ENUM(NSUInteger, LocalOAuthClientType) {
    CLIENT_TYPE_CONFIDENTIAL, CLIENT_TYPE_PUBLIC
};

/* クライアントタイプユーティリティ */
@interface LocalOAuthClientTypeUtil : NSObject

+ (NSString *) toString: (LocalOAuthClientType)clientType;
+ (LocalOAuthClientType) toValue: (NSString *)clientTypeString;

@end


/** クライアントプロトコル定義 */
@protocol LocalOAuthClientProtocol <NSObject>

@required

/*!
    パッケージ名を返す（追加）.
    @return	パッケージ名
 */
- (LocalOAuthPackageInfo *) packageInfo;

/*!
    Client id that the client has registered at the auth provider.

    @return the stored client id
 */
- (NSString *) clientId;

/*!
    Client secret that the client has registered at the auth provider.

    @return the stored client secret
 */
- (NSString *) clientSecret;

/**
    Redirect URL that the client has registered at the auth provider.

    @return redirect callback url for code and token flows.(NSString*配列)
 */
- (NSArray *) redirectURIs;

/*!
    @return {key:(NSString *) value: (id)} の配列
 */
- (NSArray *) properties;

/*!
    @return responseType
 */
- (BOOL) isResponseTypeAllowed: (LocalOAuthResponseType)responseType;

- (BOOL) isGrantTypeAllowed: (LocalOAuthGrantType)grantType;

- (LocalOAuthClientType) clientType;


@optional


@end

/** クライアント */
@interface LocalOAuthClient : NSObject
@property(strong, nonatomic) id <LocalOAuthClientProtocol, NSObject> delegate;

- (LocalOAuthPackageInfo *) packageInfo;
- (NSString *) clientId;
- (NSString *) clientSecret;
- (NSArray *) redirectURIs;
- (NSArray *) properties;
- (BOOL) isResponseTypeAllowed: (LocalOAuthResponseType)responseType;
- (BOOL) isGrantTypeAllowed: (LocalOAuthGrantType)grantType;
- (LocalOAuthClientType) clientType;



@end


