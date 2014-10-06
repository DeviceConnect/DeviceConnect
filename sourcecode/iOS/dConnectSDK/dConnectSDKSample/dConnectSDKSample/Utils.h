//
//  Utils.h
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/09/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectSDK.h>

@interface Utils : NSObject

+ (void) authorizeOrRefreshTokenWithForceRefresh:(BOOL)force
                                         success:(DConnectAuthorizationSuccessBlock)success
                                           error:(DConnectAuthorizationFailBlock)error;

+ (void) authorizeWithCompletion:(DConnectAuthorizationSuccessBlock)success
                           error:(DConnectAuthorizationFailBlock)error;

+ (void) refreshTokenWithCompletion:(DConnectAuthorizationSuccessBlock)success
                              error:(DConnectAuthorizationFailBlock)error;

@end
