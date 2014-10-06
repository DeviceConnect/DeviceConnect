//
//  DConnectVibrationProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
/*! 
 @file
 @brief Vibrationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: vibration。
 */
extern NSString *const DConnectVibrationProfileName;

/*!
 @brief アトリビュート: vibrate。
 */
extern NSString *const DConnectVibrationProfileAttrVibrate;

/*!
 @brief パラメータ: pattern。
 */
extern NSString *const DConnectVibrationProfileParamPattern;

/*!
 @brief 振動パターンで使われる区切り文字。
 */
extern NSString *const DConnectVibrationProfileVibrationDurationDelim;

/*!
 @brief デフォルトの最大バイブレーション鳴動時間。 500 ミリ秒。
 */
extern const long long DConnectVibrationProfileDefaultMaxVibrationTime;

@class DConnectVibrationProfile;

/*!
 @protocol DConnectVibrationProfileDelegate
 @brief Vibration Profileの各APIリクエスト通知用デリゲート。
 
 Vibration Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectVibrationProfileDelegate <NSObject>
@optional

#pragma mark - Put Methods

/*!
 
 @brief バイブ鳴動開始リクエストを受け取ったことをデリゲートに通知する。
 
 profileがバイブ鳴動開始リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Vibration Start API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] pattern 鳴動パターン
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectVibrationProfile *)profile didReceivePutVibrateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         pattern:(NSArray *) pattern;

#pragma mark - Delete Methods

/*!
 
 @brief バイブ鳴動停止リクエストを受け取ったことをデリゲートに通知する。
 
 profileがバイブ鳴動停止リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Vibration Stop API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectVibrationProfile *)profile didReceiveDeleteVibrateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

@end

/*!
 @class DConnectVibrationProfile
 @brief Vibrationプロファイル。
 
 Vibration Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectVibrationProfile : DConnectProfile


/*!
@brief DConnectVibrationProfileのデリゲートオブジェクト。

デリゲートは @link DConnectVibrationProfileDelegate @endlink を実装しなくてはならない。
デリゲートはretainされない。
*/
@property (nonatomic, weak) id<DConnectVibrationProfileDelegate> delegate;

/*!
 @brief バイブレーションの最大鳴動時間。
 
 バイブレーションのパターンが省略された場合、自動的にデフォルト値として適用される。<br/>
 デバイスごとに適切な数値を設定すること。
 デフォルトでは @link DConnectVibrationProfileDefaultMaxVibrationTime @endlink が設定される。
 */
@property (nonatomic, assign) long long maxVibrationTime;

#pragma mark - Getter

/*!
 @brief リクエストから鳴動パターンを取得する。
 
 @param[in] request リクエストパラメータ
 
 @retval NSString* 鳴動パターンの文字列
 @retval nil リクエストに鳴動パターンが指定されていない場合
 */
+ (NSString *) patternFromRequest:(DConnectMessage *)request;

#pragma mark - Utility

/*!
 @brief 鳴動パターンを文字列から解析し、数値の配列に変換する。
 
 数値の前後の半角のスペースは無視される。その他の半角、全角のスペースは不正なフォーマットとして扱われる。
 
 @param[in] pattern 鳴動パターン文字列。
 
 @retval NSArray* 鳴動パターンの配列
 @retval nil 鳴動パターンが解析できない場合
 */
- (NSArray *) parsePattern:(NSString *)pattern;

@end
