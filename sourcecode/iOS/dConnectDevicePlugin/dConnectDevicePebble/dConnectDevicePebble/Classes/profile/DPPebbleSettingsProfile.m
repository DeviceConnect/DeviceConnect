//
//  DPPebbleSettingsProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleSettingsProfile.h"

@interface DPPebbleSettingsProfile ()

/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;

@end


@implementation DPPebbleSettingsProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectSettingsProfileDelegate

- (BOOL)         profile:(DConnectSettingsProfile *)profile
didReceiveGetDateRequest:(DConnectRequestMessage *)request
                response:(DConnectResponseMessage *)response
                deviceId:(NSString *)deviceId
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = @(PROFILE_SETTING);
        dic[@(KEY_ATTRIBUTE)] = @(SETTING_ATTRIBUTE_DATE);
        dic[@(KEY_ACTION)] = @(ACTION_GET);
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                NSNumber *data = dic[@(KEY_PARAM_SETTING_DATE)];
                if(data == nil){
                    [response setErrorToTimeout];
                }else{
                    [response setResult:DConnectMessageResultTypeOk];
                    [DConnectSettingsProfile setDate:(NSString*)data target:response];
                }
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
