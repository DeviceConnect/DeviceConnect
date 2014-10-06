//
//  RESTfulNormalSystemProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/18.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalSystemProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalSystemProfileTest
 * @brief Systemプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalSystemProfileTest

/*!
 * @brief デバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・versionにString型の値が返ってくること。
 * ・supportsにJSONArray型の値が返ってくること。
 * ・pluginsにテスト用デバイスプラグインの情報が含まれていること。
 * </pre>
 */
- (void) testHttpNormalSystemGet
{
    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system"];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    CHECK_RESPONSE(@"{\"result\":0,\"supports\":[\"files\",\"system\",\"authorization\",\"network_service_discovery\"],\"version\":\"1.0\"}", request);
}

/*!
 * @brief デバイスのシステムプロファイルを取得する.
 * <pre>
 * 【HTTP通信】
 * Method: GET
 * Path: /system/device?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * ・versionにStringが返ってくること。
 * </pre>
 */
- (void) testHttpNormalSystemDeviceGet
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/system/device?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"GET"];
    
    NSMutableArray *supports = [NSMutableArray array];
    [supports addObject:DConnectBatteryProfileName];
    [supports addObject:DConnectConnectProfileName];
    [supports addObject:DConnectDeviceOrientationProfileName];
    [supports addObject:DConnectFileDescriptorProfileName];
    [supports addObject:DConnectFileProfileName];
    [supports addObject:DConnectMediaStreamRecordingProfileName];
    [supports addObject:DConnectMediaPlayerProfileName];
    [supports addObject:DConnectNetworkServiceDiscoveryProfileName];
    [supports addObject:DConnectPhoneProfileName];
    [supports addObject:DConnectProximityProfileName];
    [supports addObject:DConnectSettingsProfileName];
    [supports addObject:DConnectSystemProfileName];
    [supports addObject:DConnectVibrationProfileName];
    NSMutableString *paramSupports = [NSMutableString string];
    [paramSupports appendString:@"["];
    for (int i = 0; i < supports.count; i++) {
        if (i > 0) {
            [paramSupports appendString:@","];
        }
        [paramSupports appendString:[NSString stringWithFormat:@"\"%@\"", supports[i]]];
    }
    [paramSupports appendString:@"]"];
    NSString *expectedJson = [NSString stringWithFormat:@"{\"result\":0,\"supports\":%@,\"version\":\"1.0\"}", paramSupports];
    CHECK_RESPONSE(expectedJson, request);
}

// MEMO: 下記のテストは手動で行う.
//- (void) testHttpNormalSystemDeviceWakeupPut
//{
//    NSURL *uri = [NSURL URLWithString:@"http://localhost:4035/gotapi/system/device/wakeup?pluginId=DeviceTestPlugin%2Edconnect"];
//    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
//    [request setHTTPMethod:@"PUT"];
//
//    CHECK_RESPONSE(@"{\"result\":0}", request);
//}

@end
