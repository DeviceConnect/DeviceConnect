//
//  LocalOAuthSQLiteScopeDb.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthSQLiteScopeDb : NSObject

/** tokensテーブルのtokenID. */
@property long long tokensTokenId;

/** profilesテーブルのプロファイルID. */
@property long long profilesProfileId;

/**
 * トークン更新日時.
 * - 新規作成時およびアクセストークン再承認されたときに更新する。
 * - 1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値。
 */
@property long long timestamp;

/** トークン有効期間(sec単位). */
@property long long expirePeriod;


/*!
    コンストラクタ.
 */
- (id)init;

/*!
    コンストラクタ.
    @param tokensTokenId トークンID
    @param profilesProfileId プロファイルID
    @param timestamp タイムスタンプ
    @param expirePeriod 有効期限[sec]
 */
- (id)init: (long long)tokensTokenId_
profilesProfileId:(long long)profilesProfileId_
 timestamp:(long long)timestamp_
expirePeriod:(long long)expirePeriod_;




@end
