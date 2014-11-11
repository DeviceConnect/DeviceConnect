//
//  Hash.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface Hash : NSObject

/**
 * 文字列からハッシュを生成する。
 * @return 16進数の文字列ハッシュ
 *
 * @see man CC_SHA
 * @see <a href="http://qiita.com/keroxp/items/fe6d3f200847a1dc2073">
 * http://qiita.com/keroxp/items/fe6d3f200847a1dc2073</a>
 */
NSString *SHA256Hash(NSString *seed);

@end
