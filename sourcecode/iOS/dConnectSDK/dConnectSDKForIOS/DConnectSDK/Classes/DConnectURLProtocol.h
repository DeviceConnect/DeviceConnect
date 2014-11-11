//
//  DConnectURLProtocol.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectRequestMessage.h"

/*!
 HTTPレスポンスとボディデータのコンテナー
 */
@interface ResponseContext : NSObject

@property (nonatomic, strong) NSURLResponse *response; ///< HTTPレスポンス
@property (nonatomic, strong) NSData* data;            ///< HTTPレスポンスのボディデータ

@end

@interface NSURLRequest (DConnect)
/*!
 URLパラメータが記述されたマルチパートボディを解析し、パラメータをリクエストメッセージに追加する。
 @param[in,out] requestMessage URLパラメータを追加するリクエストメッセージ
 */
- (void)addParametersFromMultipartToRequestMessage:(DConnectMessage *)requestMessage;

/*!
 URLパラメータが記述されたボディを解析し、パラメータをリクエストメッセージに追加する。
 @param[in,out] requestMessage URLパラメータを追加するリクエストメッセージ
 */
- (void)addParametersFromHTTPBodyToRequestMessage:(DConnectRequestMessage *)requestMessage;

/*!
 URLパラメータが記述された文字列を解析し、パラメータをリクエストメッセージに追加する。
 @param[in] urlParameterStr URLパラメータが記述された文字列
 @param[in,out] requestMessage URLパラメータを追加するリクエストメッセージ
 @param[in] doDecode パーセントエンコーディングをデコードするかどうか
 */
+ (void)addURLParametersFromString:(NSString *)urlParameterStr
                  toRequestMessage:(DConnectRequestMessage *)requestMessage
                   percentDecoding:(BOOL)doDecode;

@end

@interface DConnectURLProtocol : NSURLProtocol

/*!
 Device Connect ServerのURLのホスト部分
 */
+ (NSString *)host;

/*!
 Device Connect ServerのURLのポート部分
 */
+ (int)port;

/*!
 Device Connect ServerのURLのスキーマ
 */
+ (NSString *)scheme;

/*!
 Device Connect ServerのURLのホスト部分を設定する
 @param[in] host Device Connect ServerのURLのホスト部分
 */
+ (void)setHost:(NSString *)host;

/*!
 Device Connect ServerのURLのポート部分を設定する
 @param[in] port Device Connect ServerのURLのポート部分
 */
+ (void)setPort:(int)port;

/*!
 Device Connect ServerのURLのスキーマを設定する

 @param[in] scheme スキーマ
 */
+ (void)setScheme:(NSString *)scheme;

/*
 ##################################################################
 dConnectManagerによるリクエスト・レスポンス変換処理。テストに使えるかも。

 HTTPリクエスト ⇒ リクエストメッセージ
 HTTPリクエスト ⇒ HTTPレスポンス
 レスポンスメッセージ+HTTPリクエスト+リクエストメッセージ ⇒ HTTPレスポンス
 ##################################################################
 */

/*!
 HTTPリクエストからリクエストメッセージを生成する
 @param[in] request HTTPリクエスト
 @return リクエストメッセージ
 */
+ (DConnectRequestMessage *) requestMessageWithHTTPReqeust:(NSURLRequest *)request;

/*!
 HTTPリクエストを処理して、HTTPレスポンスのコンテキストを作成し、コールバックに引き渡す
 @param[in] request HTTPリクエスト
 @param[in] callback コールバック。<br>引数<code>responseCtx</code>: HTTPレスポンスのコンテキスト
 */
+ (void) responseContextWithHTTPRequest:(NSURLRequest *)request
                               callback:(void(^)(ResponseContext* responseCtx))callback;

/*!
 レスポンスメッセージ、HTTPリクエスト、リクエストメッセージをもとにHTTPレスポンスのコンテキストを生成する
 @param[in] responseMessage dConnectManagerからのレスポンスメッセージ
 @param[in] request dConnectManagerが受け取ったHTTPリクエスト
 @param[in] requestMessage HTTPリクエストから生成されたリクエストメッセージ
 @param[in] callback コールバック。<br>引数<code>responseCtx</code>: HTTPレスポンスのコンテキスト
 */
+ (void) responseContextWithResponseMessage:(DConnectResponseMessage *)responseMessage
                                    precedingHTTPRequest:(NSURLRequest *)request
                                 precedingRequestMessage:(DConnectRequestMessage *)requestMessage
                                                callback:(void(^)(ResponseContext* responseCtx))callback;

/*!
 @brief レスポンスメッセージのURIをFilesプロファイルに置き換える。
 @param[in] response レスポンスメッセージ
 */
+ (void) convertUri:(DConnectMessage *) response;

@end
