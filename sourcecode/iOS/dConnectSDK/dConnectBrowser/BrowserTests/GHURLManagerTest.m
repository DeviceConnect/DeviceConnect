//
//  GHURLManagerTest.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <XCTest/XCTest.h>
#import "GHURLManager.h"

@interface GHURLManagerTest : XCTestCase

@end

@implementation GHURLManagerTest

- (void)setUp
{
    [super setUp];
}

- (void)tearDown
{
    [super tearDown];
}

//URLチェック 正当性チェック
- (void)isURLString
{
    GHURLManager *manager = [[GHURLManager alloc]init];
    NSString* url = @"http://www.yahoo.co.jp";
    XCTAssertEqual([manager isURLString:url], YES);
}


//URLチェック 普通の文字列
- (void)isURLStringFail
{
    GHURLManager *manager = [[GHURLManager alloc]init];
    NSString* url = @"よく使うと思われるcheckingTypesは以下の通り";
    XCTAssertEqual([manager isURLString:url], NO);
}


//URLチェック httpなし
- (void)isURLStringNoHTTP
{
    GHURLManager *manager = [[GHURLManager alloc]init];
    NSString* url = @"://www.yahoo.co.jp";
    XCTAssertEqual([manager isURLString:url], NO);
}

@end
