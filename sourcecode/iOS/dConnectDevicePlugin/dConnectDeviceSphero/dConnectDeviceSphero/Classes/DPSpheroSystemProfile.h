//
//  DPSpheroSystemProfile.h
//  dConnectDeviceSphero
//
//  Created by 星貴之 on 2014/06/23.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//
/*! @file
 @brief SpheroデバイスプラグインのSystemProfile機能を提供する。
 @author NTT DOCOMO
 @date 作成日(2014.7.22)
 */
#import <DConnectSDK/DConnectSDK.h>
/*!
 @class DPSpheroSystemProfile
 @brief SpheroデバイスプラグインのSystemProfile機能を提供する
 */
@interface DPSpheroSystemProfile : DConnectSystemProfile<DConnectSystemProfileDataSource,DConnectSystemProfileDelegate>

@end
