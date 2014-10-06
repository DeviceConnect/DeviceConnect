//
//  LocalOAuthSQLiteClient.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthClient.h"


@interface LocalOAuthSQLiteClient : NSObject<LocalOAuthClientProtocol>

/*!
    コンストラクタ.
 */
- (id) init;

/*!
    コンストラクタ.
    @param[in] clientId クライアントID
    @param[in] packageInfo パッケージ情報
    @param[in] clientType クライアントタイプ
    @param[in] redirectURIs リダイレクトURIs(NSString*の配列)
    @param[in] properties プロパティ({key: NSString* , value: id})
 */
- (id) init: (NSString *)clientId
packageInfo:(LocalOAuthPackageInfo *)packageInfo
 clientType:(LocalOAuthClientType)clientType
redirectURIs:(NSArray *)redirectURIs
 properties:(NSDictionary *)properties;

/*!
    ID設定.
    @param[in] id_    ID
 */
- (void) setId: (long long) id_;

/*!
    クライアントID取得.
    @return クライアントID
 */
- (NSString *) clientId;

/*!
    クライアントID設定.
    @param[in] clientId  クライアントID
 */
- (void) setClientId: (NSString *)clientId;

/*!
    パッケージ情報を取得.
    @return	パッケージ名
 */
- (LocalOAuthPackageInfo *) packageInfo;

/*!
    パッケージ情報設定.
    @param[in] packageInfo   パッケージ情報
 */
- (void) setPackageInfo: (LocalOAuthPackageInfo *) packageInfo;

/*!
    クライアントシークレット取得.
    @return クライアントシークレット
 */
- (NSString *) clientSecret;

/*!
    クライアントシークレット設定.
    @param[in] clientSecret クライアントシークレット
 */
- (void) setClientSecret: (NSString *)clientSecret;

/*!
    リダイレクトURIs取得.
    @return リダイレクトURIs(NSString *の配列)
 */
- (NSArray *) redirectURIs;

/*!
    リダイレクトURIs設定.
 */
- (void) setRedirectURIs: (NSArray *)redirectURIs;

/*!
    プロパティ取得.
    @return プロパティ{key:NSString *, value:id}の配列
 */
- (NSArray *) properties;

/*!
    レスポンスタイプが指定されたものと一致するか.
    @param[in] responseType レスポンスタイプ
    @return YES: 一致する / NO: 一致しない
 */
- (BOOL) isResponseTypeAllowed: (LocalOAuthResponseType) responseType;

/*!
    グラントタイプが指定されたものと一致するか.
    @param[in] grantType グラントタイプ
    @return YES: 一致する / NO: 一致しない
 */
- (BOOL) isGrantTypeAllowed: (LocalOAuthGrantType) grantType;

/*!
    クライアントタイプ取得.
    @return クライアントタイプ
 */
- (LocalOAuthClientType) clientType;

/*!
    クライアントタイプ設定.
    @param[in] clientType    クライアントタイプ
 */
- (void) setClientType: (LocalOAuthClientType) clientType;

/*!
    登録日時取得.
    @return  登録日時
 */
- (long long) registrationDate;

/*!
    登録日時設定.
    @param[in] registrationDate      登録日時
 */
- (void) setRegistrationDate: (long long) registrationDate;












@end
