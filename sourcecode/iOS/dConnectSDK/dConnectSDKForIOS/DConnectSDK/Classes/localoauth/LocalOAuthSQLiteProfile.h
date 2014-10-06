//
//  LocalOAuthSQLiteProfile.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthSQLiteProfile : NSObject

/** ID. */
@property long id_;

/** プロファイル名. */
@property NSString *profileName;

/** description. */
@property NSString *profileDescription;


/*!
    コンストラクタ.
    @param[in] profileName_ プロファイル名
    @param[in] description_ description
 */
+ (LocalOAuthSQLiteProfile *)initWithProfileName: (NSString *)profileName_;


@end
