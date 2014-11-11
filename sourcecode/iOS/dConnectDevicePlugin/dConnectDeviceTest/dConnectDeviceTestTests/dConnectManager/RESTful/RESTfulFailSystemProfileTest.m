//
//  RESTfulFailSystemProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

@interface RESTfulFailSystemProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailSystemProfileTest
 * @brief Systemプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulFailSystemProfileTest

/*!
 * @brief POSTメソッドでシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /system
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief PUTメソッドでシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /system
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief DELETEメソッドでシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /system
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief deviceIdを指定せずにデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdに空文字を指定してデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdを指定してデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief POSTメソッドでデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /system/device?deviceId=123456789&deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/system/device?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief PUTメソッドでデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /system/device?deviceId=123456789&deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/system/device?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief DELETEメソッドでデバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /system/device?deviceId=123456789&deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/system/device?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief pluginId無しで/system/device/wakeupにアクセスする.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/wakeup
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutNoPluginId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief pluginId空文字で/system/device/wakeupにアクセスする.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/wakeup
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutEmptyPluginId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief 不正なpluginIdで/system/device/wakeupにアクセスする.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/wakeup
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutInvalidPluginId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief GETメソッドで設定画面表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/wakeup?pluginId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId=DeviceTestPlugin.dconnect"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief POSTメソッドで設定画面表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /system/device/wakeup?pluginId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId=DeviceTestPlugin.dconnect"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief DELETEメソッドで設定画面表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /system/device/wakeup?pluginId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemDeviceWakeupPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId=DeviceTestPlugin.dconnect"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief GETメソッドでイベント全消去要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/events?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemEventsDeleteInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/events?sessionKey=test"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief POSTメソッドでイベント全消去要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /system/device/events?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemEventsDeleteInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/events?sessionKey=test"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief PUTメソッドでイベント全消去要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /system/device/events?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemEventsDeleteInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/events?sessionKey=test"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

- (void) testHttpFailSystemKeywordPut
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/keyword"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief GETメソッドでキーワード表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device/keyword
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemKeywordInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/keyword"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief POSTメソッドでキーワード表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /system/device/keyword
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemKeywordInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/keyword"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief DELETEメソッドでキーワード表示要求を送信する.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /system/device/keyword
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailSystemKeywordInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/keyword"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

@end
