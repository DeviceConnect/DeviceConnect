//
//  RESTfulNormalVibrationProfileTest.m
//  DConnectDeviceTest
//
//  Created by Masaru Takano on 2014/08/18.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

#import "RESTfulTestCase.h"

@interface RESTfulNormalVibrationProfileTest : RESTfulTestCase

@end

/*!
 * @class RESTfulNormalVibrationProfileTest
 * @brief Vibratorプロファイルの正常系テスト.
 * @author NTT DOCOMO, INC.
 */
@implementation RESTfulNormalVibrationProfileTest

/*!
 * @brief Vibration開始要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: PUT
 * Path: /vibration/vibrate?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalVibrationVibratePost
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/vibration/vibrate?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"PUT"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

/*!
 * @brief Vibration停止要求を送信するテストを行う.
 * <pre>
 * 【HTTP通信】
 * Method: DELETE
 * Path: /vibration/vibrate?deviceid=xxxx
 * </pre>
 * <pre>
 * 【期待する動作】
 * ・resultに0が返ってくること。
 * </pre>
 */
- (void) testHttpNormalNotificationNotifyDelete
{
    NSURL *uri = [NSURL URLWithString:[NSString stringWithFormat:@"http://localhost:4035/gotapi/vibration/vibrate?deviceId=%@", self.deviceId]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:uri];
    [request setHTTPMethod:@"DELETE"];
    
    CHECK_RESPONSE(@"{\"result\":0}", request);
}

@end
