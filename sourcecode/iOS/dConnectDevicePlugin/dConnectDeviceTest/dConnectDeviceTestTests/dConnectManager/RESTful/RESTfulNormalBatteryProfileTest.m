//
//  RESTfulNormalBatteryProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/18.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalBatteryProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalBatteryProfileTest
 * @brief Batteryプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalBatteryProfileTest

/*!
 * @brief バッテリー全属性取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /battery?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・chargingがfalseで返ってくること。
 * ・chargingtimeが50000で返ってくること。
 * ・dischargingtimeが10000で返ってくること。
 * ・levelが0.5で返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"level\":0.5,\"charging\":false,\"dischargingTime\":10000,\"chargingTime\":50000}", request);
}

/*!
 * @brief バッテリーcharging属性取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /battery/charging?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・chargingがfalseで返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryChargingGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/charging?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"charging\":false}", request);
}

/*!
 * @brief バッテリーchargingtime属性取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /battery/chargingtime?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・chargingtimeが50000で返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryChargingTimeGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/chargingTime?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"chargingTime\":50000}", request);
}

/*!
 * @brief バッテリーdischargingtime属性取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /battery/dischargingtime?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・chargingtimeが50000で返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryDischargingTimeGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/dischargingTime?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"dischargingTime\":10000}", request);
}

/*!
 * @brief バッテリーlevel属性取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /battery/level?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・levelが0.5で返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryLevelGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/level?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"level\":0.5}", request);
}

/*!
 * @brief バッテリーonchargingchangeを登録するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /battery/onchargingchange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryOnChargingChangePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/onchargingchange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"battery\":{\"charging\":false}}");
}

/*!
 * @brief バッテリーonchargingchangeを解除するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /battery/onchargingchange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryOnChargingChangeDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/onchargingchange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief onbatterychange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /battery/onbatterychange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryOnBatteryChangePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/onbatterychange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"battery\":{\"level\":0.5,\"dischargingTime\":10000,\"chargingTime\":50000}}");
}

/*!
 * @brief onbatterychange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /battery/onbatterychange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalBatteryOnBatteryChangeDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/battery/onbatterychange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

@end
