//
//  DConnectManagerSystemProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSystemProfile.h"

/**
 * DConnectManager用のシステムプロファイル.
 */
@interface DConnectManagerSystemProfile : DConnectSystemProfile

/*!
 @brief システム情報を取得する。
 
 @param[in] request リクエスト
 @param[in] response レスポンス
 
 @retval YES 処理を行った場合
 @retval NO 処理を行われなかった場合
 */
- (BOOL) didReceiveGetSystemRequest:(DConnectRequestMessage *)request
response:(DConnectResponseMessage *)response;

@end
