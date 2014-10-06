//
//  LocalOAuthClientData.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthClientData : NSObject

/** クライアントID. */
@property NSString *clientId;
    
/** クライアントシークレット. */
@property NSString *clientSecret;

+ (id)init;

/*!
    コンストラクタ.

    @param[in] clientId クライアントID
    @param[in] clientSecret クライアントシークレット
    @return ClientDataオブジェクト
 */
+ (LocalOAuthClientData *) clientDataWithClientIdClientSecret: (NSString *)clientId clientSecret: (NSString *)clientSecret_;



@end
