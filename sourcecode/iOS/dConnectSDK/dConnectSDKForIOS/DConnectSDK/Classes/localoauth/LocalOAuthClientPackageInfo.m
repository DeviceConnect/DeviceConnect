//
//  LocalOAuthClientPackageInfo.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthClientPackageInfo.h"

@implementation LocalOAuthClientPackageInfo

- (LocalOAuthClientPackageInfo *) initWithPackageInfo: (LocalOAuthPackageInfo *)packageInfo_
                                             clientId:(NSString *)clientId_ {
 
    self = [super init];
    
    if (self) {
        self.packageInfo = packageInfo_;
        self.clientId = clientId_;
    }
    
    return self;
}


@end
