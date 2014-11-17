//
//  RESTfulNormalMediaStreamRecordingProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalMediaStreamRecordingProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalMediaStreamRecordingProfileTest
 * @brief MediaStreamRecordingプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalMediaStreamRecordingProfileTest

/*!
 * @brief 指定したスマートデバイス上で使用可能なカメラ情報を取得するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/mediarecorder?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・recorderに長さ1のBundle配列が格納されていること。
 * ・recorder[0].idが"test_camera_0"であること。
 * ・recorder[0].stateが"inactive"であること。
 * ・recorder[0].imageWidthが1920であること。
 * ・recorder[0].imageHeightが1080であること。
 * ・recorder[0].mimeTypeが"video/mp4"であること。
 * ・recorder[0].configが"test_config"であること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingMediaRecorderGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mediarecorder?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"recorders\":[{\"id\":\"test_camera_id\",\"imageHeight\":1080,\"name\":\"test_camera_name\",\"state\":\"inactive\",\"config\":\"test_config\",\"mimeType\":\"video/mp4\",\"imageWidth\":1920}]}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して写真撮影依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/takephoto?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・uriが"test.mp4"であること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingTakePhotoPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/takephoto?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"path\":\"test.png\",\"uri\":\"http://localhost:4035/gotapi/files?uri=content%3A%2F%2Ftest%2Ftest%2Emp4\"}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音の開始依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: POST
 * Path: /mediastream_recording/record?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・uriが"test.mp4"であること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingRecordPost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/record?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"path\":\"test.mp4\",\"uri\":\"http://localhost:4035/gotapi/files?uri=content%3A%2F%2Ftest%2Ftest%2Emp4\"}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音の一時停止依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/pause?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingPausePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/pause?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音の再開依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/resume?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingResumePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/resume?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音の停止依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/stop?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingStopPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/stop?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/mutetrack?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingMuteTrackPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/mutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したスマートデバイスに対して動画撮影または音声録音のミュート依頼を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/unmutetrack?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingUnmuteTrackPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/unmutetrack?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したスマートデバイスのカメラがサポートするオプションの一覧を取得するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /mediastream_recording/options?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・settingsに長さ1のBundle配列が格納されていること。
 * ・settings[0].imageWidthが1920であること。
 * ・settings[0].imageHeightが1080であること。
 * ・settings[0].mimeTypeが"video/mp4"であること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOptionsGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"imageHeight\":{\"min\":0,\"max\":0},\"result\":0,\"mimeType\":[\"video/mp4\"],\"imageWidth\":{\"min\":0,\"max\":0}}", request);
}

/*!
 * @brief 指定したスマートデバイスのカメラにオプションを設定するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/options?deviceid=xxxx&target=xxxx&imageWidth=xxxx&imageHeight=xxxx&mimeType=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOptionsPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/options?deviceId=%@&imageHeight=1080&target=test_camera_id&mimeType=video/mp4&imageWidth=1920", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 写真撮影イベントのコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onphoto?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信すること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnPhotoPut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"photo\":{\"path\":\"test.png\",\"mimeType\":\"video/mp4\"}}");
}

/*!
 * @brief 写真撮影イベントのコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onphoto?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnPhotoDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onphoto?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 動画撮影または音声録音開始イベントのコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/onrecordingchange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信すること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnRecordingChangePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"media\":{\"status\":\"recording\",\"path\":\"test.mp4\",\"mimeType\":\"video/mp4\"}}");
}

/*!
 * @brief 動画撮影または音声録音開始イベントのコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/onrecordingchange?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnRecordingChangeDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/onrecordingchange?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

/*!
 * @brief 動画撮影または音声録音の一定時間経過イベントのコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /mediastream_recording/ondeviceavailable?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・コールバック登録後にイベントを受信すること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnDataAvailablePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"media\":{\"uri\":\"http://localhost:4035/gotapi/files?uri=content%3A%2F%2Ftest%2Ftest%2Emp4\",\"mimeType\":\"video/mp4\"}}");
}

/*!
 * @brief 動画撮影または音声録音の一定時間経過イベントのコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /mediastream_recording/ondeviceavailable?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalMediaStreamRecordingOnDataAvailableDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/mediastream_recording/ondataavailable?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

@end
