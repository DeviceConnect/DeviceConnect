//
//  LocalOAuthAccessTokenData.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthAccessTokenScope.h"

@interface LocalOAuthAccessTokenData : NSObject

/** アクセストークン. */
@property NSString *_accessToken;
    
/** スコープ配列. */
@property NSArray *_scopes;


/*!
    @brief コンストラクタ.
    @param[in] accessToken アクセストークン
    @param[in] scopes スコープ毎の有効期限の配列
 */
+ (LocalOAuthAccessTokenData *)accessTokenDataWithAccessToken: (NSString *)accessToken scopes:(NSArray *)scopes_;

    
    
@end
