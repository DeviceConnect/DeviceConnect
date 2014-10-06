//
//  LocalOAuthAuthSession.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>
#import "LocalOAuthResponseType.h"
#import "LocalOAuthRedirectionURI.h"

@interface LocalOAuthAuthSession : NSObject

// Normalized attributes for data storage.(key:(NSString*), value:(id))
@property NSMutableDictionary *attribs;

+ (LocalOAuthAuthSession *)init;
- (BOOL)equals: (id)obj;

+ (LocalOAuthAuthSession *) newAuthSession;
+ (LocalOAuthAuthSession *) toAuthSession: (NSMutableDictionary *)attribs;

- (NSMutableDictionary *) toMap;
- (NSString *) getId;
- (void) setClientId: (NSString *) clientId;
- (NSString *) getClientId;
- (void) setGrantedScope: (NSArray *)scope;
- (NSArray *) getGrantedScope;
- (void) setRequestedScope: (NSArray *) scope;
- (NSArray *) getRequestedScope;
- (void) setScopeOwner: (NSString *)owner;
- (NSString *) getScopeOwner;
- (BOOL) getAuthFlow: (LocalOAuthResponseType *)pResponseType;
- (void)setState: (NSString *)state;
- (NSString *) getState;
- (void) setRedirectionURI: (LocalOAuthRedirectionURI *) uri;
- (LocalOAuthRedirectionURI *) getRedirectionURI;
- (void) setSessionTimeout: (int)timeSeconds;
- (int)getSessionTimeout;
- (void) setApplicationName: (NSString *)applicationName;
- (NSString *) getApplicationName;
- (id) getAttribute: (NSString *) name;
- (void) setAttribute: (NSString *)name value:(id)value;
- (id) removeAttribute: (NSString *)name;
- (void) updateActivity;


@end
