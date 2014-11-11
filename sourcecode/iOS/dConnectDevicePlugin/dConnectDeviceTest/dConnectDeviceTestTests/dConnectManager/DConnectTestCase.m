//
//  DConnectTestCase.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
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
