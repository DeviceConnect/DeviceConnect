//
//  LocalOAuthUtils.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthUtils : NSObject

/*!
    現在時刻を1970年からの経過ミリ秒で返す.
    @return 経過ミリ秒
 */
+ (long long)getCurrentTimeInMillis;

@end
