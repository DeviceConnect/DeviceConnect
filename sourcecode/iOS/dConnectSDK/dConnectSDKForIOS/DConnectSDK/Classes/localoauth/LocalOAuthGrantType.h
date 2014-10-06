//
//  LocalOAuthGrantType.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/** クライアントタイプ. */
typedef NS_ENUM(NSUInteger, LocalOAuthGrantType) {
    
    /**
     * Defined in 4.1 Authorization Code Grant.
     */
    GRANT_TYPE_AUTHORIZATION_CODE,
    
    /**
     * Defined in 4.3 Resource Owner Password Credentials Grant.
     */
    GRANT_TYPE_PASSWORD,
    
    /**
     * Defined in 4.4 Client Credentials Grant.
     */
    GRANT_TYPE_CLIENT_CREDENTIALS,
    
    /**
     * Defined in 6 Refreshing an Access Token.
     */
    GRANT_TYPE_REFRESH_TOKEN
};

@interface LocalOAuthGrantTypeUtil : NSObject

+ (NSString *) toString: (LocalOAuthGrantType) grantType;
+ (LocalOAuthGrantType) toValue: (NSString *) str;

@end

