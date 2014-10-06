//
//  LocalOAuthClientManager.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthClientManager.h"

@implementation LocalOAuthClientManager
@synthesize delegate = _delegate;

- (LocalOAuthClient *) createClient: (LocalOAuthPackageInfo *)packageInfo
               clientType:(LocalOAuthClientType)clientType
             redirectURIs:(NSArray *)redirectURIs
               properties:(NSDictionary *)properties {
    LocalOAuthClient *client = [_delegate createClient:packageInfo clientType:clientType redirectURIs:redirectURIs properties:properties];
    return client;
}

- (void) deleteClient: (NSString *)clientId {
    [_delegate deleteClient:clientId];
}

- (LocalOAuthClient *) findById: (NSString *)clientId {
    LocalOAuthClient *client = [_delegate findById: clientId];
    return client;
}

- (LocalOAuthClient *) findByPackageInfo: (LocalOAuthPackageInfo *) packageInfo {
    LocalOAuthClient *client = [_delegate findByPackageInfo: packageInfo];
    return client;
}


@end