//
//  LocalOAuth2Main.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuth2Main.h"
#import "LocalOAuth2Settings.h"
#import "CipherAuthSignature.h"
#import "LocalOAuthDbCacheController.h"
#import "LocalOAuthUtils.h"
#import "LocalOAuthScope.h"
#import "LocalOAuthSQLiteToken.h"
#import "LocalOAuthConfirmAuthParams.h"
#import "LocalOAuthConfirmAuthViewController.h"
#import "LocalOAuthAccessTokenListViewController.h"
#import "LocalOAuthConfirmAuthRequest.h"
#import "LocalOAuthScopeUtil.h"

#import "DConnectProfile.h"
#import "DConnectDevicePlugin.h"


/** authorization_code. */
NSString *const LOCALOAUTH_AUTHORIZATION_CODE =@"authorization_code";

/** 例外メッセージ(初期化されていない) */
NSString *const EXCEPTON_NOT_BEEN_INITIALIZED = @"Not been initialized.";





/** LocalOAuth2Mainインスタンスを格納する(key:クラス名(NSString*), value:インスタンス(LocalOAuth2Main*)) */
static NSMutableDictionary *_instanceArray = nil;

/** 承認確認画面リクエストキュー(LocalOAuthConfirmAuthRequest*型の配列、アクセスする際はsynchronizedが必要). */
static NSMutableArray *_requestQueue = nil;

/** 承認確認画面リクエストキュー用Lockオブジェクト. */
static NSObject *_lockForRequstQueue = nil;


@interface LocalOAuth2Main() {
    
    /** キー(クラス名) */
    NSString *_key;
    
    /** 暗号化キー */
    NSString *_keyPair;
    
    /** 自動テストモードフラグ */
    BOOL _autoTestMode;
    
    /** UserManager. */
    LocalOAuthSampleUserManager *_userManager;
}



/*!
 クライアントデータ削除.
 
 @param[in] clientId クライアントID
 */
- (void) removeClientByClientId: (NSString *)clientId;

/*!
 アクセストークン発行処理.
 
 @param[in] params 承認確認画面のパラメータ
 @return アクセストークンデータ(アクセストークン, 有効期間(アクセストークン発行時間から使用可能な時間。単位:ミリ秒) を返す。<br>
 */
- (LocalOAuthAccessTokenData *)publishAccessTokenWithParams:  (LocalOAuthConfirmAuthParams *)params;

/*!
 トークン生成
 [Android版] SQLiteTokenManager.generateToken()
 @param[in] client
 @param[in] username
 @param[in] scopes Scope[]
 @param[in] applicationName
 @return トークン
 */
- (LocalOAuthToken *)generateToken: (LocalOAuthDbCacheController *)db
                            client: (LocalOAuthClient *)client
                          username: (NSString *)username
                            scopes: (NSArray *) scopes
                   applicationName: (NSString *)applicationName;

/*!
 Scope[]からAccessTokenScope[]に変換して返す.
 [android版] LocalOAuthMain.scopesToAccessTokenScopes()
 @param scopes[in] Scope[]の値
 @return AccessTokenScope[]の値
 */
- (NSArray *) accessTokenScopesWithScopes: (NSArray *)scopes;

/*!
 クライアントデータ取得(なければAuthorizatonExceptionをスロー).
 
 @param[in] confirmAuthParams パラメータ
 @return クライアントデータ
 */
- (LocalOAuthClient *) findClientByParams: (LocalOAuthDbCacheController *)db
                        confirmAuthParams: (LocalOAuthConfirmAuthParams *)confirmAuthParams;

/*!
 承認確認画面リクエスト数を取得.
 @return リクエスト数
 */
- (int) countRequest;

/*!
 承認確認画面リクエストをキューに追加.
 @param request[in] リクエスト
 */
- (void) enqueueRequest: (LocalOAuthConfirmAuthRequest *)request;

/*!
 キュー先頭の承認確認画面リクエストをキューから取得する.
 @return not null: 取得したリクエスト / null: キューにデータなし
 */
- (LocalOAuthConfirmAuthRequest *) pickupRequest;

/*!
 threadIdが一致する承認確認画面リクエストをキューから取得する。(キューから削除することも可能).
 @param isDeleteRequest[in] true: スレッドIDが一致したリクエストを返すと同時にキューから削除する。 / false: 削除しない。
 @return not null: 取り出されたリクエスト / null: 該当するデータなし(存在しないthreadIdが渡された、またはキューにデータ無し)
 */
