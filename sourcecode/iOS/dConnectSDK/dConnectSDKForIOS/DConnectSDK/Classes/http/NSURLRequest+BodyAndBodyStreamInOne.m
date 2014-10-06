/**
 NSURLRequest+BodyAndBodyStreamInOne
 Copyright (c) 2014 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */

#import "NSURLRequest+BodyAndBodyStreamInOne.h"

@implementation NSURLRequest (BodyAndBodyStreamInOne)

- (NSData *)body
{
    NSData *bodyData;
    if (!(bodyData = [self HTTPBody])) {
        // ボディは「- HTTPBody」もしくは「- HTTPBodyStream」によって取得する。
        // TODO: 巨大ファイルが送られてきた際に、whileループでメインスレッドが固まる羽目にならないように考慮。
        NSInputStream *bodyStream = [self HTTPBodyStream];
        if (!bodyStream) {
            // ボディデータ無し
            return nil;
        }
        NSMutableData *mutableData = [NSMutableData data];
        NSInteger readLen;
        NSUInteger totalReadLen = 0;
        NSUInteger chunkLen = 4096;
        uint8_t *buf = malloc(sizeof(uint8_t) * chunkLen);
        [bodyStream open];
        while (YES) {
            readLen = [bodyStream read:buf maxLength:chunkLen];
            if (readLen < 0) {
                free(buf);
                [NSException raise:@"HTTP Error" format:@"Failed to read HTTP body stream."];
            } else if (readLen > 0) {
                totalReadLen += readLen;
                [mutableData appendBytes:buf length:readLen];
            } else {
                bodyData = mutableData;
                break;
            }
        }
        free(buf);
        [bodyStream close];
    }
    return bodyData;
}

@end
