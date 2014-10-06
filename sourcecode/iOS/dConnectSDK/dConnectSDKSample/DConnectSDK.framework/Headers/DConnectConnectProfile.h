//
//  DConnectConnectProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Connectプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>

/*!
 @brief プロファイル名。
 */
extern NSString *const DConnectConnectProfileName;

/*!
 @brief インターフェース: bluethooth。
 */
extern NSString *const DConnectConnectProfileInterfaceBluetooth;

/*!
 @brief アトリビュート: wifi。
 */
extern NSString *const DConnectConnectProfileAttrWifi;

/*!
 @brief アトリビュート: bluetooth。
 */
extern NSString *const DConnectConnectProfileAttrBluetooth;

/*!
 @brief アトリビュート: discoverable。
 */
extern NSString *const DConnectConnectProfileAttrDiscoverable;

/*!
 @brief アトリビュート: ble。
 */
extern NSString *const DConnectConnectProfileAttrBLE;

/*!
 @brief アトリビュート: nfc。
 */
extern NSString *const DConnectConnectProfileAttrNFC;

/*!
 @brief アトリビュート: wifichange。
 */
extern NSString *const DConnectConnectProfileAttrOnWifiChange;

/*!
 @brief アトリビュート: bluetoothchange。
 */
extern NSString *const DConnectConnectProfileAttrOnBluetoothChange;

/*!
 @brief アトリビュート: blechange。
 */
extern NSString *const DConnectConnectProfileAttrOnBLEChange;

/*!
 @brief アトリビュート: nfcchange。
 */
extern NSString *const DConnectConnectProfileAttrOnNFCChange;

/*!
 @brief パラメータ: enable。
 */
extern NSString *const DConnectConnectProfileParamEnable;

/*!
 @brief パラメータ: connectStatus。
 */
extern NSString *const DConnectConnectProfileParamConnectStatus;

@class DConnectConnectProfile;

/*!
 @protocol DConnectConnectProfileDelegate
 @brief Connect Profile各のAPIリクエスト通知用デリゲート。
 
 Connect Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectConnectProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief wifi取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがwifi取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] WiFi Connect API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetWifiRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief bluetooth取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがbluetooth取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Connect API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetBluetoothRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief ble取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがble取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] BLE Connect API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetBLERequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief nfc取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileがnfc取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] NFC Connect API [GET]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveGetNFCRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 @brief WiFi機能有効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがWiFi機能有効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] WiFi Connect API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutWiFiRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief Bluetooth機能有効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBluetooth機能有効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Connect API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutBluetoothRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief Bluetooth検索可能状態有効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBluetooth検索可能状態有効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Discoverable Status API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutBluetoothDiscoverableRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief BLE機能有効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBLE機能有効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] BLE Connect API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutBLERequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief NFC機能有効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがNFC機能有効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] NFC Connect API [PUT]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutNFCRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

#pragma mark Event Registration

/*!
 @brief onwifichangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonwifichangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] WiFi Connect Status Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnWifiChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief onbluetoothchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonbluetoothchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Connect Status Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnBluetoothChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief onblechangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonblechangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] BLE Connect Status Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnBLEChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

/*!
 @brief onnfcchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonnfcchangeイベント登録リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] NFC Connect Status Change Event API [Register]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceivePutOnNFCChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId sessionKey:(NSString *)sessionKey;

#pragma mark - Delete Methods

/*!
 @brief WiFi機能無効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがWiFi機能無効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] WiFi Connect API [DELETE]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteWiFiRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief Bluetooth機能無効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBluetooth機能無効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Connect API [DELETE]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteBluetoothRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief Bluetooth検索可能状態無効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBluetooth検索可能状態無効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Discoverable Status API [DELETE]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteBluetoothDiscoverableRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

/*!
 @brief BLE機能無効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがBLE機能無効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] BLE Connect API [DELETE]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteBLERequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId;

/*!
 @brief NFC機能無効化リクエストを受け取ったことをデリゲートに通知する。
 
 profileがNFC機能無効化リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] NFC Connect API [DELETE]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlink で返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteNFCRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

#pragma mark Event Unregistration

/*!
 @brief onwifichangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonwifichangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] WiFi Connect Status Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnWifiChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onbluetoothchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonbluetoothchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Bluetooth Connect Status Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnBluetoothChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onblechangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonblechangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] BLE Connect Status Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnBLEChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;

/*!
 @brief onnfcchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがonnfcchangeイベント解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] NFC Connect Status Change Event API [Unregister]
 </p>
 
 @param[in] profile このイベントを通知するDConnectConnectProfileのオブジェクト
 @param[in] request リクエスト
 @param[in,out] response レスポンス
 @param[in] deviceId デバイスID
 @param[in] sessionKey セッションキー
 @retval YES レスポンスパラメータを返却する
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectConnectProfile *)profile didReceiveDeleteOnNFCChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionKey;


@end

/*!
 @class DConnectConnectProfile
 @brief Connectプロファイル。
 
 Connect Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectConnectProfile : DConnectProfile

/*!
 @brief ConnectProfileのデリゲートオブジェクト。
 
 デリゲートは @link DConnectConnectProfileDelegate @endlink を実装しなくてはならない。
 デリゲートはretainされない。
 */
@property (nonatomic, weak) id<DConnectConnectProfileDelegate> delegate;

#pragma mark - Setters

/*!
 @brief メッセージに有効状態を設定する。
 @param[in] enable 有効状態
 @param[in,out] message 有効状態を格納するメッセージ
 */
+ (void) setEnable:(BOOL)enable target:(DConnectMessage *)message;

/*!
 @brief メッセージに接続状態を設定する。
 @param[in] connectStatus 接続状態
 @param[in,out] message 接続状態を格納するメッセージ
 */
+ (void) setConnectStatus:(DConnectMessage *)connectStatus target:(DConnectMessage *)message;

@end
