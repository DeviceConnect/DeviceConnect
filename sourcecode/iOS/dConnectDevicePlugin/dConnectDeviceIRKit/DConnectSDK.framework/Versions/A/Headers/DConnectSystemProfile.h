//
//  DConnectSystemProfile.h
//  dConnectManager
//
//  Created by 小林 伸郎 on 2014/05/02.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief Systemプロファイルを実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <DConnectSDK/DConnectProfile.h>
#import <UIKit/UIKit.h>

/*! @brief プロファイル名: system。 */
extern NSString *const DConnectSystemProfileName;

/*! @brief インターフェース: device。 */
extern NSString *const DConnectSystemProfileInterfaceDevice;
/*! @brief 属性: device。 */
extern NSString *const DConnectSystemProfileAttrDevice;
/*! @brief 属性: wakeup。 */
extern NSString *const DConnectSystemProfileAttrWakeUp;
/*! @brief 属性: keyword。 */
extern NSString *const DConnectSystemProfileAttrKeyword;
/*! @brief 属性: events。 */
extern NSString *const DConnectSystemProfileAttrEvents;

/*! @brief パラメータ: supports。 */
extern NSString *const DConnectSystemProfileParamSupports;
/*! @brief パラメータ: plugins。 */
extern NSString *const DConnectSystemProfileParamPlugins;
/*! @brief パラメータ: pluginId。 */
extern NSString *const DConnectSystemProfileParamPluginId;
/*! @brief パラメータ: id。 */
extern NSString *const DConnectSystemProfileParamId;
/*! @brief パラメータ: name。 */
extern NSString *const DConnectSystemProfileParamName;
/*! @brief パラメータ: version。 */
extern NSString *const DConnectSystemProfileParamVersion;
/*! @brief パラメータ: connect。 */
extern NSString *const DConnectSystemProfileParamConnect;
/*! @brief パラメータ: wifi。 */
extern NSString *const DConnectSystemProfileParamWiFi;
/*! @brief パラメータ: bluetooth。 */
extern NSString *const DConnectSystemProfileParamBluetooth;
/*! @brief パラメータ: nfc。 */
extern NSString *const DConnectSystemProfileParamNFC;
/*! @brief パラメータ: ble。 */
extern NSString *const DConnectSystemProfileParamBLE;

/*!
 @enum DConnectSystemProfileConnectState
 @brief デバイスの接続状態定数。
 */
typedef NS_ENUM(NSInteger, DConnectSystemProfileConnectState) {
    DConnectSystemProfileConnectStateNone = 0,  /*!< 非対応。 */
    DConnectSystemProfileConnectStateOn,        /*!< 接続ON。 */
    DConnectSystemProfileConnectStateOff        /*!< 接続OFF。 */
};

@class DConnectSystemProfile;

/*!
 @protocol DConnectSystemProfileDelegate
 @brief Systemプロファイルのデリゲート。
 
 <p>
 システム情報を提供するAPI。<br/>
 システム情報を提供するデバイスプラグインは当デリゲートを継承し、対応APIを実装すること。 <br/>
 </p>
 */
@protocol DConnectSystemProfileDelegate <NSObject>
@optional

#pragma mark - Get Methods

/*!
 @brief 周辺機器のシステム情報取得リクエストハンドラー。
 
 周辺機器のシステム情報取得を提供し、その結果をレスポンスパラメータに格納する。
 
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
 @brief d-ConnectManager設定キーワード表示リクエストハンドラー。
 
 d-ConnectManagerに設定されているキーワードを表示し、その結果をレスポンスパラメータに格納する。<br/>
 @note d-ConnectManager以外で実装する必要はない。
 
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
 @brief イベント解除リクエストハンドラー。
 
 指定されたセッションキーに紐づくイベントを全て解除し、その結果をレスポンスパラメータに格納する。
 
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
        response:(DConnectResponseMessage *)response sessionKey:(NSString *)sessionKey;

@end


/*!
 @protocol DConnectSystemProfileDataSource
 @brief Systemプロファイルのデータソースデリゲート。

 <p>
 情報を取得する。
 </p>
 */
