//
//  RESTfulNormalAuthorizationProfileTest.m
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/09/02.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalAuthorizationProfileTest : RESTfulTestCase

@end

/*!
 @class RESTfulNormalAuthorizationProfileTest
 @brief RESTful Authorizationプロファイルの正常系テスト。
 */
@implementation RESTfulNormalAuthorizationProfileTest

/*!
 * @brief
 * クライアント作成テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/create_client?packageName=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・clientIdにstring型の値が返ること。
 * ・clientSecretにstring型の値が返ること。
 * </pre>
 */
- (void) testHttpNormalCreateClient
{
    NSArray *client = [self createClientForPackage:@"abc"];
    XCTAssertNotNil(client[0], @"clientId must not be nil.");
    XCTAssertNotNil(client[1], @"clientSecret must not be nil.");
}

/*!
 * @brief
 * クライアント作成済みのパッケージについてクライアントを作成し直すテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /authorization/create_client?packageName=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・異なるclientIdが返ること。
 * ・異なるclientSecretが返ること。
 * </pre>
 */
- (void) testHttpNormalCreateClientOverwrite
{
    NSString *package = @"abc";
    NSArray *client = [self createClientForPackage:package];
    XCTAssertNotNil(client[0], @"clientId must not be nil.");
    XCTAssertNotNil(client[1], @"clientSecret must not be nil.");
    NSArray *newClient = [self createClientForPackage:package];
    XCTAssertNotNil(newClient[0], @"clientId must not be nil.");
    XCTAssertNotNil(newClient[1], @"clientSecret must not be nil.");
    XCTAssertNotEqual(client[0], newClient[0]);
    XCTAssertNotEqual(client[1], newClient[1]);
}

// MEMO: 以下のテストは手動で行う.
//- (void) testHttpRequestAccessToken
//{
//    NSString *package = @"abc";
//    NSArray *client = [self createClientForPackage:package];
//    XCTAssertNotNil(client[0], @"clientId must not be nil.");
//    XCTAssertNotNil(client[1], @"clientSecret must not be nil.");
//    
//    AccessToken *accessToken = [self requestAccessTokenWithClientId:client[0]
//                                                       clientSecret:client[1]
//                                                             scopes:[NSArray arrayWithObjects:@"battery", nil]
//                                                    applicationName:@"dConnectManagerTest"];
//    XCTAssertNotNil(accessToken, @"accessToken must not be nil.");
//    XCTAssertNotNil(accessToken.token, @"accessToken.token must not be nil.");
//    XCTAssertNotNil(accessToken.expirePeriods, @"accessToken.expirePeriods must not be nil.");
//    XCTAssertNotNil(accessToken.signature, @"accessToken.signatue must not be nil.");
//}

// MEMO: 以下のテストは手動で行う.
//- (void) testHttpRequestAccessTokenMultiScope
//{
//    NSString *package = @"abc";
//    NSArray *client = [self createClientForPackage:package];
//    XCTAssertNotNil(client[0], @"clientId must not be nil.");
//    XCTAssertNotNil(client[1], @"clientSecret must not be nil.");
//    
//    AccessToken *accessToken = [self requestAccessTokenWithClientId:client[0]
//                                                       clientSecret:client[1]
//                                                             scopes:[NSArray arrayWithObjects:@"battery", @"connect", @"deviceorientation", nil]
//                                                    applicationName:@"dConnectManagerTest"];
//    XCTAssertNotNil(accessToken, @"accessToken must not be nil.");
//    XCTAssertNotNil(accessToken.token, @"accessToken.token must not be nil.");
//    XCTAssertNotNil(accessToken.expirePeriods, @"accessToken.expirePeriods must not be nil.");
//    XCTAssertNotNil(accessToken.signature, @"accessToken.signatue must not be nil.");
//}

@end
