//
//  DPPebbleManager.h
//  PebbleSample
//
//  Created by 小林伸郎 on 2014/08/23.
//  Copyright (c) 2014年 小林伸郎. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <PebbleKit/PebbleKit.h>
#import "pebble_device_plugin_defines.h"

/*!
 @brief アプリ起動通知ブロック。
 */
typedef void (^DPPebbleLauchBlocks)(BOOL success);

/*!
 @brief コマンドレスポンス通知ブロック。
 */
typedef void (^DPPebbleCommandBlocks)(NSDictionary *response);

/*!
 @brief データ送信正否通知ブロック。
 */
typedef void (^DPPebbleDataBlocks)(BOOL success);

/*!
 @brief イベント受信ブロック。
 */
typedef void (^DPPebbleEventBlocks)(NSDictionary *message);

/*!
 @brief Pebbleとの接続を管理するクラス。
 */
@interface DPPebbleManager : NSObject <PBPebbleCentralDelegate>

/*!
 @brief イベント受信用のコールバックを追加する。
 
 @param[in] callback コールバック
 @param[in] profile プロファイル
 */
- (void) addEventCallback:(DPPebbleEventBlocks)callback profile:(NSNumber *)profile;


/*!
 @brief 接続中のPebbleリストを取得する。
 */
- (NSArray*) getWatchesList ;

/*!
 @brief 接続中のPebble名を取得する。
 */
- (NSString*) getConnectWatcheName ;
/*!
 @brief Pebble側のアプリを起動する.
 */
- (void) sendAppMessagesLaunch:(DPPebbleLauchBlocks)callback;

/*!
 @brief Pebbleへコマンドを送信する。
 
 @param[in] request リクエストコマンド
 @param[in] callback コールバックblocks
 */
- (void) sendCommandToPebble:(NSMutableDictionary *)request callback:(DPPebbleCommandBlocks)callback;

/*!
 @brief 指定されたデータをPebbleに送信する.
 
 dataがnilの場合には何も処理を行わない。
 エラーも発生しないので注意すること。
 
 @param[in] data 送信するデータ
 @param[in] callback 送信成功、失敗通知コールバック
 */
- (void) sendDataToPebble:(NSData *)data callback:(DPPebbleDataBlocks)callback;
-(void)reSend:(NSDictionary*)request;
/*!
 @brief Notificationを送信する。
 @param[in] title タイトル
 @param[in] body メッセージ
 @param[in] type メッセージタイプ
 */
- (void) sendNotificationTitle:(NSString *)title body:(NSString *)body type:(int)type;

/*!
 @brief 指定されたimageをPebbleで表示できるデータに変換する。
 
 @param[in] image 変換する画像
 @param[in] size 変換サイズ
 
 @retval 変換後のデータ
 @retval nil 変換に失敗した場合
 */
//+ (NSData *) convertImage:(UIImage *)image size:(CGSize)size;

+ (NSData *) convertImage:(NSData *)image;

/*!
 @brief 指定されたパターンをPebbleで読み込めるデータに変換する。
 @param[in] pattern 変換するパターン
 @retval 変換後のデータ
 @retval 変換に失敗した場合
 */
+ (NSData *) convertVibrationPattern:(NSArray *)pattern;

@end
