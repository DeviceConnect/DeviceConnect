//
//  RESTfulFailMediaStreamRecordingProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/22.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

/*!
 * @class RESTfulFailMediaStreamRecordingProfileTest
 * @brief MediaStreamRecordingプロファイルの異常系テスト.
 * @author NTT DOCOMO, INC.
 */
@interface RESTfulFailMediaStreamRecordingProfileTest : RESTfulTestCase

@end

@implementation RESTfulFailMediaStreamRecordingProfileTest

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/mediarecorder
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/mediarecorder?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/mediarecorder?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/mediarecorder?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @biref メソッドにPUTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/mediarecorder?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/mediarecorder?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMediaRecorderGetInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/takephoto
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/takephoto"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/takephoto?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/takephoto?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/takephoto?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/takephoto?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/takephoto?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingTakePhotoPostInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/record
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/record"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/record?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/record?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/record?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPUTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/record?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostInvalidMethodPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/record?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingRecordPostInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/pause
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/pause"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/pause?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/pause?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/pause?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/pause?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/pause?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingPausePutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/resume
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/resume"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/resume?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/resume?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/resume?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/resume?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/resume?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingResumePutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/stop
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/stop"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/stop?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/stop?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/stop?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/stop?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/stop?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingStopPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/mutetrack
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/mutetrack?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/mutetrack?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/mutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定して動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/mutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して動画撮影や音声録音のミュート依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/mutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingMuteTrackPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/unmutetrack
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/unmutetrack
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/unmutetrack?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにGETを指定して動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/unmutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定して動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/unmutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して動画撮影や音声録音のミュート解除依頼を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/unmutetrack?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingUnmuteTrackPutInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdを指定せずに再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/options
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsGetNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/options?deviceId=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsGetEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/options?deviceId=123456789
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsGetInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief deviceId無しで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/options?imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options?imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態で再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/options?deviceId=&imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options?imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920&deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdで再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/options?deviceId=122345678&imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/options?imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920&deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief メソッドにPOSTを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/options?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにDELETEを指定して再生コンテンツの変更要求を送信するテスト.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/options?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOptionsInvalidMethodDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoPutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoPutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonphoto属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoPutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKey無しでonphoto属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoPutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonphoto属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */- (void) testHttpFailMediaStreamRecordingOnPhotoPutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKey無しでonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonphoto属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonphoto属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/onphoto?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonphoto属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/onphoto?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnPhotoInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondataavailable?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailablePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondataavailable?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailablePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondataavailable?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailablePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKeyなしでondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailablePutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyを空状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailablePutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondataavailable?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondataavailable?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondataavailable?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKey無しでondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でondataavailable属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してondataavailable属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してondataavailable属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/ondataavailable?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnDataAvailableInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief deviceIdが無い状態でonrecordingchange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangePutNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonrecordingchange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangePutEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonrecordingchange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangePutInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKey無しでonrecordingchange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangePutNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonrecordingchange属性のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangePutEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief deviceIdが無い状態でonrecordingchange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeDeleteNoDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief deviceIdが空状態でonrecordingchange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?deviceId=&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeDeleteEmptyDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId="];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":5}", request);
}

/*!
 * @brief 存在しないdeviceIdでonrecordingchange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?deviceId=123456789&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeDeleteInvalidDeviceId
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=12345678"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":6}", request);
}

/*!
 * @brief sessionKey無しでonrecordingchange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeDeleteNoSessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief sessionKeyが空状態でonrecordingchange属性のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx&sessionKey=
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeDeleteEmptySessionKey
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@&sessionKey=", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":10}", request);
}

/*!
 * @brief メソッドにGETを指定してonrecordingchange属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeInvalidMethodGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

/*!
 * @brief メソッドにPOSTを指定してonrecordingchange属性のリクエストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/onrecordingchange?deviceId=xxxx&sessionKey=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに1が返ってくること。
 * </pre>
 */
- (void) testHttpFailMediaStreamRecordingOnRecordingChangeInvalidMethodPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];

    CHECK_RESPONSE(@"{\"result\":1,\"errorCode\":8}", request);
}

@end
