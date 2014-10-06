//
//  DConnectNetworkServiceDiscoveryProfile.h
//  dConnectManager
//
//  Created by 小林 伸郎 on 2014/05/02.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief Network Service Discoveryプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>

/*! @brief プロファイル名: network_service_discovery。 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileName;

/*!
 @brief 属性: getnetworkservices.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices;

/*!
 @brief 属性: onservicechange.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange;

/*!
 @brief パラメータ: networkService.
 */

extern NSString *const DConnectNetworkServiceDiscoveryProfileParamNetworkService;

/*!
 @brief パラメータ: services.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamServices;

/*!
 @brief パラメータ: state.
 */

extern NSString *const DConnectNetworkServiceDiscoveryProfileParamState;

/*!
 @brief パラメータ: id.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamId;

/*!
 @brief パラメータ: name.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamName;

/*!
 @brief パラメータ: type.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamType;

/*!
 @brief パラメータ: online.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamOnline;

/*!
 @brief パラメータ: config.
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamConfig;

/*!
 @brief ネットワークタイプ: unknown。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeUnknown;
/*!
 @brief ネットワークタイプ: wifi。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeWiFi;
/*!
 @brief ネットワークタイプ: bluetooth。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeBluetooth;
/*!
 @brief ネットワークタイプ: nfc。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeNFC;
/*!
 @brief ネットワークタイプ: ble。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileNetworkTypeBLE;

@class DConnectNetworkServiceDiscoveryProfile;

/*!
 @protocol DConnectNetworkServiceDiscoveryProfileDelegate
 @brief Network Service Discovery プロファイルのデリゲート。
 
 <p>
 スマートデバイス検索機能を提供するAPI。<br/>
 スマートデバイス検索機能を提供するデバイスプラグインは当クラスを継承し、対応APIを実装すること。 <br/>
 本クラスでは Found Event と Lost Event は処理しない。デバイスプラグインの任意のタイミングでデバイスの検出、消失のイベントメッセージをd-Connectに送信する必要がある。
 </p>
 */
@protocol DConnectNetworkServiceDiscoveryProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 dConnect Managerに接続されている、デバイスプラグイン対応デバイス一覧を取得する。
 
 各デバイスプラグインは、この関数を実装することで対応デバイスの検索が行える。
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Network Service Discovery API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response;

#pragma mark - Put Methods

/*!
 onservicechangeイベント登録リクエストハンドラー。
 
 onservicechangeイベントを登録し、その結果をレスポンスパラメータに格納する。
 
 各デバイスプラグインは、この関数を実装することで対応デバイスの検索が行える。
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Network Service Discovery Status Change Event API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceivePutOnServiceChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods

/*!
 onservicechangeイベント解除リクエストハンドラー。
 
 onservicechangeイベントを解除し、その結果をレスポンスパラメータに格納する。
 
 各デバイスプラグインは、この関数を実装することで対応デバイスの検索が行える。
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Network Service Discovery Status Change Event API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectNetworkServiceDiscoveryProfile *)profile didReceiveDeleteOnServiceChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

@end


/*!
 @brief NetworkServiceDiscoveryプロファイル.
 
 以下のメソッドを実装することで、NetworkServiceDiscoveryプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 - didReceiveGetGetNetworkServicesRequest: response:
 
 以下にサンプルソースを記載する。
 @todo TODO: サンプルソース Here!
 */
@interface DConnectNetworkServiceDiscoveryProfile : DConnectProfile

/*!
 @brief DConnectNetworkServiceDiscoveryProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectNetworkServiceDiscoveryProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief dConnectメッセージにデバイス一覧を設定する。
 
 @param[in] services デバイス一覧
 @param[in,out] message dConnectメッセージ
 */
+ (void) setServices:(DConnectArray *)services target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイス情報を設定する。
 
 @param[in] networkService デバイス情報
 @param[in,out] message dConnectメッセージ
 */
+ (void) setNetworkService:(DConnectMessage *)networkService target:(DConnectMessage *)message;

/*!
 @brief デバイスIDを設定する。
 
 @param[in] id デバイスID
 @param[in,out] message dConnectメッセージ
 */
+ (void) setId:(NSString *)id target:(DConnectMessage *)message;

/*!
 @brief デバイス名を設定する。
 
 @param[in] name デバイス名
 @param[in,out] message dConnectメッセージ
 */
+ (void) setName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief デバイスのネットワークタイプを設定する。
 
 @param[in] type デバイスのネットワークタイプ
 @param[in,out] message dConnectメッセージ
 */
+ (void) setType:(NSString *)type target:(DConnectMessage *)message;

/*!
 @brief デバイスのオンライン状態を設定する。
 
 @param[in] online オンライン: true、 オフライン: false
 @param[in,out] message dConnectメッセージ
 */
+ (void) setOnline:(BOOL)online target:(DConnectMessage *)message;

/*!
 @brief デバイスの設定情報を設定する。
 
 @param[in] config 設定情報文字列
 @param[in,out] message dConnectメッセージ
 */
+ (void) setConfig:(NSString *)config target:(DConnectMessage *)message;

/*!
 @brief デバイスの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setState:(BOOL)state target:(DConnectMessage *)message;

@end
