//
//  DConnectSettings+Private.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectSettings.h"

@implementation DConnectSettings

- (id) init {
    self = [super init];
    if (self) {
        self.host = @"localhost";
        self.port = 4035;
        self.useLocalOAuth = YES;
    }
    return self;
}

@end
