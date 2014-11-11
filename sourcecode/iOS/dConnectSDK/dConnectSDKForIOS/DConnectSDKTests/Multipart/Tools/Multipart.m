//
//  Multipart.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "Multipart.h"
#import "Hash.h"

@implementation Multipart

- (id)init
{
    self = [super init];
    if (self) {
        items = [NSMutableDictionary new];
        bound = SHA256Hash(@"asdlfjb;adiuhasdf@@asdfj");
    }
    return self;
}

- (void)addData:(NSData *)data forKey:(NSString *)key
{
    [items setObject:data forKey:key];
}

- (void)addString:(NSString *)string forKey:(NSString *)key
{
    [self addData:[string dataUsingEncoding:NSUTF8StringEncoding] forKey:key];
}

- (BOOL)hasItems
{
    return 0<[items count];
}

- (NSString *)contentType
{
    return [NSString stringWithFormat:@"multipart/form-data; boundary=%@", bound];
}

- (NSData *)body
{
    NSMutableData *data = [NSMutableData data];
    for (id key in items) {
        NSData *value = [items objectForKey:key];
        [data appendData:[[NSString stringWithFormat:@"--%@\r\n", bound] dataUsingEncoding:NSUTF8StringEncoding]];
        [data appendData:[[NSString stringWithFormat:@"Content-Disposition: form-data; name=\"%@\"\r\n", key] dataUsingEncoding:NSUTF8StringEncoding]];
        [data appendData:[[NSString stringWithFormat:@"Content-Length: %d\r\n", [value length]] dataUsingEncoding:NSUTF8StringEncoding]];
        [data appendData:[@"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
        [data appendData:value];
        [data appendData:[@"\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    }
    [data appendData:[[NSString stringWithFormat:@"--%@--\r\n", bound] dataUsingEncoding:NSUTF8StringEncoding]];
    return data;
}

@end
