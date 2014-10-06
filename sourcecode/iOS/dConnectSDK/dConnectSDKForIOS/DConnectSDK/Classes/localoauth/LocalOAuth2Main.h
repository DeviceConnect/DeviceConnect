//
//  LocalOAuthValues.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthSampleUserManager.h"
#import "LocalOAuthTokenManager.h"
#import "LocalOAuthClientManager.h"
#import "LocalOAuthClientData.h"
#import "LocalOAuthAccessTokenData.h"
#import "LocalOAuthAccessTokenScope.h"
#import "LocalOAuthCheckAccessTokenResult.h"
#import "LocalOAuthClientPackageInfo.h"
#import "LocalOAuthSQLiteClient.h"
#import "LocalOAuthConfirmAuthParamsBuilder.h"
#import "LocalOAuthConfirmAuthParams.h"
#import "LocalOAuthTypedefs.h"


/** authorization_code. */
extern NSString *const LOCALOAUTH_AUTHORIZATION_CODE;


@interface LocalOAuth2Main : NSObject

/*!
    LocalOAuthインスタンス取得.
    @param[in]  clazz   クラスインスタンス
    @return LocalOAuthインスタンス。クラスインスタンスが一緒であれば同じ値を返す。
 */
+ (LocalOAuth2Main *)sharedOAuthForClass: (Class)clazz;

/*!
 LocalOAuthインスタンス取得.(LocalOAuth2Main内部で利用する)
 @param[in]  key   キー
 @return LocalOAuthインスタンス。キーが一緒であれば同じ値を返す。
 */
+ (LocalOAuth2Main *)sharedOAuthForKey: (NSString *)key;


-(void) setAutoTestModeWithFlag: (BOOL)autoTestMode;
-(BOOL) autoTestMode;



/*!
    ユーザーデータ追加.
    @param[in] user ユーザーID
    @param[in] pass パスワード
 */
- (void)addUserWithUserId: (NSString *)user pass:(NSString *)pass;


/*!
    (2)クライアントを登録する。アプリやデバイスプラグインがインストールされるときに実行する.
    @param[in]  packageInfo パッケージ情報
    @return クライアントデータ
*/
- (LocalOAuthClientData *)createClientWithPackageInfo: (LocalOAuthPackageInfo *)packageInfo;

/*!
    (3)クライアントを破棄する。アプリやデバイスプラグインがアンインストールされるときに実行する.
    @param[in] clientId クライアントID
 */
- (void)destroyClientWithClientId: (NSString *)clientId;


/*!
    (4)アプリまたはデバイスプラグインから受け取ったsignatureが、LocalOAuthで生成したsignatureと一致するかチェックする.
    @param[in] クライアントID
    @param[in] グラントタイプ
    @param[in] デバイスID
    @param[in] スコープ
    @param[in] signature
    @return YES:一致 / NO:一致しない
 */
- (BOOL) checkSignatureWithClientId: (NSString *)clientId
                          grantType: (NSString *)grantType
                           deviceId: (NSString *)deviceId
                             scopes: (NSArray *)scopes
                          signature: (NSString *)signature;

/*!
    (4)-2.d-Connect Managerから受け取ったアクセストークン受信用signatureが、アプリで生成したsignatureと一致するかチェックする.
    @param[in] アクセストークン
    @param[in] クライアントシークレット
    @param[in] signature
    @return YES:一致 / NO:一致しない
 */
- (BOOL) checkSignatureWithAccessToken: (NSString *)accessToken
                          clientSecret: (NSString *)clientSecret
                             signature: (NSString *)signature;

/*!
    (5)アクセストークン発行承認確認画面表示.

    - Activityはprocess指定を行わないのでd-Connect Managerのスレッドとは別プロセスとなる。<br>
    - d-ConnectManagerのサービスととプロセス間通信が行えるように(0)onBind()でBindする。<br>
    - Messenger, Handler はLocalOAuth内部に持つ。<br>

    - 状況別で動作が変わる。<br>

    - (a)有効なアクセストークンが存在しない。(失効中も含む) =>
    承認確認画面を表示する。承認/拒否はBindされたServiceへMessageで通知される。

    - (b)アクセストークンは存在するがスコープが不足している。 =>
    承認確認画面を表示する。承認/拒否はBindされたServiceへMessageで通知される。<br>

    - (c)アクセストークンは存在しスコープも満たされている。 => 承認確認画面は表示しない。(Message通知されない)<br>

    @param[in] confirmAuthParams パラメータ
    @param[in] publishAccessTokenListener アクセストークン発行リスナー(承認確認画面で承認／拒否ボタンが押されたら実行される)
 */
