//
//  DConnectSystemProfile.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief Systemプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfile.h>
#import <UIKit/UIKit.h>

/*!
 
 @brief プロファイル名: system。
 */
extern NSString *const DConnectSystemProfileName;


/*!
 @brief インターフェース: device。
 */
extern NSString *const DConnectSystemProfileInterfaceDevice;

/*!
 @brief アトリビュート: device。
 */
extern NSString *const DConnectSystemProfileAttrDevice;

/*!
 @brief アトリビュート: wakeup。
 */
extern NSString *const DConnectSystemProfileAttrWakeUp;

/*!
 @brief アトリビュート: keyword。
 */
extern NSString *const DConnectSystemProfileAttrKeyword;
/*!
 
 @brief アトリビュート: events。
 */
extern NSString *const DConnectSystemProfileAttrEvents;

/*!
 @brief パラメータ: supports。
 */
extern NSString *const DConnectSystemProfileParamSupports;

/*!
 @brief パラメータ: plugins。
 */
extern NSString *const DConnectSystemProfileParamPlugins;

/*!
 @brief パラメータ: pluginId。
 */
extern NSString *const DConnectSystemProfileParamPluginId;

/*!
 @brief パラメータ: id。
 */
extern NSString *const DConnectSystemProfileParamId;

/*!
 @brief パラメータ: name。
 */
extern NSString *const DConnectSystemProfileParamName;

/*!
 @brief パラメータ: version。
 */
extern NSString *const DConnectSystemProfileParamVersion;

/*!
 @brief パラメータ: connect。
 */
extern NSString *const DConnectSystemProfileParamConnect;

/*!
 @brief パラメータ: wifi。
 */
extern NSString *const DConnectSystemProfileParamWiFi;

/*!
 @brief パラメータ: bluetooth。
 */
extern NSString *const DConnectSystemProfileParamBluetooth;

/*!
 @brief パラメータ: nfc。
 */
extern NSString *const DConnectSystemProfileParamNFC;

/*!
 @brief パラメータ: ble。
 */
extern NSString *const DConnectSystemProfileParamBLE;

/*!
 @enum DConnectSystemProfileConnectState
 @brief デバイスの接続状態定数。
 */
typedef NS_ENUM(NSInteger, DConnectSystemProfileConnectState) {
    DConnectSystemProfileConnectStateNone = 0,  /*!< 非対応 */
    DConnectSystemProfileConnectStateOn,        /*!< 接続ON */
    DConnectSystemProfileConnectStateOff        /*!< 接続OFF */
};

@class DConnectSystemProfile;

/*!
 @protocol DConnectSystemProfileDelegate
 @brief System Profileの各APIリクエスト通知用デリゲート。
 
 System Profileの各APIへのリクエスト受信通知を受け取るデリゲート。
 */
@protocol DConnectSystemProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!

 @brief 周辺機器のシステム情報取得リクエストを受け取ったことをデリゲートに通知する。
 
 profileが周辺機器のシステム情報取得リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] Device System API [GET]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] deviceId デバイスID
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSystemProfile *)profile didReceiveGetDeviceRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId;

#pragma mark - Put Methods

/*!
 
 @brief DConnectManager設定キーワード表示リクエストを受け取ったことをデリゲートに通知する。
 
 profileがDConnectManager設定キーワード表示リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 @attention DConnectManager以外で実装する必要はない。
 
 <p>
 [対応するAPI] System Show Keyword API [PUT]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSystemProfile *)profile didReceivePutKeywordRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response;

#pragma mark - Delete Methods

/*!
 
 @brief イベント一括解除リクエストを受け取ったことをデリゲートに通知する。
 
 profileがイベント一括解除リクエストを受け取ったことをデリゲートに通知する。<br>
 実装されない場合には、Not supportのエラーが返却される。
 
 <p>
 [対応するAPI] System Events Unregister API [DELETE]
 </p>
 
 @param[in] profile プロファイル
 @param[in] request リクエストパラメータ
 @param[in,out] response レスポンスパラメータ
 @param[in] sessionKey セッションキー
 
 @retval YES レスポンスパラメータを返却する。
 @retval NO レスポンスパラメータを返却しないので、@link DConnectManager::sendResponse: @endlinkで返却すること。
 */
- (BOOL) profile:(DConnectSystemProfile *)profile didReceiveDeleteEventsRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
      sessionKey:(NSString *)sessionKey;

@end