- (LocalOAuthConfirmAuthRequest *) dequeueRequest: (long long)threadId isDeleteRequest: (BOOL)isDeleteRequest;

/*!
 アクセストークン発行確認画面を表示.
 @param request[in] アクセストークン発行確認画面表示リクエストデータ
 */
- (void) startConfirmAuthViewController: (LocalOAuthConfirmAuthRequest *)request;

/*!
 スレッドID取得.
 @return スレッドID
 */
- (LocalOAuthThreadId) getThreadId;


@end



@implementation LocalOAuth2Main


+ (LocalOAuth2Main *)sharedOAuthForClass: (Class)clazz {
    
    NSString *key = [clazz description];
    
    LocalOAuth2Main *oauth = [LocalOAuth2Main sharedOAuthForKey: key];
    return oauth;
}

+ (LocalOAuth2Main *)sharedOAuthForKey: (NSString *)key {
    
    /* mInstanceArray初期化 */
    if (_instanceArray == nil) {
        _instanceArray = [NSMutableDictionary dictionary];
    }
    
    /* キューが初期化されていなければ初期化する */
    if (_requestQueue == nil) {
        _requestQueue = [NSMutableArray array];
        _lockForRequstQueue = [[NSObject alloc]init];
    }
    

    
    LocalOAuth2Main *instance = [_instanceArray objectForKey: key];
    if (instance != nil) {
        /* classに対応するインスタンスが存在すればそれを返す */
        return instance;
        
    } else {
        /* classに対応するインスタンスが無ければインスタンス作成して追加する */
        LocalOAuth2Main *instance = [[LocalOAuth2Main alloc]initWithKey: key];
        [_instanceArray setObject:instance forKey:key];
        
        return instance;
    }
}




/*!
 初期化処理.
 @param[in] key インスタンス識別キー
 @return LocalOAuthインスタンス
 */
- (LocalOAuth2Main *)initWithKey: (NSString *)key {
    
    self = [super init];
    
    _key = key;
    
    /* デフォルト値を設定 */
    [self generateCipherKey];
    [self setAutoTestModeWithFlag: NO];
    
    _userManager = [LocalOAuthSampleUserManager init];
    
    /* ユーザー追加 */
    [self addUserWithUserId: LOCALOAUTH_USER
                       pass: LOCALOAUTH_PASS];
    
    return self;
}

-(void) setAutoTestModeWithFlag: (BOOL)autoTestMode {
    _autoTestMode = autoTestMode;
}

-(BOOL) autoTestMode {
    return _autoTestMode;
}




- (void)addUserWithUserId: (NSString *)user pass:(NSString *)pass {
    
    LocalOAuthSampleUser *sampleUser = [_userManager addUser:user];
    [sampleUser setPassword: pass];
}




- (LocalOAuthClientData *)createClientWithPackageInfo: (LocalOAuthPackageInfo *)packageInfo {
    
    /* 引数チェック */
    if (packageInfo == nil) {
        @throw @"packageInfo is null.";
    } else if (packageInfo.packageName == nil) {
        @throw @"packageName is null.";
    } else if ([packageInfo.packageName length] <= 0) {
        @throw @"packageInfo is empty.";
    }
    
    /* クライアント追加 */
    LocalOAuthClientData *clientData = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        clientData = [db createClient: packageInfo];
    }
    return clientData;
}

- (void)destroyClientWithClientId: (NSString *)clientId {
    
    /* 引数チェック */
    if (clientId  == nil) {
        @throw @"clientId is null.";
    }
    
    [self removeClientByClientId: clientId];
}

