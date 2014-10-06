
//
//  DPPebbleDeviceOrientationProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleDeviceOrientationProfile.h"
#import "DPPebbleDevicePlugin.h"

/** milli G を m/s^2 の値にする係数. */
#define G_TO_MS2_COEFFICIENT 9.81/1000.0

@interface DPPebbleDeviceOrientationProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;
/*!
 @brief PebbleID保存用変数。
 */
@property (nonatomic) NSString *pDeviceId;
@end


@implementation DPPebbleDeviceOrientationProfile

/*!
 @brief PebbleID取得用変数。
 */
-(NSString*)getDeviceId{
    return self.pDeviceId;
}

/*!
 @brief PebbleID保存用変数。
 */
-(void)setDeviceId:(DConnectRequestMessage*)request{
    self.pDeviceId=request.deviceId;
}

- (id) initWithDevicePlugin:(DPPebbleDevicePlugin *)plugin {
    self = [super init];
    if (self) {
        self.mgr = plugin.mgr;
        self.delegate = self;
        
        __unsafe_unretained typeof(self) eventCallbackSelf = self;
        [self.mgr addEventCallback:^(NSDictionary *message) {
            NSNumber *xx = message[@(KEY_PARAM_DEVICE_ORIENTATION_X)];
            NSNumber *yy = message[@(KEY_PARAM_DEVICE_ORIENTATION_Y)];
            NSNumber *zz = message[@(KEY_PARAM_DEVICE_ORIENTATION_Z)];
            NSNumber *intervalX = message[@(KEY_PARAM_DEVICE_ORIENTATION_INTERVAL)];
            
            double x = [xx intValue] *(double)G_TO_MS2_COEFFICIENT;
            double y = [yy intValue] *(double)G_TO_MS2_COEFFICIENT;
            double z = [zz intValue] *(double)G_TO_MS2_COEFFICIENT;
            
            long long interval = [intervalX longLongValue];
            
            DConnectMessage *orientation = [DConnectMessage message];
            DConnectMessage *accelerationIncludingGravity = [DConnectMessage message];
            [DConnectDeviceOrientationProfile setX:x target:accelerationIncludingGravity];
            [DConnectDeviceOrientationProfile setY:y target:accelerationIncludingGravity];
            [DConnectDeviceOrientationProfile setZ:z target:accelerationIncludingGravity];
            
            [DConnectDeviceOrientationProfile setAccelerationIncludingGravity:accelerationIncludingGravity target:orientation];
            [DConnectDeviceOrientationProfile setInterval:interval target:orientation];
            
            // イベントの取得
            DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
            NSArray *evts = [mgr eventListForDeviceId:[eventCallbackSelf getDeviceId]
                                              profile:DConnectDeviceOrientationProfileName
                                            attribute:DConnectDeviceOrientationProfileAttrOnDeviceOrientation];
            // イベント送信
            for (DConnectEvent *evt in evts) {
                DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
                [DConnectDeviceOrientationProfile setOrientation:orientation target:eventMsg];
                [plugin sendEvent:eventMsg];
            }
        }
                           profile:@(PROFILE_DEVICE_ORIENTATION)];
        
    }
    return self;
}


#pragma mark - DConnectDeviceOrientationProfileDelegate

- (BOOL)                        profile:(DConnectDeviceOrientationProfile *)profile
didReceivePutOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                             sessionKey:(NSString *)sessionKey
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
    } else {
        [self setDeviceId:request];
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_DEVICE_ORIENTATION];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION];
        dic[@(KEY_ACTION)] = [NSNumber numberWithUint32:ACTION_PUT];
        
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {}];
        Class class = [DPPebbleDevicePlugin class];
        DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:class];
        DConnectEventError error = [mgr addEventForRequest:request];
        
        switch (error) {
            case DConnectEventErrorNone:
                [response setResult:DConnectMessageResultTypeOk];
                break;
            case DConnectEventErrorInvalidParameter:
                [response setErrorToInvalidRequestParameter];
                break;
            case DConnectEventErrorNotFound:
            case DConnectEventErrorFailed:
            default:
                [response setErrorToUnknown];
                break;
        }
        return YES;
    }
}

- (BOOL)                           profile:(DConnectDeviceOrientationProfile *)profile
didReceiveDeleteOnDeviceOrientationRequest:(DConnectRequestMessage *)request
                                  response:(DConnectResponseMessage *)response
                                  deviceId:(NSString *)deviceId
                                sessionKey:(NSString *)sessionKey
{
    NSString* mDeviceId=[self.mgr getConnectWatcheName];
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
        return YES;
    }else if(![deviceId isEqualToString:mDeviceId]) {
        [response setErrorToNotFoundDevice];
        return YES;
        
    } else if(sessionKey == nil) {
        [response setErrorToInvalidRequestParameterWithMessage:@"sessionKey must be specified."];
        return YES;
    } else {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        dic[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_DEVICE_ORIENTATION];
        dic[@(KEY_ATTRIBUTE)] = [NSNumber numberWithUint32:DEVICE_ORIENTATION_ATTRIBUTE_ON_DEVICE_ORIENTATION];
        dic[@(KEY_ACTION)] =[NSNumber numberWithUint32:ACTION_DELETE];
        [self.mgr sendCommandToPebble:dic callback:^(NSDictionary *dic) {
            if (dic) {
                DConnectEventManager *mgr = [DConnectEventManager sharedManagerForClass:[DPPebbleDevicePlugin class]];
                DConnectEventError error = [mgr removeEventForRequest:request];
                switch (error) {
                    case DConnectEventErrorNone:
                        [response setResult:DConnectMessageResultTypeOk];
                        break;
                    case DConnectEventErrorInvalidParameter:
                        [response setErrorToInvalidRequestParameter];
                        break;
                    case DConnectEventErrorNotFound:
                    case DConnectEventErrorFailed:
                    default:
                        [response setErrorToUnknown];
                        break;
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
