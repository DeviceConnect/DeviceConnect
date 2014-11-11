//
//  DConnectTestCase.h
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/12.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <XCTest/XCTest.h>
#import <DConnectSDK/DConnectSDK.h>
#import <DConnectSDK/DConnectUtil.h>
#import "AccessToken.h"

/*! dConnectManagerのホスト名. */
extern NSString *const DConnectHost;
/*! dConnectManagerのポート番号. */
extern int DConnectPort;

@interface DConnectTestCase : XCTestCase

/**
 * テスト用のデバイスプラグインのIDを保持する.
 */
@property (nonatomic) NSString *deviceId;

/**
 * テスト用のクライアントIDを保持する.
 */
@property (nonatomic) NSString *clientId;

- (NSArray*) createClientForPackage:(NSString*)package;

- (AccessToken*) requestAccessTokenWithClientId:(NSString*)clientId
                                   clientSecret:(NSString*)clientSecret
                                         scopes:(NSArray*)scopes
                                applicationName:(NSString*)applicationName;

@end
