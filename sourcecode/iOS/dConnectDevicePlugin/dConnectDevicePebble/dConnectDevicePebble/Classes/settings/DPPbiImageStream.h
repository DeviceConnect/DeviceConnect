//
//  DPPbiImageStream.h
//  PebbleSample
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 小林伸郎. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <PebbleKit/PebbleKit.h>

/*!
 @brief Pebbleで表示できる画像フォーマットに変換するクラス。
 */
@interface DPPbiImageStream : NSObject

/*!
 @brief 指定されたPBBitmapをPebbleに送信するデータフォーマットに変換する。
 
 @param[in] bitmap 変換するデータ
 @retval 変換後のデータ
 @retval nil 変換に失敗した場合
 */
+ (NSData *) createPibImageData:(PBBitmap *)bitmap;

@end
