//
//  RESTfulNormalNotificationProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/18.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalNotificationProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalNotificationProfileTest
 * @brief Notificationプロファイルの正常系テスト.
 *
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalNotificationProfileTest

/*!
 * @brief 通知を送信するテストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /notification/notify?deviceid=xxxx&type=0&body=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・notificationidに1が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationNotifyPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/notify?deviceId=%@&type=0", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"notificationId\":\"1\",\"result\":0}", request);
}

/*!
 * @brief 通知の消去要求を送信するテストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/notify?deviceid=xxxx&notificationId=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationNotifyDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/notify?deviceId=%@&notificationId=1", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 通知クリックイベントのコールバック登録テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclick?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信できること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnClickPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"notificationId\":\"1\"}");
}

/*!
 * @brief 通知クリックイベントのコールバック解除テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnClickDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclick?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 通知表示イベントのコールバック登録テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信できること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnShowPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"notificationId\":\"1\"}");
}

/*!
 * @brief 通知表示イベントのコールバック解除テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onshow?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnShowDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onshow?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 通知消去イベントのコールバック登録テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信できること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnClosePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"notificationId\":\"1\"}");
}

/*!
 * @brief 通知消去イベントのコールバック解除テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onclose?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnCloseDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onclose?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 通知操作エラー発生イベントのコールバック登録テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信できること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnErrorPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"notificationId\":\"1\"}");
}

/*!
 * @brief 通知操作エラー発生イベントのコールバック解除テストを行う.
 *
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /notification/onerror?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 *
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationOnErrorDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/notification/onerror?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

@end
