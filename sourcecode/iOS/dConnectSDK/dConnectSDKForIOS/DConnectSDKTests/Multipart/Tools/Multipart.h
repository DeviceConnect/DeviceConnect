/**
 * Multipart.h
 * DConnectSDK
 *
 * Created by 福井 重和 on 2014/05/08.
 * Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
 *
 * @see <a href="http://d.hatena.ne.jp/eth0jp/20110415/1302808197">
 * http://d.hatena.ne.jp/eth0jp/20110415/1302808197</a>
 */

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
