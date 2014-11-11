//
//  DConnectURLProtocol.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectURLProtocol.h"

#import "DConnectManager+Private.h"
#import "DConnectMessage+Private.h"
#import "DConnectFilesProfile.h"
#import "DConnectMultipartParser.h"
#import "DConnectFileManager.h"
#import "NSURLRequest+BodyAndBodyStreamInOne.h"
#import "DConnectURIBuilder.h"

/// 内部用タイプを定義する。
#define EXTRA_INNER_TYPE @"_type"

/// HTTPからの通信タイプを定義する。
#define EXTRA_TYPE_HTTP @"http"

/*!
 @define プロファイルがない場合のException名。
 */
#define HAVE_NO_API_EXCEPTION @"no-api-exception"

/*!
 @define プロファイルがない場合のException名。
 */
#define HAVE_NO_PROFILE_EXCEPTION @"no-profile-exception"

/*!
 @define 実装されていないActionの場合のException名。
 */
#define NOT_SUPPORT_ACTION_EXCEPTION @"no-action-exception"

/*!
 @define JSONのマイムタイプ。
 */
#define MIME_TYPE_JSON @"application/json; charset=UTF-8"



@implementation ResponseContext

@end



@interface DConnectURLProtocol ()

/*!
 HTTPレスポンス用のヘッダーを生成する。
 @param request HTTPリクエスト
 @param mimeType レスポンスのMIMEタイプ
 @param data レスポンスで返却するデータ
 @return ヘッダー
 */
+ (NSDictionary *)generateHeadersWithRequest:(NSURLRequest *)request
                                    mimeType:(NSString *)mimeType
                                        data:(NSData *)data;

+ (NSString *) percentEncodeString:(NSString *)string withEncoding:(NSStringEncoding)encoding;
+ (NSString *) stringByURLDecodingWithString:(NSString *)string;

/*!
 HTTPメソッド名からDevice Connect で定義されたメソッド名を取得する。
 @param httpMethod HTTPメソッド名
 @return d-Connectメソッド名
 */
int getDConnectMethod(NSString *httpMethod);

@end

@implementation DConnectURLProtocol

// Device Connect ServerのURLのホスト部分の実態
static NSString* host = @"localhost";

// Device Connect ServerのURLのポート部分の実態
static int port = 4035;

static NSString *scheme = @"http";

#pragma mark - NSURLProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest*)request
{
    DCLogD(@"URL: %@", request.URL.absoluteString);
    DCLogD(@"Method: %@", request.HTTPMethod);
    if ([request.URL.host isEqualToString:host] && ([request.URL.port intValue] == port)) {
        return YES;
    }
    return NO;
}

+ (NSURLRequest*)canonicalRequestForRequest:(NSURLRequest*)request
{
    return request;
}

