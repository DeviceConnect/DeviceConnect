//
//  DConnectAuthorizationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectAuthorizationProfile+Private.h"
#import "LocalOAuth2Main.h"
#import "DConnectManager+Private.h"
#import "DConnectDevicePlugin+Private.h"

NSString *const DConnectAuthorizationProfileName = @"authorization";
NSString *const DConnectAuthorizationProfileAttrCreateClient = @"create_client";
NSString *const DConnectAuthorizationProfileAttrRequestAccessToken = @"request_accesstoken";

NSString *const DConnectAuthorizationProfileParamPackage = @"package";
NSString *const DConnectAuthorizationProfileParamClientId = @"clientId";
NSString *const DConnectAuthorizationProfileParamClientSecret = @"clientSecret";
NSString *const DConnectAuthorizationProfileParamGrantType = @"grantType";
NSString *const DConnectAuthorizationProfileParamScope = @"scope";
NSString *const DConnectAuthorizationProfileParamScopes = @"scopes";
NSString *const DConnectAuthorizationProfileParamApplicationName = @"applicationName";
NSString *const DConnectAuthorizationProfileParamSignature = @"signature";
NSString *const DConnectAuthorizationProfileParamExpirePeriod = @"expirePeriod";
NSString *const DConnectAuthorizationProfileParamAccessToken = @"accessToken";

NSString *const DConnectAuthorizationProfileGrantTypeAuthorizationCode = @"authorization_code";


@interface DConnectAuthorizationProfile ()

@property (nonatomic) id object;

@end


@implementation DConnectAuthorizationProfile

- (id) initWithObject:(id)object {
    self = [super init];
    if (self) {
        self.object = object;
    }
    return self;
}

- (NSString *) profileName {
    return DConnectAuthorizationProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    NSString *deviceId = [request deviceId];
    NSString *attribute = [request attribute];
    
    if (attribute) {
        if ([attribute isEqualToString:DConnectAuthorizationProfileAttrCreateClient]) {
            NSString *package = [DConnectAuthorizationProfile packageFromRequest:request];
            send = [self didReceiveGetCreateClientRequest:request response:response deviceId:deviceId package:package];
        } else if ([attribute isEqualToString:DConnectAuthorizationProfileAttrRequestAccessToken]) {
            NSString *clientId = [DConnectAuthorizationProfile clientIdFromRequest:request];
            NSString *grantType = [DConnectAuthorizationProfile grantTypeFromRequest:request];
            NSString *scope = [DConnectAuthorizationProfile scopeFromeFromRequest:request];
            NSArray *scopes = [DConnectAuthorizationProfile parsePattern:scope];
            NSString *applicationName = [DConnectAuthorizationProfile applicationNameFromRequest:request];
            NSString *signature = [DConnectAuthorizationProfile signatureFromRequest:request];
            send = [self didReceiveGetRequestAccessTokenRequest:request response:response deviceId:deviceId clientId:clientId grantType:grantType scopes:scopes applicationName:applicationName signature:signature];
        } else {
            [response setErrorToUnknownAttribute];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveGetCreateClientRequest:(DConnectRequestMessage *)request
                                 response:(DConnectResponseMessage *)response
                                 deviceId:(NSString *)deviceId
                                  package:(NSString *)package
{
    if (package == nil || package.length <= 0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        LocalOAuth2Main *oauth = [LocalOAuth2Main sharedOAuthForClass:[self.object class]];
		LocalOAuthPackageInfo *packageInfo = [[LocalOAuthPackageInfo alloc] initWithPackageNameDeviceId:package deviceId:deviceId];
        LocalOAuthClientData *clientData = [oauth createClientWithPackageInfo:packageInfo];
        if (clientData) {
            [response setResult:DConnectMessageResultTypeOk];
            [DConnectAuthorizationProfile setClientId:clientData.clientId target:response];
            [DConnectAuthorizationProfile setClientSceret:clientData.clientSecret target:response];
        } else {
            [response setErrorToUnknown];
        }
    }
    return YES;
}

- (BOOL) didReceiveGetRequestAccessTokenRequest:(DConnectRequestMessage *)request
                                       response:(DConnectResponseMessage *)response
                                       deviceId:(NSString *)deviceId
                                       clientId:(NSString *)clientId
                                      grantType:(NSString *)grantType
                                         scopes:(NSArray *)scopes
                                applicationName:(NSString *)applicationName
                                      signature:(NSString *)signature
{
    if (signature == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"signature is nil."];
        return YES;
    } else if (clientId == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"clientId is nil."];
        return YES;
    } else if (grantType == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"grantType is nil."];
        return YES;
    } else if (scopes == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"scope is nil."];
        return YES;
    } else if (applicationName == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"applicationName is nil"];
        return YES;
    }
    
    LocalOAuth2Main *oauth = [LocalOAuth2Main sharedOAuthForClass:[self.object class]];
    if ([oauth checkSignatureWithClientId:clientId grantType:grantType deviceId:deviceId scopes:scopes signature:signature]) {
        
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * 60);
        BOOL isDevicePlugin = [_object isKindOfClass:[DConnectDevicePlugin class]];
        
        LocalOAuthConfirmAuthParams *params = [LocalOAuthConfirmAuthParams new];
        params.applicationName = applicationName;
        params.clientId = clientId;
        params.deviceId = deviceId;
        params.grantType = grantType;
        params.scope = scopes;
        params.isForDevicePlugin = isDevicePlugin;
        params.object = _object;
        
        [oauth confirmPublishAccessTokenWithParams:params
                        receiveAccessTokenCallback:^(LocalOAuthAccessTokenData *accessTokenData) {
                            if (accessTokenData) {
                                NSString *chkSignature = [oauth createSignatureWithAccessToken:accessTokenData._accessToken clientId:clientId];
                                
                                [response setResult:DConnectMessageResultTypeOk];
                                [DConnectAuthorizationProfile setAccessToken:accessTokenData._accessToken target:response];
                                [DConnectAuthorizationProfile setSignature:chkSignature target:response];
                                
                                DConnectArray *arr = [DConnectArray array];
                                NSArray *scopes = accessTokenData._scopes;
                                for (LocalOAuthAccessTokenScope *s in scopes) {
                                    DConnectMessage *msg = [DConnectMessage message];
                                    [DConnectAuthorizationProfile setScope:s._scope target:msg];
                                    [DConnectAuthorizationProfile setExpirePriod:s._expirePeriod target:msg];
                                    [arr addMessage:msg];
                                }
                                [DConnectAuthorizationProfile setScopes:arr target:response];
                            } else {
                                [response setErrorToAuthorizationWithMessage:@"Cannot create a access token."];
                            }
                            dispatch_semaphore_signal(semaphore);
                        }
                          receiveExceptionCallback:^(NSString *exceptionMessage) {
                              [response setErrorToAuthorizationWithMessage:@"Cannot create a access token."];
                              dispatch_semaphore_signal(semaphore);
                          }];
 
        long result = dispatch_semaphore_wait(semaphore, timeout);
        if (result != 0) {
            [response setErrorToAuthorizationWithMessage:@"timeout"];
        }
    } else {
        [response setErrorToAuthorizationWithMessage:@"signature does not match."];
    }
    return YES;
}

