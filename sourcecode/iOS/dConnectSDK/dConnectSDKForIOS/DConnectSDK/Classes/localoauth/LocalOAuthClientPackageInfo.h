//
//  LocalOAuthClientPackageInfo.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthPackageInfo.h"

@interface LocalOAuthClientPackageInfo : NSObject

/** パッケージ情報. */
@property LocalOAuthPackageInfo *packageInfo;

/** クライアントID. */
@property NSString *clientId;


/*!
    イニシャライザ.
    @param[in] packageInfo   パッケージ情報
    @param[in] clientId      クライアントID
 */
- (LocalOAuthClientPackageInfo *) initWithPackageInfo: (LocalOAuthPackageInfo *)packageInfo
                                             clientId:(NSString *)clientId;

@end
