//
//  DPPebbleManager.m
//  PebbleSample
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 小林伸郎. All rights reserved.
//

#import "DPPebbleManager.h"
#import "DPPbiImageStream.h"
#import <PebbleKit/NSNumber+stdint.h>
#include <stdio.h>
#include <stdlib.h>

/*!
 @brief UUID.
 */
static NSString *const  DPPebbleUUID = @"ecfbe3b5-65f4-4532-be4e-3d013058d1f5";
static NSTimer *reTryTimer ;
static int reTryCounter=0;
/*!
 @define タイムアウト時間
 */
#define TIMEOUT 30

/*!
 @define 分割するデータサイズを定義。
 
 <p>
 分割サイズは、Pebbleアプリ側でも定義してあるので大きくする場合には、Pebbleアプリ側の定義も修正すること。
 </p>
 */
#define BUF_SIZE 64

/*!
 @define pebble表示用の画像サイズ
 */
#define MaxWidth 144
#define MaxHeight 120


/*!
 @brief レスポンスを返却するためのコールバック。
 */
@interface DPPebbleCallbackInfo : NSObject
/*!
 @brief コマンドを返却するブロック。
 */
@property (nonatomic, strong) DPPebbleCommandBlocks callback;
/*!
 @brief スレッドを止めているセマフォ。
 */
@property (nonatomic, strong) dispatch_semaphore_t semaphore;
/*!
 @brief イニシャライザ。
 @param[in] callback コールバックするブロック
 @param[in] semaphore セマフォ
 @retval DPPebbleCallbackInfoインスタンス
 */
- (id) initWithCallback:(DPPebbleCommandBlocks) callback semaphore:(dispatch_semaphore_t) semaphore;
/*!
 @brief イニシャライザ。
 @param[in] callback コールバックするブロック
 @param[in] semaphore セマフォ
 @retval DPPebbleCallbackInfoインスタンス
 */
+ (id) callback:(DPPebbleCommandBlocks) callback semaphore:(dispatch_semaphore_t) semaphore;
@end




@implementation DPPebbleCallbackInfo
- (id) initWithCallback:(DPPebbleCommandBlocks)callback semaphore:(dispatch_semaphore_t)semaphore {
    self = [super init];
    if (self) {
        _callback = callback;
        _semaphore = semaphore;
    }
    return self;
}
+ (id) callback:(DPPebbleCommandBlocks)callback semaphore:(dispatch_semaphore_t)semaphore {
    return [[DPPebbleCallbackInfo alloc] initWithCallback:callback semaphore:semaphore];
}
@end

@interface DPPebbleManager ()

/*!
 @brief Pebbleを操作するインスタンス。
 */
@property (nonatomic, strong) PBWatch *connectedWatch;

/*!
 @brief レスポンスを格納するDictionary。
 */
@property (nonatomic, strong) NSMutableDictionary *mResponseBlockMap;

@property (nonatomic, strong) NSMutableDictionary *mEventBlockMap;

/*!
 @brief ハンドラを設定する。
 */
- (void) setHandler;

- (BOOL) sendRequest:(NSMutableDictionary *)request;
- (BOOL) sendLength:(NSInteger)streamTotalLength;
- (BOOL) sendBody:(NSData *)data index:(int)index;

+ (UIImage *) resizeAspectFitWithSize:(UIImage *)srcImg size:(CGSize)size;

@end



@implementation DPPebbleManager

- (id) init {
    self = [super init];
    if (self) {
        srand([[NSDate date] timeIntervalSinceReferenceDate]);
        
        self.mResponseBlockMap = [NSMutableDictionary dictionary];
        self.mEventBlockMap = [NSMutableDictionary dictionary];
        
        uuid_t myAppUUIDbytes;
        NSUUID *myAppUUID = [[NSUUID alloc] initWithUUIDString:DPPebbleUUID];
        [myAppUUID getUUIDBytes:myAppUUIDbytes];
        [[PBPebbleCentral defaultCentral] setDelegate:self];
        [[PBPebbleCentral defaultCentral] setAppUUID:[NSData dataWithBytes:myAppUUIDbytes length:16]];
        //        self.connectedWatch = [[PBPebbleCentral defaultCentral] connectedWatches];
        self.connectedWatch = [[PBPebbleCentral defaultCentral] lastConnectedWatch];
        
        if (self.connectedWatch) {
            [self setHandler];
        }
    }
    return self;
}
-(void)initSender{
    uuid_t myAppUUIDbytes;
    NSUUID *myAppUUID = [[NSUUID alloc] initWithUUIDString:DPPebbleUUID];
    [myAppUUID getUUIDBytes:myAppUUIDbytes];
    [[PBPebbleCentral defaultCentral] setDelegate:self];
    [[PBPebbleCentral defaultCentral] setAppUUID:[NSData dataWithBytes:myAppUUIDbytes length:16]];
    self.connectedWatch = [[PBPebbleCentral defaultCentral] lastConnectedWatch];
    
}
- (NSArray*) getWatchesList {
    [[PBPebbleCentral defaultCentral] setDelegate:self];
    uuid_t myAppUUIDbytes;
    [[PBPebbleCentral defaultCentral] setAppUUID:[NSData dataWithBytes:myAppUUIDbytes length:16]];
    return [[PBPebbleCentral defaultCentral] connectedWatches];
    
}
- (NSString*) getConnectWatcheName {
    //DeviceIdの取得　　URL対応のため空白と：を除去
    return [[self.connectedWatch.name
             stringByReplacingOccurrencesOfString:@" " withString:@""]
            stringByReplacingOccurrencesOfString:@":" withString:@""];;
}

