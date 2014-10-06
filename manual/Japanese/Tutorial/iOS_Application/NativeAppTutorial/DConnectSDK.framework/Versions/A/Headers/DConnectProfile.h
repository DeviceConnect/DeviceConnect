//
//  DConnectProfile.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief プロファイルの基礎機能を提供する。
 @author NTT DOCOMO
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectProfileProvider.h>

/*!
 @class DConnectProfile
 @brief プロファイルのベースクラス。
 
 このサンプルコードでは、以下のようなURLに対応する。<br>
 GET http://{dConnectドメイン}/gotapi/example/test?deviceId=xxxx
 
 */
@interface DConnectProfile : NSObject

/*!
 @brief プロファイルプロバイダ。
 */
@property (nonatomic, weak) id<DConnectProfileProvider> provider;

/*!
 @brief プロファイル名を取得する。
 
 実装されていない場合には、nilを返却する。
 
 @return プロファイル名
 */
- (NSString *) profileName;

/*!
 @brief プロファイルの表示名を取得する。
 
 実装されていない場合には、nilを返却する。
 @return プロファイルの表示名
 */
- (NSString *) displayName;

/*!
 
 @brief プロファイルの説明文を取得する。
 実装されていない場合には、nilを返却する。
 
 @return プロファイルの説明文
 */
- (NSString *) detail;

/*!
 
 @brief プロファイルの有効期間(分)を取得する。
 実装されていない場合には、180日とする。
 
 @return 有効期間(分)
 */
- (long long) expirePeriod;

/*!
 @brief リクエストを受領し、各メソッドにリクエストを配送する。
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @return responseが処理済みならYES、そうでなければNO。responseの非同期更新がまだ完了していないなどの理由でresponseを返却すべきでない状況ならばNOを返すべき。
 */
- (BOOL) didReceiveRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 
 @brief GETメソッドリクエスト受信時に呼び出される。
 
 この関数でRESTfulのGETメソッドに対応する処理を記述する。
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @return responseが処理済みならYES、そうでなければNO。responseの非同期更新がまだ完了していないなどの理由でresponseを返却すべきでない状況ならばNOを返すべき。
 */
- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 @brief POSTメソッドリクエスト受信時に呼び出される。
 
 この関数でRESTfulのPOSTメソッドに対応する処理を記述する。
 
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @return responseが処理済みならYES、そうでなければNO。responseの非同期更新がまだ完了していないなどの理由でresponseを返却すべきでない状況ならばNOを返すべき。
 */
- (BOOL) didReceivePostRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 
 @brief PUTメソッドリクエスト受信時に呼び出される。
 この関数でRESTfulのPUTメソッドに対応する処理を記述する。
 
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @return responseが処理済みならYES、そうでなければNO。responseの非同期更新がまだ完了していないなどの理由でresponseを返却すべきでない状況ならばNOを返すべき。
 */
- (BOOL) didReceivePutRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 @brief DELETEメソッドリクエスト受信時に呼び出される。
 この関数でRESTfulのDELETEメソッドに対応する処理を記述する。
 
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @return responseが処理済みならYES、そうでなければNO。responseの非同期更新がまだ完了していないなどの理由でresponseを返却すべきでない状況ならばNOを返すべき。
 */
- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;


@end
