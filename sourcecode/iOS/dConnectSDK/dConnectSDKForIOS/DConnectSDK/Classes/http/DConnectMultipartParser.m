/**
 DConnectMultipartParser
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

#import <DConnectSDK/DConnectSDK.h>
#import "DConnectMultipartParser.h"
#import "NSString+Extension.h"
#import "NSURLRequest+BodyAndBodyStreamInOne.h"

@interface DConnectMultipartParser (Private)

int header_field(multipart_parser* p, const char *at, size_t length);
int header_value(multipart_parser* p, const char *at, size_t length);
int part_data_begin(multipart_parser* p);
int part_data(multipart_parser* p, const char *at, size_t length);
int part_data_end(multipart_parser* p);
//int body_end(multipart_parser* p);

@end

@interface NSURLRequest (DConnect)

/**
 * Multipart Content-Typeのboundaryパラメータ値を取得する。
 * @return Multipart Content-Typeのboundaryパラメータ
 * @throw boundaryパラメータ値が取得できない
 */
- (NSString *)boundary;

@end

#pragma mark - Public Method -

@implementation DConnectMultipartParser {
    /// マルチパートパーサ本体
    multipart_parser* parser;
    /// マルチパートパーサ本体の設定（コールバック設定）
    multipart_parser_settings callbacks;
    
    NSMutableData *bodyBuf;
}

+ (instancetype)multipartParserWithURL:(NSURLRequest *)url
                              userData:(id)userData {
    DConnectMultipartParser *multi = [DConnectMultipartParser new];
    
    multi.url = url;
    multi.userData = userData;
    
    // 各解析ステージ毎に呼び出されるコールバックの設定
    memset(&multi->callbacks, 0, sizeof(multipart_parser_settings));
    multi->callbacks.on_header_field = header_field;
    multi->callbacks.on_header_value = header_value;
    multi->callbacks.on_part_data_begin = part_data_begin;
    multi->callbacks.on_part_data = part_data;
    multi->callbacks.on_part_data_end = part_data_end;
    //    multi->callbacks.on_body_end = body_end;
    
    multi->parser = multipart_parser_init([[url boundary] UTF8String], &multi->callbacks);
    multipart_parser_set_data(multi->parser, (__bridge void *)(userData));
    
    return multi;
}

- (void)parse {
    NSData *bodyData = [_url body];
    if (bodyData) {
        multipart_parser_execute(self->parser, [bodyData bytes], [bodyData length]);
    } else {
        multipart_parser_execute(self->parser, NULL, 0);
    }
}

- (void)dealloc
{
    multipart_parser_free(self->parser);
}

@end

@implementation UserData

+ (instancetype)userDataWithRequest:(DConnectMessage *)request {
    UserData *userData = [UserData new];
    userData.request = request;
    return userData;
}

@end

#pragma mark - Private Methods -

@implementation DConnectMultipartParser (Private)

int header_field(multipart_parser* p, const char *at, size_t length) {
    // ユーザデータを取得
    UserData *userData =
    (__bridge UserData *)multipart_parser_get_data(p);
    
    NSString *str = [NSString stringWithUTF8StringAddingNullTermination:at length:length];
    
    userData.isContentDisposition =
    [str caseInsensitiveCompare:@"Content-Disposition"] == NSOrderedSame;
    
    return 0;
}

int header_value(multipart_parser* p, const char *at, size_t length) {
    // ユーザデータを取得
    UserData *userData =
    (__bridge UserData *)multipart_parser_get_data(p);
    
    if (userData.isContentDisposition) {
        NSString *str = [NSString stringWithUTF8StringAddingNullTermination:at length:length];
        
        for (NSString *paramEntry in [str componentsSeparatedByString:@";"]) {
            NSArray *keyVal = [paramEntry componentsSeparatedByString:@"="];
            // キーと値のペアになっている。
            if (keyVal.count == 2) {
                NSString *key =
                [[keyVal objectAtIndex:0] stringByTrimmingCharactersInSet:
                 [NSCharacterSet whitespaceCharacterSet]];
                NSMutableCharacterSet *charSet = [NSMutableCharacterSet whitespaceCharacterSet];
                [charSet formUnionWithCharacterSet:
                 [NSMutableCharacterSet characterSetWithCharactersInString:@"\"'"]];
                NSString *val =
                [[keyVal objectAtIndex:1] stringByTrimmingCharactersInSet:charSet];
                if ([key isEqualToString:@"name"]) {
                    userData.name = val;
                } else if ([key isEqualToString:@"filename"]) {
                    userData.filename = val;
                }
            }
        }
    }
    return 0;
}

