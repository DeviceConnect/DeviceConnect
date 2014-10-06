//
//  DConnectUtil.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectUtil.h"
#import "DConnectRequestMessage.h"
#import "DConnectResponseMessage.h"
#import "DConnectAuthorizationProfile+Private.h"
#import "DConnectManager+Private.h"
#import "CipherAuthSignature.h"
#import "LocalOAuth2Main.h"

@interface DConnectUtil()
+ (DConnectResponseMessage *) executeRequest:(DConnectRequestMessage *)request;
@end

@implementation DConnectUtil

#pragma mark - Authorization

+ (void) authorizeWithPackageName:(NSString *)packageName
                          appName:(NSString *)appName
                           scopes:(NSArray *)scopes
                          success:(DConnectAuthorizationSuccessBlock)success
                            error:(DConnectAuthorizationFailBlock)error
{
    
    if (!appName) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"App name is nil."
                                     userInfo:nil];
    } else if (!scopes || scopes.count == 0) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"No scopes."
                                     userInfo:nil];
    } else if (!success || !error) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"Callback is nil."
                                     userInfo:nil];
    }
    
    DConnectRequestMessage *req = [DConnectRequestMessage message];
    req.action = DConnectMessageActionTypeGet;
    req.profile = DConnectAuthorizationProfileName;
    req.attribute = DConnectAuthorizationProfileAttrCreateClient;
    [req setString:packageName forKey:DConnectAuthorizationProfileParamPackage];
    
    DConnectResponseMessage *res = [self executeRequest:req];
    
    if (res.result == DConnectMessageResultTypeError) {
        error([res integerForKey:DConnectMessageErrorCode]);
        return;
    }
    
    NSString *clientId = [res stringForKey:DConnectAuthorizationProfileParamClientId];
    NSString *clientSecret = [res stringForKey:DConnectAuthorizationProfileParamClientSecret];
    
    if (!clientId || !clientSecret) {
        error(DConnectMessageErrorCodeUnknown);
    } else {
        [self refreshAccessTokenWithClientId:clientId clientSecret:clientSecret
                                     appName:appName scopes:scopes
                                     success:success error:error];
    }
    
    
}

+ (void) asyncAuthorizeWithPackageName:(NSString *)packageName
                               appName:(NSString *)appName
                                scopes:(NSArray *)scopes
                               success:(DConnectAuthorizationSuccessBlock)success
                                 error:(DConnectAuthorizationFailBlock)error
{
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [DConnectUtil authorizeWithPackageName:packageName
                                       appName:appName
                                        scopes:scopes
                                       success:success
                                         error:error];
    });
}


+ (void) refreshAccessTokenWithClientId:(NSString *)clientId
                           clientSecret:(NSString *)clientSecret
                                appName:(NSString *)appName
                                 scopes:(NSArray *)scopes
                                success:(DConnectAuthorizationSuccessBlock)success
                                  error:(DConnectAuthorizationFailBlock)error
{

    if (!clientId) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"Client ID is nil."
                                     userInfo:nil];
    } else if (!clientSecret) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"Client secret is nil."
                                     userInfo:nil];
    } else if (!appName) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"App name is nil."
                                     userInfo:nil];
    } else if (!scopes || scopes.count == 0) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"No scopes."
                                     userInfo:nil];
    } else if (!success || !error) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException
                                       reason:@"Callback is nil."
                                     userInfo:nil];
    }
    
    NSString *signature = [CipherAuthSignature generateSignatureWithClientId:clientId
                                                                   grantType:LOCALOAUTH_AUTHORIZATION_CODE
                                                                    deviceId:nil
                                                                      scopes:scopes
                                                                clientSecret:clientSecret];
    
    DConnectRequestMessage *req = [DConnectRequestMessage message];
    req.action = DConnectMessageActionTypeGet;
    req.profile = DConnectAuthorizationProfileName;
    req.attribute = DConnectAuthorizationProfileAttrRequestAccessToken;
    [DConnectAuthorizationProfile setClientId:clientId target:req];
    [DConnectAuthorizationProfile setClientSceret:clientSecret target:req];
    [DConnectAuthorizationProfile setScope:[self combineScopes:scopes] target:req];
    [req setString:DConnectAuthorizationProfileGrantTypeAuthorizationCode
            forKey:DConnectAuthorizationProfileParamGrantType];
    [DConnectAuthorizationProfile setSignature:signature target:req];
    [req setString:appName forKey:DConnectAuthorizationProfileParamApplicationName];
    
    DConnectResponseMessage *res = [self executeRequest:req];
    
    if (res.result == DConnectMessageResultTypeError) {
        error([res integerForKey:DConnectMessageErrorCode]);
    } else {
        NSString *accessToken = [res stringForKey:DConnectAuthorizationProfileParamAccessToken];
        success(clientId, clientSecret, accessToken);
    }
}

+ (NSString *)generateSignatureWithClientId: (NSString *)clientId
                                  grantType: (NSString *)grantType
                                   deviceId: (NSString *)deviceId
                                     scopes: (NSArray *)scopes
                               clientSecret: (NSString *)clientSecret
{
    return [CipherAuthSignature generateSignatureWithClientId:clientId
                                                    grantType:grantType
                                                     deviceId:deviceId
                                                       scopes:scopes
                                                 clientSecret:clientSecret];
}

+ (NSString *)generateSignatureWithAccessToken: (NSString *)accessToken
                                  clientSecret: (NSString *)clientSecret
{
    return [CipherAuthSignature generateSignatureWithAccessToken:accessToken
                                                    clientSecret:clientSecret];
}

+ (NSString *) combineScopes:(NSArray *)scopes {
    
    NSMutableString *str = [NSMutableString string];
    for (int i = 0; i < scopes.count; i++) {
        NSString *scope = [scopes objectAtIndex:i];
        if (i > 0) {
            [str appendString:@","];
        }
        [str appendString:[scope stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceCharacterSet]]];
    }
    
    return str;
}

+ (DConnectResponseMessage *) executeRequest:(DConnectRequestMessage *)request {
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    __block DConnectResponseMessage *res = nil;
    [[DConnectManager sharedManager] sendRequest:request callback:^(DConnectResponseMessage *response) {
        res = response;
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return res;
}

+ (void) showAccessTokenList {
    [[LocalOAuth2Main sharedOAuthForClass:[DConnectManager class]] startAccessTokenListActivity];
}

@end
