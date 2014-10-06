//
//  DPPbiImageStream.m
//  PebbleSample
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 小林伸郎. All rights reserved.
//

#import "DPPbiImageStream.h"

void test(char *buf, int value) {
}

@interface DPPbiImageStream()

+ (void) setShort:(short)value target:(NSMutableData *)data;

@end


@implementation DPPbiImageStream

+ (void) setShort:(short)value target:(NSMutableData *)data {
    char buf[2];
    buf[0] = (char) (value & 0xff);
    buf[1] = (char) (value >> 8) & 0xff;
    [data appendBytes:buf length:2];
}

+ (NSData *) createPibImageData:(PBBitmap *)bitmap {
    if (!bitmap) {
        return nil;
    }
    
    if (bitmap.bounds.size.w > 144 ||
        bitmap.bounds.size.h > 168) {
        return nil;
    }
    
    NSMutableData *data = [NSMutableData data];
    
    [DPPbiImageStream setShort:bitmap.rowSizeBytes target:data];
    [DPPbiImageStream setShort:bitmap.infoFlags target:data];
    [DPPbiImageStream setShort:bitmap.bounds.origin.x target:data];
    [DPPbiImageStream setShort:bitmap.bounds.origin.y target:data];
    [DPPbiImageStream setShort:bitmap.bounds.size.w target:data];
    [DPPbiImageStream setShort:bitmap.bounds.size.h target:data];
    [data appendData:bitmap.pixelData];
    
    return data;
}

@end