#pragma mark - Public Method -

- (void) addEventCallback:(DPPebbleEventBlocks)callback profile:(NSNumber *)profile {
    self.mEventBlockMap[profile] = callback;
}

- (void) sendAppMessagesLaunch:(DPPebbleLauchBlocks)callback; {
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.connectedWatch appMessagesLaunch:^(PBWatch *watch, NSError *error) {
            callback(!error);
        }];
    });
}

- (void) sendCommandToPebble:(NSMutableDictionary *)request callback:(DPPebbleCommandBlocks)callback {
    __weak DPPebbleManager *_self = self;
    
    if (request == nil || callback == nil) {
        return;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * TIMEOUT);
        
        // リクエストコードを作成して、リクエストに追加
        NSNumber *requestCode = @(rand());
        request[@(KEY_PARAM_REQUEST_CODE)] = requestCode;
        
        // リクエスト情報を保持
        _self.mResponseBlockMap[requestCode] = [DPPebbleCallbackInfo callback:callback semaphore:semaphore];
        
        
        // Pebbleに送信
        // mainスレッドからだとコールバックがくる。
        dispatch_async(dispatch_get_main_queue(), ^{
            [_self.connectedWatch appMessagesPushUpdate:request onSent:^(PBWatch *watch, NSDictionary *update, NSError *error) {
                if (error) {
                    //再送信
                    // 送信に失敗した場合には、５回までアプリを起動してから再送する
                    
                    // 自動でタイマーはスタートします。
                    reTryTimer = [NSTimer
                                      // タイマーイベントを発生させる感覚。「2.0」は 2秒 型は float
                                      scheduledTimerWithTimeInterval:2.0
                                      // 呼び出すメソッドの呼び出し先(selector) self はこのファイル(.m)
                                      target:self
                                      // 呼び出すメソッド名。「:」で自分自身(タイマーインスタンス)を渡す。
                                      // インスタンスを渡さない場合は、「timerInfo」
                                      selector:@selector(reSend:)
                                      // 呼び出すメソッド内で利用するデータが存在する場合は設定する。ない場合は「nil」
                                      userInfo:request//(NSMutableDictionary *)request
                                      // 上記で設定した秒ごとにメソッドを呼び出す場合は、「YES」呼び出さない場合は「NO」
                                      repeats:YES
                                      ];
 
                    reTryCounter=0;
                    [reTryTimer isValid];
                }
            }
             ];
        }
                       );
        
        
        // レスポンスがPebbleから返却されるまで待つ
        long result = dispatch_semaphore_wait(semaphore, timeout);
        if (result != 0) {
            // タイムアウト
            DPPebbleCallbackInfo *info = nil;
            @synchronized (_self.mResponseBlockMap) {
                info = self.mResponseBlockMap[requestCode];
                if (info) {
                    [_self.mResponseBlockMap removeObjectForKey:requestCode];
                }
            }
            if (info) {
                callback(nil);
            }
        }
        
        
        
    }
                   );
    
}



-(void)reSend:(NSTimer*)mReTryTimer{
    __weak DPPebbleManager *_self = self;
    NSDictionary* request=(NSDictionary* )[mReTryTimer userInfo];
    if(reTryCounter>5){
        [mReTryTimer invalidate];
        return;
    }
    [_self sendAppMessagesLaunch:^(BOOL success) {
        
        if (success) {
            [_self.connectedWatch appMessagesPushUpdate:request onSent:^(PBWatch *watch, NSDictionary *update, NSError *error) {
                if (!error) {
                    //
                    [mReTryTimer invalidate];
//                    PBErrorCode //エラー参照用
                     reTryCounter=0;
                }else{
                    if([error code]==9){
                        [self initSender];
                                           }
                     reTryCounter++;
                }
            }];
        }
        
    }
     ];
    
}

