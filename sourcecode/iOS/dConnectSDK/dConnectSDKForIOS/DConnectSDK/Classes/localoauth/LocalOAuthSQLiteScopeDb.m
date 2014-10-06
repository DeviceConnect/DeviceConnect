//
//  LocalOAuthSQLiteScopeDb.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthSQLiteScopeDb.h"
#import "LocalOAuthUtils.h"

@implementation LocalOAuthSQLiteScopeDb

- (id)init {
    self = [super init];
    
    if (self) {
        self.tokensTokenId = 0;
        self.profilesProfileId = 0;
        self.timestamp = [LocalOAuthUtils getCurrentTimeInMillis];
        self.expirePeriod = 0;
    }
    
    return self;
}

- (id)init: (long long)tokensTokenId_
  profilesProfileId:(long long)profilesProfileId_
          timestamp:(long long)timestamp_
       expirePeriod:(long long)expirePeriod_ {
    self = [super init];
    
    self.tokensTokenId = tokensTokenId_;
    self.profilesProfileId = profilesProfileId_;
    self.timestamp = timestamp_;
    self.expirePeriod = expirePeriod_;

    return self;
}





@end