- (void)startLoading
{
    DCLogD(@"URL: %@", self.request.URL);
    
    [NSFileHandle fileHandleForReadingFromURL:nil error:nil];
    
    __weak DConnectURLProtocol *weakSelf = self;
    [DConnectURLProtocol responseContextWithHTTPRequest:self.request
                                               callback:
     ^(ResponseContext* responseCtx) {
         
#ifdef DEBUG_LEVEL
#if DEBUG_LEVEL > 3
         if (responseCtx.data) {
             DCLogD(@"data: %@", [[NSString alloc] initWithData:responseCtx.data encoding:NSUTF8StringEncoding]);
         }
#endif
#endif
         if (responseCtx.response) {
             // レスポンスあり；成功。
             
             // レスポンスを返す。
             [[weakSelf client] URLProtocol:weakSelf didReceiveResponse:responseCtx.response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
             // レスポンスのデータを返す。
             [[weakSelf client] URLProtocol:weakSelf didLoadData:responseCtx.data];
         } else {
             // レスポンス無し；失敗
             
             // エラーを設定する
             // TODO: もっと色々な、詳細なエラー設定ができないだろうか？
             [[weakSelf client] URLProtocol:weakSelf didFailWithError:
              [NSError errorWithDomain:NSCocoaErrorDomain code:NSURLErrorUnknown userInfo:nil]];
         }
         // データのローディング終了を告げる。
         [[weakSelf client] URLProtocolDidFinishLoading:weakSelf];
     }];
}

- (void)stopLoading
{
    // 何もしない
}

#pragma mark - Public

+ (NSString *) host {
    return host;
}

+ (int) port {
    return port;
}

+ (NSString *) scheme {
    return scheme;
}

+ (void) setHost:(NSString *)h {
    host = h;
}

+ (void) setPort:(int)p {
    port = p;
}

+ (void) setScheme:(NSString *)s {
    scheme = s;
}

+ (DConnectRequestMessage *) requestMessageWithHTTPReqeust:(NSURLRequest *)request
{
    NSURL *url = [request URL];
    DConnectRequestMessage *requestMessage = [DConnectRequestMessage message];
    
    // HTTPリクエストのURLのパスセグメントを取得
    NSMutableCharacterSet *whiteAndSlash = [NSMutableCharacterSet whitespaceCharacterSet];
    [whiteAndSlash formUnionWithCharacterSet:
     [NSMutableCharacterSet characterSetWithCharactersInString:@"/"]];
    NSString *trimmedPath = [url.path stringByTrimmingCharactersInSet:whiteAndSlash];
    NSArray *pathComponentArr = [trimmedPath componentsSeparatedByString:@"/"];
    
    // パラメータ「key=val」をパースし、パラメータ用NSDicitonaryに格納する。
    [NSURLRequest addURLParametersFromString:url.query
                            toRequestMessage:requestMessage
                             percentDecoding:YES];
    
    // URLのパスセグメントの数から、プロファイル・属性・インターフェースが何なのかを判定する。
    NSString *api, *profile, *attr, *interface;
    api = profile = attr = interface = nil;
    
    if ([pathComponentArr count] == 1 &&
        [pathComponentArr[0] length] != 0)
    {
        api = [pathComponentArr objectAtIndex:0];
    } else if ([pathComponentArr count] == 2 &&
               [pathComponentArr[0] length] != 0 &&
               [pathComponentArr[1] length] != 0)
    {
        api = [pathComponentArr objectAtIndex:0];
        profile = [pathComponentArr objectAtIndex:1];
    } else if ([pathComponentArr count] == 3 &&
               [pathComponentArr[0] length] != 0 &&
               [pathComponentArr[1] length] != 0 &&
               [pathComponentArr[2] length] != 0)
    {
        api = [pathComponentArr objectAtIndex:0];
        profile = [pathComponentArr objectAtIndex:1];
        attr = [pathComponentArr objectAtIndex:2];
    } else if ([pathComponentArr count] == 4 &&
               [pathComponentArr[0] length] != 0 &&
               [pathComponentArr[1] length] != 0 &&
               [pathComponentArr[2] length] != 0 &&
               [pathComponentArr[3] length] != 0)
    {
        api = [pathComponentArr objectAtIndex:0];
        profile = [pathComponentArr objectAtIndex:1];
        interface = [pathComponentArr objectAtIndex:2];
        attr = [pathComponentArr objectAtIndex:3];
    }
    
    if (api == nil || ![api isEqualToString:DConnectMessageDefaultAPI]) {
        [NSException raise:HAVE_NO_API_EXCEPTION
                    format:@"No valid api was detected in URL."];
    }
    
    if (profile == nil) {
        [NSException raise:HAVE_NO_PROFILE_EXCEPTION
                    format:@"No valid profile was detected in URL."];
    }
    
    // リクエストメッセージにHTTPリクエストのメソッドに対応するアクション名を格納する
    int methodId = getDConnectMethod([request HTTPMethod]);
    if (methodId == -1) {
        [NSException raise:NOT_SUPPORT_ACTION_EXCEPTION
                    format:@"Unknown method"];
    }
    [requestMessage setAction:methodId];
    
    // リクエストメッセージにプロファイル・インターフェース・属性・パラメータ各種を突っ込む。
    requestMessage.api = api;
    requestMessage.profile = profile;
    
    if (interface) {
        requestMessage.interface = interface;
    }
    
    if (attr) {
        requestMessage.attribute = attr;
    }
    
    // パラメータがHTTPボディに記述されているなら、解析しリクエストメッセージに追加する。
    [request addParametersFromHTTPBodyToRequestMessage:requestMessage];
    
    return requestMessage;
}

+ (void) responseContextWithHTTPRequest:(NSURLRequest *)request
                               callback:(void(^)(ResponseContext* responseCtx))callback
{
    if ([[request HTTPMethod] isEqualToString:@"OPTIONS"]) {
        // CORSのプリフライトリクエストである「OPTIONS」へのリクエストを返す。
        
        ResponseContext *responseCtx = [ResponseContext new];
        
        NSMutableDictionary *headerDict =
        [DConnectURLProtocol generateHeadersWithRequest:request
                                               mimeType:@"text/plain"
                                                   data:nil].mutableCopy;
        [headerDict setValue:@"POST, GET, PUT, DELETE" forKey:@"Access-Control-Allow-Methods"];
        
        responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                           statusCode:200
                                                          HTTPVersion:@"HTTP/1.1"
                                                         headerFields:headerDict];
        responseCtx.data = nil;
        
        callback(responseCtx);
    }
    else {
        // dConnectのホスト&ポートへのリクエストであれば、d-ConnectのRESTful APIへのアクセス有り。
        //
        // [android]リクエストとレスポンスの1対1対応を取る為のユニークなリクエストIDを生成
        // [ios]リクエストとレスポンスの1対1対応を取る、レスポンス返却用コールバックの用意？
        
        @try {
            // HTTPリクエストを解析して、dconnectのリクエストに変換
            DConnectRequestMessage *requestMessage = [DConnectURLProtocol requestMessageWithHTTPReqeust:request];
            
            // [android] DConnectServiceのextraにおいて、リクエストIDを指定し、かつ内部用タイプをHTTPに指定し、
            // DConnectServiceを開始する。
            // [ios] NSDictionaryにおいて、内部用タイプをHTTPに指定し、DConnectManagerにリクエストと
            // レスポンスを渡す。
            [requestMessage setString:EXTRA_TYPE_HTTP forKey:EXTRA_INNER_TYPE];
            [[DConnectManager sharedManager] sendRequest:requestMessage
                                                  isHttp:YES
                                                callback:^(DConnectResponseMessage *responseMessage) {
                // ##############################################
                // dconnectのレスポンスをHTTPレスポンスに変換
                // ##############################################
                
                // [android] Map上のリクエストに対するレスポンスIntentのエントリを削除
                // [ios] 非同期での実装を行わず、同期的に関数の返り値・引数経由でのNSDictionary返却を受け付けていれば、
                // 特にリクエスト=レスポンス間の相互関連Mapを保持する必要なし。
                
                // HTTPレスポンスを作成
                [DConnectURLProtocol responseContextWithResponseMessage:responseMessage
                                                   precedingHTTPRequest:request
                                                precedingRequestMessage:requestMessage
                                                               callback:callback];
            }];
        }
        @catch (NSException *exception) {
            DCLogE(@"Exception:\n%@", [exception reason]);
            
            ResponseContext *responseCtx = [ResponseContext new];
            NSDictionary *headerDict = [DConnectURLProtocol generateHeadersWithRequest:request
                                                                              mimeType:MIME_TYPE_JSON
                                                                                  data:responseCtx.data];
            // 各exceptionに合わせてエラーメッセージを設定
            NSString *name = [exception name];
            if ([name isEqualToString:HAVE_NO_API_EXCEPTION]) {
                responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                                   statusCode:404
                                                                  HTTPVersion:@"HTTP/1.1"
                                                                 headerFields:headerDict];
                const char *rawData = [[exception reason] UTF8String];
                responseCtx.data = [NSData dataWithBytes:rawData length:strlen(rawData)];
            } else if ([name isEqualToString:HAVE_NO_PROFILE_EXCEPTION]) {
                responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                                   statusCode:200
                                                                  HTTPVersion:@"HTTP/1.1"
                                                                 headerFields:headerDict];
                const char *rawData = "{\"result\":1,\"errorCode\":2,\"errorMessage\":\"Non-supported Profile was accessed.\"}";
                responseCtx.data = [NSData dataWithBytes:rawData length:strlen(rawData)];
            } else if ([name isEqualToString:NOT_SUPPORT_ACTION_EXCEPTION]) {
                responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                                   statusCode:501
                                                                  HTTPVersion:@"HTTP/1.1"
                                                                 headerFields:headerDict];
                responseCtx.data = nil;
            } else {
                responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                                   statusCode:200
                                                                  HTTPVersion:@"HTTP/1.1"
                                                                 headerFields:headerDict];
                const char *rawData = "{\"result\":1,\"errorCode\":1,\"errorMessage\":\"Unknown error was encountered.\"}";
                responseCtx.data = [NSData dataWithBytes:rawData length:strlen(rawData)];
            }
            callback(responseCtx);
        }
    }
}

