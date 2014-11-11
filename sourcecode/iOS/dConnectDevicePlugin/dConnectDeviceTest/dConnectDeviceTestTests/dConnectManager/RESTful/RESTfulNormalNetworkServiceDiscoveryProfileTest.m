//
//  RESTfulNormalNetworkServiceDiscoveryProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/19.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalNetworkServiceDiscoveryProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalNetworkServiceDiscoveryProfileTest
 * @brief Network Service Discoveryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalNetworkServiceDiscoveryProfileTest

/*!
 * @brief デバイス一覧取得リクエストを送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・servicesに少なくとも1つ以上のサービスが発見されること。
 * ・servicesの中に「Test Success Device」のnameを持ったサービスが存在すること。
 * </pre>
 */
- (void) testHttpNormalNetworkServiceDiscoveryGetNetworkServicesGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices"];
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
    XCTAssertNotNil(actualResponse);
    
    NSArray *services = [actualResponse objectForKey:DConnectNetworkServiceDiscoveryProfileParamServices];
    XCTAssertTrue(services.count > 0);
    BOOL found = NO;
    for (NSDictionary *service in services) {
        NSString *deviceName = [service objectForKey:DConnectNetworkServiceDiscoveryProfileParamName];
        if ([deviceName isEqualToString:@"Test Success Device"]) {
            found = YES;
            break;
        }
    }
    XCTAssertTrue(found == YES);
}

/*!
 * @brief デバイス検知イベントのテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET and DELETE
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・Test Success Device」のnameを持ったサービスの通知をうけること。
 * </pre>
 */
- (void) testHttpNormalNetworkServiceDiscoveryOnServiceChangeEvent
{
    // イベント登録
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/network_service_discovery/onservicechange?sessionKey=%@", self.clientId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
    // テスト用イベント送信要求
    uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/event?deviceId=%@&sessionKey=%@", self.deviceId, self.clientId]];
    request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
    // 受信したイベントのチェック
    CHECK_EVENT(@"{\"profile\":\"network_service_discovery\",\"attribute\":\"onservicechange\",\"sessionKey\":\"test_client\",\"networkService\":{\"id\":\"test_device_id.DeviceTestPlugin.dconnect\",\"name\":\"Test Success Device\",\"online\":true,\"state\":true,\"type\":\"TEST\",\"config\":\"test config\"}}");
    
    // イベント登録解除
    uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/network_service_discovery/onservicechange?sessionKey=%@", self.clientId]];
    request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

@end
