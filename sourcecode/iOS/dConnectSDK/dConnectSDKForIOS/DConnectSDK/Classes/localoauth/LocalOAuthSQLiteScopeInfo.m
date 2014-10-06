//
//  LocalOAuthSQLiteScopeInfo.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteScopeInfo.h"

@implementation LocalOAuthSQLiteScopeInfo

- (LocalOAuthSQLiteScopeInfo *)initWithParameter: (long long)tokensTokenId
  profilesProfileId: (long long)profilesProfileId
          timestamp: (long long)timestamp
       expirePeriod: (long long)expirePeriod
        profileName: (NSString *)profileName_ {
    
    self = [super init: tokensTokenId profilesProfileId:profilesProfileId timestamp:timestamp expirePeriod:expirePeriod];
    
    if (self) {
        self.profileName = profileName_;
    }
    
    return self;
}



@end
