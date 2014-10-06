//
//  DConnectFileCacheController.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/16.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectMemoryCacheController.h>

@interface DConnectFileCacheController : DConnectMemoryCacheController

- (id) initWithClass:(Class)clazz;
- (id) initWithClass:(Class)clazz autoFlush:(BOOL)autoFlush;
- (id) initWithKey:(NSString *)key;
- (id) initWithKey:(NSString *)key autoFlush:(BOOL)autoFlush;

+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz;
+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key;

+ (DConnectFileCacheController *) controllerWithClass:(Class)clazz autoFlush:(BOOL)autoFlush;
+ (DConnectFileCacheController *) controllerWithKey:(NSString *)key autoFlush:(BOOL)autoFlush;

@end
