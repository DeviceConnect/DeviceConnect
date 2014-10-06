//
//  DConnectProfile.h
//  dConnectManager
//
//  Created by 小林 伸郎 on 2014/05/02.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief プロファイルの基礎機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectMessage.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectProfileProvider.h>

/*!
 @brief プロファイルの基底クラス。

 このサンプルコードでは、以下のようなURLに対応する。<br>
 GET http://{dConnectドメイン}/example/test?deviceId=xxxx

 @code

 @interface ExampleProfile : DConnectProfile

 @end

 @implementation ExampleProfile

 - (NSString *) profileName {
     return @"example";
 }

 // RESTfulのGETメソッド要求に対応する処理
 - (BOOL) didReceiveGetRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response {
     NSString *deviceId = [self getDeviceId:request];
     if (!deviceId) {
         // デバイスIDが存在しない場合
 [DConnectUtil setEmptyDeviceIdForResponse:response];
     } else if (![self checkDeviceId:deviceId]) {
         // デバイスが存在しない場合
 [DConnectUtil setNotFoundDeviceForResponse:response];
     } else {
         NSString *attribute = [self getAttribute:request];
         if ([attribute isEqualToString:@"test"]) {
             //
             // ここに各プロファイル処理を記述する
             //
         } else {
             // このプロファイルでは指定されていないattributeが指定された場合
 [DConnectUtil setIllegalProfileForResponse:respnose];
         }
     }
     return YES;
 }

 - (BOOL) checkDeviceId:(NSString *)deviceId {
      // deviceIdに対応するデバイスが存在するかをチェック
      return YES;
 }

 @end

 @endcode
 */
@interface DConnectProfile : NSObject

/**
 * プロファイルプロバイダ.
 */
@property (nonatomic, weak) id<DConnectProfileProvider> provider;

/**
 * プロファイル名を取得する.
 * 実装されていない場合には、nilを返却する。
 * @return プロファイル名
 */
- (NSString *) profileName;

/**
 * リクエストを受領し、各メソッドにリクエストを配送する.
 * <pre>
 * DConnectMessage *data = [DConnectMessage message];
 * [data setString:@"data1" forKey:@"key1"];
 * [data setString:@"data2" forKey:@"key2"];
 *
 * [response setInteger:0 forKey:@"result"];
 * [response setString:@"sample" forKey:@"samplekey"];
 * [response setMessage:data forKey:@"data"];
 * </pre>
 * 上記のようなレスポンスを格納した場合には、JSONでは以下のようになる。
 * <pre>
 * {
 *     "result": 0,
 *     "samplekey": "sample",
 *     "data" : {
 *         "key1": "data1",
 *         "key2": "data2"
 *     }
 * }
 * </pre>
 * @param[in] request リクエスト
 * @param[in,out] response レスポンス
 * @return <code>response</code>が処理済みなら<code>YES</code>、そうでなければ<code>NO</code>。<code>response</code>の非同期更新がまだ完了していないなどの理由で<code>response</code>を返却すべきでない状況ならば<code>NO</code>を返すべき。
 */
- (BOOL) didReceiveRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/**
 * GETメソッドリクエスト用受信ハンドラー.
 * この関数でRESTfulのGETメソッドに対応する処理を記述する。
 * 取得
 * @param[in] request リクエスト
 * @param[in,out] response レスポンス
 * @return <code>response</code>が処理済みなら<code>YES</code>、そうでなければ<code>NO</code>。<code>response</code>の非同期更新がまだ完了していないなどの理由で<code>response</code>を返却すべきでない状況ならば<code>NO</code>を返すべき。
 */
- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/**
 * POSTメソッドリクエスト用受信ハンドラー.
 * この関数でRESTfulのPOSTメソッドに対応する処理を記述する。
 * 新規作成
 * @param[in] request リクエスト
 * @param[in,out] response レスポンス
 * @return <code>response</code>が処理済みなら<code>YES</code>、そうでなければ<code>NO</code>。<code>response</code>の非同期更新がまだ完了していないなどの理由で<code>response</code>を返却すべきでない状況ならば<code>NO</code>を返すべき。
 */
- (BOOL) didReceivePostRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/**
 * PUTメソッドリクエスト用受信ハンドラー.
 * この関数でRESTfulのPUTメソッドに対応する処理を記述する。
 * 更新
 * @param[in] request リクエスト
 * @param[in,out] response レスポンス
 * @return <code>response</code>が処理済みなら<code>YES</code>、そうでなければ<code>NO</code>。<code>response</code>の非同期更新がまだ完了していないなどの理由で<code>response</code>を返却すべきでない状況ならば<code>NO</code>を返すべき。
 */
- (BOOL) didReceivePutRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/**
 * DELETEメソッドリクエスト用受信ハンドラー.
 * この関数でRESTfulのDELETEメソッドに対応する処理を記述する。
 * 削除
 * @param[in] request リクエスト
 * @param[in,out] response レスポンス
 * @return <code>response</code>が処理済みなら<code>YES</code>、そうでなければ<code>NO</code>。<code>response</code>の非同期更新がまだ完了していないなどの理由で<code>response</code>を返却すべきでない状況ならば<code>NO</code>を返すべき。
 */
- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;


@end
