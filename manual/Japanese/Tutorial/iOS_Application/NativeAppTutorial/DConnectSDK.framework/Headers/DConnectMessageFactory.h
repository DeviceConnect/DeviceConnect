//
//  DConnectMessageFactory.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief メッセージとHTTPリクエストの相互変換を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>

/*!
 @class DConnectMessageFactory
 @brief メッセージとHTTPデータの相互変換機能を提供する。
 */
@interface DConnectMessageFactory : NSObject

/*!
 @brief DConnectRequestMessageをNSURLRequestへ変換する。
 
 @param[in] request リクエストデータ
 @return DConnectRequestMessageの設定内容に沿うNSURLRequestのオブジェクト
 */
+ (NSURLRequest *) requestForMessage:(DConnectRequestMessage *)request;

/*!
 @brief NSURLRequestをDConnectRequestMessageへ変換する。
 
 @param[in] request リクエストデータ
 @return NSURLRequestの設定内容に沿うDConnectRequestMessageのオブジェクト
 */
+ (DConnectRequestMessage *) messageForRequest:(NSURLRequest *)request;

/*!
 @brief NSURLResponseをDConnectResponseMessageへ変換する。
 
 @param[in] response レスポンスデータ
 @param[in] data レスポンスボディ
 @return NSURLResponseの設定内容に沿うDConnectResponseMessageのオブジェクト
 */
+ (DConnectResponseMessage *) messageForResponse:(NSURLResponse *)response data:(NSData *)data;

@end
