//
//  DPHueConst.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! @file
 @brief Hueの機能を管理する。
 @author NTT DOCOMO
 @date 作成日(2014.7.15)
 */
#import <Foundation/Foundation.h>
#import <HueSDK_iOS/HueSDK.h>
#import <DConnectSDK/DConnectSDK.h>
#import <DCMDevicePluginSDK/DCMDevicePluginSDK.h>


/*!
 @class DPHueManager
 @brief Hueのマネージャクラス。
 
 Hueの機能を管理する。
 */
@interface DPHueManager : NSObject {
    PHHueSDK *phHueSDK;
    PHNotificationManager *notificationManager;
    PHBridgeSearching *bridgeSearching;
}

/*!
 @brief Hueデバイスプラグインのレスポンスステータス。
 */
typedef enum BridgeConnectState : NSInteger {
    STATE_INIT,
    STATE_CONNECT,
    STATE_NON_CONNECT,
    STATE_NOT_AUTHENTICATED,
    STATE_ERROR_NO_NAME,
    STATE_ERROR_NO_LIGHTID,
    STATE_ERROR_INVALID_LIGHTID,
    STATE_ERROR_LIMIT_GROUP,
    STATE_ERROR_CREATE_FAIL_GROUP,
    STATE_ERROR_DELETE_FAIL_GROUP,
    STATE_ERROR_NOT_FOUND_LIGHT,
    STATE_ERROR_NO_GROUPID,
    STATE_ERROR_NOT_FOUND_GROUP,
    STATE_ERROR_INVALID_COLOR,
    STATE_ERROR_UPDATE_FAIL_LIGHT_STATE,
    STATE_ERROR_CHANGE_FAIL_LIGHT_NAME,
    STATE_ERROR_UPDATE_FAIL_GROUP_STATE,
    STATE_ERROR_CHANGE_FAIL_GROUP_NAME,
    STATE_ERROR_INVALID_BRIGHTNESS
} BridgeConnectState;

/*!
 @brief Hue Bridge リスト。
 */
@property (nonatomic) NSDictionary *hueBridgeList;
/*!
 @brief Hue Brdige のステータス。
 */
@property (nonatomic) BridgeConnectState bridgeConnectState;


/*!
 @brief Lightのステートを返すブロック。
 */
typedef void (^DPHueLightStatusBlock)(BridgeConnectState state);

/*!
 @brief DPHueManagerの共有インスタンスを返す。
 @return DPHueManagerの共有インスタンス。
 */
+(instancetype)sharedManager;

/*!
 @brief ブリッジの初期化
 */
-(void)initHue;


/*!
 @brief ブリッジの検索。
 @param[out] completionHandler ブリッジの検索結果を通知するブロック。
 */
-(void)searchBridgeWithCompletion:(PHBridgeSearchCompletionHandler)completionHandler;

/*!
 @brief ブリッジへの認証依頼を行う。
 @param[in] ipAddress ブリッジのIPアドレス。
 @param[in] macAddress ブリッジのMacアドレス。
 @param[in, out] receiver ブリッジからのレスポンスを通知されるインスタンス。
 @param[in, out] localConnectionSuccessSelector ブリッジからの成功レスポンスが通知されるセレクター。
 @param[in, out] noLocalConnection ブリッジからの失敗レスポンスが通知されるセレクター。
 @param[in, out] notAuthenticated ブリッジからの認証失敗のレスポンスが通知されるセレクター。
 */
-(void)startAuthenticateBridgeWithIpAddress:(NSString*)ipAddress
                                 macAddress:(NSString*)macAddress
                                   receiver:(id)receiver
             localConnectionSuccessSelector:(SEL)localConnectionSuccessSelector
                          noLocalConnection:(SEL)noLocalConnection
                           notAuthenticated:(SEL)notAuthenticated;

/*!
 @brief Pushlinkの確認開始。
 @param[in, out] receiver Pushlinkのレスポンスを通知されるインスタンス。
 @param[in, out] pushlinkAuthenticationSuccessSelector Pushlinkの成功レスポンスが通知されるセレクター。
 @param[in, out] pushlinkAuthenticationFailedSelector Pushlinkの失敗レスポンスが通知されるセレクター。
 @param[in, out] pushlinkNoLocalConnectionSelector PUSHLINKブリッジに接続できないことが通知されるセレクター。
 @param[in, out] pushlinkNoLocalBridgeSelector ブリッジが見つからないことが通知されるセレクター。
 @param[in, out] pushlinkButtonNotPressedSelector Pushlinkのボタンが押されていないことが通知されるセレクター。
 */
