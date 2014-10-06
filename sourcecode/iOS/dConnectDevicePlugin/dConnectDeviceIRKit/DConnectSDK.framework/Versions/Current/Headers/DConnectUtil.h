//
//  DConnectUtil.h
//  DConnectSDK
//
//  Created by 小林 伸郎 on 2014/05/09.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief dConnectManagerで使用するユーティリティを提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectMessage.h>

typedef void (^DConnectAuthorizationSuccessBlock)(NSString *clientId, NSString *clientSecret, NSString *accessToken);
typedef void (^DConnectAuthorizationFailBlock)(DConnectMessageErrorCodeType errorCode);

/*! @brief DConnectで使用するユーティリティクラス.
 */
@interface DConnectUtil : NSObject

+ (void) asyncAuthorizeWithPackageName:(NSString *)packageName
                               appName:(NSString *)appName
                                scopes:(NSArray *)scopes
                               success:(DConnectAuthorizationSuccessBlock)success
                                 error:(DConnectAuthorizationFailBlock)error;

+ (void) authorizeWithPackageName:(NSString *)packageName
                          appName:(NSString *)appName
                           scopes:(NSArray *)scopes
                          success:(DConnectAuthorizationSuccessBlock)success
                            error:(DConnectAuthorizationFailBlock)error;


+ (void) refreshAccessTokenWithClientId:(NSString *)clientId
                           clientSecret:(NSString *)clientSecret
                                appName:(NSString *)appName
                                 scopes:(NSArray *)scopes
                                success:(DConnectAuthorizationSuccessBlock)success
                                  error:(DConnectAuthorizationFailBlock)error;

@end