- (BOOL) checkSignatureWithClientId: (NSString *)clientId
                          grantType: (NSString *)grantType
                           deviceId: (NSString *)deviceId
                             scopes: (NSArray *)scopes
                          signature: (NSString *)signature {
    
    /* 引数チェック */
    if (signature == nil) {
        @throw @"signature is null.";
    } else if (clientId == nil) {
        @throw @"clientId is null.";
    } else if (grantType == nil) {
        @throw @"grantType is null.";
    } else if (scopes == nil) {
        @throw @"scopes is null.";
    }
    
    BOOL result = NO;
    
    /* LocalOAuthが保持しているクライアントシークレットを取得 */
    LocalOAuthClient *client = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        client = [db findClientByClientId: clientId];
    }
    if (client != nil) {
        NSString *clientSecret = [client clientSecret];
        
        /*
         * LocalOAuthが保持しているclientSecretとリクエストのclient_id, grant_type,
         * scopesを結合して暗号化しsignature作成
         */
        NSString *innerSignature =
        [CipherAuthSignature generateSignatureWithClientId: clientId
                                                 grantType: grantType
                                                  deviceId: deviceId
                                                    scopes: scopes
                                              clientSecret: clientSecret];
        
        /* Signature一致判定 */
        if ([innerSignature isEqualToString: signature]) {
            result = YES;
        } else {
            NSMutableString *strScopes = [NSMutableString string];
            int scopeCount = [scopes count];
            for (int i = 0; i < scopeCount; i++) {
                if (i > 0) {
                    [strScopes appendString: @","];
                }
                [strScopes appendString: [scopes objectAtIndex: i]];
            }
            DCLogD(@"checkSignature() - signature not equal.");
            DCLogD(@" - signature: %@", signature);
            DCLogD(@" - innerSignature:%@", innerSignature);
            DCLogD(@" - clientId:%@", clientId);
            DCLogD(@" - grantType:%@", grantType);
            DCLogD(@" - deviceId:%@", deviceId);
            DCLogD(@" - scopes:%@", strScopes);
            DCLogD(@" - clientSecret:%@", clientSecret);
        }
    } else {
        DCLogD(@"client not found.  clientId: %@", clientId);
    }
    
    return result;
}

- (BOOL) checkSignatureWithAccessToken: (NSString *)accessToken
                          clientSecret: (NSString *)clientSecret
                             signature: (NSString *)signature {
    
    /* 引数チェック */
    if (signature == nil) {
        @throw @"signature is null.";
    } else if (accessToken == nil) {
        @throw @"accessToken is null.";
    } else if (clientSecret == nil) {
        @throw @"clientSecret is null.";
    }
    
    /* Signature作成 */
    NSString *innerSignature =
    [CipherAuthSignature generateSignatureWithAccessToken: accessToken
                                             clientSecret: clientSecret];
    
    /* Signatureが一致するか */
    BOOL result = [signature isEqualToString: innerSignature];
    if (!result) {
        DCLogD(@"checkSignature() - signature not equal.");
        DCLogD(@" - signature: %@", signature);
        DCLogD(@" - innerSignature:%@", innerSignature);
        DCLogD(@" - accessToken:%@", accessToken);
        DCLogD(@" - clientSecret:%@", clientSecret);
    }
    
    return result;
}

