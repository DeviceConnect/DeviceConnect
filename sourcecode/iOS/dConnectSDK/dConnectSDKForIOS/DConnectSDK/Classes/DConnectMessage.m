//
//  DConnectMessage.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMessage+Private.h"
#import "NSString+Extension.h"

NSString *const DConnectMessageAction = @"action";
NSString *const DConnectMessageDeviceId = @"deviceId";
NSString *const DConnectMessagePluginId = @"pluginId";
NSString *const DConnectMessageProfile = @"profile";
NSString *const DConnectMessageInterface = @"interface";
NSString *const DConnectMessageAttribute = @"attribute";
NSString *const DConnectMessageSessionKey = @"sessionKey";
NSString *const DConnectMessageAccessToken = @"accessToken";
NSString *const DConnectMessageAPI = @"api";

NSString *const DConnectMessageResult = @"result";
NSString *const DConnectMessageErrorCode = @"errorCode";
NSString *const DConnectMessageErrorMessage = @"errorMessage";

NSString *const DConnectMessageDefaultAPI = @"gotapi";


@interface DConnectArray ()
@property (nonatomic) NSMutableArray *array;
@end

@interface DConnectMessage ()
@property (nonatomic) NSMutableDictionary *dictionary;

- (NSArray *) arrayByRemovingNotJSONObject:(NSArray *)array;
- (NSDictionary *) dictionaryByRemovingNotJsonObject:(NSDictionary *)dic;
- (id) JSONObjectForObject:(id)object;
@end


@implementation DConnectArray

- (id) init {
    self = [super init];
    if (self) {
        self.array = [NSMutableArray array];
    }
    return self;
}

+ (instancetype) initWithArray:(NSArray *)array {
    DConnectArray *dcArray = [self alloc];
    if (dcArray) {
        dcArray.array = array.mutableCopy;
    }
    return dcArray;
}


#pragma mark - NSFastEnumeration

-(NSUInteger)countByEnumeratingWithState:(NSFastEnumerationState *)state objects:(__unsafe_unretained id [])buffer count:(NSUInteger)len {
    NSUInteger bufferIndex = 0;
    
    // state->stateは次の列挙位置が格納されている。初回の呼び出し時には0。
    NSUInteger listIndex = state->state;
    // 列挙したいリストの長さを設定します. ここではこのクラス自体がArrayではないので、_arrayの長さを設定
    NSUInteger listLength = _array.count;
    
    // リストのサイズ分までbufferにオブジェクトを格納する.
    while (bufferIndex < len)
    {
        // ただし、保持しているリストを全て列挙できたら終了する。
        // かならずしも全部の列挙が、１回で完了するわけではないので、whileだけではぴったり終われない。
        if (listIndex >= listLength) {
            break;
        }
        
        // バッファに実際に渡したいオブジェクトを格納する.
        // 実際にはどれが入っているかわからないので、arrayに格納されているclassをみて返すオブジェクトを変える必要がある.
        NSObject* obj = [_array objectAtIndex:listIndex];
        // 各プリミティブに関してはNSNumberでのみ返す.
        if([obj isKindOfClass:[NSNumber class]]) {
            buffer[bufferIndex] = obj;
        }else if([obj isKindOfClass:[NSDictionary class]]) {
            buffer[bufferIndex] = [self messageAtIndex:listIndex];
        }else if([obj isKindOfClass:[NSString class]]) {
            buffer[bufferIndex] = (NSString*)obj;
        }else if([obj isKindOfClass:[NSData class]]) {
            buffer[bufferIndex] = (NSData*)obj;
        }else {
            buffer[bufferIndex] = [NSNull null];
        }
        bufferIndex++;
        listIndex++;
        
    }
    
    // このデリゲートは何回も呼び出される可能性がある（buffer/lenのサイズが16らしい)ので現在の状態をstate構造体に保存する.
    state->state = listIndex;
    state->itemsPtr = buffer;
    state->mutationsPtr = (unsigned long*)(__bridge void*)self;
    
    // この呼出で列挙できた数を返す.
    return bufferIndex;
    
}

- (void) addInteger:(int)num {
    [self.array addObject:[NSNumber numberWithInt:num]];
}

- (void) addLong:(long)num {
    [self.array addObject:[NSNumber numberWithLong:num]];
}

- (void) addFloat:(float)num {
    [self.array addObject:[NSNumber numberWithFloat:num]];
}

- (void) addDouble:(double)num {
    [self.array addObject:[NSNumber numberWithDouble:num]];
}

- (void) addData:(NSData *)data {
    [self.array addObject:data];
}

- (void) addString:(NSString *)string {
    [self.array addObject:string];
}

- (void) addMessage:(DConnectMessage *)message {
    [self.array addObject:message];
}

- (void) addArray:(DConnectArray *)array {
    [self.array addObject:array];
}

- (void) addNumber:(NSNumber *)number {
    [self.array addObject:number];
}

