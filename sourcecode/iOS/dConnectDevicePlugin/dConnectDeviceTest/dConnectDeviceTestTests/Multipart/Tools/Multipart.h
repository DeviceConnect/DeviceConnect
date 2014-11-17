//
//  Multipart.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
#import <Foundation/Foundation.h>

/**
 * Multipart
 */
@interface Multipart : NSObject {
    NSString *bound;
    NSMutableDictionary *items;
}

/**
 * NSDataをHTTPパケットボディのKey-Valueに追加する。
 * @param data 追加するNSData
 * @param key キー
 */
- (void)addData:(NSData *)data forKey:(NSString *)key;
/**
 * NSStringをHTTPパケットボディのKey-Valueに追加する。
 * @param string 追加するNSString
 * @param key キー
 */
- (void)addString:(NSString *)string forKey:(NSString *)key;

/**
 * HTTPパケットボディにKey-Valueエントリがあるかどうかを返す。
 * @return HTTPパケットボディにKey-Valueエントリが有れば<code>true</code>、なければ<code>false</code>
 */
- (BOOL)hasItems;
/**
 * HTTPパケットのコンテンツタイプを返す。
 * @return コンテンツタイプを表す文字列
 */
- (NSString *)contentType;
/**
 * HTTPパケットのボディを返す。
 * @return ボディを表す文字列
 */
- (NSData *)body;

@end
