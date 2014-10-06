//
//  LocalOAuthScope.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthScope : NSObject

/**
 * スコープ名.
 */
@property NSString *scope;

/*!
    トークン更新日時.
    - 新規作成時およびアクセストークン再承認されたときに更新する。
    - 1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値。
 */
@property long long timestamp;

/** トークン有効期間(sec単位). */
@property long long expirePeriod;


/*!
    コンストラクタ.
    @param[in] scope スコープ名
    @param[in] timestamp トークン更新日時(1970/1/1 00:00:00 UTC からの経過ミリ秒。System.currentTimeMillis()で取得した値)
    @param[in] expirePeriod トークン有効期限（sec単位）
 */
- (id)initWithScope: (NSString *)scope
          timestamp: (long long)timestamp
       expirePeriod: (long long)expirePeriod;

/*!
    スコープ名の配列を返す.
    @param[in] scope スコープ配列(Scope[])
    @return スコープ名のString[]配列(0件ならnullを返す)
 */
+ (NSArray *) toScopeStringArray: (NSArray *)scope;

/*!
    文字列に展開したScope値を解析してScope型に戻す.
    @param[in] strScope 文字列に展開したScope値("{スコープ名},{トークン更新日時},{トークン有効期限[sec]}")
    @return Scope型のオブジェクト。変換失敗ならnullを返す。
 */
+ (LocalOAuthScope *)parse: (NSString *)strScope;

/*!
    有効期限表示文字列を返す.
    @return 有効期限表示文字列
 */
- (NSString *) getStrExpirePeriod;

/*!
    有効期限切れか判定.
 
    @return YES: 有効期限切れ / NO: 有効期限内
 */
- (BOOL) isExpired;

- (NSString *) toString;


@end
