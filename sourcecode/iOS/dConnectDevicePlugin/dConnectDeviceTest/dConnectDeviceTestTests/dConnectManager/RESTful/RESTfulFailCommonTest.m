//
//  RESTfulFailCommonTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

#define DCONNECT_RESPONSE_TIMEOUT_SEC 60

@interface RESTfulFailCommonTest : RESTfulTestCase

@end

@implementation RESTfulFailCommonTest

/**
 * GETメソッドでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfileGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":2}", request);
}

/**
 * POSTメソッドでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfilePost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":2}", request);
}

/**
 * POSTメソッドでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfilePut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":2}", request);
}

/**
 * DELETEメソッドでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfileDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":2}", request);
}

/**
 * 余分なパスでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testTooManyPathComponents
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/profile/interface/attribute/extra"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 空のプロファイルでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: //ping/ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyProfile
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi//ping/ping?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 空のインターフェースでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping//ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyInterface
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping//ping?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 空の属性でリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping//ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyAttribute
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/ping/ping/?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"path\":\"/ping/ping\"}", request);
}

/**
 * 空のプロファイル、空のインターフェースでリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping//ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyProfileEmptyInterface
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi///ping?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 空のプロファイル、空の属性でリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /ping//ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpNormalEmptyProfileEmptyAttribute
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi//ping/?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 空のプロファイル、空の属性でリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfileNoApiNoProfileNoInterfaceNoAttribute
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 不正なAPI名のGETリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 ＊API名: not_gotapi
 * Path: /
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfileInvalidApiNoProfileNoInterfaceNoAttribute
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/not_gotapi"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 不正なapi名のGETリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * API名: not_gotapi
 * Path: /ping
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testNoProfileInvalidApiValidPath
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/not_gotapi/ping/?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    XCTAssertNil(error);
    XCTAssertNotNil(response);
    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertEqual(404, [httpResponse statusCode]);
}

/**
 * 存在しないファイルのGETリクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /files
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・HTTP200が返ること.
 * </pre>
 */
- (void) testHttpFailFileNotFound
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/files?uri=file:///path/to/file_not_found"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSURLResponse *response = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                                     error:nil];

    NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
    XCTAssertNotNil(response);
    XCTAssertEqual(404, httpResponse.statusCode);
}

//- (void) testHttpNormalSyncResponseTimout
//{
//    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/timeout/sync?deviceId=%@", self.deviceId]];
//    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
//    [request setHTTPMethod:@"GET"];
//    [request setTimeoutInterval:DCONNECT_RESPONSE_TIMEOUT_SEC * 2];
//    
//    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":7}", request);
//}
//
//- (void) testHttpNormalAyncResponseTimout
//{
//    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/timeout/async?deviceId=%@", self.deviceId]];
//    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
//    [request setHTTPMethod:@"GET"];
//    [request setTimeoutInterval:DCONNECT_RESPONSE_TIMEOUT_SEC * 2];
//    
//    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":7}", request);
//}

@end
