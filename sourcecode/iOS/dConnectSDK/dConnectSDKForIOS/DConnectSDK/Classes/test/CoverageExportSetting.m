//
//  CoverageExportSetting.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

@interface CoverageExportSetting : NSObject
@end

@implementation CoverageExportSetting
#ifdef DEBUG
// コードカバレッジを出力するためのコード
+ (void) load {
    [[NSUserDefaults standardUserDefaults] setValue:@"XCTestLog,GcovTestObserver"
                                             forKey:@"XCTestObserverClass"];
}
#endif
@end