//
//  DPHueConst.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//
// 基本的にDPHueDevicePlugin.mで値を定義する。

#ifndef Hue_defines_h
#define Hue_defines_h

#define DPHUE_SETTING_PAGE_COUNT_IPHONE 3
#define DPHUE_SETTING_PAGE_COUNT_IPAD 3

extern NSString *const DPHueBundleName;

#define DPHueBundle() \
[NSBundle bundleWithPath:[[NSBundle mainBundle] pathForResource:DPHueBundleName ofType:@"bundle"]]

#define DPHueLocalizedString(bundle, key) \
[bundle localizedStringForKey:key value:@"" table:nil]

#endif
