//
//  DPHueItemBridge.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

/*! @file
 @brief 設定画面でHueのブリッジのデータを保持する。
 @author NTT DOCOMO
 @date 作成日(2014.7.15)
 */
#import <Foundation/Foundation.h>


/*!
 @class DPHueItemBridge
 @brief Hueのブリッジのデータを保持する。
 */
@interface DPHueItemBridge : NSObject <NSCopying>

/*!
 @brief HueのブリッジのMacアドレス。
 */
@property (nonatomic, copy) NSString *macAddress;

/*!
 @brief HueのブリッジのIPアドレス。
 */
@property (nonatomic, copy) NSString *ipAddress;
@end
