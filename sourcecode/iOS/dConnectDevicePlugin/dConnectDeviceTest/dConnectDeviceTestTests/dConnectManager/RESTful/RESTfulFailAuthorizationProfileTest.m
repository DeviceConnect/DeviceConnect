//
//  RESTfulFailAuthorizationProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

NSString *GRANT_TYPE = @"authorization_code";

@interface RESTfulFailAuthorizationProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailAuthorizationProfileTest
 * @brief Authorizationプロファイルの異常系テスト.
 */
@implementation RESTfulFailAuthorizationProfileTest

/*!
 * @brief
 * packageが無い状態でクライアント作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/create_client
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailCreateClientGetNoPackage
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/authorization/create_client"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * packageが空状態でクライアント作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/create_client?package=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailCreateClientGetEmptyPackage
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/authorization/create_client?package="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * メソッドにPOSTを指定してクライアント作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /authorization/create_client?package=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailCreateClientGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/authorization/create_client?package=abc"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief
 * メソッドにPUTを指定してクライアント作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /authorization/create_client?package=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailCreateClientGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/authorization/create_client?package=abc"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief
 * メソッドにDELETEを指定してクライアント作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /authorization/create_client?package=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailCreateClientGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/authorization/create_client?package=abc"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief
 * clientIdが無い状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetNoClientId
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?grantType=%@&scope=battery&applicationName=test&signature=%@", GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * clientIdに空文字を指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clintId=&grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetEmptyClientId
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=&grantType=%@&scope=battery&applicationName=test&signature=%@", GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * grantTypeを指定なしにした状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clintId=xxxx&grantType=&scope=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetNoGrantType
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&scope=battery&applicationName=test&signature=%@", client[0], signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * grantTypeに空文字を指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clintId=xxxx&grantType=&scope=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetEmptyGrantType
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=&scope=battery&applicationName=test&signature=%@", client[0], signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * 不正なgrantTypeを指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?grantType=xxxx&scope=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetInvalidGrantType
{
    NSString *const undefined_grant_type = @"undefined_grant_type";
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:undefined_grant_type
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];

    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=test&signature=%@", client[0], undefined_grant_type, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":11}", request);
}

/*!
 * @brief
 * scopeが無い状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetNoScope
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&applicationName=test&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * scopeに空文字を指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=authorization_code&scope=&applicationName=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetEmptyScope
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=&applicationName=test&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * applicationNameが無い状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&scope=xxxx&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetNoApplicationName
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * applicationに空文字を指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=authorization_code&scope=&applicationName=&signature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetEmptyApplicationName
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * signatureが無い状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetNoSignature
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@", client[0], GRANT_TYPE]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * signatureに空文字を指定した状態でアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=authorization_code&scope=xxxx&applicationName=xxxx&signature=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetEmptySignature
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=test&signature=", client[0], GRANT_TYPE]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief
 * メソッドにPOSTを指定してアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetInvalidMethodPost
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=test&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief
 * メソッドにPUTを指定してアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetInvalidMethodPut
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=test&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief
 * メソッドにDELETEを指定してアクセストークン作成を行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /authorization/request_accesstoken?
 *           clientId=xxxx&grantType=xxxx&scope=xxxx&applicationName=xxxxsignature=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailRequestAccessTokenGetInvalidMethodDelete
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client);
    XCTAssertNotNil(client[0]);
    XCTAssertNotNil(client[1]);
    NSString *signature = [DConnectUtil generateSignatureWithClientId:client[0]
                                                            grantType:GRANT_TYPE
                                                             deviceId:nil
                                                               scopes:[NSArray arrayWithObjects:@"battery", nil]
                                                         clientSecret:client[1]];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/authorization/request_accesstoken?clientId=%@&grantType=%@&scope=battery&applicationName=test&signature=%@", client[0], GRANT_TYPE, signature]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

@end
