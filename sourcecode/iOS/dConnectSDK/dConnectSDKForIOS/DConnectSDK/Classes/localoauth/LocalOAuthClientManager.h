//
//  LocalOAuthClientManager.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

#import "LocalOAuthClient.h"
#import "LocalOAuthPackageInfo.h"


/** ClientManagerプロトコル定義 */
@protocol LocalOAuthClientManagerProtocol <NSObject>

@required

/*!
    Used for creating a data entry representation for a oauth client

    @param[in] packageInfo	パッケージ情報(追加)
    @param[in] clientType
    @param[in] redirectURIs  NSString * の配列
    @param[in] properties    key:(NSString *), value:(id) のマップ
    @return
 */
- (LocalOAuthClient *) createClient: (LocalOAuthPackageInfo *)packageInfo
               clientType:(LocalOAuthClientType)clientType
             redirectURIs:(NSArray *)redirectURIs
               properties:(NSDictionary *)properties;

/*!
    Delete a client_id from the implementing backed database.

    @param[in] id client_id of the client to remove
 */
- (void) deleteClient: (NSString *)clientId;

/*!
    Search for a client_id if present in the database.
 
    @param[in] id client_id to search for.
    @return client POJO or null if not found.
 */
- (LocalOAuthClient *) findById: (NSString *)clientId;

/*!
    Search for a client_id if present in the database(追加).
 
    @param[in] packageInfo	パッケージ情報
    @return client POJO or null if not found.
 */
- (LocalOAuthClient *) findByPackageInfo: (LocalOAuthPackageInfo *) packageInfo;



@optional

@end



/** ClientManager */
@interface LocalOAuthClientManager : NSObject
@property(nonatomic, assign) id <LocalOAuthClientManagerProtocol, NSObject> delegate;

- (LocalOAuthClient *) createClient: (LocalOAuthPackageInfo *)packageInfo
               clientType:(LocalOAuthClientType)clientType
             redirectURIs:(NSArray *)redirectURIs
               properties:(NSDictionary *)properties;
- (void) deleteClient: (NSString *)clientId;
- (LocalOAuthClient *) findById: (NSString *)clientId;
- (LocalOAuthClient *) findByPackageInfo: (LocalOAuthPackageInfo *) packageInfo;

@end
