//
//  DPPebbleVibrationProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleVibrationProfile.h"
#import "DPPebbleManager.h"


@interface DPPebbleVibrationProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;
@end


@implementation DPPebbleVibrationProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectVibrationProfileDelegate

- (BOOL)            profile:(DConnectVibrationProfile *)profile
didReceivePutVibrateRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                    pattern:(NSArray *) pattern
{
    NSString* mDeviceId = [self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else {
        NSData *p = [DPPebbleManager convertVibrationPattern:pattern];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = @(PROFILE_VIBRATION);
        dic[@(KEY_ATTRIBUTE)] = @(VIBRATION_ATTRIBUTE_VIBRATE);
        dic[@(KEY_ACTION)] = @(ACTION_PUT);
        if (p == nil) {
            dic[@(KEY_PARAM_VIBRATION_LEN)] = @(0);
        } else {
            dic[@(KEY_PARAM_VIBRATION_LEN)] = @(p.length / 2);
            dic[@(KEY_PARAM_VIBRATION_PATTERN)] = p;
        }
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

- (BOOL)               profile:(DConnectVibrationProfile *)profile
didReceiveDeleteVibrateRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
{
    NSString* mDeviceId = [self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = @(PROFILE_VIBRATION);
        dic[@(KEY_ATTRIBUTE)] = @(VIBRATION_ATTRIBUTE_VIBRATE);
        dic[@(KEY_ACTION)] = @(ACTION_DELETE);
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                [response setResult:DConnectMessageResultTypeOk];
            } else {
                [response setErrorToUnknown];
            }
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];
        }];
        // 非同期で返却するのでNO
        return NO;
    }
}

@end
