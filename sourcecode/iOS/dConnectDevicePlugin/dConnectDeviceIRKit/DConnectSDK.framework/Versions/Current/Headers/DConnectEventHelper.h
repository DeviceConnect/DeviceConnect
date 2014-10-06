//
//  DConnectEventHelper.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/31.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>
#import <DConnectSDK/DConnectManager.h>

/*!
 @brief リクエストメッセージに対するレスポンスメッセージを受け取るblocks.
 
 @param response レスポンスメッセージ
 */
typedef void (^DConnectEventHandler)(DConnectMessage *message);

@interface DConnectEventHelper : NSObject

/*!
 
 */
+ (DConnectEventHelper *) sharedHelper;

/*!
 
 */
- (DConnectResponseMessage *) registerEventWithRequest:(DConnectRequestMessage *)request
                                               handler:(DConnectEventHandler)handler;

- (DConnectResponseMessage *) unregisterEventWithRequest:(DConnectRequestMessage *)request;

@end
