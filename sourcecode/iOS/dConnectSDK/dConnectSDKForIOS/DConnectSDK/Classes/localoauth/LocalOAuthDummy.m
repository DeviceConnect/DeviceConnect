//
//  LocalOAuthDummy.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthDummy.h"

/** ダミー値(RedirectURI). */
NSString *const DUMMY_REDIRECTURI = @"dummyRedirectURI";

/** ダミー値(OriginalRef). */
NSString *const DUMMY_ORIGINALREF = @"dummyOriginalRef";

/** ダミー値(Reference). */
NSString *const DUMMY_REFERENCE = @"dummyReference";

/** ダミー値(Scope). */
NSString *const SCOPE1 = @"scope1";

/** userテーブルのuser_id(usersは今回作らないので、useridに格納する値を定義する). */
long long const USERS_USER_ID = 0;