+ (void) responseContextWithResponseMessage:(DConnectResponseMessage *)responseMessage
                       precedingHTTPRequest:(NSURLRequest *)request
                    precedingRequestMessage:(DConnectRequestMessage *)requestMessage
                                   callback:(void(^)(ResponseContext* responseCtx))callback
{
    NSString *mimeType;
    ResponseContext *responseCtx = [ResponseContext new];
    BOOL processed = NO;
    NSInteger statusCode = 200;
    
    if ([requestMessage.profile isEqualToString:DConnectFilesProfileName]) {
        // 特殊処理：DConnectResponseMessageからHTTPレスポンス/ファイルデータ（任意MIMEタイプ）を生成する
        // HTTPレスポンスのボディ（任意コンテンツ）を用意

        switch ([responseMessage result]) {
            case DConnectMessageResultTypeOk:
                responseCtx.data = [responseMessage dataForKey:DConnectFilesProfileParamData];
                mimeType = [responseMessage stringForKey:DConnectFilesProfileParamMimeType];
                processed = YES;
                break;
            case DConnectMessageResultTypeError:
                // エラーのJSONを返す；HTTPステータスコードを404（Not Found）に変えておく。
                statusCode = 404;
                break;
                
            default:
                break;
        }
    }
    if (!processed) {
        // URIを変換
        [self convertUri:responseMessage];
        
        // JSONに変換
        NSString *json = [responseMessage convertToJSONString];
        if (!json) {
            // レスポンスメッセージからのJSON生成失敗；エラー用データを用意する。
            // 原因不明エラーで、メッセージにJSON生成失敗の旨を記す。
            NSString *dataStr =
            [NSString stringWithFormat:@"{\"%@\":%lu,\"%@\":%lu,\"%@\":\"Failed to generate a JSON body.\"}",
             DConnectMessageResult, (unsigned long)DConnectMessageResultTypeError,
             DConnectMessageErrorCode, (unsigned long)DConnectMessageErrorCodeUnknown,
             DConnectMessageErrorMessage];
            const char *rawData = dataStr.UTF8String;
            responseCtx.data = [NSData dataWithBytes:rawData length:strlen(rawData)];
        } else {
            responseCtx.data = [json dataUsingEncoding:NSUTF8StringEncoding];
        }
        
        mimeType = MIME_TYPE_JSON;
    }
    
    NSDictionary *headerDict = [DConnectURLProtocol generateHeadersWithRequest:request
                                                                      mimeType:mimeType
                                                                          data:responseCtx.data];
    responseCtx.response = [[NSHTTPURLResponse alloc] initWithURL:[request URL]
                                                       statusCode:statusCode
                                                      HTTPVersion:@"HTTP/1.1"
                                                     headerFields:headerDict];
    
    callback(responseCtx);
}

