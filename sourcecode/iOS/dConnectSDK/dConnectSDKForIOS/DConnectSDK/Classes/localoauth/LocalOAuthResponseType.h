//
//  LocalOAuthResponseType.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/** クライアントタイプ. */
typedef NS_ENUM(NSUInteger, LocalOAuthResponseType) {

    /**
     * Defined in 4.1 Authorization Code Grant.
     */
    RESPONSE_TYPE_CODE,
    
    /**
     * Defined in 4.2 Implicit Grant.
     */
    RESPONSE_TYPE_TOKEN
};

@interface LocalOAuthResponseTypeUtil : NSObject

+ (NSString *) toString: (LocalOAuthResponseType)responseType;
+ (LocalOAuthResponseType) toValue: (NSString *)str;

@end

