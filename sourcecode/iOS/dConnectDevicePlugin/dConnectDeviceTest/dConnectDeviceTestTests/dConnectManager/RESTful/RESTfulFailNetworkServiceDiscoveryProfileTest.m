//
//  RESTfulFailNetworkServiceDiscoveryProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/20.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulFailNetworkServiceDiscoveryProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailNetworkServiceDiscoveryProfileTest
 * @brief Network Service Discovery プロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulFailNetworkServiceDiscoveryProfileTest

/*!
 * @brief POSTメソッドでgetnetworkservicesでデバイスの探索を行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNetworkServiceDiscoveryGetNetworkServicesGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief PUTメソッドでgetnetworkservicesでデバイスの探索を行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNetworkServiceDiscoveryGetNetworkServicesGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief DELETEメソッドでgetnetworkservicesでデバイスの探索を行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNetworkServiceDiscoveryGetNetworkServicesGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

@end
