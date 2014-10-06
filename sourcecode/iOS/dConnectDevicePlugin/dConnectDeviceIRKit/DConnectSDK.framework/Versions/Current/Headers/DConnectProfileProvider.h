//
//  DConnectProfileProvider.h
//  DConnectSDK
//
//  Created by 小林 伸郎 on 2014/05/09.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief プロファイルを管理するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <Foundation/Foundation.h>

@class DConnectProfile;

/*! @brief プロファイル機能を提供するためのクラス.
 */
@protocol DConnectProfileProvider <NSObject>

/**
 * プロファイルを追加する.
 *
 * @param[in] profile 追加するプロファイル
 */
- (void) addProfile:(DConnectProfile *) profile;

/**
 * プロファイルを削除する.
 *
 * @param[in] profile 削除するプロファイル
 */
- (void) removeProfile:(DConnectProfile *) profile;

/**
 * 指定した名前のプロファイルを取得する.
 * 指定した名前のプロファイルが存在しない場合にはnilを返却する。
 *
 * @param[in] name プロファイル名
 * @return DConnectProfileのインスタンス
 */
- (DConnectProfile *) profileWithName:(NSString *) name;

/**
 * プロファイル一覧を取得する.
 *
 * @return プロファイル一覧
 */
- (NSArray *) profiles;

@end
