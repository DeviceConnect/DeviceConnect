//
//  DConnectBatteryProfileTest.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectNormalTestCase.h"
#import <DConnectSDK/DConnectSDK.h>

@interface DConnectBatteryProfileTest : DConnectNormalTestCase

@end

@implementation DConnectBatteryProfileTest

- (void) testBattery {
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    [builder setHost:DConnectHost];
    [builder setPort:DConnectPort];
    [builder setProfile:DConnectBatteryProfileName];
    [builder addParameter:self.deviceId forName:DConnectMessageDeviceId];
    
    NSURL *uri = [builder build];
    NSURLRequest *request = [NSURLRequest requestWithURL:uri];
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
    
    // 各パラメータのチェック
    NSNumber *charging = dic[DConnectBatteryProfileParamCharging];
    NSNumber *chargingTime = dic[DConnectBatteryProfileParamChargingTime];
    NSNumber *dischargingTime = dic[DConnectBatteryProfileParamDischargingTime];
    NSNumber *level = dic[DConnectBatteryProfileParamLevel];
    
    XCTAssertNotNil(charging, @"charging is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssertNotNil(chargingTime, @"chargingTime is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssertNotNil(dischargingTime, @"dischargingTime is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssertNotNil(level, @"level is nil. \"%s\"", __PRETTY_FUNCTION__);
    
    XCTAssert([charging boolValue] == TestDevicePluginBatteryCharging, @"charging is not NO. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([chargingTime intValue] >= 0, @"chargingTime is less than 0. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([dischargingTime intValue] >= 0, @"dischargingTime is less than 0. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([level floatValue] >= 0 && [level floatValue] <= 1,
              @"The following condition is not met: 0 <= level <= 1.0 . \"%s\"", __PRETTY_FUNCTION__);
}

- (void) testCharging {
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    [builder setHost:DConnectHost];
    [builder setPort:DConnectPort];
    [builder setProfile:DConnectBatteryProfileName];
    [builder setAttribute:DConnectBatteryProfileAttrCharging];
    [builder addParameter:self.deviceId forName:DConnectMessageDeviceId];
    
    NSURL *uri = [builder build];
    NSURLRequest *request = [NSURLRequest requestWithURL:uri];
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
    
    // パラメータチェック
    NSNumber *charging = dic[DConnectBatteryProfileParamCharging];
    XCTAssertNotNil(charging, @"charging is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([charging boolValue] == TestDevicePluginBatteryCharging, @"charging is not NO. \"%s\"", __PRETTY_FUNCTION__);
}

- (void) testChargingTime {
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    [builder setHost:DConnectHost];
    [builder setPort:DConnectPort];
    [builder setProfile:DConnectBatteryProfileName];
    [builder setAttribute:DConnectBatteryProfileAttrChargingTime];
    [builder addParameter:self.deviceId forName:DConnectMessageDeviceId];
    
    NSURL *uri = [builder build];
    NSURLRequest *request = [NSURLRequest requestWithURL:uri];
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
    
    // パラメータのチェック
    NSNumber *chargingTime = dic[DConnectBatteryProfileParamChargingTime];
    XCTAssertNotNil(chargingTime, @"chargingTime is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([chargingTime intValue] >= 0, @"chargingTime is less than 0. \"%s\"", __PRETTY_FUNCTION__);
}

- (void) testDischargingTime {
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    [builder setHost:DConnectHost];
    [builder setPort:DConnectPort];
    [builder setProfile:DConnectBatteryProfileName];
    [builder setAttribute:DConnectBatteryProfileAttrDischargingTime];
    [builder addParameter:self.deviceId forName:DConnectMessageDeviceId];
    
    NSURL *uri = [builder build];
    NSURLRequest *request = [NSURLRequest requestWithURL:uri];
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
    
    // パラメータのチェック
    NSNumber *dischargingTime = dic[DConnectBatteryProfileParamDischargingTime];
    XCTAssertNotNil(dischargingTime, @"dischargingTime is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([dischargingTime intValue] >= 0, @"dischargingTime is less than 0. \"%s\"", __PRETTY_FUNCTION__);
}

- (void) testLevel {
    DConnectURIBuilder *builder = [DConnectURIBuilder new];
    [builder setHost:DConnectHost];
    [builder setPort:DConnectPort];
    [builder setProfile:DConnectBatteryProfileName];
    [builder setAttribute:DConnectBatteryProfileAttrLevel];
    [builder addParameter:self.deviceId forName:DConnectMessageDeviceId];
    
    NSURL *uri = [builder build];
    NSURLRequest *request = [NSURLRequest requestWithURL:uri];
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
    
    // パラメータのチェック
    NSNumber *level = dic[DConnectBatteryProfileParamLevel];
    XCTAssertNotNil(level, @"level is nil. \"%s\"", __PRETTY_FUNCTION__);
    XCTAssert([level floatValue] >= 0 && [level floatValue] <= 1,
              @"The following condition is not met: 0 <= level <= 1.0 . \"%s\"", __PRETTY_FUNCTION__);
}

@end
