//
//  CipherAuthSignature.h
//  DConnectSDK
//
//  Created by Mitsuhiro Suzuki on 2014/07/25.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CipherAuthSignature : NSObject

+ (NSString *)generateSignature: (NSString *)clientId
                      grantType: (NSString *)grantType
                       deviceId: (NSString *)deviceId
                         scopes: (NSArray *)scopes
                   clientSecret: (NSString *)clientSecret;

+ (NSString *)generateSignature: (NSString *)accessToken
                   clientSecret: (NSString *)clientSecret;


@end