- (void) sendDataToPebble:(NSData *)data callback:(DPPebbleDataBlocks)callback {
    __weak DPPebbleManager *_self = self;
    
    if (data == nil) {
        return;
    }
    
    data=[DPPebbleManager convertImage:data];
    
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSUInteger streamRemainLegnth = data.length;
        NSUInteger count = streamRemainLegnth / BUF_SIZE + 1;
        
        if (![_self sendLength:streamRemainLegnth]) {
            callback(NO);
            return;
        }
        for (int i = 0; i < count; i++) {
            if (![_self sendBody:data index:i]) {
                callback(NO);
                return;
            }
        }
        callback(YES);
    });
}

/*!
 @brief pebble用の画像にコンバート。
 @param　data 表示する変換前の画像
 */


+(NSData*)convertImage:(NSData*)data
{
    UIImage* image = [[UIImage alloc] initWithData:data];
    
    
	CIImage *sourceImage = [[CIImage alloc] initWithImage:image];
	
	// 拡縮
	CGFloat w = image.size.width;
	CGFloat h = image.size.height;
	float scale = (w > h ? MaxWidth/w : MaxHeight/h);
    //	scale -= 0.001;
	CIImage *scaledImage = [sourceImage imageByApplyingTransform:CGAffineTransformMakeScale(scale, scale)];
    
	// 白黒フィルタ
	CIFilter *ciFilter = [CIFilter filterWithName:@"CIPhotoEffectNoir"
									keysAndValues:kCIInputImageKey, scaledImage, nil];
	CIContext *ciContext = [CIContext contextWithOptions:nil];
	CGImageRef cgimg = [ciContext createCGImage:[ciFilter outputImage] fromRect:[[ciFilter outputImage] extent]];
	image = [UIImage imageWithCGImage:cgimg scale:1 orientation:UIImageOrientationUp];
	CGImageRelease(cgimg);
    
	return [self convert:image];
}


/*!
 @brief GBitmapへ変換。
 @param　image 表示する変換前の画像
 */

// GBitmapへ変換
+ (NSData*)convert:(UIImage*)image
{
	NSMutableData *data = [NSMutableData data];
	int w = image.size.width;
	int h = image.size.height;
	
	// row_size_bytes
	int row_size_bytes = (w + 31) / 32;
	row_size_bytes *= 4;
	[data appendBytes:&row_size_bytes length:2];
	
	// info_flags
	Byte info_flags[2] = {0, 0x10};
	[data appendBytes:info_flags length:2];
	
	// pos
	UInt16 pos[2] = {0, 0};
	[data appendBytes:pos length:4];
	
	// size
	UInt16 size[2] = {w, h};
	[data appendBytes:size length:4];
	
	// image data
	size_t bytesPerRow = CGImageGetBytesPerRow(image.CGImage);
	CFDataRef pixelData = CGDataProviderCopyData(CGImageGetDataProvider(image.CGImage));
	const UInt8 *rawData = CFDataGetBytePtr(pixelData);
    
	// データ書き込み
	for (int y = 0; y < h; ++y) {
		int currentBit = 0;
		Byte buff = 0;
		for (int x = 0; x < row_size_bytes * 8; ++x) {
			if (x < w) {
				int pixelInfo = (int)bytesPerRow * y + x * 4; // The image is png
				UInt8 r = rawData[pixelInfo];
				//UInt8 g = rawData[pixelInfo+1];
				//UInt8 b = rawData[pixelInfo+2];
				UInt8 a = rawData[pixelInfo+3];
				
				if (a < 127) {
					// 透明
					buff = buff | (0x01 << currentBit);
				} else {
					// 白黒（白黒フィルタがかかっているのでrでOK!）
					// ランダムディザ
					if (r>150) {
						buff = buff | (0x01 << currentBit);
					} else if (r>110) {
						if (rand()%255<r) {
							buff = buff | (0x01 << currentBit);
						}
					}
				}
			}
			
			// データ書き込み
			currentBit++;
			if (currentBit>7) {
				[data appendBytes:&buff length:1];
				currentBit = 0;
				buff = 0;
			}
		}
	}
	
	return data;
}

- (void) sendNotificationTitle:(NSString *)title body:(NSString *)body type:(int)type {
    // TODO
}

+ (NSData *) convertVibrationPattern:(NSArray *)pattern {
    if (pattern == nil || pattern.count == 0) {
        return nil;
    } else {
        NSMutableData *data = [NSMutableData data];
        for (NSNumber *value in pattern) {
            int v = [value intValue];
            char buf[2];
            buf[0] = (char) (v >> 8) & 0xff;
            buf[1] = (char) (v & 0xff);
            [data appendBytes:buf length:2];
        }
        return data;
    }
}

#pragma mark - Private Method -