- (void) confirmPublishAccessTokenWithParams: (LocalOAuthConfirmAuthParams *)confirmAuthParams
                  receiveAccessTokenCallback: (ReceiveAccessTokenCallback)receiveAccessTokenCallback
                    receiveExceptionCallback: (ReceiveExceptionCallback)receiveExceptionCallback {
    
    /* 引数チェック */
    if (confirmAuthParams == nil) {
        @throw @"confirmAuthParams is null.";
    } else if (receiveAccessTokenCallback == nil) {
        @throw @"receiveAccessTokenCallback is null.";
    } else if (receiveExceptionCallback == nil) {
        @throw @"receiveExceptionCallback is null.";
    } else if ([confirmAuthParams applicationName] == nil || [[confirmAuthParams applicationName] length] <= 0) {
        @throw @"ApplicationName is null.";
    } else if ([confirmAuthParams clientId] == nil || [[confirmAuthParams clientId] length] <= 0) {
        @throw @"ClientId is null.";
    } else if ([confirmAuthParams grantType] == nil || [[confirmAuthParams grantType] length] <= 0) {
        @throw @"GrantType is null.";
    } else if ([confirmAuthParams scope] == nil || [[confirmAuthParams scope] count] <= 0) {
        @throw @"Scope is null.";
    } else if ([confirmAuthParams object] == nil) {
        @throw @"Object is null.";
    }
    
    /* 対応していないgrantTypeなら例外通知 */
    if (![[confirmAuthParams grantType] isEqualToString: LOCALOAUTH_AUTHORIZATION_CODE]) {
        receiveExceptionCallback(@"GrantType is unknown value.");
        return;
    }
    
    /* トークンの状態取得 */
    BOOL isExpiredAccessToken = NO;  /* YES: 有効期限切れ / NO: 有効期限内 */
    BOOL isIncludeScope = NO;        /* YES: 要求スコープが全て含まれている / NO: 一部または全部含まれていない */
    
    LocalOAuthToken *token = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        
        /* クライアントをDBから読み込み */
        LocalOAuthClient *client = [self findClientByParams: db
                                 confirmAuthParams: confirmAuthParams];
        
        /* トークンをDBから読み込み */
        token = [db findTokenByClientUsername: client
                                                      username: LOCALOAUTH_USERNAME];
    }
    
    if (token != nil) {
        /* アクセストークンが存在するか確認(全スコープの有効期限が切れていたら有効期限切れとみなす) */
        LocalOAuthSQLiteToken *sqliteToken = token.delegate;
        if ([sqliteToken isExpired]) {
            isExpiredAccessToken = YES;
        }
    }
    
    /* (a), (b)なら承認確認画面を表示する */
    if (token == nil /* (a) */
        || isExpiredAccessToken /* (a) */
        || !isIncludeScope) { /* (b) */
        
        /* デバイスプラグインオブジェクトを取得(デバイスプラグインでなければnil) */
        DConnectDevicePlugin *devicePlugin = nil;
        if ([confirmAuthParams isForDevicePlugin]) {
            devicePlugin = (DConnectDevicePlugin *) [confirmAuthParams object];
        }
        
        /* 表示文字列を取得する */
        NSArray * scopes = [confirmAuthParams scope];
        NSMutableArray *displayScopes = [NSMutableArray array];
        for (NSString *scope in scopes) {
            
            /* 表示用スコープ名取得(標準名称またはデバイスプラグインが提供する名称、取れなければそのまま返す) */
            NSString *displayScope = [LocalOAuthScopeUtil displayScope: scope
                                                          devicePlugin: devicePlugin];
            
            [displayScopes addObject:displayScope];
        }
        
        /* リクエストデータを生成する */
        NSDate *currentTime = [NSDate date];
        LocalOAuthThreadId threadId = [self getThreadId];
        LocalOAuthConfirmAuthRequest *request =
                [[LocalOAuthConfirmAuthRequest alloc] initWithParameter: threadId
                                                                 params: confirmAuthParams
                                             receiveAccessTokenCallback: receiveAccessTokenCallback
                                               receiveExceptionCallback: receiveExceptionCallback
                                                            currentTime: currentTime
                                                          displayScopes: displayScopes];
        
        /* キューが空なら、リクエストをキューに追加した後すぐにViewControllerを起動する */
        if ([self countRequest] <= 0) {
            [self enqueueRequest: request];
            [self startConfirmAuthViewController: request];
        /* 空で無ければ、リクエストをキューに追加して先の処理が完了した後に処理する */
        } else {
            [self enqueueRequest: request];
        }
    }
}

- (LocalOAuthAccessTokenData *) findAccessTokenByPackageInfo: (LocalOAuthPackageInfo *)packageInfo {
    
    /* 引数チェック */
    if (packageInfo == nil) {
        @throw @"packageInfo is null.";
    } else if (packageInfo.packageName == nil) {
        @throw @"packageName is null.";
    }

    /* クライアント追加 */
    LocalOAuthAccessTokenData *acccessTokenData = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        acccessTokenData = [db findAccessToken: packageInfo];
    }
    
    return acccessTokenData;
}

