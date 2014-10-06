//
//  DPPebbleNetworkServiceDiscoveryProfile.h
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
#import "DPPebbleManager.h"

/*!
 @brief Pebble用 Network Service Discovery プロファイル。
 */
@interface DPPebbleNetworkServiceDiscoveryProfile : DConnectNetworkServiceDiscoveryProfile <DConnectNetworkServiceDiscoveryProfileDelegate>

/*!
 @brief PebbleManager付きで初期化する。
 @param[in] mgr PebbleManagerのインスタンス
 */
- (id) initWithPebbleManager:(DPPebbleManager *)mgr;

@end
