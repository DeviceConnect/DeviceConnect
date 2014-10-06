//
//  DeviceInfo.m
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import "DeviceInfo.h"

@implementation DeviceInfo
{
    NSString* _friendlyName;
    NSString* _version;
    NSMutableArray* _serviceNameArray;
    NSMutableArray* _serviceURLArray;
}

// set friendly name of the device
- (void) setFriendlyName :(NSString*) friendlyName
{
//    NSLog(@"DeviceInfo setFriendlyName = %@", friendlyName);
    _friendlyName = friendlyName;
}

// get friendly name of the device
- (NSString*) getFriendlyName
{
    return _friendlyName;
}

// set version of the device
- (void) setVersion :(NSString*) version
{
//    NSLog(@"DeviceInfo setVersion = %@", version);
    _version = version;
}

// get version of the device
- (NSString*) getVersion
{
    return _version;
}

// adds service to the list
- (void) addService : (NSString*) serviceName : (NSString*) serviceUrl
{
//    NSLog(@"DeviceInfo addService = %@:%@", serviceName, serviceUrl);
    if(_serviceNameArray == NULL || _serviceNameArray.count == 0)
    {
        _serviceNameArray = [[NSMutableArray alloc] init];
    }
    if(_serviceURLArray == NULL || _serviceURLArray.count == 0)
    {
        _serviceURLArray = [[NSMutableArray alloc] init];
    }
    [_serviceNameArray addObject:serviceName];
    [_serviceURLArray addObject:[serviceUrl stringByAppendingFormat:@"/%@",serviceName]];
//    NSLog(@"DeviceInfo _serviceNameArray size = %lu", (unsigned long)_serviceNameArray.count);
}

// finds the ActionListURL for a given service
- (NSString*) findActionListUrl : (NSString*) service
{
    long index = [_serviceNameArray indexOfObject:service];
    if(index >= 0)
    {
        return [_serviceURLArray objectAtIndex:index];
    }
    return @"Not found";
}

@end
