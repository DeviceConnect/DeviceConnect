//
//  DConnectBaseCacheController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectBaseCacheController.h"

@implementation DConnectBaseCacheController

- (BOOL) checkParameterOfEvent:(DConnectEvent *)event {
    
    if (event == nil
        || event.profile == nil
        || event.attribute == nil
        || event.sessionKey == nil)
    {
        return NO;
    }
    
    return YES;
}

#pragma mark - DConnectEventCacheController

- (DConnectEventError) addEvent:(DConnectEvent *)event {
    return DConnectEventErrorFailed;
}

- (DConnectEventError) removeEvent:(DConnectEvent *)event {
    return DConnectEventErrorFailed;
}

- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey {
    return NO;
}

- (BOOL) removeAll {
    return NO;
}


- (DConnectEvent *) eventForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                           interface:(NSString *)interface attribute:(NSString *)attribute
                          sessionKey:(NSString *)sessionKey
{
    return nil;
}

- (NSArray *) eventsForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                      interface:(NSString *)interface attribute:(NSString *)attribute
{
    return nil;
}

- (void) flush {
    // do nothing.
}


@end
