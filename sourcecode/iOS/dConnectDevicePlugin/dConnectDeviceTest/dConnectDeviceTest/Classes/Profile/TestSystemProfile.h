//
//  TestSystemProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <DConnectSDK/DConnectSDK.h>

@interface TestSystemProfile : DConnectSystemProfile<DConnectSystemProfileDelegate, DConnectSystemProfileDataSource>

#pragma mark - DConnectSystemProfileDataSource
- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile;
- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request;

@end
