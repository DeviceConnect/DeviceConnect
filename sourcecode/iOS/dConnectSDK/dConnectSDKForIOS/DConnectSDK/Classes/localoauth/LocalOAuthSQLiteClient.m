//
//  LocalOAuthSQLiteClient.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteClient.h"
#import "LocalOAuthUtils.h"
#import "LocalOAuthDummy.h"

@interface LocalOAuthSQLiteClient() {
    
    /** ID. */
    long long _id;
    
    /** クライアントID. */
    NSString *_clientId;
    
    /** パッケージ情報. */
    LocalOAuthPackageInfo *_packageInfo;
    
    /** クライアントシークレット. */
    NSString *_clientSecret;
    
    /** クライアントタイプ. */
    LocalOAuthClientType _clientType;
    
    /** 登録日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値). */
    long long _registrationDate;

}

@end

@implementation LocalOAuthSQLiteClient

- (id) init {
    self = [super init];
    
    if (self) {
        _id = 0;
        _clientId = nil;
        _packageInfo = nil;
        _clientSecret = nil;
        _clientType = CLIENT_TYPE_CONFIDENTIAL;
        _registrationDate = [LocalOAuthUtils getCurrentTimeInMillis];
    }
    
    return self;
}

- (id) init: (NSString *)clientId
  packageInfo:(LocalOAuthPackageInfo *)packageInfo
   clientType:(LocalOAuthClientType)clientType
 redirectURIs:(NSArray *)redirectURIs
   properties:(NSDictionary *)properties {

    self = [super init];
    
    _id = 0;
    _clientId = clientId;
    _packageInfo = packageInfo;
    _clientType = clientType;
    //        this.mRedirectURIs = redirectURIs;
    //        this.mProperties = properties;
    _registrationDate = [LocalOAuthUtils getCurrentTimeInMillis];
    return self;
}

- (void) setId: (long long) id_ {
    _id = id_;
}

- (NSString *) clientId {
    return _clientId;
}

- (void) setClientId: (NSString *)clientId {
    _clientId = clientId;
}

- (LocalOAuthPackageInfo *) packageInfo {
    return _packageInfo;
}

- (void) setPackageInfo: (LocalOAuthPackageInfo *) packageInfo {
    _packageInfo = packageInfo;
}

- (NSString *) clientSecret {
    return _clientSecret;
}

- (void) setClientSecret: (NSString *)clientSecret {
    _clientSecret = clientSecret;
}

- (NSArray *) redirectURIs {
    NSArray * redirectURIs = @[DUMMY_REDIRECTURI];
    return redirectURIs;
}

- (void) setRedirectURIs: (NSArray *)redirectURIs {
    /* @[DUMMY_REDIRECTURI] */
}

- (NSArray *) properties {
    return nil;
}

- (BOOL) isResponseTypeAllowed: (LocalOAuthResponseType) responseType {
    BOOL result = responseType == RESPONSE_TYPE_CODE;
    return result;
}

- (BOOL) isGrantTypeAllowed: (LocalOAuthGrantType) grantType {
    BOOL result = grantType == GRANT_TYPE_AUTHORIZATION_CODE;
    return result;
}

- (LocalOAuthClientType) clientType {
    return _clientType;
}

- (void) setClientType: (LocalOAuthClientType) clientType {
    _clientType = clientType;
}

- (long long) registrationDate {
    return _registrationDate;
}

- (void) setRegistrationDate: (long long) registrationDate {
    _registrationDate = registrationDate;
}



@end
