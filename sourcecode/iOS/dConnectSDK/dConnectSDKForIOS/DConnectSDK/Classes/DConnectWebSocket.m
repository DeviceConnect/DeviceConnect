//
//  DConnectWebSocket.m
//  websocket
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectWebSocket.h"
#import "HTTPMessage.h"
#import "DConnectMessage.h"

#define TIMEOUT_READ_FIRST_HEADER_LINE       30
#define TIMEOUT_READ_SUBSEQUENT_HEADER_LINE  30
#define MAX_HEADER_LINE_LENGTH             8190
#define HTTP_REQUEST_HEADER                  10

@interface DConnectWebSocket ()

/*! @brief Websocketの処理を行うキュー.
 */
@property (nonatomic) dispatch_queue_t serverQueue;

/*! @brief ソケット.
 */
@property (nonatomic) GCDAsyncSocket *asyncSocket;

/*! @brief HTTPリクエスト.
 */
@property (nonatomic) HTTPMessage *request;

/*! @brief websocketの一覧.
 */
@property (nonatomic) NSMutableArray *websocketList;

/*! @brief websocketとsessionKeyの対応表.
 */
@property (nonatomic) NSMutableDictionary *websocketDic;

@end



@implementation DConnectWebSocket

- (instancetype) init {
    self = [super init];
    if (self) {
        self.websocketList = [NSMutableArray array];
        self.websocketDic = [NSMutableDictionary dictionary];
        self.host = @"localhost";
        self.port = 4035;
    }
    return self;
}

- (instancetype) initWithHost:(NSString *)host port:(int)port {
    self = [super init];
    if (self) {
        self.websocketList = [NSMutableArray array];
        self.websocketDic = [NSMutableDictionary dictionary];
        self.host = host;
        self.port = port;
    }
    return self;
}

- (BOOL) start {
    self.serverQueue = dispatch_queue_create("WebSocketServer", NULL);
    self.asyncSocket = [[GCDAsyncSocket alloc] initWithDelegate:self delegateQueue:self.serverQueue];
    
    if ([self isUseSSL]) {
        NSDictionary *settings = [self createSSLConfiguration];
        if (settings) {
            [self.asyncSocket startTLS:settings];
        }
    }
    
    NSError *error = nil;
    BOOL success = [self.asyncSocket acceptOnInterface:self.host port:self.port error:&error];
#ifdef DEBUG
    if (!success) {
        DCLogE(@"Failed to initialize the websocket. %@", error);
    }
#endif
    return success;
}

- (void) stop {
    for (int i = 0; i < self.websocketList.count; i++) {
        WebSocket *socket = self.websocketList[i];
        [socket stop];
    }
    [self.websocketList removeAllObjects];
    
    [self.websocketDic removeAllObjects];
}

- (void) sendEvent:(NSString *)event forSessionKey:(NSString *)sessionKey {
    WebSocket *socket = [self.websocketDic objectForKey:sessionKey];
    if (socket) {
        [socket sendMessage:event];
    }
}

- (BOOL) isUseSSL {
    return NO;
}

- (NSDictionary *) createSSLConfiguration {
    // TODO: SSLの実装
    return nil;
}

#pragma mark - WebSocketDelegate Methods -

- (void)webSocketDidOpen:(WebSocket *)ws {
    [self.websocketList addObject:ws];
}

- (void)webSocket:(WebSocket *)ws didReceiveMessage:(NSString *)msg {
    NSData *jsonData = [msg dataUsingEncoding:NSUnicodeStringEncoding];
    if (jsonData) {
        // JSONをNSDictionaryに変換する
        NSError *error = nil;
        NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData
                                                            options:NSJSONReadingAllowFragments
                                                              error:&error];
        if (!error) {
            NSString *sessionKey = [dic objectForKey:DConnectMessageSessionKey];
            if (sessionKey) {
                [self.websocketDic setObject:ws forKey:sessionKey];
            }
        }
    }
}

- (void)webSocketDidClose:(WebSocket *)ws {
    [self.websocketList removeObject:ws];
    
    for (id key in [self.websocketDic keyEnumerator]) {
        WebSocket *socket = [self.websocketDic objectForKey:key];
        if (ws == socket) {
            [self.websocketDic removeObjectForKey:key];
            return;
        }
    }
}

#pragma mark - GCDAsyncSocketDelegate Methods -

- (void)socket:(GCDAsyncSocket *)sock didAcceptNewSocket:(GCDAsyncSocket *)newSocket {
    dispatch_async(self.serverQueue, ^{
        if ([self isUseSSL]) {
            NSDictionary *settings = [self createSSLConfiguration];
            if (settings) {
                [newSocket startTLS:settings];
            }
        }
        
        [newSocket readDataToData:[GCDAsyncSocket CRLFData]
                      withTimeout:TIMEOUT_READ_FIRST_HEADER_LINE
                        maxLength:MAX_HEADER_LINE_LENGTH
                              tag:HTTP_REQUEST_HEADER];
	});

    // 新しい通信がきたので、リクエストを新規に作成
    self.request = [[HTTPMessage alloc] initEmptyRequest];
}

- (void)socketDidDisconnect:(GCDAsyncSocket *)socket withError:(NSError *)err {
    DCLogD(@"socketDidDisconnect:withError: %@", err);
}

- (void)socket:(GCDAsyncSocket *)socket didReadData:(NSData *)data withTag:(long)tag {
    if (tag == HTTP_REQUEST_HEADER) {
		BOOL result = [self.request appendData:data];
		if (!result) {
            // データの追加に失敗
		} else if (![self.request isHeaderComplete]) {
            [socket readDataToData:[GCDAsyncSocket CRLFData]
                       withTimeout:TIMEOUT_READ_SUBSEQUENT_HEADER_LINE
                         maxLength:MAX_HEADER_LINE_LENGTH
                               tag:HTTP_REQUEST_HEADER];
		} else {
            if ([WebSocket isWebSocketRequest:self.request]) {
                WebSocket *websocket = [[WebSocket alloc] initWithRequest:self.request socket:socket];
                websocket.delegate = self;
                [websocket start];
            } else {
                // Websocket以外
            }
        }
    }
#ifdef DEBUG
    else {
        DCLogW(@"NO Websocket request.");
	}
#endif
}

@end
