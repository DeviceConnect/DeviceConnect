//
//  DPPebbleImage.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPPebbleImage.h"

@implementation DPPebbleImage

/*!
 @define pebble表示用の画像サイズ
 */
#define MaxWidth 144
#define MaxHeight 120


/*!
 @brief pebble用の画像にコンバート。
 @param　data 表示する変換前の画像
 */
+(NSData*)convertImage:(NSData*)data
{
	UIImage* image = [[UIImage alloc] initWithData:data];
	if (!image) {
		return nil;
	}
	
	CIImage *sourceImage = [[CIImage alloc] initWithImage:image];
	
	// 拡縮
	CGFloat w = image.size.width;
	CGFloat h = image.size.height;
	if (w==0 || h==0) {
		return nil;
	}
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
	
	return [DPPebbleImage convert:image];
}


/*!
 @brief GBitmapへ変換。
 @param　image 表示する変換前の画像
 */
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

@end
