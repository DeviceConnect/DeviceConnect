//
//  RESTfulStressTest.m
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/20.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulStressTest : RESTfulTestCase

@end

@interface Counter : NSObject <SRWebSocketDelegate>
{
    NSString* _clientId;
    SRWebSocket* _webSocket;
    int _count;
    dispatch_semaphore_t _semaphore;
}

@property (nonatomic) NSMutableArray *objects;

- (id) initWithCount:(int)count;
- (BOOL) receivedAll;
- (void) signalWithObject:(id)event;
- (void) openWebSocketWithClientId:(NSString*) clientId;
- (void) closeWebSocket;
- (void) start;
@end

/*!
 * @class RESTfulStressTest
 * @brief RESTful APIの負荷テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulStressTest

/*!
 * @brief 負荷テストを行う.
 * 1000回連続リクエストを送信する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /network_service_discovery/getnetworkservices
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・すべてresultに0が返ってくること。
 * </pre>
 */
- (void) testStress
{
    for (int i = 0; i < 1000; i++) {
        NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices"];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
        [request setHTTPMethod:@"GET"];
        
        CHECK_RESPONSE(@"{\"services\":[{\"config\":\"test config\",\"id\":\"test_device_id.DeviceTestPlugin.dconnect\",\"name\":\"Test Success Device\",\"online\":true,\"type\":\"TEST\"},{\"config\":\"test config\",\"id\":\"!#$'()-~¥@[;+:*],._/=?&%^|`\\\"{}<>.DeviceTestPlugin.dconnect\",\"name\":\"Test Device ID Special Characters\",\"online\":true,\"type\":\"TEST\"}]}", request);
    }
}

/*!
 * @brief イベント受信に関する負荷テストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /event/unique
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・すべてresultに0が返ってくること。
 * </pre>
 */
- (void) testStressTestManyReceivedEventAsync
{
    const int count = 10;
    Counter *counter = [[Counter alloc] initWithCount:count];
    [counter openWebSocketWithClientId:self.clientId];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/event/unique?deviceId=%@&sessionKey=%@&count=%d", self.deviceId, self.clientId, count]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"POST"];
    CHECK_RESPONSE(@"{\"result\":0}", request);
    
    [counter start];
    XCTAssertTrue([counter receivedAll]);
    
    for (id data in [counter objects]) {
        if (![data isKindOfClass:[NSData class]]){
            XCTFail(@"Received invalid data.");
            break;
        }
    }
}

/*!
 * @brief イベント登録に関する負荷テストテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /event/unique
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・すべてresultに0が返ってくること。
 * </pre>
 */
- (void) testStressTestManyEventRegistrationAsync
{
    const int count = 10;
    Counter *counter = [[Counter alloc] initWithCount:count];
    
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/network_service_discovery/getnetworkservices?sessionKey=%@", self.clientId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    for (int i = 0; i < count; i++) {
        [NSURLConnection sendAsynchronousRequest:request
                                           queue:[NSOperationQueue new]
                               completionHandler:^(NSURLResponse *response, NSData *data, NSError *connectionError)
         {
             [NSThread sleepForTimeInterval:1.0];
             [counter signalWithObject:data];
         }];
    }
    [counter start];
    XCTAssertTrue([counter receivedAll]);
    
    NSString *expectedJson = @"{\"result\":0}";
    for (id data in [counter objects]) {
        if (![data isKindOfClass:[NSData class]]){
            XCTFail(@"Received invalid data.");
            break;
        }
        
        NSDictionary *actualResponse = [NSJSONSerialization JSONObjectWithData:(NSData *) data
                                                                       options:NSJSONReadingMutableContainers
                                                                         error:nil];
        
        XCTAssertNotNil(actualResponse);
        NSDictionary *expectedResponse = [NSJSONSerialization JSONObjectWithData:[expectedJson dataUsingEncoding:NSUTF8StringEncoding]
                                                                         options:NSJSONReadingMutableContainers
                                                                           error:nil];
        
        XCTAssertNotNil(actualResponse);
        XCTAssertTrue([self assertDictionary:expectedResponse actual:actualResponse], "expected=%@, but actual=%@", expectedResponse, actualResponse);
    }
}

@end

@implementation Counter

- (id) initWithCount:(int)count
{
    self = [super init];
    if (self) {
        _count = count;
    }
    return self;
}

- (BOOL) receivedAll
{
    return _count == 0;
}

- (void) signalWithObject:(id)event
{
    @synchronized(self) {
        _count--;
        [_objects addObject:event];
        if (_count == 0) {
            dispatch_semaphore_signal(_semaphore);
        }
    }
}

- (void) openWebSocketWithClientId:(NSString*) clientId
{
    _clientId = clientId;
    _webSocket = [[SRWebSocket new] initWithURLRequest:[NSURLRequest requestWithURL:[NSURL URLWithString:@"ws://localhost:4035/gotapi/websocket"]]];
    [_webSocket setDelegateOperationQueue:[NSOperationQueue new]];
    [_webSocket setDelegate:self];
    [_webSocket open];
}

- (void) closeWebSocket
{
    [_webSocket close];
}

- (void) start
{
    _semaphore = dispatch_semaphore_create(0);
    dispatch_semaphore_wait(_semaphore, dispatch_time(DISPATCH_TIME_NOW, 30 * NSEC_PER_SEC));
}

- (void) webSocketDidOpen:(SRWebSocket *)webSocket {
    [_webSocket send:[NSString stringWithFormat:@"{\"sessionKey\":\"%@\"}", _clientId]];
}

- (void) webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    
}

- (void) webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    
}

- (void) webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message {
    if(!message) {
        return;
    }
    NSData* data = [message dataUsingEncoding:NSUTF8StringEncoding];
    if(!data) {
        return;
    }
    [self signalWithObject:[NSJSONSerialization JSONObjectWithData:data options:0 error:nil]];
}

@end
