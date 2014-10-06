//
//  DConnectBaseCacheController.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectEventCacheController.h>

@interface DConnectBaseCacheController : NSObject<DConnectEventCacheController>

- (BOOL) checkParameterOfEvent:(DConnectEvent *)event;

@end
