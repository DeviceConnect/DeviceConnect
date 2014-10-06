//
//  LocalOAuth2Settings.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuth2Settings.h"

/** 1秒あたりのミリ秒数. */
long long const MSEC = 1000;

/** 1分あたりの秒数. */
long long const MINUTE = 60;

/** 1時間あたりの秒数. */
long long const HOUR = 60 * MINUTE;

/** 1日あたりの秒数. */
long long const DAY = 24 * HOUR;

/** 長時間使用されなかったクライアントをクリーンアップするまでの時間[sec]. */
long long const LocalOAuth2Settings_CLIENT_CLEANUP_TIME = 30 * DAY;

/** 無効な状態で残っているトークン(発行したクライアントIDがすでに破棄されている)をクリーンアップするまでの時間[sec]. */
long long const LocalOAuth2Settings_TOKEN_CLEANUP_TIME = 10 * DAY;


/** クライアント数の上限.  */
int const LocalOAuth2Settings_CLIENT_MAX = 100;

/** スコープ毎のアクセストークンの有効期限デフォルト値[sec]. */
long long const LocalOAuth2Settings_DEFAULT_TOKEN_EXPIRE_PERIOD = 180 * DAY;  // 180日間[sec]

/** 有効期限0が設定されたときに、初回アクセスを「有効期限内」として返す猶予時間[秒]. */
long long const LocalOAuth2Settings_ACCESS_TOKEN_GRACE_TIME = 1 * MINUTE; /* 1分[秒] */

@implementation LocalOAuth2Settings

@end
