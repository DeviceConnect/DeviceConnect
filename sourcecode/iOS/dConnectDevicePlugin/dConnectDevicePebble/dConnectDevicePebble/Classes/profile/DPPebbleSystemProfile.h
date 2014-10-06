//
//  DPPebbleSystemProfile.h
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>
#import "DPPebbleManager.h"

@interface DPPebbleSystemProfile : DConnectSystemProfile <DConnectSystemProfileDelegate, DConnectSystemProfileDataSource>

/*!
 @brief PebbleManager付きで初期化する。
 @param[in] mgr PebbleManagerのインスタンス
 */
- (id) initWithPebbleManager:(DPPebbleManager *)mgr;

@end
