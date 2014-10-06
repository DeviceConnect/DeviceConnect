//
//  DConnectSDKFailTestCase.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <XCTest/XCTest.h>
#import <DConnectSDK/DConnectSDK.h>

@interface DConnectManagerTestCase : XCTestCase

@end

@implementation DConnectManagerTestCase

- (void)setUp
{
    [super setUp];
    // dConnectManagerの起動
    [DConnectManager sharedManager];
}

- (void)tearDown
{
    [super tearDown];
}

- (void)testDConnectManager {
    // キャッチされない例外もしくはエラーでFail判定
    [DConnectManager sharedManager];
}

- (void) testSystemProfile {
    NSString *url = [NSString stringWithFormat:@"http://localhost:8080/system"];
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:url]];
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
    
    // 各パラメータ取得
    NSString *version = [dic objectForKey:DConnectSystemProfileParamVersion];
    NSArray *support = [dic objectForKey:DConnectSystemProfileParamSupports];
    
    XCTAssertNotNil(version, @"version is nil.\"%s\"", __PRETTY_FUNCTION__);
    XCTAssertNotNil(support, @"support is nil.\"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([version isEqualToString:@"1.0"], @"Failed to get version.\"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([support count] > 0, @"support is empty.\"%s\"", __PRETTY_FUNCTION__);
}

@end
