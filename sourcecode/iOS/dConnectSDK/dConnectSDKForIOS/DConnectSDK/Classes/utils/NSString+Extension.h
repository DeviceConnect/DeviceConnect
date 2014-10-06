//
//  NSString+Extension.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

@interface NSString (Extension)

/**
 NULL終端文字の無いC文字列から文字列を生成する。
 @param[in] bytes C文字列（NULL終端文字無し）へのポインタ
 @param[in] length C文字列（NULL終端文字無し）の長さ
 @return <code>bytes</code>のコピーにNULL終端文字を追加したものから生成した文字列
 */
+ (instancetype)stringWithUTF8StringAddingNullTermination:(const char *const) bytes length:(NSUInteger)length;

@end
