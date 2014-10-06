//
//  DConnectDevicePlugin.h
//  dConnectManager
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! 
 @file
 @brief デバイスプラグイン実装するための機能を提供する。
 @author NTT DOCOMO
 */
#import <DConnectSDK/DConnectProfileProvider.h>
#import <DConnectSDK/DConnectRequestMessage.h>
#import <DConnectSDK/DConnectResponseMessage.h>

/*! 
 @class DConnectDevicePlugin
 @brief デバイスプラグインのベースクラス。
 
 各デバイスプラグインはこのクラスを継承して作成すること。
 @code
 
 @interface ExampleDevicePlugin : DConnectDevicePlugin

 @end

 @implementation ExampleDevicePlugin

 - (id) init {
     self = [super init];
     if (self) {
         // プロファイルを追加
         [self addProfile:[ExampleNetworkServiceDiscoveryProfile new]];
         [self addProfile:[ExampleSystemProfile new]];
     }
     return self;
 }

 @end

 @endcode
 */
@interface DConnectDevicePlugin : NSObject <DConnectProfileProvider>

/*! 
 @brief デバイスプラグインの名前。
 
 プラグイン名を変更したい場合には、この変数に値を代入すること。
 */
@property (nonatomic) NSString *pluginName;

/*!
 @brief Local OAuth認証を行うフラグ。
 
 <p>
 認証を行わない場合にはNOを設定する。<br/>
 デフォルトではYESが設定されている。
 </p>
 */
@property (nonatomic) BOOL useLocalOAuth;

/*!
 @brief イベントを送信する。

 @param[in] event イベント
 @return 送信が成功した場合はYES、それ以外はNO
 */
- (BOOL) sendEvent:(DConnectMessage *) event;

/*!
 @brief リクエストを受領し、各メソッドにリクエストを配送する。
 
 @param[in] request リクエスト
 @param[in,out] response 返答を格納するレスポンス
 @return 各デバイスプラグインに配送する場合はYES、それ以外はNO
 */
- (BOOL) didReceiveRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 @brief アプリケーションがバックグラウンドへの遷移時に呼び出される。
 */
- (void)applicationDidEnterBackground;

/*!
 @brief アプリケーションがフォアグランドへの遷移時に呼び出される。
 */
- (void)applicationWillEnterForeground;

@end
