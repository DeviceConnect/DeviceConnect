//
//  DConnectVibrationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectVibrationProfile.h"

NSString *const DConnectVibrationProfileName = @"vibration";
NSString *const DConnectVibrationProfileAttrVibrate = @"vibrate";
NSString *const DConnectVibrationProfileParamPattern = @"pattern";

NSString *const DConnectVibrationProfileVibrationDurationDelim = @",";
const long long DConnectVibrationProfileDefaultMaxVibrationTime = 500;

@interface DConnectVibrationProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

- (BOOL) isNumberString:(NSString *)str;

@end

@implementation DConnectVibrationProfile

#pragma mark - init Methods
- (id) init {
    self = [super init];
    if (self) {
        self.maxVibrationTime = DConnectVibrationProfileDefaultMaxVibrationTime;
    }
    return self;
}

#pragma mark - DConnect Profile Methods
- (NSString *) profileName {
    return DConnectVibrationProfileName;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    
    if ([attribute isEqualToString:DConnectVibrationProfileAttrVibrate]) {
        if ([self hasMethod:@selector(profile:didReceivePutVibrateRequest:response:deviceId:pattern:) response:response])
        {
            NSString *patternStr = [DConnectVibrationProfile patternFromRequest:request];
            NSArray *pattern = [self parsePattern:patternStr];
            send = [_delegate profile:self didReceivePutVibrateRequest:request response:response
                             deviceId:[request deviceId] pattern:pattern];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    
    if ([attribute isEqualToString:DConnectVibrationProfileAttrVibrate]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteVibrateRequest:response:deviceId:) response:response])
        {
            send = [_delegate profile:self didReceiveDeleteVibrateRequest:request response:response
                             deviceId:[request deviceId]];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Getter

+ (NSString *) patternFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectVibrationProfileParamPattern];
}

#pragma mark - Utility

- (NSArray *) parsePattern:(NSString *)pattern {

    NSMutableArray *result = [NSMutableArray array];
    if (!pattern || pattern.length == 0) {
        [result addObject:[NSNumber numberWithLongLong:self.maxVibrationTime]];
        return result;
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
            } else if (![self isNumberString:valueStr]) {
                [result removeAllObjects];
                break;
            }
            
            long long value = [valueStr longLongValue];
            [result addObject:[NSNumber numberWithLongLong:value]];
        }
        
        // 解析に失敗した場合には、nilを返却する
        if (result.count == 0) {
            result = nil;
        }
    } else if ([self isNumberString:pattern]) {
        NSNumber *time = [NSNumber numberWithLongLong:[pattern longLongValue]];
        [result addObject:time];
    }
    
    if (result.count == 0) {
        return nil;
    }
    
    return result;
}

#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

- (BOOL) isNumberString:(NSString *)str {
    NSCharacterSet *digitCharSet = [NSCharacterSet characterSetWithCharactersInString:@"0123456789"];
    NSScanner *aScanner = [NSScanner localizedScannerWithString:str];
    [aScanner setCharactersToBeSkipped:nil];
    [aScanner scanCharactersFromSet:digitCharSet intoString:NULL];
    return [aScanner isAtEnd];
}

@end