@protocol DConnectSystemProfileDataSource <NSObject>
@optional

/*!
 @brief WiFiの接続状態を取得する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneを返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile wifiStateForDeviceId:(NSString *)deviceId;

/*!
 @brief Bluetoothの接続状態を取得する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneを返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile bluetoothStateForDeviceId:(NSString *)deviceId;

/*!
 @brief NFCの接続状態を取得する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneを返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile nfcStateForDeviceId:(NSString *)deviceId;

/*!
 @brief BLEの接続状態を取得する。
 
 実装されていない場合にはDConnectSystemProfileConnectStateNoneを返却する。
 
 @param[in] profile プロファイル
 @param[in] deviceId デバイスID
 
 @retval DConnectSystemProfileConnectStateNone 非対応
 @retval DConnectSystemProfileConnectStateOn 接続ON
 @retval DConnectSystemProfileConnectStateOff 接続OFF
 */
- (DConnectSystemProfileConnectState) profile:(DConnectSystemProfile *)profile bleStateForDeviceId:(NSString *)deviceId;

@required

/*!
 @brief デバイスプラグインのバージョンを取得する。
 
 @param[in] profile プロファイル
 
 @retval NSString* バージョン
 */
- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile;

/*!
 @brief デバイスプラグインの設定画面用のUIViewControllerを取得する。
 
 @param[in] sender プロファイル
 @param[in] request リクエスト
 
 @retval UIViewController* 設定画面用のUIViewController
 */
- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request;

@end

/*!
 @class DConnectSystemProfile
 @brief Systemプロファイル.
 
 以下のメソッドを実装することで、Systemプロファイルを実装することができる。<br>
 実装されない部分は未サポートエラーを返却する。
 @par
 @li wifiStateForDeviceId:
 @li bluetoothStateForDeviceId:
 @li nfcStateForDeviceId:
 @li bleStateForDeviceId:
 @li didReceiveGetDeviceRequest: response: deviceId:
 @li didReceivePutWakeUpRequest: response: deviceId:
 @li didReceiveDeleteWakeUpRequest: response: deviceId:
 */
@interface DConnectSystemProfile : DConnectProfile

/*!
 @brief DConnectSystemProfileDelegateを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectSystemProfileDelegate> delegate;

/*!
 @brief DConnectSystemProfileDataSourceを実装したデリゲートを設定する。
 
 retainはされません。
 */
@property (nonatomic, weak) id<DConnectSystemProfileDataSource> dataSource;

#pragma mark - Setter

/*!
 @brief dConnectメッセージにバージョンを格納する。
 
 @param[in] version バージョン名
 @param[in,out] message dConnectメッセージ
 */
+ (void) setVersion:(NSString *)version target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにサポートしているI/Fの一覧を格納する。
 
 @param[in] supports サポートしているI/F一覧
 @param[in,out] message dConnectメッセージ
 */
+ (void) setSupports:(DConnectArray *)supports target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにサポートしているデバイスプラグインの一覧を格納する。
 
 @param[in] plugins サポートしているデバイスプラグイン一覧
 @param[in,out] message dConnectメッセージ
 */
+ (void) setPlugins:(DConnectArray *)plugins target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグインIDを格納する。
 
 @param[in] pluginId デバイスプラグインID
 @param[in,out] message dConnectメッセージ
 */
+ (void) setId:(NSString *)pluginId target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグイン名を格納する。
 
 @param[in] name デバイスプラグイン名
 @param[in,out] message dConnectメッセージ
 */
+ (void) setName:(NSString *)name target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスの接続状態を設定する。
 
 @param[in] connect デバイスの接続状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setConnect:(DConnectMessage *)connect target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグインの接続状態にWiFiの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setWiFiState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグインの接続状態にBluetoothの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setBluetoothState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグインの接続状態にNFCの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message dConnectメッセージ
 */
+ (void) setNFCState:(DConnectSystemProfileConnectState)state target:(DConnectMessage *)message;

/*!
 @brief dConnectメッセージにデバイスプラグインの接続状態にBLEの接続状態を設定する。
 
 @param[in] state 接続状態
 @param[in,out] message dConnectメッセージ
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
