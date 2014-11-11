//
//  RESTfulNormalCommonTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalCommonTest : RESTfulTestCase

@end

@implementation RESTfulNormalCommonTest

/**
 * OPTIONSメソッドでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: OPTIONS
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalMethodOptions
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"OPTIONS"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                                         returningResponse:&response
                                                     error:&error];
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(200, httpResponse.statusCode);
    NSDictionary * headers = httpResponse.allHeaderFields;
    NSString *allowedMethods = [headers objectForKey:@"Access-Control-Allow-Methods"];
    XCTAssertEqual(@"POST, GET, PUT, DELETE", allowedMethods);
}

/**
 * attribute無しのリクエストの送信.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping//
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyInterfaceEmptyAttribute
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping//?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"path\":\"/ping\"}", request);
}

/**
 * リクエストパラメータに特殊文字を指定するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalRequestParametersWithURLEncodedReservedCharacters
{
    NSString *reservedChars = @":/?#[]@!$&'()*+,;=";
    NSString *reservedCharsEncoded = [reservedChars stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet alphanumericCharacterSet]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping?key1=%@&key2=%@&deviceId=%@&key3=%@&key4=%@", reservedCharsEncoded,  reservedCharsEncoded, self.deviceId, reservedCharsEncoded, reservedCharsEncoded]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    NSData *data = [NSURLConnection sendSynchronousRequest:request
                                         returningResponse:&response
                                                     error:&error];
    XCTAssertNotNil(data);
    XCTAssertNil(error);
    NSDictionary *actualResponse = [NSJSONSerialization JSONObjectWithData:data
                                                                   options:NSJSONReadingMutableContainers
                                                                     error:nil];
    XCTAssertTrue([reservedChars isEqualToString:[actualResponse objectForKey:@"key1"]]);
    XCTAssertTrue([reservedChars isEqualToString:[actualResponse objectForKey:@"key2"]]);
    XCTAssertTrue([reservedChars isEqualToString:[actualResponse objectForKey:@"key3"]]);
    XCTAssertTrue([reservedChars isEqualToString:[actualResponse objectForKey:@"key4"]]);
}

/**
 * POSTメソッドでマルチパートを送信できることのテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ること.
 * </pre>
 */
- (void) testHttpNormalPostRequestParametersWithMultipart
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping"]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    Multipart* multi = [Multipart new];
    [multi addData:[self.deviceId dataUsingEncoding:NSUTF8StringEncoding] forKey:@"deviceId"];
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];

    CHECK_RESPONSE(@"{\"result\":0, \"path\":\"/ping\"}", request);
}

/**
 * PUTメソッドでマルチパートを送信できることのテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ること.
 * </pre>
 */
- (void) testHttpNormalPutRequestParametersWithMultipart
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping"]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    Multipart* multi = [Multipart new];
    [multi addData:[self.deviceId dataUsingEncoding:NSUTF8StringEncoding] forKey:@"deviceId"];
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];

    CHECK_RESPONSE(@"{\"result\":0, \"path\":\"/ping\"}", request);
}

/**
 * POSTメソッドでマルチパート側のパラメータが優先されることのテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ること.
 * </pre>
 */
- (void) testHttpNormalPostRequestParametersWithBothMultipartAndParameterPart
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping?deviceId=invalid"]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    Multipart* multi = [Multipart new];
    [multi addData:[self.deviceId dataUsingEncoding:NSUTF8StringEncoding] forKey:@"deviceId"];
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];
    
    // マルチパートで指定したパラメータが優先されること
    CHECK_RESPONSE(@"{\"result\":0, \"path\":\"/ping\"}", request);
}

/**
 * PUTメソッドでマルチパート側のパラメータが優先されることのテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ること.
 * </pre>
 */
- (void) testHttpNormalPutRequestParametersWithBothMultipartAndParameterPart
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping?deviceId=invalid"]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    Multipart* multi = [Multipart new];
    [multi addData:[self.deviceId dataUsingEncoding:NSUTF8StringEncoding] forKey:@"deviceId"];
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];
    
    // マルチパートで指定したパラメータが優先されること
    CHECK_RESPONSE(@"{\"result\":0, \"path\":\"/ping\"}", request);
}

@end