- (LocalOAuthCheckAccessTokenResult *)checkAccessTokenWithScope: (NSString *)scope
                                                  specialScopes: (NSArray *)specialScopes
                                                    accessToken: (NSString *)accessToken
{
    /* 引数チェック */
    if (scope == nil) {
        @throw @"scope is null.";
    }
    
    // 指定されたスコープの場合には無視する
    if (specialScopes && [specialScopes containsObject:scope]) {
        
        LocalOAuthCheckAccessTokenResult *result =
        [LocalOAuthCheckAccessTokenResult checkAccessTokenResultWithFlags: YES
                                     isExistAccessToken: YES
                                           isExistScope: YES
                                           isNotExpired: YES];
        return result;
    }
    
    BOOL isExistClientId = NO; /*
                                * YES: アクセストークンを発行したクライアントIDあり /
                                * NO: アクセストークンを発行したクライアントIDなし.
                                */
    BOOL isExistAccessToken = NO; /*
                                   * YES: アクセストークンあり / NO:
                                   * アクセストークンなし
                                   */
    BOOL isExistScope = NO; /* YES: スコープあり / NO: スコープなし */
    BOOL isNotExpired = NO; /* YES: 有効期限内 / NO: 有効期限切れ */
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        
        /* アクセストークンを元にトークンを検索する */
        LocalOAuthToken *t = [db findTokenByAccessToken: accessToken];
        LocalOAuthSQLiteToken *token = (LocalOAuthSQLiteToken *)t.delegate;
        if (token != nil) {
            isExistAccessToken = YES; /* アクセストークンあり */

            NSArray *scopes = [token scope]; /* Scope[] */
            NSUInteger scopeCount = [scopes count];
            for (NSUInteger i = 0; i < scopeCount; i++) {
                LocalOAuthScope *s = [scopes objectAtIndex: i];
                
                // リリースビルド時には無効になる
#ifdef DEBUG
                /* token.scopeに"*"が含まれていたら、どんなスコープにもアクセスできる */
                if ([[s scope] isEqualToString: @"*"]) {
                    isExistScope = YES; /* スコープあり */
                    isNotExpired = YES; /* 有効期限 */
                    break;
                }
#endif
                if ([[s scope] isEqualToString: scope]) {
                    isExistScope = YES; /* スコープあり */
                    if ([s expirePeriod] == 0) {
                        /* 有効期限0の場合は、トークン発行から1分以内の初回アクセスなら有効期限内とする */
                        long long t = [LocalOAuthUtils getCurrentTimeInMillis] - [token registrationDate];
                        if (0 <= t && t <= (LocalOAuth2Settings_ACCESS_TOKEN_GRACE_TIME * MSEC)
                            && [token isFirstAccess]) {
                            isNotExpired = YES;
                        }
                    } else if ([s expirePeriod] > 0) {
                        /* 有効期限1以上の場合は、トークン発行からの経過時間が有効期限内かを判定して返す */
                        isNotExpired = ![s isExpired];
                    } else {
                        /* 有効期限にマイナス値が設定されていたら、有効期限切れとみなす */
                        isNotExpired = NO;
                    }
                    break;
                }
            }

            /* specialScopesに登録されていればスコープチェックOKとする */
            if (!isExistScope && specialScopes != nil) {
                NSUInteger specialScopeCount = [specialScopes count];
                for (NSUInteger i = 0; i < specialScopeCount; i++) {
                    NSString *s = [specialScopes objectAtIndex: i];
                    
                    if ([scope isEqualToString:s]) {
                        isExistScope = YES; /* スコープあり */
                        isNotExpired = YES; /* 有効期限 */
                        break;
                    }
                }
            }

            /* このトークンを発行したクライアントIDが存在するかチェック */
            if ([db findClientByClientId: [token clientId]] != nil) {
                isExistClientId = YES;
            }
            
            /* トークンのアクセス時間更新 */
            [db updateTokenAccessTime: token];
        }
    }
    
    LocalOAuthCheckAccessTokenResult *result =
    [LocalOAuthCheckAccessTokenResult checkAccessTokenResultWithFlags: isExistClientId
                                 isExistAccessToken: isExistAccessToken
                                       isExistScope: isExistScope
                                       isNotExpired: isNotExpired];
    if (![result checkResult]) {
        DCLogD(@"checkAccessToken() - error.");
        DCLogD(@" - isExistClientId: %d", isExistClientId);
        DCLogD(@" - isExistAccessToken: %d", isExistAccessToken);
        DCLogD(@" - isExistScope: %d", isExistScope);
        DCLogD(@" - isNotExpired: %d", isNotExpired);
        DCLogD(@" - accessToken: %@", accessToken);
        DCLogD(@" - scope: %@", scope);
    }
    
    return result;
}

- (NSString *) createSignatureWithClientId: (NSString *)clientId
                                 grantType: (NSString *)grantType
                                  deviceId: (NSString *)deviceId
                                    scopes: (NSArray *)scopes
                              clientSecret: (NSString *)clientSecret {
    
    /* 引数チェック */
    if (clientId == nil) {
        @throw @"clientId is null.";
    } else if (grantType == nil) {
        @throw @"grantType is null.";
    } else if (scopes == nil) {
        @throw @"scopes is null.";
    } else if (clientSecret == nil) {
        @throw @"clientSecret is null.";
    }
    
    NSString *signature =
    [CipherAuthSignature generateSignatureWithClientId: clientId
                                             grantType: grantType
                                              deviceId: deviceId
                                                scopes: scopes
                                          clientSecret: clientSecret];
    return signature;
}

