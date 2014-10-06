//
//  DConnectTestCase.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>
#import "DConnectNormalTestCase.h"
#import "TestDevicePlugin.h"

NSString *const DConnectHost = @"localhost";
int DConnectPort = 8080;

@implementation DConnectNormalTestCase

- (void)setUp
{
    [super setUp];
    // dConnectManagerのインスタンスを作成
    [DConnectManager sharedManager];
    
    // deviceIdを検索しておく
    if (!self.deviceId) {
        [self searchTestDevicePlugin];
    }
}

- (void)tearDown
{
    [super tearDown];
}

- (void) searchTestDevicePlugin {
    NSURL *url = [NSURL URLWithString:@"http://localhost:8080/network_service_discovery/getnetworkservices"];
    NSURLRequest *request = [NSURLRequest requestWithURL:url];
    NSURLResponse *response = nil;
    NSError *error = nil;
    NSData *data = [NSURLConnection sendSynchronousRequest:request
                                         returningResponse:&response
                                                     error:&error];
    // 通信チェック
    XCTAssertNotNil(data, @"Failed to connect dConnectManager. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssertNil(error, @"Failed to connect dConnectManager. \"%s\"", __PRETTY_FUNCTION__);
    
    NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data
                                                        options:NSJSONReadingMutableContainers
                                                          error:nil];
    
    // resultのチェック
    NSNumber *result = [dic objectForKey:DConnectMessageResult];
    XCTAssert([result intValue] == DConnectMessageResultTypeOk);
    
    // デバイスのチェック
    NSArray *services = [dic objectForKey:DConnectNetworkServiceDiscoveryProfileParamServices];
    for (int i = 0; i < [services count]; i++) {
        NSDictionary *s = (NSDictionary *)[services objectAtIndex:i];
        NSString *name = [s objectForKey:DConnectNetworkServiceDiscoveryProfileParamName];
        NSString *deviceId = [s objectForKey:DConnectNetworkServiceDiscoveryProfileParamId];
        if ([TestDevicePluginName isEqualToString:name]) {
            self.deviceId = deviceId;
        }
    }
    XCTAssertNotNil(self.deviceId, @"Can't found deviceId.");
}

@end
