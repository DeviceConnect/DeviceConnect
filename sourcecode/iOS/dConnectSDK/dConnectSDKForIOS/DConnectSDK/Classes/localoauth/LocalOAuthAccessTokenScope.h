//
//  LocalOAuthAccessTokenScope.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthAccessTokenScope : NSObject

/** スコープ名. */
@property NSString *_scope;
    
/** 有効期限[秒]. */
@property long _expirePeriod;


/*!
    コンストラクタ.
    @param[in] scope スコープ名
    @param[in] expirePeriod 有効期限[秒]
 */
+ (LocalOAuthAccessTokenScope *) accessTokenScopeWithScope: (NSString *)scope expirePeriod:(long)expirePeriod_;



@end