+ (void) convertUri:(DConnectMessage *) response
{
    NSArray *keys = [response allKeys];
    for (NSString *key in keys) {
        NSObject *obj = [response objectForKey:key];
        if ([key isEqualToString:@"uri"]) {
            NSString *uri = (NSString *)obj;
            
            // http, httpsで指定されているURLは直接アクセスできるのでFilesAPIを利用しない
            NSString *pattern = @"^https?://.+";
            NSRegularExpression *re = [NSRegularExpression regularExpressionWithPattern:pattern options:0 error:nil];
            NSTextCheckingResult *result = [re firstMatchInString:uri options:0 range:NSMakeRange(0, uri.length)];
            
            if (!result || result.numberOfRanges < 1) {
                // http, https以外の場合はuriパラメータ値をdConnectManager Files API向けURLに置き換える。
                DConnectURIBuilder *builder = [DConnectURIBuilder new];
                [builder setProfile:DConnectFilesProfileName];
                [builder addParameter:uri forName:DConnectFilesProfileParamUri];
                [response setString:[[builder build] absoluteString] forKey:@"uri"];
            }
        } else if ([obj isKindOfClass:[DConnectMessage class]]) {
            [self convertUri:(DConnectMessage *)obj];
        } else if ([obj isKindOfClass:[DConnectArray class]]) {
            DConnectArray *arr = (DConnectArray *) obj;
            for (int i = 0; i < arr.count; i++) {
                NSObject *a = [arr objectAtIndex:i];
                if ([a isKindOfClass:[DConnectMessage class]]) {
                    [self convertUri:(DConnectMessage *) a];
                }
            }
        }
    }
}

