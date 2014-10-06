//
//  CipherAuthSignature.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface CipherAuthSignature : NSObject

+ (NSString *)generateSignatureWithClientId: (NSString *)clientId
                                  grantType: (NSString *)grantType
                                   deviceId: (NSString *)deviceId
                                     scopes: (NSArray *)scopes
                               clientSecret: (NSString *)clientSecret;

+ (NSString *)generateSignatureWithAccessToken: (NSString *)accessToken
                                  clientSecret: (NSString *)clientSecret;


@end