- (void) setHandler {
    __weak DPPebbleManager *_self = self;
    [self.connectedWatch appMessagesAddReceiveUpdateHandler:^BOOL(PBWatch *watch, NSDictionary *update) {
        
        NSNumber *action = update[@(KEY_ACTION)];
        if (action != nil && [action intValue] == ACTION_EVENT) {
            NSNumber *profile = update[@(KEY_PROFILE)];
            if (profile != nil) {
                DPPebbleEventBlocks callback = _self.mEventBlockMap[profile];
                if (callback) {
                    callback(update);
                }
            }
        } else {
            NSNumber *requestCode = update[@(KEY_PARAM_REQUEST_CODE)];
            DPPebbleCallbackInfo *info = nil;
            @synchronized (_self.mResponseBlockMap) {
                info = _self.mResponseBlockMap[requestCode];
                if (info) {
                    [_self.mResponseBlockMap removeObjectForKey:requestCode];
                }
            }
            if (info) {
                if (info.callback) {
                    info.callback(update);
                }
                if (info.semaphore) {
                    dispatch_semaphore_signal(info.semaphore);
                }
            }
        }
        // ackを返却する
        return YES;
    }];
}

- (BOOL) sendRequest:(NSMutableDictionary *)request {
    __weak DPPebbleManager *_self = self;
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * TIMEOUT);
    
    __block BOOL send = NO;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [_self.connectedWatch appMessagesPushUpdate:request onSent:^(PBWatch *watch, NSDictionary *update, NSError *error) {
            if([error code]==9){
                [self initSender];
            }
            send = (error == nil);
            dispatch_semaphore_signal(semaphore);
        }];
    });
    
    long result = dispatch_semaphore_wait(semaphore, timeout);
    if (result != 0) {
        return NO;
    }
    return send;
}

- (BOOL) sendLength:(NSInteger)streamTotalLength {
    NSMutableDictionary *request = [NSMutableDictionary dictionary];
    request[@(KEY_PROFILE)] = [NSNumber numberWithUint32:PROFILE_BINARY];
    request[@(KEY_PARAM_BINARY_LENGTH)] = [NSNumber numberWithUint32:(UInt32)streamTotalLength];
    
    // 3回リトライする
    for (int i = 0; i < 3; i++) {
        if ([self sendRequest:request]) {
            return YES;
        } else {
            dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
            dispatch_time_t timeout = dispatch_time(DISPATCH_TIME_NOW, NSEC_PER_SEC * TIMEOUT);
            
            __block BOOL isLaunched = NO;
            
            [self sendAppMessagesLaunch:^(BOOL success) {
                isLaunched = success;
                dispatch_semaphore_signal(semaphore);
            }];
            
            long result = dispatch_semaphore_wait(semaphore, timeout);
            if (result != 0 || isLaunched == NO) {
                return NO;
            }
        }
    }
    return NO;
}

- (BOOL) sendBody:(NSData *)data index:(int)index {
    BOOL last = (data.length / BUF_SIZE == index);
    
    NSRange range;
    range.location = index * BUF_SIZE;
    if (last) {
        range.length = data.length - index * BUF_SIZE;
    } else {
        range.length = BUF_SIZE;
    }
    NSData *send = [data subdataWithRange:range];
    
    NSMutableDictionary *request = [NSMutableDictionary dictionary];
    request[@(KEY_PROFILE)] = @(PROFILE_BINARY);
    request[@(KEY_PARAM_BINARY_INDEX)] = @(index);
    request[@(KEY_PARAM_BINARY_BODY)] = send;
    return [self sendRequest:request];
}

+ (UIImage *) resizeAspectFitWithSize:(UIImage *)srcImg size:(CGSize)size {
    CGFloat widthRatio  = size.width  / srcImg.size.width;
    CGFloat heightRatio = size.height / srcImg.size.height;
    CGFloat ratio = (widthRatio < heightRatio) ? widthRatio : heightRatio;
    
    CGSize resizedSize = CGSizeMake(srcImg.size.width*ratio, srcImg.size.height*ratio);
    
    UIGraphicsBeginImageContext(resizedSize);
    [srcImg drawInRect:CGRectMake(0, 0, resizedSize.width, resizedSize.height)];
    UIImage* resizedImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    return resizedImage;
}


#pragma mark - PBPebbleCentralDelegate -

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidConnect:(PBWatch*)watch isNew:(BOOL)isNew {
    
    self.connectedWatch = watch;
    if (self.connectedWatch) {
        [self setHandler];
    }
}

- (void)pebbleCentral:(PBPebbleCentral*)central watchDidDisconnect:(PBWatch*)watch {
    
    if (self.connectedWatch == watch || [watch isEqual:self.connectedWatch]) {
        self.connectedWatch = nil;
    }
}

@end
