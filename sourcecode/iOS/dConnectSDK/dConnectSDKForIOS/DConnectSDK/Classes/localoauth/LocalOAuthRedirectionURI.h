//
//  LocalOAuthRedirectionURI.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthRedirectionURI : NSObject

@property  NSString *uri;

@property  BOOL dynamicConfigured;

+ (LocalOAuthRedirectionURI *) init: (NSString *)uri_;
+ (LocalOAuthRedirectionURI *) initWithdynamicConfigured: (NSString *)uri_  dynamicConfigured:(BOOL)dynamicConfigured;

- (NSString *)toString;
- (NSString *)getURI;
- (BOOL) isDynamicConfigured;

@end
