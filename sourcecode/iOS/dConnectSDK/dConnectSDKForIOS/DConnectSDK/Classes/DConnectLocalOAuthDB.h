//
//  DConnectLocalOAuthDB.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/*!
 @brief LocalOAuthのデータを格納する。
 */
@interface DConnectAuthData : NSObject
/*! @brief AuthDataのユニークID。 */
@property (nonatomic) int id;
/*! @brief デバイスID。 */
@property (nonatomic) NSString *deviceId;
/*! @brief クライアントID。 */
@property (nonatomic) NSString *clientId;
/*! @brief クライアントシークレット。 */
@property (nonatomic) NSString *clientSecret;
@end


/*!
 @brief Local OAuthのDBを管理するクラス。
 */
@interface DConnectLocalOAuthDB : NSObject

/*!
 @brief DConnectLocalOAuthDBを取得する。
 */
+ (DConnectLocalOAuthDB *) sharedLocalOAuthDB;

/*!
 @brief AuthDataをDBに格納する。
 
 @param[in] deviceId デバイスID
 @param[in] clientId クライアントID
 @param[in] clientSecret クライアントシークレット
 
 @retval YES DBに保存成功した場合
 @retval NO DBに保存失敗した場合
 */
- (BOOL)addAuthDataWithDeviceId:(NSString *)deviceId clientId:(NSString *)clientId clientSecret:(NSString *)clientSecret;

- (DConnectAuthData *)getAuthDataByDeviceId:(NSString *)deviceId;
- (BOOL)deleteAuthDataByDeviceId:(NSString *)deviceId;

- (BOOL)addAccessToken:(NSString *)accessToken withAuthData:(DConnectAuthData *)data;
- (NSString *)getAccessTokenByAuthData:(DConnectAuthData *)data;
- (BOOL)deleteAccessTokenByAuthData:(DConnectAuthData *)data;
- (BOOL)deleteAccessToken:(NSString *)accessToken;
@end