#pragma mark - Private

+ (NSDictionary *)generateHeadersWithRequest:(NSURLRequest *)request
                                    mimeType:(NSString *)mimeType
                                        data:(NSData *)data
{
    NSMutableString *allowHeaders = @"XMLHttpRequest".mutableCopy;
    NSString *requestHeaders = [request valueForHTTPHeaderField:@"Access-Control-Request-Headers"];
    if (requestHeaders) {
        [allowHeaders appendString:[NSString stringWithFormat:@", %@", requestHeaders]];
    }
    
    return @{@"Content-Type" : mimeType,
             @"Content-Length" : data ?
             [NSString stringWithFormat:@"%lu", (unsigned long)[data length]] : @"0",
             @"Date": [[NSDate date] descriptionWithLocale:nil],
             @"Access-Control-Allow-Origin" : @"*",
             @"Access-Control-Allow-Headers" : allowHeaders,
             @"Connection": @"close",
             // TODO: dConnectのバージョンを付与する？.plistの方からバンドルバージョンを持ってくる感じ？
             @"Server" : @"dConnectServer",
             // TODO: [2014/05/26 福井] もっとマシなLast-Modifiedを。現行バージョン配布日にするとか？.plistの方からバンドルバージョンを持ってくる感じ？ビルドしたときの時間とか？__BUILD_***とかのマクロでビルド時間とれる？
             @"Last-Modified" : @"Fri, 26 May 2014 00:00:00 +0900",
             @"Cache-Control" : @"private, max-age=0, no-cache"
             };
}

+ (NSString *) percentEncodeString:(NSString *)string withEncoding:(NSStringEncoding)encoding
{
    NSCharacterSet *allowedCharSet = [[NSCharacterSet characterSetWithCharactersInString:@"%;/?:@&=$+{}<>., "] invertedSet];
    return [string stringByAddingPercentEncodingWithAllowedCharacters:allowedCharSet];
}

+ (NSString *) stringByURLDecodingWithString:(NSString *)string {
    // application/x-www-form-urlencoded (formから)の場合半角スペースは"+"に変換されるためデコード前に"+"を
    // 半角スペースに変換しておく。( http://ja.wikipedia.org/wiki/%E3%83%91%E3%83%BC%E3%82%BB%E3%83%B3%E3%83%88%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%87%E3%82%A3%E3%83%B3%E3%82%B0 )
    // XMLHttpRequestからの場合はパーセントエンコーディング(%20に変換)される。
    string = [string stringByReplacingOccurrencesOfString:@"+" withString:@" "];
    string = [string stringByRemovingPercentEncoding];
    
    return string;
}

