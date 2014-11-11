//
//  AccessToken.h
//  dConnectDeviceTest
//
//  Created by Masaru Takano on 2014/09/02.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AccessToken : NSObject

@property (nonatomic) NSString *token;
@property (nonatomic) NSMutableDictionary *expirePeriods;
@property (nonatomic) NSString *signature;

- (id) initWithResponse:(NSDictionary*)response;

@end