- (int) integerAtIndex:(NSUInteger)index {
    NSNumber *num = [self numberAtIndex:index];
    if (num) {
        return [num intValue];
    }
    return INT_MIN;
}

- (long) longAtIndex:(NSUInteger)index {
    NSNumber *num = [self numberAtIndex:index];
    if (num) {
        return [num longValue];
    }
    return LONG_MIN;
}

- (float) floatAtIndex:(NSUInteger)index {
    NSNumber *num = [self numberAtIndex:index];
    if (num) {
        return [num floatValue];
    }
    return FLT_MIN;
}

- (double) doubleAtIndex:(NSUInteger)index {
    NSNumber *num = [self numberAtIndex:index];
    if (num) {
        return [num doubleValue];
    }
    return DBL_MIN;
}

- (NSData *) dataAtIndex:(NSUInteger)index {
    return (NSData *) [self.array objectAtIndex:index];
}

- (NSString *) stringAtIndex:(NSUInteger)index {
    return (NSString *) [self.array objectAtIndex:index];
}

- (NSNumber *) numberAtIndex:(NSUInteger)index {
    id value = [self.array objectAtIndex:index];
    
    if ([value isKindOfClass:[NSString class]]) {
        return [NSNumber numberWithDouble:[((NSString *) value) doubleValue]];
    } else {
        return (NSNumber *) value;
    }
}

- (DConnectMessage *) messageAtIndex:(NSUInteger)index {
    return (DConnectMessage *) [self.array objectAtIndex:index];
}

- (DConnectArray *) arrayAtIndex:(NSUInteger)index {
    return (DConnectArray *) [self.array objectAtIndex:index];
}

- (id) objectAtIndex:(NSUInteger)index {
    return [self.array objectAtIndex:index];
}

- (unsigned int) count {
    return (unsigned int) self.array.count;
}

- (BOOL) containsObject:(id)obj {
    return [self.array containsObject:obj];
}

- (id) copyWithZone:(NSZone *)zone {
    DConnectArray *array = [[[self class] allocWithZone:zone] init];
    array.array = [[NSMutableArray allocWithZone:zone] initWithArray:self.array copyItems:YES];
    return array;
}

+ (instancetype) array {
    return [self new];
}

- (NSArray *) internalArray {
    return _array;
}

@end

@implementation DConnectMessage

- (id) init {
    self = [super init];
    if (self) {
        self.dictionary = [NSMutableDictionary dictionary];
    }
    return self;
}

+ (instancetype) initWithDictionary:(NSDictionary *)dict {
    DConnectMessage *dcMessage = [self alloc];
    if (dcMessage) {
        dcMessage.dictionary = dict.mutableCopy;
    }
    return dcMessage;
}

// TODO: Dictionaryへのインサートでnilチェックが必要かの検討

- (void) setInteger:(int)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithInt:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setLong:(long)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithLong:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setLongLong:(long long)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithLongLong:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setFloat:(float)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithFloat:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setDouble:(double)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithDouble:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setBool:(BOOL)num forKey:(NSString *)aKey {
    NSNumber *value = [NSNumber numberWithBool:num];
    if (value) {
        [self.dictionary setObject:value forKey:aKey];
    }
}

- (void) setData:(NSData *)data forKey:(NSString *)aKey {
    [self.dictionary setObject:data forKey:aKey];
}

- (void) setString:(NSString *)string forKey:(NSString *)aKey {
    [self.dictionary setObject:string forKey:aKey];
}

- (void) setArray:(DConnectArray *)array forKey:(NSString *)aKey {
    [self.dictionary setObject:array forKey:aKey];
}

- (void) setMessage:(DConnectMessage *)message forKey:(NSString *)aKey {
    [self.dictionary setObject:message forKey:aKey];
}

- (void) setNumber:(NSNumber *)number forKey:(NSString *)aKey {
    [self.dictionary setObject:number forKey:aKey];
}

- (int) integerForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num intValue];
    }
    return INT_MIN;
}

- (long) longForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num longValue];
    }
    return LONG_MIN;
}

- (long long) longLongForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num longLongValue];
    }
    return LONG_LONG_MIN;
}

- (float) floatForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num floatValue];
    }
    return FLT_MIN;
}

- (double) doubleForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num doubleValue];
    }
    return DBL_MIN;
}

- (BOOL) boolForKey:(NSString *)aKey {
    NSNumber *num = [self numberForKey:aKey];
    if (num) {
        return [num boolValue];
    }
    return NO;
}

- (NSData *) dataForKey:(NSString *)aKey {
    id data = [self.dictionary objectForKey:aKey];
    if ([data isKindOfClass:[NSData class]]) {
        // ファイルデータの場合は解析時にNSDataにしてある。
        
        return data;
    } else if ([data isKindOfClass:[NSString class]]) {
        // 通常の文字列データの場合は文字列として取り出すので、要求に応じてNSDataに変換する。
        
        return [NSData dataWithBytes:[(NSString*)data UTF8String] length:[(NSString*)data length]];
    }
    return nil;
}

