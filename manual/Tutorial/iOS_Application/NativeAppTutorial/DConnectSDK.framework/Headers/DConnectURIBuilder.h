//
//  DConnectURIBuilder.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief URIの作成機能を提供する。
 @author NTT DOCOMO
 */

/*!
 @class DConnectURIBuilder
 @brief 指定された情報からAPIへのURLを提供する。
 */
@interface DConnectURIBuilder : NSObject

/*!
 @brief スキーム。
 
 デフォルトでは「http」が設定される。
 */
@property (nonatomic, copy) NSString *scheme;

/*!
 @brief ホスト名。
 
 デフォルトでは @link DConnectSettings::host @endlink が設定される。
 */
@property (nonatomic, copy) NSString *host;

/*!
 @brief ポート番号。
 
 デフォルトでは @link DConnectSettings::port @endlink が設定される。
 */
@property (nonatomic) int port;

/*!
 @brief プロファイル名。
 */
@property (nonatomic, copy) NSString *profile;

/*!
 @brief インターフェース名。
 */
@property (nonatomic, copy) NSString *interface;

/*!
 @brief アトリビュート名。
 */
@property (nonatomic, copy) NSString *attribute;

/*!
 @brief API名。
 */
@property (nonatomic, copy) NSString *api;

/*!
 @brief パス。
 
 APIのパスを文字列で設定する。
 このパラメータが設定されている場合はビルド時に api、profile、interface、attribute は無視される。
 */
@property (nonatomic, copy) NSString *path;

/*!
 @brief パラメータを格納するDictionary。
 */
@property (nonatomic, strong, readonly) NSMutableDictionary *params;

/*!
 @brief 設定されたURL情報からNSURLを生成する。
 
 クエリストリング内の各パラメータ名、パレメータ値はパーセントエンコードされる。
 
 @return 設定された情報から生成されるNSURLのインスタンス。設定情報に誤りがある場合はnilを返す。
 */
- (NSURL *) build;

/*!
 @brief パラメータを追加する。
 
 @param[in] parameter パラメータ値
 @param[in] name パラメータ名
 */
- (void) addParameter:(NSString *)parameter forName:(NSString *)name;

@end
