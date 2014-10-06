//
//  GcovTestObserver.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GcovTestObserver.h"

@implementation GcovTestObserver
-(void)stopObserving {
    [super stopObserving];
    extern void __gcov_flush(void);
    __gcov_flush();
}
@end
