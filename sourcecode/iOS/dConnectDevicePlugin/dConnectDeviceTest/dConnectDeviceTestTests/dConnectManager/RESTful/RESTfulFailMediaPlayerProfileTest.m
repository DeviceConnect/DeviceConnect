//
//  RESTfulFailMediaPlayerProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/22.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

/*!
 * @class RESTfulFailMediaPlayerProfileTest
 * @brief MediaPlayerプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@interface RESTfulFailMediaPlayerProfileTest : RESTfulTestCase

@end

@implementation RESTfulFailMediaPlayerProfileTest

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/media?mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/media?deviceId=&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1&deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/media?deviceId=123456789&mediaId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1&deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1&deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media?mediaId=1&deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/media?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/media?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/media?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/media?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media_list
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media_list"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media_list?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media_list?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/media_list?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/media_list?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/media_list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/media_list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPUTを指定して再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/media_list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/media_list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツ一覧の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/media_list?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMediaListInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/media_list?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずにコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play_status?mediaId=xxxx&status=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play_status"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play_status?deviceId=&mediaId=xxxx&status=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play_status?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play_status?deviceId=123456789&mediaId=xxxx&status=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play_status?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/play_status?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play_status?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにPUTを指定してコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play_status?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play_status?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定してコンテンツ再生状態の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play_status?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayStatusInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play_status?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/play?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/play?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してコンテンツ再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/play?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/play?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してコンテンツ再生要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/play?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPlayPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/play?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/stop
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/stop"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/stop?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/stop?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/stop?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/stop?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してコンテンツ停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/stop?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/stop?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してコンテンツ停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/stop?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerStopPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/pause
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/pause"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/pause?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/pause?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/pause?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/pause?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してコンテンツ一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/pause?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/pause?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してコンテンツ一時停止要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/pause?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerPausePutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/resume
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/resume"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/resume?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/resume?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/resume?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/resume?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定してコンテンツ一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/resume?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumeInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/resume?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumeInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定してコンテンツ一時停止解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/resume?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerResumeInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/seek?mediaId=xxxx&pos=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek?pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/seek?deviceId=&pos=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek?deviceId=&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/seek?deviceId=123456789&pos=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek?deviceId=12345678&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/seek
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/seek?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/seek?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/seek?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/seek?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/seek?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生位置の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/seek?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerSeekInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/seek?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生音量の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/volume?volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume?pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生音量の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/volume?deviceId=&volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume?deviceId=&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生音量の変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/volume?deviceId=123456789&volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume?deviceId=12345678&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに再生音量の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/volume?volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumeGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生音量の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/volume?deviceId=&volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumeGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生音量の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/volume?deviceId=123456789&volume=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumeInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/volume?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生音量の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/volume?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumeInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/volume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生音量の取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/volume?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerVolumeInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/volume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生音量のミュート要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/mute?pos=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMutePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生音量のミュート要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/mute?deviceId=&pos=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMutePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId=&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生音量のミュート要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/mute?deviceId=12345678&pos=0
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMutePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId=12345678&pos=0"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに再生音量のミュート状態取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/mute
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生音量のミュート状態取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/mute?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生音量のミュート状態取得要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/mute?deviceId=12345678
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceIdを指定せずに再生音量のミュート解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/mute
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生音量のミュート解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/mute?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生音量のミュート解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/mute
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/mute?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定してミュート要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/mute
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerMuteInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/mute?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

/*!
 * @brief deviceIdを指定せずにコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/onstatuschange
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/onstatuschange?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/onstatuschange?deviceId=12345678
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyを指定せずコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/onstatuschange?deviceId=xxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangePutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /media_player/onstatuschange?deviceId=xxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangePutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdを指定せずコンテンツ再生状態の変化通知解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/onstatuschange
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でコンテンツ再生状態の変化通知解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/onstatuschange?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでコンテンツ再生状態の変化通知解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/onstatuschange?deviceId=12345678
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyを指定せずコンテンツ再生状態の変化通知解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/onstatuschange?deviceId=xxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でコンテンツ再生状態の変化通知解除要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /media_player/onstatuschange?deviceId=xxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /media_player/onstatuschange?deviceId=xxx&sessionKey=xxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してコンテンツ再生状態の変化通知要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /media_player/onstatuschange?deviceId=xxx&sessionKey=xxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaPlayerOnStatusChangeInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/media_player/onstatuschange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":3}", request);
}

@end
