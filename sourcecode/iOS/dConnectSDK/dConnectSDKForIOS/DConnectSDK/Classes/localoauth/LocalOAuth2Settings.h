//
//  LocalOAuth2Settings.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>


/** 1秒あたりのミリ秒数. */
extern long long const MSEC;

/** 1分あたりの秒数. */
extern long long const MINUTE;

/** 1時間あたりの秒数. */
extern long long const HOUR;

/** 1日あたりの秒数. */
extern long long const DAY;

/** 長時間使用されなかったクライアントをクリーンアップするまでの時間[sec]. */
extern long long const LocalOAuth2Settings_CLIENT_CLEANUP_TIME;

/** 無効な状態で残っているトークン(発行したクライアントIDがすでに破棄されている)をクリーンアップするまでの時間[sec]. */
extern long long const LocalOAuth2Settings_TOKEN_CLEANUP_TIME;


/** クライアント数の上限.  */
extern int const LocalOAuth2Settings_CLIENT_MAX;

/** スコープ毎のアクセストークンの有効期限デフォルト値[sec]. */
extern long long const LocalOAuth2Settings_DEFAULT_TOKEN_EXPIRE_PERIOD;  // 180日間[sec]

/** 有効期限0が設定されたときに、初回アクセスを「有効期限内」として返す猶予時間[秒]. */
extern long long const LocalOAuth2Settings_ACCESS_TOKEN_GRACE_TIME; /* 1分[秒] */


@interface LocalOAuth2Settings : NSObject

@end
