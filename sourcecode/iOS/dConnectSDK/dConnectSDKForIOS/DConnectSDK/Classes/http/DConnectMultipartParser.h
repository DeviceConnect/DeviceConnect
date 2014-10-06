/**
 DConnectMultipartParser
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

#import "DConnectMessage.h"
#import "multipart_parser.h"

@interface DConnectMultipartParser : NSObject

+ (instancetype)multipartParserWithURL:(NSURLRequest *)url
                              userData:(id)userData;

- (void)parse;

@property NSURLRequest *url;
@property id userData;

@end

/**
 * DConnectMultipartParserに引き渡すユーザデータ
 */
@interface UserData : NSObject

+ (instancetype)userDataWithRequest:(DConnectMessage *)request;

/// dConnectリクエストメッセージ
@property DConnectMessage* request;
/// HTTPリクエストのヘッダ解析ステージにおいて、Content-Typeを解析中かどうか
@property BOOL isContentDisposition;
/// Content-Typeのnameパラメータの値
@property NSString *name;
/// ファイルが指定された際の、Content-Typeのfilenameパラメータの値
@property NSString *filename;
/// ファイルが指定された際の、ファイルを一時保管URL
@property NSURL *uri;
/**
 * パース中にコンテンツデータを溜め込むバッファ
 */
@property NSMutableData *bodyBuf;

@end