- (NSString *) stringForKey:(NSString *)aKey {
    return (NSString *) [self.dictionary objectForKey:aKey];
}

- (DConnectArray *) arrayForKey:(NSString *)aKey {
    return [self.dictionary objectForKey:aKey];
}

- (DConnectMessage *) messageForKey:(NSString *)aKey {
    return [self.dictionary objectForKey:aKey];
}

- (NSNumber *) numberForKey:(NSString *)aKey {
    id obj = [self.dictionary objectForKey:aKey];
    if ([obj isKindOfClass:[NSString class]]) {
        return [NSNumber numberWithDouble:[((NSString *) obj) doubleValue]];
    } else {
        return (NSNumber *) [self.dictionary objectForKey:aKey];
    }
}

- (id) objectForKey:(NSString *)aKey {
    return [self.dictionary objectForKey:aKey];
}

- (BOOL) hasKey:(NSString *)aKey {
    return [[self.dictionary allKeys] containsObject:aKey];
}

- (id) convertToJSONObject {
    
    NSError *error = nil;
    @try {
        NSDictionary *dic = [self dictionaryByRemovingNotJsonObject:_dictionary];
        NSData *data = [NSJSONSerialization dataWithJSONObject:dic options:0 error:&error];
        if (!error) {
            id json = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
            if (!error) {
                return json;
            }
        }
    }
    @catch (NSException *exception) {
        return nil;
    }
    
    return nil;
}

- (NSString *) convertToJSONString {
    
    NSError *error = nil;
    NSDictionary *dic = [self dictionaryByRemovingNotJsonObject:_dictionary];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:0 error:&error];
    
    if (error) {
        return nil;
    }
    
    // フォワードスラッシュ（/）のエスケープを外す（JSONの仕様によればフォワードスラッシュのエスケープは任意）
    NSMutableString *dataStr = [NSMutableString stringWithUTF8StringAddingNullTermination:[jsonData bytes]
                                                                                   length:[jsonData length]];
    
    [dataStr replaceOccurrencesOfString:@"\\/" withString:@"/" options:0 range:NSMakeRange(0, dataStr.length)];
    return dataStr;
}

- (NSArray *) allKeys {
    return [self.dictionary allKeys];
}

- (id) copyWithZone:(NSZone *)zone {
    DConnectMessage *msg = [[[self class] allocWithZone:zone] init];
    msg.dictionary = [[NSMutableDictionary allocWithZone:zone] initWithDictionary:self.dictionary copyItems:YES];
    return msg;
}

+ (instancetype) message {
    return [self new];
}

- (NSMutableDictionary *) internalDictionary {
    return _dictionary;
}

- (NSDictionary *) dictionaryByRemovingNotJsonObject:(NSDictionary *)dic {
    
    if ([NSJSONSerialization isValidJSONObject:dic]) {
        return dic;
    }
    
    NSMutableDictionary *jsonObject = [NSMutableDictionary dictionary];
    NSArray *keys = [dic allKeys];
    
    for (id key in keys) {
        
        if (![key isKindOfClass:[NSString class]]) {
            continue;
        }
        
        id value = [dic objectForKey:key];
        id obj = [self JSONObjectForObject:value];
        if (obj) {
            [jsonObject setObject:obj forKey:key];
        }
    }
    
    return jsonObject;
}

- (NSArray *) arrayByRemovingNotJSONObject:(NSArray *)array {
    
    if ([NSJSONSerialization isValidJSONObject:array]) {
        return array;
    }
    
    NSMutableArray *jsonObject = [NSMutableArray array];
    
    for (id value in array) {
        id obj = [self JSONObjectForObject:value];
        if (obj) {
            [jsonObject addObject:obj];
        }
    }
    
    return jsonObject;
}

- (id) JSONObjectForObject:(id)object {
    
    id json = nil;
    
    if ([object isKindOfClass:[DConnectMessage class]]) {
        json = [self dictionaryByRemovingNotJsonObject:((DConnectMessage *) object).dictionary];
    } else if ([object isKindOfClass:[DConnectArray class]]) {
        DConnectArray *da = (DConnectArray *) object;
        json = [self arrayByRemovingNotJSONObject:da.array];
    } else if ([object isKindOfClass:[NSDictionary class]]) {
        json = [self dictionaryByRemovingNotJsonObject:(NSDictionary *) object];
    } else if ([object isKindOfClass:[NSArray class]]) {
        json = [self arrayByRemovingNotJSONObject:(NSArray *) object];
    } else if ([object isKindOfClass:[NSString class]]
               || [object isKindOfClass:[NSNumber class]]
               || [object isKindOfClass:[NSData class]])
    {
        json = object;
    }
    
    return json;
}

@end
