//
//  LocalOAuthSQLiteToken.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteToken.h"
#import "LocalOAuthScope.h"

@interface LocalOAuthSQLiteToken() {
    
    /** ID. */
    long long _id;
    
    /** アクセストークン(未発行ならnull). */
    NSString *_accessToken;
    
    /** トークンタイプ("code"を設定). */
    NSString *_tokenType;
    
    /** リフレッシュトークン(未使用のためnullを設定). */
    NSString *_refreshToken;
    
    /** スコープ配列(Scope*の配列). */
    NSArray *_scope;
    
    /** クライアントID. */
    NSString *_clientId;
    
    /** ユーザー名(1件固定なので今回はnullを設定). */
    NSString *_username;
    
    /** 登録日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    long long _registrationDate;
    
    /** アクセス日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    long long _accessDate;
    
    /** アプリケーション名. */
    NSString *_applicationName;

}

@end

@implementation LocalOAuthSQLiteToken

- (long long) id_ {
    return _id;
}
- (NSString *) accessToken {
    return _accessToken;
}
- (NSString *) tokenType {
    return _tokenType;
}
- (NSString *) refreshToken {
    return _refreshToken;
}
- (NSArray *) scope {
    return _scope;
}
- (NSString *) clientId {
    return _clientId;
}
- (NSString *) username {
    return _username;
}
- (long long) registrationDate {
    return _registrationDate;
}
- (long long) accessDate {
    return _accessDate;
}
- (NSString *)applicationName {
    return _applicationName;
}

- (void) setId: (long long)id_ {
    _id = id_;
}

- (void) setAccessToken: (NSString *)accessToken {
    _accessToken = accessToken;
}

- (void) setTokenType: (NSString *)tokenType {
    _tokenType = tokenType;
}

- (void) setRefreshToken: (NSString *)refreshToken {
    _refreshToken = refreshToken;
}

- (void) setScope: (NSArray *)scope {
    _scope = scope;
}

- (void) setClientId: (NSString *)clientId {
    _clientId = clientId;
}

- (void) setUsername: (NSString *)username_ {
    _username = username_;
}

- (void) setRegistrationDate: (long long)registrationDate_ {
    _registrationDate = registrationDate_;
}

- (void) setAccessDate: (long long)accessDate_ {
    _accessDate = accessDate_;
}

- (void) setApplicationName: (NSString *)applicationName_ {
    _applicationName = applicationName_;
}

- (BOOL) isExpired {
    BOOL result = YES;
    NSUInteger scopeCount = [_scope count];
    for (NSUInteger i = 0; i < scopeCount; i++) {
        LocalOAuthScope *scope = [_scope objectAtIndex: i];
        if (![scope isExpired]) {
            result = NO;
            break;
        }
    }
    return result;
}

- (BOOL) isFirstAccess {
    if (_registrationDate > 0 && _accessDate > 0 && _registrationDate == _accessDate) {
        return YES;
    }
    return NO;
}


@end
