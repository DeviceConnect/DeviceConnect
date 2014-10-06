//
//  DConnectDevicePlugin.h
//  dConnectManager
//
//  Created by 小林 伸郎 on 2014/05/02.
//  Copyright (c) 2014 NTT DOCOMO, INC. All Rights Reserved.
//

/*! @file
 @brief デバイスプラグイン実装するための機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.5.14)
 */
#import <Foundation/Foundation.h>
#import <DConnectSDK/DConnectProfile.h>
#import <DConnectSDK/DConnectProfileProvider.h>

/*! @brief デバイスプラグインの基底クラス.
 
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

/*! @brief デバイスプラグインの名前.
 プラグイン名を変更したい場合には、この変数に値を代入すること。
 */
@property (nonatomic) NSString *pluginName;

/*!
 @brief イベントを送信する.

 @param[in] event イベント
 @return 送信が成功した場合はtrue,それ以外はfalse
 */
- (BOOL) sendEvent:(DConnectMessage *) event;

/*!
 @birefリクエストを受領し、各メソッドにリクエストを配送する.
 
 @param[in] request リクエスト
 @param[in,out] response 返答を格納するレスポンス
 @return 各デバイスプラグインに配送する場合はtrue、それ以外はfalse
 */
- (BOOL) didReceiveRequest:(DConnectRequestMessage *) request response:(DConnectResponseMessage *) response;

/*!
 @brief アプリケーションがバックグラウンドに移ったときのイベント。
 */
- (void)applicationDidEnterBackground;

/*!
 @brief アプリケーションがフォアグランドに移ったときのイベント。
 */
- (void)applicationWillEnterForeground;

@end
