//
//  DConnectMemoryCacheController.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/16.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectBaseCacheController.h>

@interface DConnectMemoryCacheController : DConnectBaseCacheController

@property (nonatomic, strong) NSMutableDictionary *eventMap;

@end