- (void) confirmPublishAccessTokenWithParams: (LocalOAuthConfirmAuthParams *)confirmAuthParams
                  receiveAccessTokenCallback: (ReceiveAccessTokenCallback)receiveAccessTokenCallback
                    receiveExceptionCallback: (ReceiveExceptionCallback)receiveExceptionCallback;

/*!
    (6)OAuthクライアント情報からアクセストークンを取得する.

    @param[in] packageInfo パッケージ情報
    @return not null: アクセストークンデータ / null:アクセストークンがない
 */
- (LocalOAuthAccessTokenData *) findAccessTokenByPackageInfo:(LocalOAuthPackageInfo *)packageInfo;

/*!
    (7)アクセストークンを確認する.

    @param[in] accessToken 確認するアクセストークン
    @param[in] scope このスコープがアクセストークンに含まれるかチェックする
    @param[in] specialScopes 承認許可されていなくてもアクセス可能なscopes(nullなら指定無し)
    @return チェック結果
 */
- (LocalOAuthCheckAccessTokenResult *)checkAccessTokenWithScope: (NSString *)scope
                                         specialScopes: (NSArray *)specialScopes
                                                    accessToken: (NSString *)accessToken
;

/*!
    (8)Signatureを作成する.

    @param[in] clientId クライアントID
    @param[in] grantType グラントタイプ
    @param[in] deviceId デバイスID
    @param[in] scopes スコープ
    @param[in] clientSecret クライアントシークレット
    @return not null: 作成したSignature / null: nullは返さない。
 */
- (NSString *) createSignatureWithClientId: (NSString *)clientId
                                 grantType: (NSString *)grantType
                                  deviceId: (NSString *)deviceId
                                    scopes: (NSArray *)scopes
                              clientSecret: (NSString *)clientSecret;

/**
 * (8)-2.アクセストークンを返却する際に添付するSignatureを作成する.
 *
 * @param[in] accessToken アクセストークン
 * @param[in] clientId クライアントId
 * @return not null: 作成したSignature / null: nullは返さない。
 */
- (NSString *) createSignatureWithAccessToken: (NSString *)accessToken
                                     clientId: (NSString *)clientId;

/*!
    (9)クライアントが発行したアクセストークンを破棄して利用できないようにする.<br>
    クライアントはパッケージ名で指定する.

    @param[in] packageInfo パッケージ名
 */
- (void)destroyAccessTokenByPackageInfo: (LocalOAuthPackageInfo *)packageInfo;

/*!
    (10)アクセストークンからクライアントパッケージ情報を取得する.

    @param[in] accessToken アクセストークン
    @return not null: クライアントパッケージ情報 / null:アクセストークンがないのでクライアントパッケージ情報が取得できない。
 */
- (LocalOAuthClientPackageInfo *) findClientPackageInfoByAccessToken: (NSString *)accessToken;

/*!
    (11)暗号化処理用の鍵ペア(公開鍵・秘密鍵)を生成.
 */
- (void) generateCipherKey;

/*!
    (12)暗号化処理用の公開鍵を取得.
    @return 公開鍵
 */
- (NSString *) cipherPublicKey;

/*!
    (13)アクセストークン一覧Activityを表示.
    @param[in] context コンテキスト
 */
- (void) startAccessTokenListActivity;

/*!
    (13)-1.アクセストークン一覧を取得する(startAccessTokenListActivity()用).
    @return not null: アクセストークン(SQLiteToken)の配列 / null: アクセストークンなし
 */
- (NSArray *) allAccessTokens;

/*!
    (13)-2.アクセストークンを破棄して利用できないようにする(startAccessTokenListActivity()用.

    @param[in] tokenId トークンID
 */
- (void) destroyAccessTokenByTokenId: (long long)tokenId;

/*!
    (13)-3.DBのトークンデータを削除(startAccessTokenListActivity()用.
 */
- (void) destroyAllAccessTokens;


/*!
    (13)-4.クライアントIDが一致するクライアントデータを取得する(startAccessTokenListActivity()用).
    @param[in] clientId クライアントID
    @return not null: クライアント / null: クライアントなし
 */
- (LocalOAuthClient *)findClientByClientId: (NSString *)clientId;



    
@end