#pragma mark - Setter

+ (void) setClientId:(NSString *)clientId target:(DConnectMessage *)message {
    [message setString:clientId forKey:DConnectAuthorizationProfileParamClientId];
}

+ (void) setClientSceret:(NSString *)clientSceret target:(DConnectMessage *)message {
    [message setString:clientSceret forKey:DConnectAuthorizationProfileParamClientSecret];
}

+ (void) setAccessToken:(NSString *)accessToken target:(DConnectMessage *)message {
    [message setString:accessToken forKey:DConnectAuthorizationProfileParamAccessToken];
}

+ (void) setSignature:(NSString *)signature target:(DConnectMessage *)message {
    [message setString:signature forKey:DConnectAuthorizationProfileParamSignature];
}

+ (void) setScopes:(DConnectArray *)scopes target:(DConnectMessage *)message {
    [message setArray:scopes forKey:DConnectAuthorizationProfileParamScopes];
}

+ (void) setScope:(NSString *)scope target:(DConnectMessage *)message {
    [message setString:scope forKey:DConnectAuthorizationProfileParamScope];
}

+ (void) setExpirePriod:(long long)priod target:(DConnectMessage *)message {
    [message setLongLong:priod forKey:DConnectAuthorizationProfileParamExpirePeriod];
}

#pragma mark - Getter

+ (NSString *) packageFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DConnectAuthorizationProfileParamPackage];
}

+ (NSString *) clientIdFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DConnectAuthorizationProfileParamClientId];
}

+ (NSString *) grantTypeFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DConnectAuthorizationProfileParamGrantType];
}

+ (NSString *) scopeFromeFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DConnectAuthorizationProfileParamScope];
}

+ (NSArray *) parsePattern:(NSString *)scope {
    return [scope componentsSeparatedByString:@","];
}

+ (NSString *) applicationNameFromRequest:(DConnectRequestMessage *)request {
    return [request stringForKey:DConnectAuthorizationProfileParamApplicationName];
}

+ (NSString *) signatureFromRequest:(DConnectRequestMessage *)request{
    return [request stringForKey:DConnectAuthorizationProfileParamSignature];
}

@end
