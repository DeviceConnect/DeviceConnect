//
//  DPDiceProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDiceProfile.h"
#import <DConnectSDK/DConnectUtil.h>

/*!
 @brief プロファイル名: Dice
 */
NSString *const DPDiceProfileName = @"dice";
/*!
 @brief インターフェース名: Dice
 */
 NSString *const DPDiceProfileInterfaceMagnetometer = @"magnetometer";

/*!
 @brief アトリビュート: Dice
 */
NSString *const DPDiceProfileAttrOnDice = @"ondice";
NSString *const DPDiceProfileAttrOnMagnetometer = @"onmagnetometer";
/*!
 @brief パラメータ: Dice
 */
NSString *const DPDiceProfileParamDice = @"dice";
NSString *const DPDiceProfileParamPip = @"pip";
NSString *const DPDiceProfileParamMagnetometer = @"magnetometer";
NSString *const DPDiceProfileParamFilter = @"filter";
NSString *const DPDiceProfileParamInterval = @"interval";
NSString *const DPDiceProfileParamX = @"x";
NSString *const DPDiceProfileParamY = @"y";
NSString *const DPDiceProfileParamZ = @"z";



@interface DPDiceProfile()
- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;
@end


@implementation DPDiceProfile

/*!
 @brief プロファイル名
 */
- (NSString *) profileName {
    return DPDiceProfileName;
}
#pragma mark - PUT Method
/**
 PUTリクエストを振り分ける.
 */
- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!self.delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    NSLog(@"interface:%@", interface);
    NSLog(@"attribute:%@", attribute);
    if ([attribute isEqualToString:DPDiceProfileAttrOnMagnetometer]
        && interface != nil
        && [interface isEqualToString:DPDiceProfileInterfaceMagnetometer]
        && [self hasMethod:@selector(profile:didReceivePutOnMagnetometerRequest:response:deviceId:sessionKey:) response:response])
    {
        NSString *deviceId = [request deviceId];
        NSString *sessionKey = [request sessionKey];
        send = [_delegate profile:self didReceivePutOnMagnetometerRequest:request response:response
                    deviceId:deviceId sessionKey:sessionKey];
    } else if ([attribute isEqualToString:DPDiceProfileAttrOnDice]
               && interface == nil
               && [self hasMethod:@selector(profile:didReceiveDeleteOnDiceRequest:response:deviceId:sessionKey:) response:response])
    {
        NSString *deviceId = [request deviceId];
        NSString *sessionKey = [request sessionKey];
        send = [_delegate profile:self didReceivePutOnDiceRequest:request response:response
                         deviceId:deviceId sessionKey:sessionKey];
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - DELETE Method
/**
 DELETEメソッドを振り分ける.
 */
- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!self.delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    
    NSString *attribute = [request attribute];
    NSString *interface = [request interface];
    NSLog(@"interface:%@", interface);
    if ([attribute isEqualToString:DPDiceProfileAttrOnMagnetometer]
        && interface != nil
        && [interface isEqualToString:DPDiceProfileInterfaceMagnetometer]
        && [self hasMethod:@selector(profile:didReceiveDeleteOnMagnetometerRequest:response:deviceId:sessionKey:) response:response])
    {
        NSString *deviceId = [request deviceId];
        NSString *sessionKey = [request sessionKey];
        send = [_delegate profile:self didReceiveDeleteOnMagnetometerRequest:request response:response
                
                    deviceId:deviceId sessionKey:sessionKey];
    } else if ([attribute isEqualToString:DPDiceProfileAttrOnDice]
               && interface == nil
               && [self hasMethod:@selector(profile:didReceiveDeleteOnDiceRequest:response:deviceId:sessionKey:) response:response])
    {
        NSString *deviceId = [request deviceId];
        NSString *sessionKey = [request sessionKey];
        send = [_delegate profile:self didReceiveDeleteOnDiceRequest:request response:response
                
                         deviceId:deviceId sessionKey:sessionKey];
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}
#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}
@end
