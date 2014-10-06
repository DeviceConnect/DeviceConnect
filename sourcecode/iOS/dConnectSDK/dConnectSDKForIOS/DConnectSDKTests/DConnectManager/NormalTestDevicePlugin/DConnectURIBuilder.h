//
//  DConnectURIBuilder.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

/*!
 URIを作成するためのユーティリティクラス.
 */
@interface DConnectURIBuilder : NSObject

/*! ホスト名. */
@property (nonatomic) NSString *host;
/*! ポート番号. */
@property (nonatomic) int port;
/*!
 URIのパス.
 pathが設定されている場合には、profileやattributeよりもpathを優先する。
 */
@property (nonatomic) NSString *path;
/*!
 プロファイル名.
 pathが設定されている場合は、このパラメータは無視される。
 */
@property (nonatomic) NSString *profile;
/*!
 インターフェイス名.
 pathが設定されている場合は、このパラメータは無視される。
 */
@property (nonatomic) NSString *interface;
/*!
 アトリビュート名.
 pathが設定されている場合は、このパラメータは無視される。
 */
@property (nonatomic) NSString *attribute;
/*!
 パラメータ一覧.
 */
@property (nonatomic) NSMutableDictionary *params;

/*!
 パラメータを追加する.
 @param value 追加するパラメータの値
 @param key 追加するパラメータのキー
 */
- (void) addParameter:(NSString *)value forKey:(NSString *)key;

/*!
 設定されたデータからURIを作成する.
 @return URI
 */
- (NSString *) build;

@end