- (NSString *) createSignatureWithAccessToken: (NSString *)accessToken
                                     clientId: (NSString *)clientId {
    
    /* 引数チェック */
    if (accessToken == nil) {
        @throw @"accessToken is null.";
    } else if (clientId == nil) {
        @throw @"clientId is null.";
    }
    
    /* クライアントIDを元にをクライアントデータを検索する */
    LocalOAuthClient *client = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        client = [db findClientByClientId: clientId];
    }
    NSString *clientSecret = nil;
    if (client != nil) {
        clientSecret = [client clientSecret];
    } else {
        @throw @"AuthorizatonException.CLIENT_NOT_FOUND";
    }
    
    /* Signature作成 */
    NSString *signature = [CipherAuthSignature generateSignatureWithAccessToken: accessToken
                                                                   clientSecret: clientSecret];
    return signature;
}

- (void)destroyAccessTokenByPackageInfo: (LocalOAuthPackageInfo *)packageInfo {
    
    /* 引数チェック */
    if (packageInfo == nil) {
        @throw @"packageInfo is null.";
    } else if (packageInfo.packageName == nil) {
        @throw @"packageName is null.";
    }
    
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        [db destroyAccessToken: (LocalOAuthPackageInfo *)packageInfo];
    }
}

- (LocalOAuthClientPackageInfo *) findClientPackageInfoByAccessToken: (NSString *)accessToken
{
    
    /* 引数チェック */
    if (accessToken == nil) {
        @throw @"accessToken is null.";
    }
    
    LocalOAuthClientPackageInfo *clientPackageInfo = nil;

    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        LocalOAuthToken *t = [db findTokenByAccessToken: accessToken];
        LocalOAuthSQLiteToken *token = (LocalOAuthSQLiteToken *)t.delegate ;
        if (token != nil) {
            NSString *clientId = [token clientId];
            if (clientId != nil) {
                LocalOAuthClient *client = [db findClientByClientId: clientId];
                if (client != nil) {
                    clientPackageInfo = [[LocalOAuthClientPackageInfo alloc] initWithPackageInfo: [client packageInfo]
                                                                                        clientId: clientId];
                }
            }
        }
    }
    
    return clientPackageInfo;
}

- (void) generateCipherKey {
    
    _keyPair = nil;    /* 未実装 */
}


- (NSString *) cipherPublicKey {
    
    return nil;         /* 未実装 */
}

- (void) startAccessTokenListActivity {
    
    dispatch_async(dispatch_get_main_queue(), ^{
        NSBundle *bundle = DCBundle();
        UIStoryboard *sb;
        
        if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
            sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@-iPhone", DConnectStoryboardName]
                                           bundle:bundle];
        } else{
            sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@-iPad", DConnectStoryboardName]
                                           bundle:bundle];
        }
        
        UINavigationController *top = [sb instantiateViewControllerWithIdentifier:@"TokenList"];
        LocalOAuthAccessTokenListViewController *accessTokenListViewController
        = (LocalOAuthAccessTokenListViewController *) top.viewControllers[0];
        
        [accessTokenListViewController setKey: _key];
        
        UIViewController *rootView;
        DCPutPresentedViewController(rootView);
        [rootView presentViewController:top animated:YES completion:nil];
    });
}

- (NSArray *) allAccessTokens {
    
    NSArray *tokens = nil;  /* LocalOAuthSQLiteToken[] */
    
    /* LocalOAuthが保持しているクライアントシークレットを取得 */
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        tokens = [db findTokensByUsername: LOCALOAUTH_USERNAME];
    }
    
    return tokens;
}

- (void) destroyAccessTokenByTokenId: (long long)tokenId {
    
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        [db revokeToken: tokenId];
    }
}

- (void) destroyAllAccessTokens {
    
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        [db revokeAllTokens: LOCALOAUTH_USERNAME];
    }
}

- (LocalOAuthClient *)findClientByClientId: (NSString *)clientId {
    
    LocalOAuthClient *client = nil;
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        client = [db findClientByClientId: clientId];
    }
    return client;
}

-(void) removeClientByClientId: (NSString *)clientId {
    
    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        [db removeClientData: clientId];
    }
}

