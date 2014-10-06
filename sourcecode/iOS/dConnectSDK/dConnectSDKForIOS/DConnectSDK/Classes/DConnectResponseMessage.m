//
//  DConnectResponseMessage.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectResponseMessage.h"

@implementation DConnectResponseMessage

#pragma mark - init
- (id) init {
    self = [super init];
    if (self) {
        _code = [[NSUUID UUID] UUIDString];
    }
    return self;
}

#pragma mark - Result

- (void) setResult:(DConnectMessageResultType)result {
    [self setInteger:result forKey:DConnectMessageResult];
}

- (DConnectMessageResultType) result {
    return [self integerForKey:DConnectMessageResult];
}

- (DConnectMessageErrorCodeType) errorCode {
    return [self integerForKey:DConnectMessageErrorCode];
}

#pragma mark - Error

- (void) setError:(DConnectMessageErrorCodeType)error message:(NSString *)message {
    [self setResult:DConnectMessageResultTypeError];
    [self setInteger:error forKey:DConnectMessageErrorCode];
    [self setString:message forKey:DConnectMessageErrorMessage];
}

- (void) setErrorToUnknown {
    [self setErrorToUnknownWithMessage:@"Unknown error was encountered."];
}

- (void) setErrorToUnknownWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeUnknown message:message];
}

- (void) setErrorToNotSupportProfile {
    [self setErrorToNotSupportProfileWithMessage:@"Non-supported Profile was accessed."];
}

- (void) setErrorToNotSupportProfileWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeNotSupportProfile message:message];
}

- (void) setErrorToNotSupportAction {
    [self setErrorToNotSupportActionWithMessage:@"Non-supported HTTP method was used."];
}

- (void) setErrorToNotSupportActionWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeNotSupportAction message:message];
}

- (void) setErrorToNotSupportAttribute {
    [self setErrorToNotSupportAttributeWithMessage:@"Non-supported Attribute was accessed."];
}

- (void) setErrorToNotSupportAttributeWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeNotSupportAttribute message:message];
}

- (void) setErrorToEmptyDeviceId {
    [self setErrorToEmptyDeviceIdWithMessage:@"Device ID is required."];
}

- (void) setErrorToEmptyDeviceIdWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeEmptyDeviceId message:message];
}

- (void) setErrorToNotFoundDevice {
    [self setErrorToNotFoundDeviceWithMessage:@"Device was not found."];
}

- (void) setErrorToNotFoundDeviceWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeNotFoundDevice message:message];
}

- (void) setErrorToTimeout {
    [self setErrorToTimeoutWithMessage:@"Response timeout."];
}

- (void) setErrorToTimeoutWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeTimeout message:message];
}

- (void) setErrorToUnknownAttribute {
    [self setErrorToUnknownAttributeWithMessage:@"Illegal or nonexistent attribute or interface was accessed."];
}

- (void) setErrorToUnknownAttributeWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeUnknownAttribute message:message];
}

- (void) setErrorToLowBattery {
    [self setErrorToLowBatteryWithMessage:@"No enough battery to control the device."];
}

- (void) setErrorToLowBatteryWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeLowBattery message:message];
}

- (void) setErrorToInvalidRequestParameter {
    [self setErrorToInvalidRequestParameterWithMessage:@"Request parameters are invalid."];
}

- (void) setErrorToInvalidRequestParameterWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeInvalidRequestParameter message:message];
}

- (void) setErrorToAuthorization {
    [self setErrorToAuthorizationWithMessage:@"Authorization error."];
}

- (void) setErrorToAuthorizationWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeAuthorization message:message];
}

- (void) setErrorToExpiredAccessToken {
    [self setErrorToExpiredAccessTokenWithMessage:@"Access token expired."];
}

- (void) setErrorToExpiredAccessTokenWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeExpiredAccessToken message:message];
}

- (void) setErrorToEmptyAccessToken {
    [self setErrorToEmptyAccessTokenWithMessage:@"Access token was required."];
}

- (void) setErrorToEmptyAccessTokenWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeEmptyAccessToken message:message];
}

- (void) setErrorToScope {
    [self setErrorToScopeWithMessage:@"Request is out of scope."];
}

- (void) setErrorToScopeWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeScope message:message];
}

- (void) setErrorToNotFoundClientId {
    [self setErrorToNotFoundClientIdWithMessage:@"clientId was not found."];
}

- (void) setErrorToNotFoundClientIdWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeNotFoundClientId message:message];
}

- (void) setErrorToIllegalDeviceState {
    [self setErrorToIllegalDeviceStateWithMessage:@"State of device is illegality."];
}

- (void) setErrorToIllegalDeviceStateWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeIllegalDeviceState message:message];
}

- (void) setErrorToIllegalServerState {
    [self setErrorToIllegalServerStateWithMessage:@"State of server is illegality."];
}

- (void) setErrorToIllegalServerStateWithMessage:(NSString *)message {
    [self setError:DConnectMessageErrorCodeIllegalServerState message:message];
}

@end
