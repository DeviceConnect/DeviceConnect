//
//  RESTfulFailPhoneProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/22.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulFailPhoneProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailPhoneProfileTest
 * @brief Phoneプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulFailPhoneProfileTest

/*!
 * @brief deviceIdを指定せずに通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /phone/call?phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallPostNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/call?phoneNumber=090xxxxxxxx"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /phone/call?deviceId=&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallPostEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/call?phoneNumber=090xxxxxxxx&deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /phone/call?deviceId=123456789&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallPostInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/call?phoneNumber=090xxxxxxxx&deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /phone/call?deviceId=xxxx&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/call?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPUTを指定して通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/call?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/call?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して通話発信要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/call?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneCallInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/call?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/call?phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/set?mode=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/call?deviceId=&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/set?deviceId=&mode=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/call?deviceId=123456789&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/set?deviceId=12345678&mode=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /phone/call?deviceId=xxxx&phoneNumber=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/set?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定して電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /phone/call?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/set?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して電話設定要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/call?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneSetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/set?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonconnect属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/onconnect?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonconnect属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/onconnect?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonconnect属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/onconnect?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonconnect属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/onconnect?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectPutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonconnect属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /phone/onconnect?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectPutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonconnect属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/onconnect?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonconnect属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/onconnect?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonconnect属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/onconnect?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonconnect属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/onconnect?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonconnect属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /phone/onconnect?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonconnect属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /phone/onconnect?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonconnect属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /phone/onconnect?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailPhoneOnConnectInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/phone/onconnect?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

@end
