//
//  DCLogger.m
//  dConnectDeviceHue
//
//  Created by DConnect05 on 2014/08/19.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DCLogger.h"

@implementation DCLogger

NSString * const SPACE_PAD = @"           ";
NSString * mSourceClassName;

//======================================================================
- (id)init{
    self = [super init];
    
    mSourceClassName = @"";
    
    return self;
}


- (id)initWithSourceClass:(NSObject *)sourceClass{
    self = [super init];
    
    if (sourceClass != nil) {
        mSourceClassName = sourceClass.description;
    }else{
        mSourceClassName = @"";
        
    }
    
    return self;
}

//======================================================================
- (void)entering:(NSString *)methodName param:(NSObject *)param
{
    [DCLogger entering:mSourceClassName methodName:methodName param:param];
}

+ (void)entering:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param
{
    
    NSLog(@"■ ===================");
    
    NSString *msg = [self getMsg:sourceClassName methodName:methodName param:param];

    NSLog(@"■ entering \n%@", msg);
    
}

//======================================================================
- (void)fine:(NSString *)methodName param:(NSObject *)param
{
    [DCLogger fine:mSourceClassName methodName:methodName param:param];
}

+ (void)fine:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param
{
    
    NSString *msg = [self getMsg:sourceClassName methodName:methodName param:param];
    
    NSLog(@"●  fine    \n%@", msg);
    
}

//======================================================================
- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
    paramString:(NSString *)paramString
{
    [DCLogger fine:mSourceClassName methodName:methodName paramName:paramName paramString:paramString];
}

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
    paramString:(NSString *)paramString
{
    NSString *msg = [NSString stringWithFormat:@"%@ = %@" ,paramName ,paramString];
    
    [DCLogger fine:mSourceClassName methodName:methodName param:msg];
    
}

//======================================================================
- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
   paramInt:(int)paramInt
{
    [DCLogger fine:mSourceClassName methodName:methodName paramName:paramName paramInt:paramInt];
}

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
   paramInt:(int)paramInt
{
    NSString *msg = [NSString stringWithFormat:@"%@ = %d" ,paramName ,paramInt];
    
    [DCLogger fine:mSourceClassName methodName:methodName param:msg];
    
}

//======================================================================
- (void)fine:(NSString *)methodName
 paramName:(NSString *)paramName
       paramUint:(NSUInteger)paramUint
{
    [DCLogger fine:mSourceClassName methodName:methodName paramName:paramName paramUint:paramUint];
}

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
 paramName:(NSString *)paramName
   paramUint:(NSUInteger)paramUint
{
    NSString *msg = [NSString stringWithFormat:@"%@ = %lu" ,paramName ,(unsigned long)paramUint];
    
    [DCLogger fine:mSourceClassName methodName:methodName param:msg];
    
}

//======================================================================
- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
    paramDouble:(double)paramDouble
{
    [DCLogger fine:mSourceClassName methodName:methodName paramName:paramName paramDouble:paramDouble];
}

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
paramDouble:(double)paramDouble
{
    NSString *msg = [NSString stringWithFormat:@"%@ = %f" ,paramName ,paramDouble];
    
    [DCLogger fine:mSourceClassName methodName:methodName param:msg];
    
}

//======================================================================

- (void)fine:(NSString *)methodName
   paramName:(NSString *)paramName
   paramNumber:(NSNumber *)paramNumber
{
    [DCLogger fine:mSourceClassName methodName:methodName paramName:paramName paramNumber:paramNumber];
}

+ (void)fine:(NSString *)sourceClassName
  methodName:(NSString *)methodName
   paramName:(NSString *)paramName
   paramNumber:(NSNumber *)paramNumber
{
    NSString *msg = [NSString stringWithFormat:@"%@ = %@" ,paramName ,[paramNumber stringValue]];
    
    [DCLogger fine:mSourceClassName methodName:methodName param:msg];
    
}

//======================================================================
- (void)exiting:(NSString *)methodName param:(NSObject *)param
{
    [DCLogger exiting:mSourceClassName methodName:methodName param:param];
}

+ (void)exiting:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param
{
    
    NSString *msg = [self getMsg:sourceClassName methodName:methodName param:param];
    
    NSLog(@"□  exiting \n%@", msg);
    
}

//======================================================================

+ (NSString *)getMsg:(NSString *)sourceClassName methodName:(NSString *)methodName param:(NSObject *)param
{
    NSString * paramString = @"";
    
    if (param != nil) {
        
        if ([param isKindOfClass:[NSString class]]) {
            paramString = (NSString*)param;
        } else {
            paramString = param.description;
        }
        
        paramString = param.description;
    }
    
    NSString *  msg =  [NSString stringWithFormat:@"%@%@. %@",SPACE_PAD ,sourceClassName ,methodName];
    
    if (paramString.length != 0) {
        msg = [NSString stringWithFormat:@"%@\n%@%@",msg , SPACE_PAD ,paramString];        
    }
    
    return msg;
    
}


@end
