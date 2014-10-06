//
//  LocalOAuthClientData.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "LocalOAuthClientData.h"

@implementation LocalOAuthClientData

/*!
    デフォルトのinitは使用しない
*/
+ (id)init {
    @throw @"Can't use ClientData default constructor.";
    return nil;
}

+ (LocalOAuthClientData *) clientDataWithClientIdClientSecret: (NSString *)clientId clientSecret: (NSString *)clientSecret {
    
    LocalOAuthClientData *clientData = [[LocalOAuthClientData alloc]init];
    
    if (clientData) {
        clientData.clientId = clientId;
        clientData.clientSecret = clientSecret;
    }
    
    return clientData;
}

@end
