//
//  RESTfulTestCase.h
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/12.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DConnectTestCase.h"
#import "Multipart.h"
#import "SRWebSocket.h"

//======================================================
// レスポンス・イベントのチェック用マクロ
//
// マクロとして定義した理由:
// 　テスト失敗の理由がテストメソッド毎に表示されるようにするため.
//------------------------------------------------------

#define CHECK_RESPONSE(expectedJson, req) {\
    NSURLResponse *response = nil; \
    NSError *error = nil; \
    NSData *data = [NSURLConnection sendSynchronousRequest:req \
                                         returningResponse:&response \
                                                     error:&error]; \
    XCTAssertNotNil(data); \
    XCTAssertNil(error); \
    NSDictionary *expectedResponse = [NSJSONSerialization JSONObjectWithData:[expectedJson dataUsingEncoding:NSUTF8StringEncoding] \
                                                                     options:NSJSONReadingMutableContainers \
                                                                       error:nil]; \
    NSDictionary *actualResponse = [NSJSONSerialization JSONObjectWithData:data \
                                                               options:NSJSONReadingMutableContainers \
                                                                 error:nil]; \
    XCTAssertNotNil(actualResponse); \
    XCTAssertTrue([self assertDictionary:expectedResponse actual:actualResponse], "expected=%@, but actual=%@", expectedResponse, actualResponse); \
}

#define CHECK_EVENT(expectedJson) {\
    NSDictionary *expectedEvent = [NSJSONSerialization JSONObjectWithData:[expectedJson dataUsingEncoding:NSUTF8StringEncoding] \
                                                                  options:NSJSONReadingMutableContainers \
                                                                    error:nil]; \
    NSDictionary *actualEvent = [self waitForEvent]; \
    XCTAssertNotNil(actualEvent); \
    XCTAssertTrue([self assertDictionary:expectedEvent actual:actualEvent], "expected=%@, but actual=%@", expectedEvent, actualEvent); \
}

@interface RESTfulTestCase : DConnectTestCase <SRWebSocketDelegate>

/**
 * デバイスプラグインのIDを検索して、deviceIdに設定する.
 */
- (void) searchTestDevicePlugin;

/**
 * イベントを受信するために一定時間ブロックする.
 */
- (NSDictionary *) waitForEvent;

/**
 * JSONオブジェクトを比較する.
 */
- (BOOL) assertDictionary:(NSDictionary *)expectedObject actual:(NSDictionary *)actualObject;

#pragma mark - SRWebSocketDelegate

- (void) webSocketDidOpen:(SRWebSocket *)webSocket;
- (void) webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error;
- (void) webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean;
- (void) webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message;

@end
