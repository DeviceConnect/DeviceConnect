//
//  RESTfulNormalProximityProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalProximityProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalProximityProfileTest
 * @brief Proximityプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalProximityProfileTest

/*!
 * @brief 近接センサーによる物の検知のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /proximity/ondeviceproximity?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalProximityOnDeviceProximityPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/proximity/ondeviceproximity?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"proximity\":{\"min\":0,\"max\":0,\"value\":0,\"threshold\":0}}");
}

/*!
 * @brief 近接センサーによる物の検知のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /proximity/ondeviceproximity?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalProximityOnDeviceProximityDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/proximity/ondeviceproximity?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 近接センサーによる人の検知のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /proximity/onuserproximity?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalProximityOnUserProximityPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/proximity/onuserproximity?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"proximity\":{\"near\":true}}");
}

/*!
 * @brief 近接センサーによる人の検知のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /proximity/onuserproximity?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalProximityOnUserProximityDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/proximity/onuserproximity?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

@end
