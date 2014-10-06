//
//  DConnectURIBuilder.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/29.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DConnectURIBuilder : NSObject

@property (nonatomic, strong) NSString *scheme;
@property (nonatomic, strong) NSString *host;
@property (nonatomic) int port;
@property (nonatomic, strong) NSString *profile;
@property (nonatomic, strong) NSString *interface;
@property (nonatomic, strong) NSString *attribute;
@property (nonatomic, strong) NSMutableDictionary *params;

- (NSURL *) build;

- (void) addParameter:(NSString *)parameter forName:(NSString *)name;

@end
