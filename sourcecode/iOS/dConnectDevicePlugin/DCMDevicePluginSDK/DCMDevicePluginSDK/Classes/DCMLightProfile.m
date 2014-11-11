//
//  DCMLightProfileName.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DCMLightProfile.h"
#import <DConnectSDK/DConnectUtil.h>


NSString *const DCMLightProfileName = @"light";
NSString *const DCMLightProfileInterfaceGroup = @"group";
NSString *const DCMLightProfileAttrCreate = @"create";
NSString *const DCMLightProfileAttrClear = @"clear";
NSString *const DCMLightProfileParamLightId = @"lightId";
NSString *const DCMLightProfileParamName = @"name";
NSString *const DCMLightProfileParamColor = @"color";
NSString *const DCMLightProfileParamBrightness = @"brightness";
NSString *const DCMLightProfileParamFlashing = @"flashing";
NSString *const DCMLightProfileParamLights = @"lights";
NSString *const DCMLightProfileParamOn = @"on";
NSString *const DCMLightProfileParamConfig = @"config";
NSString *const DCMLightProfileParamGroupId = @"groupId";
NSString *const DCMLightProfileParamLightGroups = @"lightGroups";
NSString *const DCMLightProfileParamLightIds = @"lightIds";
NSString *const DCMLightProfileParamGroupName = @"groupName";

@interface DCMLightProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DCMLightProfile

/*
 プロファイル名。
 */
- (NSString *) profileName {
    return DCMLightProfileName;
}

#pragma mark - DConnectProfile Method

/*
 GETリクエストを振り分ける。
 */
- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *profile = [request profile];
    NSString *attribute = [request attribute];
    
    if (profile) {
        if ([profile isEqualToString:DCMLightProfileName]
            && !attribute
            && [self hasMethod:@selector(profile:didReceiveGetLightRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetLightRequest:request response:response deviceId:deviceId];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && attribute
                   && [attribute isEqualToString:DCMLightProfileInterfaceGroup]
                   && [self hasMethod:@selector(profile:didReceiveGetLightRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveGetLightGroupRequest:request response:response deviceId:deviceId];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [response setErrorToNotSupportProfile];
    }
    
    return send;
}

/*
 POSTリクエストを振り分ける。
 */
- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *profile = [request profile];
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    
    if (profile) {
        if ([profile isEqualToString:DCMLightProfileName]
            && !interface
            && !attribute
            && [self hasMethod:@selector(profile:didReceivePostLightRequest:response:deviceId:lightId:brightness:color:flashing:) response:response])
        {
            NSString *lightId = [request stringForKey:DCMLightProfileParamLightId];
            double brightness = 1.0;
            if ([request hasKey:DCMLightProfileParamBrightness]) {
                brightness = [request doubleForKey:DCMLightProfileParamBrightness];
            }
            NSString *color = [request stringForKey:DCMLightProfileParamColor];
            NSArray *flashing = [self parsePattern:[request stringForKey:DCMLightProfileParamFlashing]];
            
            send = [_delegate profile:self didReceivePostLightRequest:request response:response deviceId:deviceId lightId:lightId brightness:brightness color:color flashing:flashing];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && !interface
                   && attribute
                   && [attribute isEqualToString:DCMLightProfileInterfaceGroup]
                   && [self hasMethod:@selector(profile:didReceivePostLightGroupRequest:response:deviceId:groupId:brightness:color:flashing:) response:response])
        {
            NSString *groupId = [request stringForKey:DCMLightProfileParamGroupId];
            double brightness = 1.0;
            if ([request hasKey:DCMLightProfileParamBrightness]) {
                brightness = [request doubleForKey:DCMLightProfileParamBrightness];
            }
            NSString *color = [request stringForKey:DCMLightProfileParamColor];
            NSArray *flashing = [self parsePattern:[request stringForKey:DCMLightProfileParamFlashing]];
            
            send = [_delegate profile:self didReceivePostLightGroupRequest:request response:response deviceId:deviceId groupId:groupId brightness:brightness color:color flashing:flashing];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && interface
                   && attribute
                   && [interface isEqualToString:DCMLightProfileInterfaceGroup]
                   && [attribute isEqualToString:DCMLightProfileAttrCreate]
                   && [self hasMethod:@selector(profile:didReceivePostLightGroupCreateRequest:response:deviceId:lightIds:groupName:) response:response])
        {
            NSString *lightIds = [request stringForKey:DCMLightProfileParamLightIds];
            NSString *groupName = [request stringForKey:DCMLightProfileParamGroupName];
            NSArray *pattern = [self parsePattern:lightIds];
            send = [_delegate profile:self didReceivePostLightGroupCreateRequest:request response:response deviceId:deviceId lightIds:pattern groupName:groupName];
        } else {
            [response setErrorToNotSupportAttribute];
        }
        
    } else {
        [response setErrorToNotSupportProfile];
    }
    
    return send;
}

/*
 PUTリクエストを振り分ける。
 */
- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *profile = [request profile];
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    
    if (profile) {
        if ([profile isEqualToString:DCMLightProfileName]
            && !interface
            && !attribute
            && [self hasMethod:@selector(profile:didReceivePutLightRequest:response:deviceId:lightId:name:brightness:color:flashing:) response:response])
        {
            NSString *lightId = [request stringForKey:DCMLightProfileParamLightId];
            double brightness = 1.0;
            if ([request hasKey:DCMLightProfileParamBrightness]) {
                brightness = [request doubleForKey:DCMLightProfileParamBrightness];
            }
            NSString *name = [request stringForKey:DCMLightProfileParamName];
            NSString *color = [request stringForKey:DCMLightProfileParamColor];
            NSArray *flashing = [self parsePattern:[request stringForKey:DCMLightProfileParamFlashing]];
            
            send = [_delegate profile:self didReceivePutLightRequest:request response:response deviceId:deviceId lightId:lightId name:name brightness:brightness color:color flashing:flashing];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && !interface
                   && attribute
                   && [attribute isEqualToString:DCMLightProfileInterfaceGroup]
                   && [self hasMethod:@selector(profile:didReceivePutLightGroupRequest:response:deviceId:groupId:name:brightness:color:flashing:) response:response])
        {
            NSString *groupId = [request stringForKey:DCMLightProfileParamGroupId];
            double brightness = 1.0;
            if ([request hasKey:DCMLightProfileParamBrightness]) {
                brightness = [request doubleForKey:DCMLightProfileParamBrightness];
            }
            NSString *name = [request stringForKey:DCMLightProfileParamName];
            NSString *color = [request stringForKey:DCMLightProfileParamColor];
            NSArray *flashing = [self parsePattern:[request stringForKey:DCMLightProfileParamFlashing]];
            
            send = [_delegate profile:self didReceivePutLightGroupRequest:request response:response deviceId:deviceId groupId:groupId name:name brightness:brightness color:color flashing:flashing];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [response setErrorToNotSupportProfile];
    }
    
    return send;
}

/*
 DELETEリクエストを振り分ける。
 */
- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *deviceId = [request deviceId];
    NSString *profile = [request profile];
    NSString *interface = [request interface];
    NSString *attribute = [request attribute];
    
    if (profile) {
        if ([profile isEqualToString:DCMLightProfileName]
            && !interface
            && !attribute
            && [self hasMethod:@selector(profile:didReceiveDeleteLightRequest:response:deviceId:lightId:) response:response])
        {
            NSString *lightId = [request stringForKey:DCMLightProfileParamLightId];
            send = [_delegate profile:self didReceiveDeleteLightRequest:request response:response deviceId:deviceId lightId:lightId];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && !interface
                   && attribute
                   && [attribute isEqualToString:DCMLightProfileInterfaceGroup]
                   && [self hasMethod:@selector(profile:didReceiveDeleteLightGroupRequest:response:deviceId:groupId:) response:response])
        {
            NSString *groupId = [request stringForKey:DCMLightProfileParamGroupId];
            send = [_delegate profile:self didReceiveDeleteLightGroupRequest:request response:response deviceId:deviceId groupId:groupId];
        } else if ([profile isEqualToString:DCMLightProfileName]
                   && interface
                   && attribute
                   && [interface isEqualToString:DCMLightProfileInterfaceGroup]
                   && [attribute isEqualToString:DCMLightProfileAttrClear]
                   && [self hasMethod:@selector(profile:didReceiveDeleteLightGroupClearRequest:response:deviceId:groupId:) response:response])
        {
            NSString *groupId = [request stringForKey:DCMLightProfileParamGroupId];
            send = [_delegate profile:self didReceiveDeleteLightGroupClearRequest:request response:response deviceId:deviceId groupId:groupId];
        } else {
            [response setErrorToNotSupportAttribute];
        }
    } else {
        [response setErrorToNotSupportProfile];
    }
    
    return send;
}


#pragma mark - Private Methods


/*
 メソッドが存在するかを確認する。
 */
- (BOOL) hasMethod:(SEL)method
          response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

/*
 flashingをパースする。
 */
- (NSArray *) parsePattern:(NSString *)pattern {
    
    NSMutableArray *result = [NSMutableArray array];
    if (!pattern) {
        return result;  //中身がない場合は長さが0の配列を返す
    }
    
    NSRange range = [pattern rangeOfString:DConnectVibrationProfileVibrationDurationDelim];
    if (range.location != NSNotFound) {
        NSArray *times = [pattern componentsSeparatedByString:DConnectVibrationProfileVibrationDurationDelim];
        for (NSString *time in times) {
            NSString *valueStr = [time stringByTrimmingCharactersInSet:
                                  [NSCharacterSet whitespaceAndNewlineCharacterSet]];
            if (valueStr.length == 0) {
                if (result.count != times.count - 1) {
                    // 数値の間にスペースがある場合はフォーマットエラー
                    // ex. 100, , 100
                    [result removeAllObjects];
                }
                break;
            }
            [result addObject:valueStr];
        }
        
        if (result.count == 0) {
            [result removeAllObjects];
        }
    } else {
        [result addObject:pattern];
    }
    
    return result;
}
@end