/*!
 @protocol DConnectSystemProfileDataSource
 @brief Systemプロファイルのデータソース。

 DConnectSystemProfileにシステム情報を提供するデータソース。
 */

@protocol DConnectSystemProfileDataSource <NSObject>
@optional

/*!
 
 @brief データソースにWiFiの接続状態を要求する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneをレスポンスとして返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile
                         wifiStateForDeviceId:(NSString *)deviceId;

/*!
 @brief データソースにBluetoothの接続状態を要求する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneをレスポンスとして返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile
                    bluetoothStateForDeviceId:(NSString *)deviceId;

/*!
 @brief データソースにNFCの接続状態を要求する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneをレスポンスとして返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile
                          nfcStateForDeviceId:(NSString *)deviceId;

/*!
 @brief データソースにBLEの接続状態を要求する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneをレスポンスとして返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile
                          bleStateForDeviceId:(NSString *)deviceId;

@required

/*!
 @brief データソースにデバイスプラグインのバージョンを要求する。
 
 @param[in] profile プロファイル
 
 @retval NSString* バージョン
 */
- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile;

/*!
 @brief データソースにデバイスプラグインの設定画面用のUIViewControllerを要求する。
 @attention このメソッドはUIスレッドで呼び出される。
 
 @param[in] sender プロファイル
 @param[in] request リクエスト
 
 @retval UIViewController* 設定画面用のUIViewController
 */
- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request;

@end

/*!
 @class DConnectSystemProfile
 @brief Systemプロファイル。
 
 System Profileの各APIへのリクエストを受信する。
 受信したリクエストは各API毎にデリゲートに通知される。
 */
@interface DConnectSystemProfile : DConnectProfile

/*!
@brief DConnectSystemProfileのデリゲートオブジェクト。

デリゲートは @link DConnectSystemProfileDelegate @endlink を実装しなくてはならない。
デリゲートはretainされない。
*/
@property (nonatomic, weak) id<DConnectSystemProfileDelegate> delegate;

/*!
 @brief DConnectSystemProfileのデータソースオブジェクト。
 
 データソースは @link DConnectSystemProfileDataSource @endlink を実装しなくてはならない。
 データソースはretainされない。
 */
@property (nonatomic, weak) id<DConnectSystemProfileDataSource> dataSource;

#pragma mark - Setter

/*!
 @brief メッセージにバージョンを格納する。
 
 @param[in] version バージョン名
 @param[in,out] message バージョンを格納するメッセージ
 */
+ (void) setVersion:(NSString *)version target:(DConnectMessage *)message;

/*!
 @brief メッセージにサポートしているI/Fの一覧を格納する。
 
 @param[in] supports サポートしているI/F一覧
 @param[in,out] message I/Fの一覧を格納するメッセージ
 */
+ (void) setSupports:(DConnectArray *)supports target:(DConnectMessage *)message;

/*!
 @brief メッセージにサポートしているデバイスプラグインの一覧を格納する。
 
 @param[in] plugins サポートしているデバイスプラグイン一覧
 @param[in,out] message デバイスプラグインの一覧を格納するメッセージ
 */
+ (void) setPlugins:(DConnectArray *)plugins target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグインIDを格納する。
 
 @param[in] pluginId デバイスプラグインID
 @param[in,out] message デバイスプラグインIDを格納するメッセージ
 */
+ (void) setId:(NSString *)pluginId target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグイン名を格納する。
 
 @param[in] name デバイスプラグイン名
 @param[in,out] message デバイスプラグイン名を格納するメッセージ
 */
+ (void) setName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスの接続状態を設定する。
 
 @param[in] connect デバイスの接続状態
 @param[in,out] message デバイスの接続状態を格納するメッセージ
 */
+ (void) setConnect:(DConnectMessage *)connect target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグインのWiFiの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message WiFiの接続状態を格納するメッセージ
 */
+ (void) setWiFiState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグインのBluetoothの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message Bluetoothの接続状態を格納するメッセージ
 */
+ (void) setBluetoothState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグインのNFCの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message NFCの接続状態を格納するメッセージ
 */
+ (void) setNFCState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief メッセージにデバイスプラグインのBLEの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message BLEの接続状態を格納するメッセージ
 */
+ (void) setBLEState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

#pragma mark - Getter

/*!
 @brief リクエストからpluginIdを取得する。
 
 @param[in] request リクエストパラメータ
 @retval NSString* デバイスプラグインID
 @retval nil pluginIdが指定されていない場合
 */
+ (NSString *) pluginIdFromRequest:(DConnectMessage *)request;

@end
