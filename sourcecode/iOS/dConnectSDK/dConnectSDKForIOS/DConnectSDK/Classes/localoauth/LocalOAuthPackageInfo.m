//
//  LocalOAuthPackageInfo.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthPackageInfo.h"

@implementation LocalOAuthPackageInfo

- (LocalOAuthPackageInfo *) initWithPackageName: (NSString *)packageName {
    
    self = [super init];
    
    if (self) {
        self.packageName = packageName;
        self.deviceId = nil;
    }
    
    return self;
}

- (LocalOAuthPackageInfo *) initWithPackageNameDeviceId: (NSString *)packageName deviceId:(NSString *)deviceId {
    
    self = [super init];
    
    self.packageName = packageName;
    self.deviceId = deviceId;
    
    return self;
}

- (BOOL) equals: (LocalOAuthPackageInfo *) o {
    
    LocalOAuthPackageInfo *cmp1 = self;
    LocalOAuthPackageInfo *cmp2 = o;
    
    BOOL isEqualPackageName = NO;
    if (cmp1.packageName == nil && cmp2.packageName == nil) {         /* 両方null */
        isEqualPackageName = YES;
    } else if (cmp1.packageName != nil && cmp2.packageName
                != nil 	/* 両方同じ文字列 */
               && [cmp1.packageName isEqualToString: cmp2.packageName] ) {
        isEqualPackageName = YES;
    }
    
    BOOL isEqualDeviceId = NO;
    if (cmp1.deviceId == nil && cmp2.deviceId == nil) {				/* 両方null */
        isEqualDeviceId = YES;
    } else if (cmp1.deviceId != nil && cmp2.deviceId != nil 		/* 両方同じ文字列 */
               && [cmp1.deviceId isEqualToString: cmp2.deviceId]) {
        isEqualDeviceId = YES;
    }
    
    if (isEqualPackageName && isEqualDeviceId) {
        return YES;
    }
    return NO;
}


@end
