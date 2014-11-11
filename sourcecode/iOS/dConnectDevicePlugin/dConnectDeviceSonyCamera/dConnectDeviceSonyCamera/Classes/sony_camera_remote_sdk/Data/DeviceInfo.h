//
//  DeviceInfo.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>

@interface DeviceInfo : NSObject

- (void) setFriendlyName :(NSString*) friendlyName;

- (NSString*) getFriendlyName;

- (void) setVersion :(NSString*) version;

- (NSString*) getVersion;

- (void) addService : (NSString*) serviceName : (NSString*) serviceUrl;

- (NSString*) findActionListUrl : (NSString*) service;

@end
