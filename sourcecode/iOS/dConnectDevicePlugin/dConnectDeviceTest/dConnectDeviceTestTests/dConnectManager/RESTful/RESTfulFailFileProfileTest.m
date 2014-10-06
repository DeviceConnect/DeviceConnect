//
//  RESTfulFailFileProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/22.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulFailFileProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulFailFileProfileTest
 * @brief Fileプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulFailFileProfileTest

/*!
 * @brief deviceIdが無い状態でファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/send?mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/send"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/send?deviceId=&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/send?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/send?deviceId=123456789&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/send?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/send?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/send?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定してファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/send?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/send?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してファイル送信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/send?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileSendPostInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/send?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/receive?mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/receive"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/receive?deviceId=&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/receive?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/receive?deviceId=123456789&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/receive?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定してファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/receive?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定してファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/receive?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してファイル受信テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /file/receive?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileReceiveGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/receive?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/list
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/list"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/list?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/list?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/list?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/list?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定してファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /file/list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定してファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してファイル一覧取得テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /file/list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileListGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でファイル更新テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/update"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でファイル更新テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/update?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 不正なdeviceIdでファイル更新テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/update?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/update?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/update?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/update?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileUpdatePutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/update?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/remove?mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/remove"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/remove?deviceId=&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/remove?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/remove?deviceId=123456789&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/file/remove?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/remove?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/remove?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定してファイル削除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file/remove?deviceId=xxxx&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailFileRemoveDeleteInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file/remove?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

@end
