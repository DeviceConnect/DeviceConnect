//
//  DPDiceProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
/*! @file
 @brief Diceプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.7.15)
 */
#import <DConnectSDK/DConnectSDK.h>
/*! @brief プロファイル名: dice。 */
extern NSString *const DPDiceProfileName;
/*!
 @brief インタフェース: magnetometer。
 */
extern NSString *const DPDiceProfileInterfaceMagnetometer;

/*!
 @brief 属性: onDice。
 */
extern NSString *const DPDiceProfileAttrOnDice;
/*!
 @brief 属性: onmagnetometer。
 */
extern NSString *const DPDiceProfileAttrOnMagnetometer;
/*!
 @brief パラメータ: dice。
 */
extern NSString *const DPDiceProfileParamDice;
/*!
 @brief パラメータ: pip。
 */
extern NSString *const DPDiceProfileParamPip;
/*!
 @brief パラメータ: magnetometer。
 */
extern NSString *const DPDiceProfileParamMagnetometer;
/*!
 @brief パラメータ: filter。
 */
extern NSString *const DPDiceProfileParamFilter;
/*!
 @brief パラメータ: interval。
 */
extern NSString *const DPDiceProfileParamInterval;
/*!
 @brief パラメータ: x。
 */
extern NSString *const DPDiceProfileParamX;
/*!
 @brief パラメータ: y。
 */
extern NSString *const DPDiceProfileParamY;
/*!
 @brief パラメータ: z。
 */
extern NSString *const DPDiceProfileParamZ;

@class DPDiceProfile;


/*!
 @brief Dice プロファイル。
 
 <p>
 DICE+の機能を提供するAPI。<br/>
 DICE+の機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DPDiceProfileDelegate<NSObject>
@optional
/*!
 Diceのイベントを登録できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{dConnectドメイン}/dice/ondice?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)               profile:(DPDiceProfile *)profile
    didReceivePutOnDiceRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
                    sessionKey:(NSString *)sessionKey;

/*!
 Diceのイベントを登録を解除できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                profile:(DPDiceProfile *)profile
  didReceiveDeleteOnDiceRequest:(DConnectRequestMessage *)request
                       response:(DConnectResponseMessage *)response
                       deviceId:(NSString *)deviceId
                     sessionKey:(NSString *)sessionKey;

/*!
 Magnetometerのイベントを登録できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                   profile:(DPDiceProfile *)profile
didReceivePutOnMagnetometerRequest:(DConnectRequestMessage *)request
                          response:(DConnectResponseMessage *)response
                          deviceId:(NSString *)deviceId
                        sessionKey:(NSString *)sessionKey;

/*!
 Magnetometerのイベントを登録を解除できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                      profile:(DPDiceProfile *)profile
didReceiveDeleteOnMagnetometerRequest:(DConnectRequestMessage *)request
                             response:(DConnectResponseMessage *)response
                             deviceId:(NSString *)deviceId
                           sessionKey:(NSString *)sessionKey;



@end

/*!
 @class DPDiceProfile
 @brief Diceプロファイル。
 
 Dice Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DPDiceProfile : DConnectProfile
/*!
 @brief DPDiceProfileのデリゲートオブジェクト。
 
 デリゲートは @link DPDiceProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, assign) id<DPDiceProfileDelegate> delegate;
@end
