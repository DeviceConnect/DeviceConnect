//
//  LocalOAuthAuthSession.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthAuthSession.h"
#import "LocalOAuthResponseType.h"
#import "LocalOAuthUtils.h"


int const DEFAULT_TIMEOUT_SEC = 600;

NSString *const ID = @"id";

NSString *const CLIENT_ID = @"client_id";

NSString *const GRANTED_SCOPE = @"granted_scope";

NSString *const REQ_SCOPE = @"requested_scope";

NSString *const FLOW = @"flow";

NSString *const CALLBACK = @"callback";

NSString *const OWNER = @"owner";

NSString *const STATE = @"state";

NSString *const LAST_ACTIVITY = @"last_activity";

NSString *const TIMEOUT_SEC = @"timeout_sec";

NSString *const APPLICATION_NAME = @"application_name";



/**
 * Helper class to establish an authentication session. The session is created
 * in the AuthorizationResource on initial OAuth request.
 *
 * At the moment it is not being cleaned up on the server side.
 *
 * The cookie that is set will get removed when the browser closes the window.
 *
 * @author Kristoffer Gronowski
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
@implementation LocalOAuthAuthSession


+ (LocalOAuthAuthSession *)init {
    
    LocalOAuthAuthSession *session = [[LocalOAuthAuthSession alloc]init];
    
    if (session) {
        session.attribs = [NSMutableDictionary dictionary];
    }
    
    return session;
}

- (BOOL)equals: (id)obj {
    
    if (obj == nil) {
        return NO;
    }
    
    NSString *selfClassName = NSStringFromClass([self class]);
    NSString *objClassName = NSStringFromClass([obj class]);
    if (![selfClassName isEqualToString: objClassName]) {
        return NO;
    }
    
    LocalOAuthAuthSession *objSession = (LocalOAuthAuthSession *)obj;
    
    BOOL result = [self.attribs isEqualToDictionary: objSession.attribs];
    return result;
}

/**
 * Instantiate new authorization session.
 *
 * @return a new authorization session.
 */
+ (LocalOAuthAuthSession *) newAuthSession {
    
    LocalOAuthAuthSession *session = [[LocalOAuthAuthSession alloc]init];
    
    // XXX: Is UUID a non-guessable value? (10.12. Cross-Site Request
    // Forgery)
    NSString *sessionId = [[NSUUID UUID] UUIDString];
    
    
    NSNumber *currentTimeInMillis = [NSNumber numberWithLongLong: [LocalOAuthUtils getCurrentTimeInMillis]];
    [session setAttribute: ID value: sessionId];
    [session setAttribute:LAST_ACTIVITY value: currentTimeInMillis];
    [session setSessionTimeout: DEFAULT_TIMEOUT_SEC];
    return session;
}

/**
 * @param attribs {key: (NSString *) value: (id)}のMap
 */
+ (LocalOAuthAuthSession *) toAuthSession: (NSMutableDictionary *)attribs {
    LocalOAuthAuthSession *session = [LocalOAuthAuthSession init];
    
    NSEnumerator *keyEnumerator = [attribs keyEnumerator];
    NSString *key = nil;
    while (key = [keyEnumerator nextObject]) {
        id value = [attribs valueForKey: key];
        [session.attribs setObject:value forKey:key];
    }
    
    return session;
}

/**
 * Get the Map interface that suitable for the database.
 *
 * @return
 */
- (NSMutableDictionary *) toMap {
    return self.attribs;
}

- (NSString *) getId {
    return (NSString *) [self getAttribute: ID];
}

/**
 * Set the client/application that created the cookie
 *
 * @param clientId
 *            POJO representing a client_id/secret
 */
- (void) setClientId: (NSString *) clientId {
    [self setAttribute: CLIENT_ID value: clientId];
}

/**
 * @return return the client that established the cookie
 */
- (NSString *) getClientId {
    return (NSString *) [self getAttribute: CLIENT_ID];
}

- (void) setGrantedScope: (NSArray *)scope {
    [self setAttribute:GRANTED_SCOPE value:scope];
}

/**
 * @return (Scope *)の配列
 */
- (NSArray *) getGrantedScope {
    
    NSArray *list = [self getAttribute: GRANTED_SCOPE];
    
    if (list == nil) {
        return nil;
    }
    
    return list;
}

/**
 * @param scope
 *            array of scopes requested but not yet approved
 */
- (void) setRequestedScope: (NSArray *) scope {
    [self setAttribute: REQ_SCOPE value:scope];
}

/**
 *
 * @return array of requested scopes
 */
- (NSArray *) getRequestedScope {
   
    NSArray *list = [self getAttribute: REQ_SCOPE];
    
    if (list == nil) {
        return nil;
    }
    return list;
}