-(void)     startPushlinkWithReceiver:(id)receiver
pushlinkAuthenticationSuccessSelector:(SEL)pushlinkAuthenticationSuccessSelector
 pushlinkAuthenticationFailedSelector:(SEL)pushlinkAuthenticationFailedSelector
    pushlinkNoLocalConnectionSelector:(SEL)pushlinkNoLocalConnectionSelector
        pushlinkNoLocalBridgeSelector:(SEL)pushlinkNoLocalBridgeSelector
     pushlinkButtonNotPressedSelector:(SEL)pushlinkButtonNotPressedSelector;

/*!
 @brief 使用できるライトの検索。
 @param[out] completionHandler ライトの検索結果を通知するブロック。
 */
-(void)searchLightWithCompletion:(PHBridgeSendErrorArrayCompletionHandler)completion;

/*!
 @brief ライトステータスの取得。
 @param[out] response DeviceConnectにレスポンスを返すためのインスタンス。
 */
-(NSDictionary*)getLightStatus;



/*!
 @brief ライトグループステータスの取得。
 @param[out] response DeviceConnectにレスポンスを返すためのインスタンス。
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(NSDictionary*)getLightGroupStatus;


/*!
 @brief ライトIDのチェック。
 @param[in] lightId ライトのID
 */
-(BOOL)checkParamLightId:(NSString*)lightId;

/*!
 @brief ライトグループIDのチェック。
 @param[in] groupId ライトグループのID
 */
-(BOOL)checkParamGroupId:(NSString*)groupId;

/*!
 @brief 設定するライトのステータスをPHLightStateのインスタンスに設定。
 @param[in] isOn true(On)/false(Off)
 @param[in] brightness ライトの明るさ
 @param[in] color ライトの色
 @retval PHLightState
 */
- (PHLightState*) getLightStateIsOn:(BOOL)isOn
                         brightness:(double)brightness
                              color:(NSString *)color;


/*!
 @brief パラメータのチェックを行う。
 @param[in] param リクエストパラメータ
 @param[in] errorState エラーステータス
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)checkParamRequiredStringItemWithParam:(NSString*)param
                                   errorState:(BridgeConnectState)errorState;
/*!
 @brief ライトのステータスを変更する。
 @param[in] lightId ライトのID
 @param[in] lightState 変更するステータス
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)changeLightStatusWithLightId:(NSString *)lightId
                         lightState:(PHLightState*)lightState
                         completion:(void(^)())completion;


/*!
 @brief ライト名の変更。
 @param[in] lightId ライトのID
 @param[in] name 変更後のライトの名前
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)changeLightNameWithLightId:(NSString *)lightId
                             name:(NSString *)name
                       completion:(void(^)())completion;

/*!
 @brief ライトグループのステータスを変更する。
 @param[in] groupId ライトグループのID
 @param[in] lightState 変更するステータス
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)changeGroupStatusWithGroupId:(NSString *)groupId
                          lightState:(PHLightState*)lightState
                          completion:(void(^)())completion;


/*!
 @brief ライトグループの名前を変更する。
 @param[in] groupId ライトグループのID
 @param[in] name 変更後のライトグループの名前
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)changeGroupNameWithGroupId:(NSString *)groupId
                               name:(NSString *)name
                         completion:(void(^)())completion;


/*!
 @brief ライトグループの作成。
 @param[in] lightIds 追加するライトのID
 @param[in] groupName 作成するライトグループの名前
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)createLightGroupWithLightIds:(NSArray*)lightIds
                          groupName:(NSString*)groupName
                         completion:(void(^)(NSString* groupId))completion;


/*!
 @brief ライトグループの削除。
 @param[in] groupId 削除するライトグループのID
 @param[in, out] completion レスポンスを返す
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
-(BOOL)removeLightGroupWithWithGroupId:(NSString*)groupId
                            completion:(void(^)())completion;

/*!
 @brief 文字列の実数判定。
 @param[in] numberString 数値判定する文字列
 @retval YES 実数である
 @retval NO 実数ではない
 */
- (BOOL)isDigitWithString:(NSString *)numberString;


/*!
 @brief ハートビートの有効化。
 */
-(void)enableHeartbeat;

/*!
 @brief ハートビートの無効化。
 */
-(void)disableHeartbeat;

/*!
 @brief PHNotificationManagerの解放。
 @param[in] receiver 登録してあるオブジェクト
 */
-(void)deallocPHNotificationManagerWithReceiver:(id)receiver;


/*!
 @brief PHNotificationManagerとPHHueSDKの解放。
 */
-(void)deallocHueSDK;


/*!
 @brief Hueのブリッジを保存。
 */
-(void)saveBridgeList;

/*!
 @brief Hueのブリッジの読み込み。
 */
-(void)readBridgeList;

@end
