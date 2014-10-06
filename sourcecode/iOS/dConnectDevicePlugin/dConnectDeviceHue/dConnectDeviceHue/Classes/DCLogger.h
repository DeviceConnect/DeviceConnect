//
//  DCLogger.h
//  dConnectDeviceHue
//
//  Created by DConnect05 on 2014/08/19.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DCLogger : NSObject

- (id)initWithSourceClass:(NSObject *)sourceClass;

- (void)entering:(NSString *)sourceMethod param:(NSObject *)param;
+ (void)entering:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param;

- (void)fine:(NSString *)sourceMethod param:(NSObject *)param;
+ (void)fine:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param;

- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
 paramString:(NSString *)paramString;

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
 paramString:(NSString *)paramString;


- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
    paramInt:(int)paramInt;

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
    paramInt:(int)paramInt;


- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
       paramUint:(NSUInteger)paramUint;

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
       paramUint:(NSUInteger)paramUint;


- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
 paramNumber:(NSNumber *)paramNumber;

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
 paramNumber:(NSNumber *)paramNumber;

- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
 paramDouble:(double)paramDouble;

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
 paramDouble:(double)paramDouble;


- (void)exiting:(NSString *)sourceMethod param:(NSObject *)param;
+ (void)exiting:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param;



@end