/**
 *
 * @param owner
 *            the identity of the user of this session (openid)
 */
- (void) setScopeOwner: (NSString *)owner {
    [self setAttribute: OWNER value: owner];
}

/**
 *
 * @return identity of the authenticated user.
 */
- (NSString *) getScopeOwner {
    return [self getAttribute: OWNER];
}

/**
 * @param flow
 *            current executing flow
 */
- (void) setAuthFlow: (LocalOAuthResponseType)flow {
    
    // Normalize
    NSString *strFlow = [LocalOAuthResponseTypeUtil toString: flow];
    [self setAttribute: FLOW value: strFlow];
}

/**
 * @param pResponseType [out]ResponseType (戻り値=YESの場合に有効)
 * @return YES: 取得できた / NO: 取得できない(FLOWが未登録)
 */
- (BOOL) getAuthFlow: (LocalOAuthResponseType *)pResponseType {
    NSString *name = [self getAttribute: FLOW];
    if (name == nil) {
        return NO;
    }
    *pResponseType = [LocalOAuthResponseTypeUtil toValue: name];
    return YES;
}

/**
 * @param state
 *            to be save and returned with code
 */
- (void)setState: (NSString *)state {
    [self setAttribute: STATE value: state];
}

/**
 * @return client oauth state parameter
 */
- (NSString *) getState {
    return [self getAttribute: STATE];
}

- (void) setRedirectionURI: (LocalOAuthRedirectionURI *) uri {
    // Normalize
    NSMutableDictionary *map = [NSMutableDictionary dictionary];
    [map setObject: [uri getURI] forKey:@"uri"];
    [map setObject: [NSNumber numberWithBool:[uri isDynamicConfigured]] forKey:@"dynamic"];
    [self setAttribute: CALLBACK value: map];
}

/**
 *
 * @return the URL used in the initial authorization call
 */
- (LocalOAuthRedirectionURI *) getRedirectionURI {

    NSMutableDictionary *map = [self getAttribute: CALLBACK];
    
    if (map == nil) {
        return nil;
    }
    
    NSString *uri = [map objectForKey: @"uri"];
    BOOL dynamic = [[map objectForKey: @"dynamic"] boolValue];
 
    LocalOAuthRedirectionURI *redirectionURI = [LocalOAuthRedirectionURI initWithdynamicConfigured:uri dynamicConfigured:dynamic];
    
    return redirectionURI;
}

/**
 * Default is 600 sec = 10min
 *
 * @param timeSeconds
 *            sets the session expiry time in seconds
 */
- (void) setSessionTimeout: (int)timeSeconds {
    [self setAttribute: TIMEOUT_SEC value:[NSNumber numberWithInt: timeSeconds]];
}

/**
 * Setting only affects new or updated sessions.
 *
 * @return current session timeout
 */
- (int)getSessionTimeout {
    NSNumber *timeout = [self getAttribute: TIMEOUT_SEC];
    return [timeout intValue];
}

// XXX [MEMO]API追加
/**
 * アプリケーション名を設定.
 * @param applicationName アプリケーション名
 */
- (void) setApplicationName: (NSString *)applicationName {
    [self setAttribute: APPLICATION_NAME value:applicationName];
}

// XXX [MEMO]API追加
/**
 * アプリケーション名を返す.
 * @return アプリケーション名
 */
- (NSString *) getApplicationName {
    return (NSString *) [self getAttribute: APPLICATION_NAME];
}

// private only used for storage
- (id) getAttribute: (NSString *) name {
    return [self.attribs objectForKey: name];
}

/**
 * Store attribute for internal use. The value must be normalized.
 *
 * @param name
 * @param value
 *            normalized value.
 */
- (void) setAttribute: (NSString *)name value:(id)value {
    if (value == nil) {
        [self removeAttribute: name];
    } else {
        [self.attribs setObject: value forKey: name];
    }
}

- (id) removeAttribute: (NSString *)name {
    id value = [self.attribs objectForKey: name];
    [self.attribs removeObjectForKey: name];
    return value;
}


- (void) updateActivity {

    NSNumber *currentTimeNumber = [NSNumber numberWithLongLong: [LocalOAuthUtils getCurrentTimeInMillis]];
    NSNumber *currentTime = currentTimeNumber;
    NSNumber *lastActivity = [self getAttribute: LAST_ACTIVITY];
    double dCurrentTime = [currentTime doubleValue];
    double dLastActivity = [lastActivity doubleValue];
    double dDelta = dCurrentTime - dLastActivity;
    if (dDelta >= [self getSessionTimeout]) {
        @throw @"AuthSessionTimeoutException";
    }
    lastActivity = currentTimeNumber;
    [self setAttribute: LAST_ACTIVITY value:lastActivity];
}

@end
