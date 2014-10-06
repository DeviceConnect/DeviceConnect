//
//  DConnectNetworkServiceDiscoveryProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <XCTest/XCTest.h>
#import <DConnectSDK/DConnectSDK.h>
#import "NormalTestDevicePlugin.h"

@interface DConnectNetworkServiceDiscoveryProfileTest : XCTestCase

@end

@implementation DConnectNetworkServiceDiscoveryProfileTest

- (void)setUp
{
    [super setUp];
    [DConnectManager sharedManager];
}

- (void)tearDown
{
    [super tearDown];
}

@end
