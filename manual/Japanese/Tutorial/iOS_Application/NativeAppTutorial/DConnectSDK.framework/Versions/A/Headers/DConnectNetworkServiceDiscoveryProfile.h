//
//  DConnectNetworkServiceDiscoveryProfile.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Network Service Discoveryプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名: network_service_discovery。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileName;

/*!
 @brief アトリビュート: getnetworkservices。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileAttrGetNetworkServices;

/*!
 @brief アトリビュート: onservicechange。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileAttrOnServiceChange;

/*!
 @brief パラメータ: networkService。
 */

extern NSString *const DConnectNetworkServiceDiscoveryProfileParamNetworkService;

/*!
 @brief パラメータ: services。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamServices;

/*!
 @brief パラメータ: state。
 */

extern NSString *const DConnectNetworkServiceDiscoveryProfileParamState;

/*!
 @brief パラメータ: id。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamId;

/*!
 @brief パラメータ: name。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamName;

/*!
 @brief パラメータ: type。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamType;

/*!
 @brief パラメータ: online。
 */
extern NSString *const DConnectNetworkServiceDiscoveryProfileParamOnline;

/*!
 @brief パラメータ: config。
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
 @brief Network Service Dicovery Profileの各APIリクエスト通知用デリゲート。
 
 Network Service Dicovery Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectNetworkServiceDiscoveryProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 
 @brief 接続されているデバイス一覧の取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileが接続されているデバイス一覧の取得リクエストを受け取ったことをデリゲートに通知する。<br>
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
 
 @brief onservicechangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonservicechangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
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
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods

/*!
 
 @brief onservicechangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonservicechangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
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
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

@end

/*!
 @class DConnectNetworkServiceDiscoveryProfile
 @brief NetworkServiceDiscovery プロファイル。
 
 Network Service Discovery Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectNetworkServiceDiscoveryProfile : DConnectProfile

/*!
 @brief DConnectNetworkServiceDiscoveryProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectNetworkServiceDiscoveryProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectNetworkServiceDiscoveryProfileDelegate> delegate;

#pragma mark - Setter

/*!
 @brief メッセージにデバイス一覧を設定する。
 
 @param[in] services デバイス一覧
 @param[in,out] message デバイス一覧を格納するメッセージ
 */
+ (void) setServices:(DConnectArray *)services target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイス情報を設定する。
 
 @param[in] networkService デバイス情報
 @param[in,out] message デバイス情報を格納するメッセージ
 */
+ (void) setNetworkService:(DConnectMessage *)networkService target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスIDを設定する。
 
 @param[in] id デバイスID
 @param[in,out] message デバイスIDを格納するメッセージ
 */
+ (void) setId:(NSString *)id target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイス名を設定する。
 
 @param[in] name デバイス名
 @param[in,out] message デバイス名を格納するメッセージ
 */
+ (void) setName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスのネットワークタイプを設定する。
 
 @param[in] type デバイスのネットワークタイプ
 @param[in,out] message デバイスのネットワークタイプを格納するメッセージ
 */
+ (void) setType:(NSString *)type target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスのオンライン状態を設定する。
 
 @param[in] online オンライン: YES、 オフライン: NO
 @param[in,out] message デバイスのオンライン状態を格納するメッセージ
 */
+ (void) setOnline:(BOOL)online target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスの設定情報を設定する。
 
 @param[in] config 設定情報文字列
 @param[in,out] message デバイスの設定情報を格納するメッセージ
 */
+ (void) setConfig:(NSString *)config target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message デバイスの接続状態を格納するメッセージ
 */
+ (void) setState:(BOOL)state target:(DConnectMessage *)message;

@end