int getDConnectMethod(NSString *httpMethod) {
    if ([httpMethod isEqualToString:@"GET"]) {
        return DConnectMessageActionTypeGet;
    } else if ([httpMethod isEqualToString:@"POST"]) {
        return DConnectMessageActionTypePost;
    } else if ([httpMethod isEqualToString:@"PUT"]) {
        return DConnectMessageActionTypePut;
    } else if ([httpMethod isEqualToString:@"DELETE"]) {
        return DConnectMessageActionTypeDelete;
    }
    return -1;
}

@end

@implementation NSURLRequest (DConnect)

- (void)addParametersFromMultipartToRequestMessage:(DConnectMessage *)requestMessage {
    UserData *userData = [UserData userDataWithRequest:requestMessage];
    DConnectMultipartParser *multiParser =
    [DConnectMultipartParser multipartParserWithURL:self
                                   userData:userData];
    
    [multiParser parse];
}

- (void)addParametersFromHTTPBodyToRequestMessage:(DConnectRequestMessage *)requestMessage
{
    NSString *contentType = [self valueForHTTPHeaderField:@"content-type"];
    if (contentType &&
        [contentType rangeOfString:@"multipart/form-data"
                           options:NSCaseInsensitiveSearch].location != NSNotFound)
    {
        // MIME Multipartかどうかの判定を行い、MultipartならMultipart解析する。
        // ファイルアップロード用HTMLフォームから送られてくるMultipartなリクエストを解析できる様な感じで
        // やってる。あと、ボディの中には多分mediaIdとdeviceIdだけしか入らない？
        //
        // Content-Dispositionヘッダは「name」や「filename」といったパラメータを用いるので留意。
        // http://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2
        
        [self addParametersFromMultipartToRequestMessage:requestMessage];
    } else if (self.body && self.body.length > 0) {
        // Content-Typeが"applicaiton/x-www-form-urlencoded"の場合、%エスケープをデコードする必要あり。
        BOOL doDecode = [contentType isEqualToString:@"application/x-www-form-urlencoded"];
        
        // Multipartでなければ、ボディ内にKey-Value形式でパラメータが記述されているかもしれないので、
        // それの解析。
        [NSURLRequest addURLParametersFromString:[[NSString alloc] initWithData:[self body] encoding:NSUTF8StringEncoding]
                                toRequestMessage:requestMessage percentDecoding:doDecode];
    }
}

/// パラメータ「key=val」or「key」をパースし、パラメータ用NSDicitonaryに格納する。
+ (void)addURLParametersFromString:(NSString *)urlParameterStr
                  toRequestMessage:(DConnectRequestMessage *)requestMessage
                   percentDecoding:(BOOL)doDecode
{
    if (!urlParameterStr) {
        return;
    }
    NSArray *paramArr = [urlParameterStr componentsSeparatedByString:@"&"];
    [paramArr enumerateObjectsWithOptions:NSEnumerationConcurrent
                               usingBlock:^(id obj, NSUInteger idx, BOOL *stop)
     {
         NSArray *keyValArr = [(NSString *)obj componentsSeparatedByString:@"="];
         NSString *key;
         NSString *val;
         
#ifdef DEBUG_LEVEL
#if DEBUG_LEVEL > 3
         // valが無くkeyのみのパラメータ
         if ([keyValArr count] == 1) {
             key = doDecode ? [DConnectURLProtocol stringByURLDecodingWithString:(NSString *)keyValArr[0]] : keyValArr[0];
             DCLogD(@"Key-only URL query parameter \"%@\" will be ignored.", key);
         }
#endif
#endif
         // key&valのパラメータ
         if ([keyValArr count] == 2) {
             
             if (doDecode) {
                 key = [DConnectURLProtocol stringByURLDecodingWithString:(NSString *)keyValArr[0]];
                 val = [DConnectURLProtocol stringByURLDecodingWithString:(NSString *)keyValArr[1]];
             } else {
                 key = keyValArr[0];
                 val = keyValArr[1];
             }
             
             if (key && val) {
                 @synchronized (requestMessage) {
                     [requestMessage setString:val forKey:key];
                 }
             }
         }
     }];
}

@end
