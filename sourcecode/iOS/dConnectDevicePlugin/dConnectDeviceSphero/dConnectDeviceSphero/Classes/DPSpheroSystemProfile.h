//
//  DPSpheroSystemProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
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
