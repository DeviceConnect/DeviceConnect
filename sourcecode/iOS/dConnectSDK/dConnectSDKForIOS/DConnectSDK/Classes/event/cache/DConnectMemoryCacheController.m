//
//  DConnectMemoryCacheController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMemoryCacheController.h"

NSString *const DConnectMemoryCacheControllerEmptyDeviceId = @"";

@interface DConnectMemoryCacheController()

- (NSString *) deviceIdByCheckingDeviceIdIsEmpty:(NSString *)deviceId;

@end
@implementation DConnectMemoryCacheController

#pragma mark - init

- (id) init {
    
    self = [super init];
    
    if (self) {
        _eventMap = [NSMutableDictionary dictionary];
    }
    
    return self;
}

#pragma mark - DConnectEventCacheController

- (DConnectEventError) addEvent:(DConnectEvent *)event {
    
    if (![self checkParameterOfEvent:event]) {
        return DConnectEventErrorInvalidParameter;
    }
    
    DC_SYNC_START(self);
    
    NSString *deviceId = [self deviceIdByCheckingDeviceIdIsEmpty:event.deviceId];
    NSMutableDictionary *events = [_eventMap objectForKey:deviceId];
    
    if (!events) {
        events = [NSMutableDictionary dictionary];
        [_eventMap setObject:events forKey:deviceId];
    }
    
    NSMutableString *path = [NSMutableString stringWithString:event.profile];
    if (event.interface) {
        [path appendString:event.interface];
    }
    [path appendString:event.attribute];
    
    NSMutableArray *eventList = [events objectForKey:path];
    if (!eventList) {
        eventList = [NSMutableArray array];
        [events setObject:eventList forKey:path];
    }
    
    for (DConnectEvent *e in eventList) {
        if ([e isEqual:event]) {
            e.accessToken = event.accessToken;
            e.updateDate = [NSDate date];
            return DConnectEventErrorNone;
        }
    }
    event.createDate = [NSDate date];
    event.updateDate = [NSDate date];
    [eventList addObject:event];
    
    DC_SYNC_END;
    
    return DConnectEventErrorNone;
}

- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey {
    
    for (NSMutableDictionary *events in _eventMap.allValues) {
        for (NSMutableArray *eventList in events.allValues) {
            NSMutableArray *removes = [NSMutableArray array];
            for (DConnectEvent *event in eventList) {
                if ([event.sessionKey isEqualToString:sessionKey]) {
                    [removes addObject:event];
                }
            }
            if (removes.count != 0) {
                [eventList removeObjectsInArray:removes];
            }
        }
    }
    
    return YES;
}

- (BOOL) removeAll {
    [_eventMap removeAllObjects];
    return _eventMap.count == 0;
}

- (DConnectEventError) removeEvent:(DConnectEvent *)event {
    
    if (![self checkParameterOfEvent:event]) {
        return DConnectEventErrorInvalidParameter;
    }
    
    DC_SYNC_START(self);
    
    NSString *deviceId = [self deviceIdByCheckingDeviceIdIsEmpty:event.deviceId];
    NSMutableDictionary *events = [_eventMap objectForKey:deviceId];
    
    if (!events) {
        return DConnectEventErrorNotFound;
    }
    
    NSMutableString *path = [NSMutableString stringWithString:event.profile];
    if (event.interface) {
        [path appendString:event.interface];
    }
    [path appendString:event.attribute];
    
    NSMutableArray *eventList = [events objectForKey:path];
    if (!eventList) {
        return DConnectEventErrorNotFound;
    }
    
    for (DConnectEvent *e in eventList) {
        if ([e isEqual:event]) {
            [eventList removeObject:e];
            if (eventList.count == 0) {
                [events removeObjectForKey:path];
            }
            return DConnectEventErrorNone;
        }
    }
    
    DC_SYNC_END;
    
    return DConnectEventErrorNotFound;
}

- (NSArray *) eventsForDeviceId:(NSString *)deviceId
                        profile:(NSString *)profile
                      interface:(NSString *)interface
                      attribute:(NSString *)attribute
{
    
    DC_SYNC_START(self);
    
    deviceId = [self deviceIdByCheckingDeviceIdIsEmpty:deviceId];
    NSMutableDictionary *events = [_eventMap objectForKey:deviceId];
    
    if (!events) {
        return [NSArray array];
    }
    
    NSMutableString *path = [NSMutableString stringWithString:profile];
    if (interface) {
        [path appendString:interface];
        
    }
    [path appendString:attribute];
    
    NSMutableArray *eventList = [events objectForKey:path];
    if (!eventList) {
        return [NSArray array];
    }
    
    return eventList;
    DC_SYNC_END;
}

- (DConnectEvent *) eventForDeviceId:(NSString *)deviceId profile:(NSString *)profile
                           interface:(NSString *)interface attribute:(NSString *)attribute
                          sessionKey:(NSString *)sessionKey
{
    DConnectEvent *event = nil;
    DC_SYNC_START(self);
    
    do {
        NSArray *eventList = [self eventsForDeviceId:deviceId
                                             profile:profile
                                           interface:interface
                                           attribute:attribute];
        
        for (DConnectEvent *e in eventList) {
            if ([event.sessionKey isEqualToString:sessionKey]) {
                event = e;
                break;
            }
        }
    } while (false);
    
    DC_SYNC_END;
    
    return event;
}

- (void) setEventMap:(NSMutableDictionary *)eventMap {
    // 常に正常に動かすため、nilは入れさせない。
    if (eventMap) {
        _eventMap = eventMap;
    }
}

#pragma mark - private

- (NSString *) deviceIdByCheckingDeviceIdIsEmpty:(NSString *)deviceId
{
    
    if (!deviceId || deviceId.length == 0) {
        deviceId = DConnectMemoryCacheControllerEmptyDeviceId;
    }
    
    return deviceId;
}

@end
