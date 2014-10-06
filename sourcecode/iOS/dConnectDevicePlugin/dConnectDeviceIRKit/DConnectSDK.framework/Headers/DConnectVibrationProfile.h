//
//  DConnectVibrationProfile.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/06/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//
/*! @file
 @brief Vibrationプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: vibration。 */
extern NSString *const DConnectVibrationProfileName;
/*! @brief 属性: vibrate。 */
extern NSString *const DConnectVibrationProfileAttrVibrate;
/*! @brief パラメータ: pattern。 */
extern NSString *const DConnectVibrationProfileParamPattern;

/*! @brief 振動パターンで使われる区切り文字。 */
extern NSString *const DConnectVibrationProfileVibrationDurationDelim;
/*! @brief デフォルトの最大バイブレーション鳴動時間。 500 ミリ秒。 */
extern const long long DConnectVibrationProfileDefaultMaxVibrationTime;

@class DConnectVibrationProfile;

/*!
 @protocol DConnectVibrationProfileDelegate
 @brief Vibrationプロファイルのデリゲート。
 
 <p>
 スマートデバイスのバイブレーション操作機能を提供するAPI。<br/>
 スマートデバイスのバイブレーション操作機能を提供するデバイスプラグインは当デリゲートを継承し、対応APIを実装すること。
 </p>
 */
@protocol DConnectVibrationProfileDelegate <NSObject>
@optional

#pragma mark - Put Methods

/*!
 @brief バイブ鳴動開始リクエストハンドラー。
 
 デバイスを鳴動させ、その結果をレスポンスパラメータに格納する。
 
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
        deviceId:(NSString *)deviceId pattern:(NSArray *) pattern;

#pragma mark - Delete Methods

/*!
 @brief バイブ鳴動停止リクエストハンドラー。
 
 デバイスの鳴動を終了させ、その結果をレスポンスパラメータに格納する。
 
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

 <p>
 以下のメソッドを実装することで、Vibrationプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 </p>
 
 @par
 @li didReceivePutVibrateRequest: response: deviceId: patter:
 @li didReceiveDeleteVibrateRequest: response: deviceId:
 @li patternFromRequest:
 @li maxVibrationTime
 */
@interface DConnectVibrationProfile : DConnectProfile

/*!
 @brief DConnectVibrationProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectVibrationProfileDelegate> delegate;

/*!
 @brief バイブレーションの最大鳴動時間。
 
 デバイスごとに適切な数値を設定すること。
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
