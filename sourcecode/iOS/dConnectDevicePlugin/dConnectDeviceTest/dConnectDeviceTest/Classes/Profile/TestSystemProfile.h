//
//  TestSystemProfile.h
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <DConnectSDK/DConnectSDK.h>

@interface TestSystemProfile : DConnectSystemProfile<DConnectSystemProfileDelegate, DConnectSystemProfileDataSource>

#pragma mark - DConnectSystemProfileDataSource
- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile;
- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request;

@end