- (LocalOAuthAccessTokenData *)publishAccessTokenWithParams:  (LocalOAuthConfirmAuthParams *)params {

    @synchronized(self) {
        LocalOAuthDbCacheController *db = [[LocalOAuthDbCacheController alloc]initWithKey: _key];
        NSString *clientId = [params clientId];
        LocalOAuthClient *client = [db findClientByClientId: clientId];
        if (client != nil) {
            NSArray *scopes = [params scope];    /* (NSString *)[] */

            /* デバイスプラグインならスコープ有効期限を取得する */
            NSMutableDictionary *supportProfiles = [NSMutableDictionary dictionary];
            if (params.isForDevicePlugin) {
                DConnectDevicePlugin *devicePlugin = (DConnectDevicePlugin *)params.object;
                
                for (NSString *scope in scopes) {
                    DConnectProfile *profile = [devicePlugin profileWithName: scope];
                    if (profile) {
                        /* デバイスプラグインから分単位の有効期限を受け取り、秒単位に変換して保持する */
                        long long expirePeriod = profile.expirePeriod * MINUTE;
                        NSNumber *e = [NSNumber numberWithLongLong: expirePeriod];
                        [supportProfiles setObject:e forKey:scope];
                    }
                }
            }
            
            /* スコープを登録(Scope型に変換して有効期限を追加する) */
            NSMutableArray *settingScopes = [NSMutableArray array]; /* (LocalOAuthScope*)[] */
            for (NSString *scope in scopes) {
                
                /* デバイスプラグインならxmlファイルに有効期限が存在すれば取得して使用する(無ければデフォルト値) */
                long long expirePeriod = LocalOAuth2Settings_DEFAULT_TOKEN_EXPIRE_PERIOD;
                if (supportProfiles != nil) {
                    NSNumber *e = [supportProfiles objectForKey:scope];
                    if (e != nil) {
                        expirePeriod = [e longLongValue];
                    }
                }
                
                LocalOAuthScope *s =
                        [[LocalOAuthScope alloc]initWithScope:scope
                                                    timestamp:[LocalOAuthUtils getCurrentTimeInMillis]
                                                 expirePeriod:expirePeriod];
                [settingScopes addObject: s];
            }
            
            NSString *username = LOCALOAUTH_USERNAME;
            NSString *applicationName = [params applicationName];
            
            LocalOAuthToken *token =
                    [self generateToken: db
                                 client: (LocalOAuthClient *)client
                               username: (NSString *)username
                                 scopes: (NSArray *) settingScopes
                        applicationName: (NSString *)applicationName
                     ];
            
            
            /* アクセストークンデータを返す */
            NSString *accessToken = [token accessToken];
            NSArray *accessTokenScopes =    /* AccessTokenScope[] */
                    [self accessTokenScopesWithScopes: [token scope]];
            LocalOAuthAccessTokenData *acccessTokenData =
            [LocalOAuthAccessTokenData accessTokenDataWithAccessToken:accessToken  scopes:accessTokenScopes];
            return acccessTokenData;
        }
    }
    return nil;
}

- (LocalOAuthToken *)generateToken: (LocalOAuthDbCacheController *)db
                            client: (LocalOAuthClient *)client
                          username: (NSString *)username
                            scopes: (NSArray *) scopes
                   applicationName: (NSString *)applicationName {
    
    LocalOAuthToken *token = [db generateToken: client
                                      username: username
                                        scopes: scopes
                               applicationName: applicationName];
    return token;
}


- (NSArray *) accessTokenScopesWithScopes: (NSArray *)scopes {
    
    if (scopes != nil && [scopes count] > 0) {
        NSMutableArray *accessTokenScopes = [NSMutableArray array]; /* AccessTokenScope[] */
        
        NSUInteger scopeCount = [scopes count];
        for (NSUInteger i = 0; i < scopeCount; i++) {
            LocalOAuthScope *scope = [scopes objectAtIndex: i];
            LocalOAuthAccessTokenScope *accessTokenScope =
            [LocalOAuthAccessTokenScope accessTokenScopeWithScope: [scope scope]
                                         expirePeriod: [scope expirePeriod]
             ];
            [accessTokenScopes addObject: accessTokenScope];
        }
        return accessTokenScopes;
    }
    return nil;
}

- (LocalOAuthClient *) findClientByParams: (LocalOAuthDbCacheController *)db
                        confirmAuthParams: (LocalOAuthConfirmAuthParams *)confirmAuthParams {
    LocalOAuthClient *client = [db findClientByClientId: [confirmAuthParams clientId]];
    if (client == nil) {
        @throw @"AuthorizatonException.CLIENT_NOT_FOUND";
    }
    return client;
}


- (int) countRequest {
    int count = 0;
    @synchronized (_lockForRequstQueue) {
        if (_requestQueue != nil) {
            count = [_requestQueue count];
        }
    }
    
    return count;
}

- (void) enqueueRequest: (LocalOAuthConfirmAuthRequest *)request {
    @synchronized (_lockForRequstQueue) {
        [_requestQueue addObject:request];
        
    }
}

