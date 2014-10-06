//
//  DConnectMessageFactory.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/29.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

/*! @file
 @brief メッセージとHttpリクエストの相互変換を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.7.29)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>


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
