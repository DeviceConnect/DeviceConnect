//
//  LocalOAuthSQLiteToken.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthToken.h"
#import "LocalOAuthTypedefs.h"

@interface LocalOAuthSQLiteToken : NSObject<LocalOAuthTokenProtocol>

/* protocolのメソッドを実装 */

- (NSString *) accessToken;
- (NSString *) tokenType;
- (NSString *) refreshToken;
- (NSArray *) scope;

/* その他のメソッド */
- (long long) id_;
- (NSString *) clientId;
- (NSString *) username;
- (long long) registrationDate;
- (long long) accessDate;
- (NSString *)applicationName;

- (void) setId: (long long)id_;
- (void) setAccessToken: (NSString *)accessToken;
- (void) setTokenType: (NSString *)tokenType;
- (void) setRefreshToken: (NSString *)refreshToken;
- (void) setScope: (NSArray *)scope;
- (void) setClientId: (NSString *)clientId;
- (void) setUsername: (NSString *)username_;
- (void) setRegistrationDate: (long long)registrationDate_;
- (void) setAccessDate: (long long)accessDate_ ;
- (void) setApplicationName: (NSString *)applicationName_;


/**
 * アクセストークンが存在するか確認(全スコープの有効期限が切れていたら有効期限切れとみなす).
 * @return YES: 有効期限切れ / NO: 有効期限内
 */
- (BOOL) isExpired;

/*!
    初回アクセスか判定(登録日時とアクセス日時が一致すればまだアクセスされていないトークンである).
    @return YES: 初回アクセスである / NO: 初回アクセスではない
 */
- (BOOL) isFirstAccess;


@end
