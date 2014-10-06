//
//  DConnectTestCase.m
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/12.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DConnectTestCase.h"

@implementation DConnectTestCase

- (void)setUp
{
    [super setUp];
    self.clientId = @"test_client";
    [DConnectTestCase startDConnectManager];
}

+ (void)startDConnectManager
{
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        DConnectManager *mgr = [DConnectManager sharedManager];
        mgr.settings.useLocalOAuth = NO;
        [mgr start];
        [mgr startWebsocket];
    });
}

- (NSArray*) createClientForPackage:(NSString *)package
{
    return nil;
}

- (AccessToken*) requestAccessTokenWithClientId:(NSString*)clientId
                                   clientSecret:(NSString*)clientSecret
                                         scopes:(NSArray*)scopes
                                applicationName:(NSString*)applicationName
{
    return nil;
}

@end
