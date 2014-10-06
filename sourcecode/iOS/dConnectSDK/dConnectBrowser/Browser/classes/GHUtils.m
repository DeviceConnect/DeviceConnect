//
//  GHUtils.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHUtils.h"
#import "GTMDefines.h"
#import "GTMNSString+HTML.h"

@implementation GHUtils


/**
 * 画像名に仕様するユニークなID(UUID)を作成します。
 *
 * @return UUID
 */
+ (NSString *)createUUID
{
	CFUUIDRef uuidObject = CFUUIDCreate(kCFAllocatorDefault);
	NSString *uuidStr = (__bridge_transfer NSString *)CFUUIDCreateString(kCFAllocatorDefault, uuidObject);
	CFRelease(uuidObject);
	return uuidStr;
}


+ (NSDate*)stringToDate:(NSString*)date
{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd"];
    [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"JST"]];
    return [formatter dateFromString:date];
}

+ (NSString*)dateToString:(NSDate*)date
{
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd"];
    [formatter setTimeZone:[NSTimeZone timeZoneWithAbbreviation:@"JST"]];
    return [formatter stringFromDate:date];
}

+(NSString*)escapeString:(NSString*)str
{
    return [str gtm_stringByEscapingForHTML];
}


//--------------------------------------------------------------//
#pragma mark - ノーティフィケーションpush
//--------------------------------------------------------------//
+ (void)postNotification:(NSDictionary*)userinfo withKey:(NSString*)key
{
    NSNotification *n = [NSNotification notificationWithName:key object:nil userInfo:userinfo];
    [[NSNotificationCenter defaultCenter] postNotification:n];
}




//--------------------------------------------------------------//
#pragma mark - 端末判定
//--------------------------------------------------------------//
+ (BOOL)isiPad
{
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
        // this device is iPad
        return YES;
    } else {
        return NO;
    }
}


//--------------------------------------------------------------//
#pragma mark - Cookie
//--------------------------------------------------------------//
+ (void)deleteCookie
{
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    [[cookieStorage cookies] enumerateObjectsUsingBlock:^(NSHTTPCookie *cookie, NSUInteger idx, BOOL *stop) {
        [cookieStorage deleteCookie:cookie];
    }];
}


+ (void)setCookieAccept:(BOOL)isAccept
{
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    if (isAccept) {
        [cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyAlways];
    }else{
        [cookieStorage setCookieAcceptPolicy:NSHTTPCookieAcceptPolicyNever];
    }
}


+ (BOOL)isCookieAccept
{
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    NSHTTPCookieAcceptPolicy policy = [cookieStorage cookieAcceptPolicy];
    
    if (policy == NSHTTPCookieAcceptPolicyNever) {
        return NO;
    }else{
        return YES;
    }
}


//--------------------------------------------------------------//
#pragma mark - 画像キャプチャ
//--------------------------------------------------------------//

#define CAPTURE @"capture"

+ (void)saveImage:(UIWebView*)view identifier:(NSString*)url
{
    
    if ([url isEqualToString:@"about:blank"]) {
        return;
    }
    
    UIImage *img = [self convertViewToImage:view];
    NSData* pngData = [[NSData alloc] initWithData:UIImagePNGRepresentation(img)];
    NSString* dir = [self caputureImageDir];
    
    //urlをファイル名とする
    NSString* savedir = [NSString stringWithFormat:@"%@/%@",dir, [self convertURLString:url]];

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_BACKGROUND, 0), ^{
        if ([pngData writeToFile:savedir atomically:YES]) {
//            LOG(@"saveImage OK");
        } else {
//            LOG(@"save Image Error");
        }
    });
}

+ (UIImage*)convertViewToImage:(UIWebView*)view
{
    CGSize size = [UIScreen mainScreen].bounds.size;
    
    //回転
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    
    if (orientation == UIInterfaceOrientationLandscapeLeft || orientation == UIInterfaceOrientationLandscapeRight) {
        UIGraphicsBeginImageContextWithOptions(CGSizeMake(size.height, size.width), NO, 0);
    }else{
        UIGraphicsBeginImageContextWithOptions(size, NO, 0);
    }
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    [view.layer renderInContext:context];
    UIImage *newimg = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    return newimg;
}


+ (NSString*)caputureImageDir
{
    NSString* cashes = [self cashesDirectory];
    NSString* savedir = [NSString stringWithFormat:@"%@/%@",cashes, CAPTURE];
    if(![[NSFileManager defaultManager]fileExistsAtPath:savedir]){
        
        NSError *error;
        if (![[NSFileManager defaultManager]createDirectoryAtPath:savedir
                                     withIntermediateDirectories:YES
                                                      attributes:nil
                                                           error:&error]) {
            LOG(@"error %@", error);
        } ;
    }
    
    return savedir;
}

+ (NSString*)cashesDirectory
{
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *dir = [paths objectAtIndex:0];
    return dir;
}


+ (UIImage*)previewImage:(NSString*)url
{
    NSString* dir = [self caputureImageDir];
    NSString* imgdir = [NSString stringWithFormat:@"%@/%@",dir, [self convertURLString:url]];
    if([[NSFileManager defaultManager]fileExistsAtPath:imgdir]){
        return [UIImage imageWithContentsOfFile:imgdir];
    }else{
        LOG(@"画像無し");
        return nil;
    }
}


+ (NSString*)convertURLString:(NSString*)urlstr
{
    NSURL *url = [NSURL URLWithString:urlstr];
    NSString* specifier = [url resourceSpecifier];
    NSString* newurl = [specifier stringByReplacingOccurrencesOfString:@"/" withString:@"_"];
    
    return newurl;
}

+ (void)clearCashes
{
    NSString* caputureImageDir = [self caputureImageDir];
    if (caputureImageDir) {
        [[NSFileManager defaultManager]removeItemAtPath:caputureImageDir error:nil];
        LOG(@"キャプチャ画像削除");
    }
}

@end
