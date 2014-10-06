//
//  DConnectDBCacheController.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "DConnectBaseCacheController.h"

@interface DConnectDBCacheController : DConnectBaseCacheController

- (id) initWithClass:(Class)clazz;
- (id) initWithKey:(NSString *)key;

+ (DConnectDBCacheController *) controllerWithClass:(Class)clazz;
+ (DConnectDBCacheController *) controllerWithKey:(NSString *)key;

@end
