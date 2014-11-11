//
//  DPDicePlusLightProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusLightProfile.h"
#import "DPDicePlusManager.h"

static int lightIds[] = {
    1, 2, 4, 8, 16, 32, -1
};

@interface DPDicePlusLightProfile ()

/*!
 @brief 指定されたライトIDが正しいかチェックする。
 
 @param[in] lightId ライトID
 
 @retval YES 正常
 @retval NO 不正
 */
- (BOOL) checkLightId:(NSString *)lightId;

/*!
 @brief 指定されたデバイスIDが正しいかチェックする。

 @param[in] deviceId デバイスID
 
 @retval YES 正常
 @retval NO 不正
 */
- (BOOL) checkDeviceId:(NSString *)deviceId;

@end


@implementation DPDicePlusLightProfile

- (id) init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DCMLightProfileDelegate delegate
#pragma mark - Get Methods

- (BOOL)          profile:(DCMLightProfile *)profile
didReceiveGetLightRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId {
    
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else {
        DConnectArray *lights = [DConnectArray array];
        for (int i = 0; i < 7; i++) {
            NSString *lightId = [NSString stringWithFormat:@"%d", lightIds[i]];
            NSString *lightName = [NSString stringWithFormat:@"Dice+ %d", lightIds[i]];
            DConnectMessage *light = [DConnectMessage new];
            [light setString:lightId forKey:DCMLightProfileParamLightId];
            [light setString:lightName forKey:DCMLightProfileParamName];
            [light setBool:NO forKey:DCMLightProfileParamOn];
            [light setString:@"" forKey:DCMLightProfileParamConfig];
            [lights addMessage:light];
        }
        [response setResult:DConnectMessageResultTypeOk];
        [response setArray:lights forKey:DCMLightProfileParamLights];
    }
    return YES;
}

#pragma mark - Post Methods

- (BOOL)           profile:(DCMLightProfile *)profile
didReceivePostLightRequest:(DConnectRequestMessage *)request
                  response:(DConnectResponseMessage *)response
                  deviceId:(NSString *)deviceId
                   lightId:(NSString *)lightId
                brightness:(double)brightness
                     color:(NSString *)color
                  flashing:(NSArray *)flashing {
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (![self checkLightId:lightId]) {
        [response setErrorToInvalidRequestParameterWithMessage:@"lightId is Invalid."];
    } else {
        NSString *rr;
        NSString *gg;
        NSString *bb;
        
        int light = lightId.intValue;
        unsigned int r = 255;
        unsigned int g = 255;
        unsigned int b = 255;
        if (color != nil && color.length > 0) {
            if ([color length] != 6) {
                [response setErrorToInvalidRequestParameterWithMessage:@"color is Invalid."];
                return YES;
            } else {
                @try {
                    rr = [color substringWithRange:NSMakeRange(0, 2)];
                    gg = [color substringWithRange:NSMakeRange(2, 2)];
                    bb = [color substringWithRange:NSMakeRange(4, 2)];
                }
                @catch (NSException *exception) {
                    [response setErrorToInvalidRequestParameterWithMessage:@"color is Invalid."];
                    return YES;
                }
                if (![[NSScanner scannerWithString:rr] scanHexInt:&r]) {
                    [response setErrorToInvalidRequestParameterWithMessage:@"color is Invalid."];
                    return YES;
                } else if (![[NSScanner scannerWithString:gg] scanHexInt:&g]) {
                    [response setErrorToInvalidRequestParameterWithMessage:@"color is Invalid."];
                    return YES;
                } else if (![[NSScanner scannerWithString:bb] scanHexInt:&b]) {
                    [response setErrorToInvalidRequestParameterWithMessage:@"color is Invalid."];
                    return YES;
                }
            }
        }

        // ライトをONにする
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr getDieByUID:deviceId];
        [mgr turnOnLightOfDie:die lightId:light red:r green:g blue:b];
        
        // レスポンスをOKに設定
        [response setResult:DConnectMessageResultTypeOk];
    }
    return YES;
}

#pragma mark - Put Methods

- (BOOL)          profile:(DCMLightProfile *)profile
didReceivePutLightRequest:(DConnectRequestMessage *)request
                 response:(DConnectResponseMessage *)response
                 deviceId:(NSString *)deviceId
                  lightId:(NSString *)lightId
                     name:(NSString *)name
               brightness:(double)brightness
                    color:(NSString *)color
                 flashing:(NSArray *)flashing {
    [response setErrorToNotSupportAction];
    return YES;
}

#pragma mark - Delete Methods

- (BOOL)             profile:(DCMLightProfile *)profile
didReceiveDeleteLightRequest:(DConnectRequestMessage *)request
                    response:(DConnectResponseMessage *)response
                    deviceId:(NSString *)deviceId
                     lightId:(NSString *)lightId {
    if (deviceId == nil) {
        [response setErrorToEmptyDeviceId];
    } else if (![self checkDeviceId:deviceId]) {
        [response setErrorToNotFoundDevice];
    } else if (![self checkLightId:lightId]) {
        [response setErrorToInvalidRequestParameterWithMessage:@"lightId is Invalid."];
    } else {
        int light = lightId.intValue;
        
        // ライトをOFFにする
        DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
        DPDie *die = [mgr getDieByUID:deviceId];
        [mgr turnOffLightOfDie:die lightId:light];
        
        // レスポンスをOKに設定
        [response setResult:DConnectMessageResultTypeOk];
    }
    return YES;
}

- (BOOL) checkLightId:(NSString *)lightId {
    if (lightId == nil) {
        return NO;
    }
    int light = lightId.intValue;
    for (int i = 0; i < 7; i++) {
        if (lightIds[i] == light) {
            return  YES;
        }
    }
    return NO;
}

- (BOOL) checkDeviceId:(NSString *)deviceId {
    DPDicePlusManager *mgr = [DPDicePlusManager sharedManager];
    DPDie *die = [mgr getDieByUID:deviceId];
    return die != nil;
}

@end
