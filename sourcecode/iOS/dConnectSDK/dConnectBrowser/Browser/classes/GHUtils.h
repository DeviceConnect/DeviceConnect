//
//  GHUtils.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface GHUtils : NSObject

/**
 * 一意のUUIDを作成
 * @return NSString UUID
 */
+ (NSString *)createUUID;

/**
 * 日付文字列からNSdateを作成する
 * @param date 日付文字列
 * @return NSdate
 */
+ (NSDate*)stringToDate:(NSString*)date;

/**
 * NSdateから日付文字列を作成
 * @param date NSDate
 * @return NSString yyyy-MM-dd
 */
+ (NSString*)dateToString:(NSDate*)date;

/**
 * エスケープ文字
 * @param str
 * @return NSString エスケープ後の文字
 */
+ (NSString*)escapeString:(NSString*)str;

/**
 * Notificationをpostする
 * @param userinfo
 * @param key tasks キー名
 */
+ (void)postNotification:(NSDictionary*)userinfo withKey:(NSString*)key;

/**
 * iPadか判定
 * @return BOOL
 */
+ (BOOL)isiPad;

/**
 * Cookieの削除
 */
+ (void)deleteCookie;

/**
 * Cookieの許可を設定
 * @param isAccept 許可するか
 */
+ (void)setCookieAccept:(BOOL)isAccept;

/**
 * NSHTTPCookieAcceptPolicyの状態を取得
 * @return 許可するか
 */
+ (BOOL)isCookieAccept;

/**
 * キャプチャ画像を保存
 * @param view 保存するview
 * @param url ファイル名とするurl
 */
+ (void)saveImage:(UIWebView*)view identifier:(NSString*)url;

/**
 * UIViewをUIImageに変換
 * @param view 変換するview
 * @return UIImage
 */
+ (UIImage*)convertViewToImage:(UIView*)view;

/**
 * キャッシュディレクトリのパスを取得
 * @return キャッシュディレクトリのパス
 */
+ (NSString*)cashesDirectory;

/**
 * 保存されているキャプチャ画像を取得
 * @param url ファイル名とするurl
 * @return UIImage
 */
+ (UIImage*)previewImage:(NSString*)url;

/**
 * ファイル名にある/を_に変換する
 * @param urlstr ファイル名とするurl
 * @return NSString
 */
+ (NSString*)convertURLString:(NSString*)urlstr;

///保存したキャプチャを削除
+ (void)clearCashes;

@end
