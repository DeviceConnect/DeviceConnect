//
//  DConnectAuthorizationProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Authrorizationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectAuthorizationProfileName;

/*!
 @brief アトリビュート: create_client。
 */
extern NSString *const DConnectAuthorizationProfileAttrCreateClient;

/*!
 @brief アトリビュート: request_accesstoken。
 */
extern NSString *const DConnectAuthorizationProfileAttrRequestAccessToken;

/*!
 @brief パラメータ: package。
 */
extern NSString *const DConnectAuthorizationProfileParamPackage;

/*!
 @brief パラメータ: clientId。
 */
extern NSString *const DConnectAuthorizationProfileParamClientId;

/*!
 @brief パラメータ: clientSecret。
 */
extern NSString *const DConnectAuthorizationProfileParamClientSecret;

/*!
 @brief パラメータ: grantType。
 */
extern NSString *const DConnectAuthorizationProfileParamGrantType;

/*!
 @brief パラメータ: scope。
 */
extern NSString *const DConnectAuthorizationProfileParamScope;

/*!
 @brief パラメータ: scopes。
 */
extern NSString *const DConnectAuthorizationProfileParamScopes;

/*!
 @brief パラメータ: applicationName。
 */
extern NSString *const DConnectAuthorizationProfileParamApplicationName;

/*!
 @brief パラメータ: signature。
 */
extern NSString *const DConnectAuthorizationProfileParamSignature;

/*!
 @brief パラメータ: expirePeriod。
 */
extern NSString *const DConnectAuthorizationProfileParamExpirePeriod;

/*!
 @brief パラメータ: accessToken。
 */
extern NSString *const DConnectAuthorizationProfileParamAccessToken;

/*!
 @brief authorization_code。
 */
extern NSString *const DConnectAuthorizationProfileGrantTypeAuthorizationCode;
