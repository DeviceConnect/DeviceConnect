//
//  RESTfulNormalFileDescriptorProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/18.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalFileDescriptorProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalFileDescriptorProfileTest
 * @brief FileDescriptorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalFileDescriptorProfileTest

/*!
 * @brief ファイルをオープンするテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file_descriptor/open?deviceid=xxxx&mediaid=xxxx&flag=xxxx&mode=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・mediaidに"test.txt"が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorOpenGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/open?deviceId=%@&flag=r&path=/test/test.png", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief ファイルをクローズするテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file_descriptor/close?deviceid=xxxx&mediaid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorClosePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/close?deviceId=%@&path=/test/test.png", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief 指定したサイズ分のデータをファイルから読み込むテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /file_descriptor/read?deviceid=xxxx&mediaid=xxxx&length=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorReadGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/read?deviceId=%@&length=1&path=/test/test.png", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"fileData\":\"\",\"size\":64000}", request);
}

/*!
 * @brief ファイルに書き込むテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file_descriptor/write?deviceid=xxxx&mediaid=xxxx
 * Entity: 文字列"test"。
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorWritePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/write?deviceId=%@&path=/test/test.png", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    Multipart* multi = [Multipart new];
    [multi addData:[@"test_media" dataUsingEncoding:NSUTF8StringEncoding] forKey:@"media"]; // TODO Base64文字列を使用するようにする.
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief ファイルの更新通知のコールバック登録テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /file_descriptor/watchfile?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorOnWatchFilePut
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/onwatchfile?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    CHECK_EVENT(@"{\"file\":{\"curr\":\"2014-06-01T00:00:00+0900\",\"path\":\"test.txt\",\"prev\":\"2014-06-01T00:00:00+0900\"}}");
}

/*!
 * @brief ファイルの更新通知のコールバック解除テストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /file_descriptor/watchfile?deviceid=xxxx&session_key=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalFileDescriptorOnWatchFileDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/file_descriptor/onwatchfile?sessionKey=%@&deviceId=%@", self.clientId, self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
}

@end
