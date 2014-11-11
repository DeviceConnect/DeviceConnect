//
//  DConnectSDKSuccessTestCase.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <XCTest/XCTest.h>
#import "DConnectManager.h"
#import "Multipart.h"

/**
 * @deprecated [2014/05/15 福井] DConnectManagerディレクトリ以下の単体テストに漸次統合する事
 * （マルチパート部分のテストは、File APIのテストに統合するとか？）
 */
@interface WebAPISuccessTestCase : XCTestCase

@end

@implementation WebAPISuccessTestCase

- (void)setUp
{
    [super setUp];
    [DConnectManager sharedManager];
}

- (void)tearDown
{
    [super tearDown];
}

- (void)testWebAPIMultipart
{
    // Multipartを用意
    Multipart* multi = [Multipart new];
    [multi addString:@"hahaha_i_" forKey:@"test1_string_key"];
    [multi addString:@"am_an_utterly_" forKey:@"test2_string_key"];
    const char data[] = "string!!!!\0";
    size_t len = strlen(data);
    [multi addData:[NSMutableData dataWithBytes:data length:len]
            forKey:@"test3_data_key"];
    //    NSLog(@"body: %@", [[NSString alloc] initWithData:multi.body
    //                                             encoding:NSUTF8StringEncoding]);
    
    // dConnectManagerへHTTPリクエストを投げる。
    NSMutableURLRequest *request =
    [NSMutableURLRequest requestWithURL:[NSURL URLWithString:@"http://localhost:8080/system"]];
    [request setValue:multi.contentType forHTTPHeaderField:@"content-type"];
    [request setHTTPBody:multi.body];
    NSURLResponse *response = nil;
    NSError *error = nil;
    [NSURLConnection sendSynchronousRequest:request
                          returningResponse:&response
                                      error:&error];
    
    NSLog(@"response: %@\n\nerror: %@", [response description], [error description]);
    
    XCTFail(@"No implementation for \"%s\"", __PRETTY_FUNCTION__);
}

@end
