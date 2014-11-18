//
//  DCMLightProfileName.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
/*! @file
 @brief Lightプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.7.22)
 */
#import <DConnectSDK/DConnectSDK.h>
/*! @brief プロファイル名: light。 */
extern NSString *const DCMLightProfileName;
/*!
 @brief インターフェイス: group。
 */
extern NSString *const DCMLightProfileInterfaceGroup;
/*!
 @brief 属性: create。
 */
extern NSString *const DCMLightProfileAttrCreate;
/*!
 @brief 属性: clear。
 */
extern NSString *const DCMLightProfileAttrClear;

/*!
 @brief パラメータ: lightId。
 */
extern NSString *const DCMLightProfileParamLightId;
/*!
 @brief パラメータ: name。
 */
extern NSString *const DCMLightProfileParamName;
/*!
 @brief パラメータ: color。
 */
extern NSString *const DCMLightProfileParamColor;
/*!
 @brief パラメータ: brightness。
 */
extern NSString *const DCMLightProfileParamBrightness;
/*!
 @brief パラメータ: flashing。
 */
extern NSString *const DCMLightProfileParamFlashing;
/*!
 @brief パラメータ: lights。
 */
extern NSString *const DCMLightProfileParamLights;
/*!
 @brief パラメータ: on。
 */
extern NSString *const DCMLightProfileParamOn;
/*!
 @brief パラメータ: config。
 */
extern NSString *const DCMLightProfileParamConfig;
/*!
 @brief パラメータ: groupId。
 */
extern NSString *const DCMLightProfileParamGroupId;
/*!
 @brief パラメータ: groups。
 */
extern NSString *const DCMLightProfileParamLightGroups;
/*!
 @brief パラメータ: lightIds。
 */
extern NSString *const DCMLightProfileParamLightIds;
/*!
 @brief パラメータ: groupName。
 */
extern NSString *const DCMLightProfileParamGroupName;


@class DCMLightProfile;

/*!
 @brief Light プロファイル。
 <p>
 デバイスのライト機能を提供するAPI。<br/>
 デバイスのライト機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DCMLightProfileDelegate<NSObject>
@optional

/*!
 @brief デバイスのライトのステータスを取得する.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 GET http://{ドメイン}/light?deviceid=xxxxx
 </pre>
 @param[in] profile プロファイル
@param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)              profile:(DCMLightProfile *)profile
    didReceiveGetLightRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId;

/*!
 @brief デバイスのライトを点灯する<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 POST http://{ドメイン}/light?deviceId=xxxxx&lightId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] lightId ライトID
 @param[in] brightness 明るさ
 @param[in] color 色
 @param[in] flashing 点滅
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)            profile:(DCMLightProfile *)profile
 didReceivePostLightRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                    lightId:(NSString*)lightId
                 brightness:(double)brightness
                      color:(NSString*)color
                   flashing:(NSArray*)flashing;
/*!
 @brief デバイスのライトのステータスを変更する.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{ドメイン}/light?deviceId=xxxxx&lightId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] lightId ライトID
 @param[in] name ライト名
 @param[in] brightness 明るさ
 @param[in] color 色
 @param[in] flashing 点滅
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)            profile:(DCMLightProfile *)profile
  didReceivePutLightRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                    lightId:(NSString*)lightId
                       name:(NSString*)name
                 brightness:(double)brightness
                      color:(NSString*)color
                   flashing:(NSArray*)flashing;
/*!
 @brief デバイスのライトを消灯させる<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{ドメイン}/light?deviceId=xxxxx&lightId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] lightId ライトID
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                 profile:(DCMLightProfile *)profile
    didReceiveDeleteLightRequest:(DConnectRequestMessage *)request
                        response:(DConnectResponseMessage *)response
                        deviceId:(NSString *)deviceId
                         lightId:(NSString*)lightId;




/*!
 @brief デバイスのライトグループのステータスを取得する.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 GET http://{ドメイン}/light/group?deviceid=xxxxx
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                profile:(DCMLightProfile *)profile
 didReceiveGetLightGroupRequest:(DConnectRequestMessage *)request
                       response:(DConnectResponseMessage *)response
                       deviceId:(NSString *)deviceId;

/*!
 @brief デバイスのライトグループを点灯する<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 POST http://{ドメイン}/light/group?deviceId=xxxxx&groupId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] groupId ライトグループID
 @param[in] brightness 明るさ
 @param[in] color 色
 @param[in] flashing 点滅
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                profile:(DCMLightProfile *)profile
didReceivePostLightGroupRequest:(DConnectRequestMessage *)request
                       response:(DConnectResponseMessage *)response
                       deviceId:(NSString *)deviceId
                        groupId:(NSString*)groupId
                     brightness:(double)brightness
                          color:(NSString*)color
                       flashing:(NSArray*)flashing;
/*!
 @brief デバイスのライトグループのステータスを変更する.<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 PUT http://{ドメイン}/light/group?deviceId=xxxxx&groupId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] groupId ライトグループID
 @param[in] name ライト名
 @param[in] brightness 明るさ
 @param[in] color 色
 @param[in] flashing 点滅
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                profile:(DCMLightProfile *)profile
 didReceivePutLightGroupRequest:(DConnectRequestMessage *)request
                       response:(DConnectResponseMessage *)response
                       deviceId:(NSString *)deviceId
                        groupId:(NSString*)groupId
                           name:(NSString*)name
                     brightness:(double)brightness
                          color:(NSString*)color
                       flashing:(NSArray*)flashing;
/*!
 @brief デバイスのライトグループを消灯させる<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{ドメイン}/light/group?deviceId=xxxxx&groupId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] groupId ライトID
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                    profile:(DCMLightProfile *)profile
  didReceiveDeleteLightGroupRequest:(DConnectRequestMessage *)request
                           response:(DConnectResponseMessage *)response
                           deviceId:(NSString *)deviceId
                            groupId:(NSString*)groupId;

/*!
 @brief デバイスのライトグループを作成<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 POST http://{ドメイン}/light/group/create?deviceId=xxxxx&groupId=yyyy&groupName=bathroom
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] groupId ライトグループID
 @param[in] groupName ライトグループ名
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                        profile:(DCMLightProfile *)profile
  didReceivePostLightGroupCreateRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                               lightIds:(NSArray*)lightIds
                              groupName:(NSString*)groupName;

/*!
 @brief デバイスのライトグループを削除する<br>
 
 実装されない場合には、Not supportのエラーが返却される。
 <pre>
 [対応するRESTful]
 DELETE http://{ドメイン}/light/group/clear?deviceId=xxxxx&groupId=yyyy
 </pre>
 @param[in] profile プロファイル
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] groupId ライトID
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL)                        profile:(DCMLightProfile *)profile
 didReceiveDeleteLightGroupClearRequest:(DConnectRequestMessage *)request
                               response:(DConnectResponseMessage *)response
                               deviceId:(NSString *)deviceId
                                groupId:(NSString*)groupId;


@end
/*!
 @class DCMLightProfile
 @brief Lightプロファイル。
 
 Light Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DCMLightProfile : DConnectProfile
/*!
 @brief DCMLightProfileのデリゲートオブジェクト。
 
 デリゲートは @link DCMLightProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, assign) id<DCMLightProfileDelegate> delegate;


@end
