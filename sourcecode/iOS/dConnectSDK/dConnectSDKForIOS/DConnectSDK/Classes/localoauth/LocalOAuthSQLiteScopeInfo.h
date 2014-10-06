//
//  LocalOAuthSQLiteScopeInfo.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteScopeDb.h"

@interface LocalOAuthSQLiteScopeInfo : LocalOAuthSQLiteScopeDb

/** プロファイル名.*/
@property NSString *profileName;

/*!
    コンストラクタ.
    @param[in] tokensTokenId tokensテーブルのトークンID
    @param[in] profilesProfileId profilesテーブルのプロファイルID
    @param[in] timestamp タイムスタンプ
    @param[in] expirePeriod 有効期限
    @param[in] profileName プロファイル名
 */
- (LocalOAuthSQLiteScopeInfo *)initWithParameter: (long long)tokensTokenId
                               profilesProfileId: (long long)profilesProfileId
                                       timestamp: (long long)timestamp
                                    expirePeriod: (long long)expirePeriod
                                     profileName: (NSString *)profileName_;


@end
