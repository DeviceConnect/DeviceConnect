//
//  RESTfulFailNotificationProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/22.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulFailNotificationProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailNotificationProfileTest
 * @brief Notificationプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulFailNotificationProfileTest

/*!
 * @brief deviceIdを指定せずに通知を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/notify?type=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyPostNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify?notificationId=1"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で通知を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/notify?deviceId=&type=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyPostEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify?notificationId=1&deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで通知を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/notify?deviceId=123456789&type=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyPostInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify?notificationId=1&deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに通知を削除するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/notify
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で通知を削除するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/notify?deviceId=&notificationId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで通知を削除するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/notify?deviceId=123456789&notificationId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/notify?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して通知を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /notification/notify?deviceId=xxxx&type=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyDeleteInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/notify?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPUTを指定して通知を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/notify?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationNotifyDeleteInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/notify?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonclick属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonclick属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonclick属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdが無い状態でonclick属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickPutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが無い状態でonclick属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?sessionKey=&deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickPutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonclick属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclick?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonclick属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclick?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonclick属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclick?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclick?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonclick属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclick?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonclick属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclick?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonclick属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /notification/onclick?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonclick属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/onclick?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClickInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowPutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowPutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonshow属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onshow?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonshow属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onshow?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonshow属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onshow?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onshow?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonshow属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonshow属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonshow属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonshow属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnShowInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonclose属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClosePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonclose属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClosePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonclose属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClosePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonclose属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClosePutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonclose属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?deviceId=&deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnClosePutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonclose属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonclose属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonclose属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onclose?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonclose属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonclose属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonclose属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonclose属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnCloseInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonerror属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonerror属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonerror属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonerror属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorPutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonerror属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorPutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonerror属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonerror属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonerror属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/notification/onerror?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyが無い状態でonerror属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonerror属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonerror属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonerror属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailNotificationOnErrorInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

@end
