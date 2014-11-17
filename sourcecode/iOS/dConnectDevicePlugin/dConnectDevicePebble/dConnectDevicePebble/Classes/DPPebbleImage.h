//
//  DPPebbleImage.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface DPPebbleImage : NSObject

/*!
 @brief pebble用の画像にコンバート。
 @param　data 表示する変換前の画像
 */
+(NSData*)convertImage:(NSData*)data;

@end
