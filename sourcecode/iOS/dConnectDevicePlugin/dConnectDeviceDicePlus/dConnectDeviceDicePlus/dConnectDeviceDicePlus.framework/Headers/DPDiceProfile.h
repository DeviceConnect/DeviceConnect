//
//  DConnectMagnetometerProfile.h
//  dConnectDeviceDicePlus
//
//  Created by 星　貴之 on 2014/07/15.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
/*!
 @brief プロファイル名: Dice
 */
extern NSString *const DPDiceProfileName;
/*!
 @brief インターフェース名: Dice
 */
extern NSString *const DPDiceProfileInterfaceMagnetometer;

/*!
 @brief アトリビュート: Dice
 */
extern NSString *const DPDiceProfileAttrOnDice;
extern NSString *const DPDiceProfileAttrOnMagnetometer;
/*!
 @brief パラメータ: Dice
 */
extern NSString *const DPDiceProfileParamDice;
extern NSString *const DPDiceProfileParamPip;
extern NSString *const DPDiceProfileParamMagnetometer;
extern NSString *const DPDiceProfileParamFilter;
extern NSString *const DPDiceProfileParamInterval;
extern NSString *const DPDiceProfileParamX;
extern NSString *const DPDiceProfileParamY;
extern NSString *const DPDiceProfileParamZ;

@class DPDiceProfile;


/*!
 @brief プロトコル: DeviceMagnetometer
  */
@protocol DPDiceProfileDelegate<NSObject>
/*!
 Diceのイベントを登録できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{dConnectドメイン}/dice/ondice?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @return 同期的にRestfulなレスポンスを返すかどうか
 */
- (BOOL) profile:(DPDiceProfile *)profile didReceivePutOnDiceRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 Diceのイベントを登録を解除できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @return 同期的にRestfulなレスポンスを返すかどうか
 */
- (BOOL) profile:(DPDiceProfile *)profile didReceiveDeleteOnDiceRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 Magnetometerのイベントを登録できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @return 同期的にRestfulなレスポンスを返すかどうか
 */
- (BOOL) profile:(DPDiceProfile *)profile didReceivePutOnMagnetometerRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 Magnetometerのイベントを登録を解除できる.<br>
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{dConnectドメイン}/dice/magnetometer/onmagnetmeter?deviceId=xxxxx&sessionKey=yyyyy
 </pre>
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @return 同期的にRestfulなレスポンスを返すかどうか
 */
- (BOOL) profile:(DPDiceProfile *)profile didReceiveDeleteOnMagnetometerRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;


@optional
@end


@interface DPDiceProfile : DConnectProfile
/*!
 DPDiceProfileのデリゲート.
 */
@property (nonatomic, assign) id<DPDiceProfileDelegate> delegate;
@end
