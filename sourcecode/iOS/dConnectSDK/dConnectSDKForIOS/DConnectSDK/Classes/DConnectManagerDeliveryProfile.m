//
//  DConnectManagerDeliveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectManagerDeliveryProfile.h"
#import "DConnectMessage+Private.h"
#import "DConnectManager+Private.h"
#import "DConnectDevicePluginManager.h"
#import "DConnectAuthorizationProfile.h"
#import "DConnectNetworkServiceDiscoveryProfile.h"
#import "DConnectSystemProfile.h"
#import "DConnectUtil.h"
#import "DConnectLocalOAuthDB.h"
#import "DConnectConst.h"

@interface DConnectManagerDeliveryProfile ()

/*!
 @brief デバイスプラグインにLocalOAuth認証を行う。
 
 @param[in] plugin 認証を行うデバイスプラグイン
 @param[in] deviceId デバイスID
 
 @retval アクセストークン
 @retval nil 認証失敗
 */
- (NSString *) authorizationToDevicePlugin:(DConnectDevicePlugin *)plugin deviceId:(NSString *)deviceId;

/*!
 @brief クライアントデータを作成する。
 
 @param[in] plugin デバイスプラグイン
 
 @retval レスポンス
 */
- (DConnectResponseMessage *) createClientToDevicePlugin:(DConnectDevicePlugin *)plugin;

/*!
 @brief アクセストークンを要求する。
 
 @param[in] plugin デバイスプラグイン
 @param[in] deviceId デバイスID
 @param[in] clientId クライアントID
 @param[in] clientSecret クライアントシークレット
 
 @retval デバイスプラグインからのレスポンス
 */
- (DConnectResponseMessage *) requestAccessTokenToDevicePlugin:(DConnectDevicePlugin *)plugin deviceId:(NSString *)deviceId clinetId:(NSString *)clientId clinetSecret:(NSString *)clientSecret;

/*!
 @brief 使用するプロファイル一覧を取得する。
 @param[in] plugin プロファイル一覧を取得するデバイスプラグイン
 @retval プロファイル一覧
 */
- (NSArray *)getScopeFromDevicePlugin:(DConnectDevicePlugin *)plugin;

@end



@implementation DConnectManagerDeliveryProfile

- (NSString *) profileName {
    return @"*";
}

- (BOOL) didReceiveRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    NSString *deviceId = [request deviceId];
    
    // MARK: wakeup以外にも例外的な動きをするProfileがある場合には再検討すること。
    // System Profileのwakeupは例外的にpluginIdで宛先を決める
    // ここでは、/system/device/wakeupの場合のみpluginIdを使用するようにする
    NSString *profileName = [request profile];
    if ([profileName isEqualToString:DConnectSystemProfileName]) {
        NSString *inter = [request interface];
        NSString *attr = [request attribute];
        if ([inter isEqualToString:DConnectSystemProfileInterfaceDevice]
            && [attr isEqualToString:DConnectSystemProfileAttrWakeUp]) {
            deviceId = [request pluginId];
            if (!deviceId) {
                [response setErrorToInvalidRequestParameterWithMessage:@"pluginId is required."];
                return YES;
            }
        }
    }
    
    if (!deviceId) {
        [response setErrorToEmptyDeviceId];
    } else {
        // 各デバイスプラグインに配送
        DConnectManager *mgr = (DConnectManager *) self.provider;
        DConnectDevicePlugin *plugin = [mgr.mDeviceManager devicePluginForDeviceId:deviceId];
        if (plugin) {
            // セッションキーにデバイスプラグインIDを付加する
            NSString *sessionKey = [request stringForKey:DConnectMessageSessionKey];
            if (sessionKey) {
                NSMutableString *s = [NSMutableString stringWithString:sessionKey];
                [s appendString:@"."];
                [s appendString:NSStringFromClass([plugin class])];
                [request setString:s forKey:DConnectMessageSessionKey];
            }
            
            // デバイスIDからデバイスプラグインIDを削除してから、各デバイスプラグインに配送する
            NSString *did = [mgr.mDeviceManager spliteDeviceId:deviceId byDevicePlugin:plugin];
            DConnectRequestMessage *copyRequest = [request copy];
            [copyRequest setString:did forKey:DConnectMessageDeviceId];
            
            // アクセストークンの取得
            // 特定のプロファイルはアクセストークン無しでもアクセスできるので無視する
            NSString *accessToken = nil;
            NSArray *scopes = DConnectIgnoreProfiles();
            if (plugin.useLocalOAuth && ![scopes containsObject:profileName]) {
                accessToken = [self authorizationToDevicePlugin:plugin deviceId:deviceId];
                if (accessToken) {
                    [copyRequest setString:accessToken forKey:DConnectMessageAccessToken];
                } else {
                    // アクセストークンの取得に失敗
                    [response setErrorToAuthorization];
                    return YES;
                }
            }

            // 実際にデバイスプラグインに送信
            BOOL send = [plugin didReceiveRequest:copyRequest response:response];
            if (send) {
                if ([response result] == DConnectMessageResultTypeError) {
                    // エラーレスポンスの場合にはエラーコードをみてアクセストークンをリフレッシュするなど
                    // して再度アクセスするようにする。
                    DConnectLocalOAuthDB *authDB = [DConnectLocalOAuthDB sharedLocalOAuthDB];
                    if ([response errorCode] == DConnectMessageErrorCodeNotFoundClientId) {
                        [authDB deleteAuthDataByDeviceId:deviceId];
                    } else if ([response errorCode] == DConnectMessageErrorCodeExpiredAccessToken) {
                        [authDB deleteAccessToken:accessToken];
                    } else {
                        return YES;
                    }
                    // アクセストークンの再取得
                    accessToken = [self authorizationToDevicePlugin:plugin deviceId:deviceId];
                    if (accessToken) {
                        [copyRequest setString:accessToken forKey:DConnectMessageAccessToken];
                        [[response internalDictionary] removeAllObjects];
                        send = [plugin didReceiveRequest:copyRequest response:response];
                    } else {
                        [response setErrorToAuthorization];
                        return YES;
                    }
                }
            }
            return send;
        } else {
            [response setErrorToNotFoundDevice];
        }
    }
    return YES;
}