- (LocalOAuthConfirmAuthRequest *) pickupRequest {
    
    LocalOAuthConfirmAuthRequest *request = nil;
    
    @synchronized (_lockForRequstQueue) {
        
        int requestCount = [_requestQueue count];
        
        /* 先頭キューを返す */
        if (requestCount > 0) {
            request = [_requestQueue objectAtIndex: 0];
        }
    }
    
    return request;
}

- (LocalOAuthConfirmAuthRequest *) dequeueRequest: (long long)threadId
                                  isDeleteRequest: (BOOL)isDeleteRequest {
    
    LocalOAuthConfirmAuthRequest *request = nil;
    
    @synchronized (_lockForRequstQueue) {
        
        /* スレッドIDが一致するリクエストデータを検索する */
        int dequeueIndex = -1;
        int requestCount = [_requestQueue count];
        for (int i = 0; i < requestCount; i++) {
            LocalOAuthConfirmAuthRequest *req = [_requestQueue objectAtIndex: i];
            if ([req threadId] == threadId) {
                dequeueIndex = i;
                break;
            }
        }
        
        if (dequeueIndex >= 0) {
            if (isDeleteRequest) {
                /* スレッドIDに対応するリクエストデータを取得し、キューから削除する */
                request = [_requestQueue objectAtIndex: dequeueIndex];
                [_requestQueue removeObjectAtIndex: dequeueIndex];
            } else {
                /* スレッドIDに対応するリクエストデータを取得 */
                request = [_requestQueue objectAtIndex: dequeueIndex];
            }
        }
    }
    
    return request;
}

- (void) startConfirmAuthViewController: (LocalOAuthConfirmAuthRequest *)request {
    
    void (^blk)() = ^() {
        /* StoryboardからUIViewControllerを取り出してパラメータを設定し表示する */
        UIViewController *topViewController;
        DCPutPresentedViewController(topViewController);
        NSBundle *bundle = DCBundle();
        UIStoryboard *sb;
        
        if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
            sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@-iPhone", DConnectStoryboardName]
                                           bundle:bundle];
        } else{
            sb = [UIStoryboard storyboardWithName:[NSString stringWithFormat:@"%@-iPad", DConnectStoryboardName]
                                           bundle:bundle];
        }
        
        UIViewController *viewController = [sb instantiateViewControllerWithIdentifier:@"Confirm"];
        LocalOAuthConfirmAuthViewController *confirmAuthViewController
        = (LocalOAuthConfirmAuthViewController *)[((UINavigationController *)viewController) viewControllers][0];
        
        [confirmAuthViewController setParameter: [request params]
                                 displayScopes : [request displayScopes]
                                setAutoTestMode: _autoTestMode
                               approvalCallback:
         ^(BOOL isApproval) {
             [topViewController dismissViewControllerAnimated:YES completion:^() {
                 /* 終了アニメーションが終わったら処理を実行する */
                 
                 /* 応答が届いたのでリクエストをキューから削除する */
                 [self dequeueRequest:[request threadId] isDeleteRequest: true];
                 
                 if (isApproval) {
                     /* 承認された */
                     @try {
                         /* アクセストークン発行処理 */
                         LocalOAuthAccessTokenData *accessTokenData =
                         [self publishAccessTokenWithParams: [request params]];
                         
                         /* 承認された */
                         [request receiveAccessTokenCallback](accessTokenData);
                         
                         /* キューにリクエストが残っていれば、次のキューを取得してActivityを起動する */
                         LocalOAuthConfirmAuthRequest *nextRequest = [self pickupRequest];
                         if (nextRequest != nil) {
                             [self startConfirmAuthViewController: nextRequest];
                         }
                         
                     } @catch (NSString *error) {
                         /* 例外 */
                         [request receiveExceptionCallback](error);
                     }
                 } else {
                     /* 拒否された */
                     [request receiveExceptionCallback](nil);
                 }
             }];
         }
         ];
        [topViewController presentViewController:viewController animated:YES completion:nil];
    };
    
    /* mainThreadならブロックをそのまま実行、mainThreadでなければmainThreadから実行 */
    if ([[NSThread currentThread] isMainThread]) {
        blk();
    } else {
        dispatch_async(dispatch_get_main_queue(), blk);
    }
}

- (LocalOAuthThreadId) getThreadId {
    LocalOAuthThreadId threadId = pthread_mach_thread_np(pthread_self());
    return threadId;
}

@end
