//
//  DeviceList.m
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import "DeviceList.h"

static NSMutableArray* _deviceListArray = nil;
static long _selectedPosition;

@implementation DeviceList

+ (void) addDevice : (DeviceInfo*) device
{
    if(_deviceListArray == NULL || _deviceListArray.count == 0)
    {
        _deviceListArray = [[NSMutableArray alloc]init];
    }
    [_deviceListArray addObject:device];
}

+ (void) selectDeviceAt:(long) position
{
    _selectedPosition = position;
}

+ (DeviceInfo*) getDeviceAt:(long) position
{
    if(_deviceListArray != NULL)
    {
        if(position < _deviceListArray.count)
        {
            return [_deviceListArray objectAtIndex:position];
        }
    }
    return NULL;
}

+ (DeviceInfo*) getSelectedDevice
{
    return [self getDeviceAt:_selectedPosition];
}

+ (long) getSize
{
    if(_deviceListArray != NULL)
    {
        return _deviceListArray.count;
    }
    return 0;
}


+ (void) reset
{
    _deviceListArray = [[NSMutableArray alloc]init];
}

@end