int part_data_begin(multipart_parser* p)
{
    // ユーザデータを取得
    UserData *userData =
    (__bridge UserData *)multipart_parser_get_data(p);
    
    // ボディ用のバッファを用意。
    userData.bodyBuf = [NSMutableData data];
    
    return 0;
}

int part_data(multipart_parser* p, const char *at, size_t length)
{
    // ユーザデータを取得
    UserData *userData =
    (__bridge UserData *)multipart_parser_get_data(p);
    
    // 新たな追加分バイトをバッファに追加。
    [userData.bodyBuf appendBytes:at length:length];
    
    return 0;
}

int part_data_end(multipart_parser* p)
{
    // ユーザデータを取得
    UserData *userData =
    (__bridge UserData *)multipart_parser_get_data(p);
    
    if (!userData.name) {
        return 0;
    }
    
    if (userData.filename) {
        // ファイルデータ（バイナリ）
        [userData.request setData:userData.bodyBuf forKey:userData.name];
    } else {
        // ファイルデータ以外（文字列）
        NSString *val =
        [[NSString alloc] initWithData:userData.bodyBuf encoding:NSUTF8StringEncoding];
        [userData.request setString:val forKey:userData.name];
    }
    userData.name = nil;
    userData.filename = nil;
    userData.bodyBuf = nil;
    
    return 0;
}

//int body_end(multipart_parser* p) {
//    // ユーザデータを取得
//    UserData *userData =
//    (__bridge UserData *)multipart_parser_get_data(p);
//
//    return 0;
//}

@end

@implementation NSURLRequest (DConnect)

- (NSString *)boundary {
    //    NSDictionary *headers = [self allHTTPHeaderFields];
    NSString *contentType = [self valueForHTTPHeaderField:@"content-type"];
    
    // Multipart Content-Typeのboundaryパラメータをキャプチャする準備
    // 参照： http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
    NSString *bcharsnospaceRegex = @"[\\d\\w'\\(\\)\\+_,-\\./:=\\?]";
    NSMutableString *bcharsRegex = @"[".mutableCopy;
    [bcharsRegex appendString:bcharsnospaceRegex];
    [bcharsRegex appendString:@"| ]"];
    NSMutableString *boundaryRegex = bcharsRegex.mutableCopy;
    [boundaryRegex appendString:@"{0,69}"];
    [boundaryRegex appendString:bcharsnospaceRegex];
    // パラメータboundaryの値を正規表現でキャプチャ：ダブルクオートされていようがいまいが。
    NSMutableString *boundaryParamRegex = @"boundary=((?:\"".mutableCopy;
    [boundaryParamRegex appendString:boundaryRegex];
    [boundaryParamRegex appendString:@"\")|(?:"];
    [boundaryParamRegex appendString:boundaryRegex];
    [boundaryParamRegex appendString:@"))"];
    
    NSRegularExpression *regex =
    [NSRegularExpression regularExpressionWithPattern:boundaryParamRegex
                                              options:0
                                                error:nil];
    NSTextCheckingResult *result =
    [regex firstMatchInString:contentType
                      options:NSMatchingReportProgress
                        range:NSMakeRange(0, contentType.length)];
    
    // 正規表現全体一致+boundaryパラメータ値キャプチャ用括弧の一致、計2つの一致があるはず
    if (result.numberOfRanges < 2) {
        @throw @"valid boundary parameter was not found in Content-Type \"multipart/*\"!";
    }
    
    return [contentType substringWithRange:[result rangeAtIndex:1]];
}

@end
