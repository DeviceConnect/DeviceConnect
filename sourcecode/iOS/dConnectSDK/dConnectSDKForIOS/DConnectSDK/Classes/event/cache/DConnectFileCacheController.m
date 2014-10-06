//
//  DConnectFileCacheController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectFileCacheController.h"

NSString *const DConnectFileCacheControllerFileName = @"com_nttdocomo_dconnect_event_cache.dat";

@interface DConnectFileCacheController() {
    NSString *_key;
    BOOL _autoFlush;
    NSString *_filePath;
}

- (void) load;

@end

@implementation DConnectFileCacheController

#pragma mark - init

- (id) init {
    @throw @"Not support default init method.";
}

- (id) initWithClass:(Class)clazz {
    return [self initWithClass:clazz autoFlush:NO];
}

- (id) initWithClass:(Class)clazz autoFlush:(BOOL)autoFlush {
    NSString *key = NSStringFromClass(clazz);
    return [self initWithKey:key autoFlush:autoFlush];
}

- (id) initWithKey:(NSString *)key {
    return [self initWithKey:key autoFlush:NO];
}

- (id) initWithKey:(NSString *)key autoFlush:(BOOL)autoFlush {
    
    if (!key) {
        @throw @"Key must not be nil.";
    }
    
    self = [super init];
    
    if (self) {
        _key = key;
        _autoFlush = autoFlush;
        NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory,
                                                             NSUserDomainMask, YES);
        NSString *path = [paths objectAtIndex:0];
        _filePath = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"%@_%@",
                                                          key, DConnectFileCacheControllerFileName]];
        [self load];
    }
    
    return self;
}

#pragma mark - DConnectEventCacheController
- (void) flush {
    DC_SYNC_START(self);
    [NSKeyedArchiver archiveRootObject:self.eventMap toFile:_filePath];
    DC_SYNC_END;
}

- (DConnectEventError) addEvent:(DConnectEvent *)event {
    DC_SYNC_START(self);
    DConnectEventError error = [super addEvent:event];
    
    if (error == DConnectEventErrorNone && _autoFlush) {
        [self flush];
    }
    
    return error;
    DC_SYNC_END;
}

- (DConnectEventError) removeEvent:(DConnectEvent *)event {
    DC_SYNC_START(self);
    DConnectEventError error = [super removeEvent:event];
    
    if (error == DConnectEventErrorNone && _autoFlush) {
        [self flush];
    }
    
    return error;
    DC_SYNC_END;
}

- (BOOL) removeAll {
    DC_SYNC_START(self);
    BOOL result = [super removeAll];
    if (_autoFlush) {
        [self flush];
    }
    return result;
    DC_SYNC_END;
}

- (BOOL) removeEventsForSessionKey:(NSString *)sessionKey {
    DC_SYNC_START(self);
    BOOL result = [super removeEventsForSessionKey:sessionKey];
    if (_autoFlush) {
        [self flush];
    }
    return result;
    DC_SYNC_END;
}

#pragma mark - private
- (void) load {
    NSMutableDictionary *dic = [NSKeyedUnarchiver unarchiveObjectWithFile:_filePath];
    self.eventMap = dic;
}


#pragma mark - static

+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz {
    return [self controllerWithClass:clazz autoFlush:NO];
}

+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz autoFlush:(BOOL)autoFlush {
    NSString *key = NSStringFromClass(clazz);
    return [self controllerWithKey:key autoFlush:autoFlush];
}

+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key {
    return [self controllerWithKey:key autoFlush:NO];
}

+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key autoFlush:(BOOL)autoFlush {
    DConnectFileCacheController *controller
    = [[DConnectFileCacheController alloc] initWithKey:key autoFlush:autoFlush];
    return controller;
}


@end
