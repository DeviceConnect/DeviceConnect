//
//  DPPebbleSettingsProfile.h
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
#import "DPPebbleManager.h"

/*!
 @brief Pebble用 Settingsプロファイル。
 */
@interface DPPebbleSettingsProfile : DConnectSettingsProfile <DConnectSettingsProfileDelegate>

/*!
 @brief PebbleManager付きで初期化する。
 @param[in] mgr PebbleManagerのインスタンス
 */
- (id) initWithPebbleManager:(DPPebbleManager *)mgr;

@end
