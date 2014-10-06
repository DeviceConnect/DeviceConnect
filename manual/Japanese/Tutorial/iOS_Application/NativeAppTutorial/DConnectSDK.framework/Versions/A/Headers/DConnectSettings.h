//
//  DConnectSettings.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*!
 @file
 @brief Managerの設定を保持するクラス。
 @author NTT DOCOMO
 */

/*!
 @class DConnectSettings
 @brief DConnectManagerの設定を保持するクラス。
 <p>
 [[DConnectManager sharedManager] start]を行った後に設定しても反映されない。
 必ず、DConnectManager開始する前に設定を行うこと。
 </p>
 
 @code
 
 // DConnectManagerの初期化
 DConnectManager *mgr = [DConnectManager sharedManager];
 
 // 設定を行う
 mgr.settings.host = @"localhost";
 mgr.settings.port = 4035;
 
 // DConnectManagerの開始
 [mgr start];
 // websocketサーバの起動
 [mgr startWebsocket];
 
 @endcode
 */
@interface DConnectSettings : NSObject

/*!
 @brief DConnectManagerで使用するホスト名。
 
 デフォルトでは、localhostが設定してある。
 */
@property (nonatomic) NSString *host;

/*!
 @brief DConnectManagerで使用するポート番号。
 
 デフォルトでは、4035が設定してある。
 */
@property (nonatomic) int port;

/*!
 @brief Local OAuthの認証チェックのON/OFF。
 
 デフォルトではYES。
 */
@property (nonatomic) BOOL useLocalOAuth;

@end