- (NSString *) authorizationToDevicePlugin:(DConnectDevicePlugin *)plugin
                                  deviceId:(NSString *)deviceId
{
    DConnectLocalOAuthDB *authDB = [DConnectLocalOAuthDB sharedLocalOAuthDB];
    DConnectAuthData *data = [authDB getAuthDataByDeviceId:deviceId];
    if (data == nil) {
        DConnectResponseMessage *response = [self createClientToDevicePlugin:plugin];
        if ([response result] == DConnectMessageResultTypeOk) {
            NSString *clientId = [response stringForKey:DConnectAuthorizationProfileParamClientId];
            NSString *clientSecret = [response stringForKey:DConnectAuthorizationProfileParamClientSecret];
            [authDB addAuthDataWithDeviceId:deviceId
                                   clientId:clientId
                               clientSecret:clientSecret];
            data = [authDB getAuthDataByDeviceId:deviceId];
        } else {
            return nil;
        }
    }
    
    NSString *accessToken = [authDB getAccessTokenByAuthData:data];
    if (accessToken == nil) {
        DConnectResponseMessage *response =
        [self requestAccessTokenToDevicePlugin:plugin
                                      deviceId:deviceId
                                      clinetId:data.clientId
                                  clinetSecret:data.clientSecret];
        if ([response result] == DConnectMessageResultTypeOk) {
            accessToken = [response stringForKey:DConnectMessageAccessToken];
            [authDB addAccessToken:accessToken withAuthData:data];
        } else {
            return nil;
        }
    }
    return accessToken;
}

- (DConnectResponseMessage *) createClientToDevicePlugin:(DConnectDevicePlugin *)plugin {
    DConnectRequestMessage *request = [DConnectRequestMessage message];
    [request setAction:DConnectMessageActionTypeGet];
    [request setProfile:DConnectAuthorizationProfileName];
    [request setAttribute:DConnectAuthorizationProfileAttrCreateClient];
    [request setString:@"manager" forKey:DConnectAuthorizationProfileParamPackage];
 
    DConnectResponseMessage *response = [DConnectResponseMessage message];
    BOOL result = [plugin didReceiveRequest:request response:response];
    if (!result) {
        // ここに入る場合はプログラム的にバグ
        [NSException raise:@"AuthorizationException" format:@"テスト"];
    }
    return response;
}

- (DConnectResponseMessage *) requestAccessTokenToDevicePlugin:(DConnectDevicePlugin *)plugin deviceId:(NSString *)deviceId clinetId:(NSString *)clientId clinetSecret:(NSString *)clientSecret {
    NSArray *scopes = [self getScopeFromDevicePlugin:plugin];
    NSString *sig = [DConnectUtil generateSignatureWithClientId:clientId
                                                      grantType:DConnectAuthorizationProfileGrantTypeAuthorizationCode
                                                       deviceId:nil
                                                         scopes:scopes
                                                   clientSecret:clientSecret];
    NSString *scope = [DConnectUtil combineScopes:scopes];
    
    DConnectRequestMessage *request = [DConnectRequestMessage message];
    [request setAction:DConnectMessageActionTypeGet];
    [request setProfile:DConnectAuthorizationProfileName];
    [request setAttribute:DConnectAuthorizationProfileAttrRequestAccessToken];
    [request setString:clientId forKey:DConnectAuthorizationProfileParamClientId];
    [request setString:DConnectAuthorizationProfileGrantTypeAuthorizationCode forKey:DConnectAuthorizationProfileParamGrantType];
    [request setString:@"manager" forKey:DConnectAuthorizationProfileParamApplicationName];
    [request setString:scope forKey:DConnectAuthorizationProfileParamScope];
    [request setString:sig forKey:DConnectAuthorizationProfileParamSignature];
    
    DConnectResponseMessage *response = [DConnectResponseMessage message];
    BOOL result = [plugin didReceiveRequest:request response:response];
    if (!result) {
        // ここに入る場合はプログラム的にバグ
        [NSException raise:@"AuthorizationException" format:@"テスト"];
    }
    return response;
}

- (NSArray *)getScopeFromDevicePlugin:(DConnectDevicePlugin *)plugin {
    NSMutableArray *scopes = [NSMutableArray array];
    NSArray *profiles = [plugin profiles];
    for (DConnectProfile *profile in profiles) {
        [scopes addObject:[profile profileName]];
    }
    return [scopes sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
}

@end
