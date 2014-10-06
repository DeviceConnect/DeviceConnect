//
//  DConnectProfileProvider.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief プロファイルを管理するための機能を提供する。
 @author NTT DOCOMO
 */

@class DConnectProfile;

/*! 
 @protocol DConnectProfileProvider
 @brief プロファイル管理機能を提供するクラス。
 */
@protocol DConnectProfileProvider <NSObject>

/*!
 @brief プロファイルを追加する。
 
 @param[in] profile 追加するプロファイル
 */
- (void) addProfile:(DConnectProfile *) profile;

/*!
 
 @brief プロファイルを削除する。
 
 @param[in] profile 削除するプロファイル
 */
- (void) removeProfile:(DConnectProfile *) profile;

/*!
 
 @brief 指定した名前のプロファイルを取得する。
 
 指定した名前のプロファイルが存在しない場合にはnilを返却する。

 @param[in] name プロファイル名
 @return DConnectProfileのインスタンス
 */
- (DConnectProfile *) profileWithName:(NSString *) name;

/*!

 @brief プロファイル一覧を取得する。

 @return プロファイル一覧
 */
- (NSArray *) profiles;

@end
