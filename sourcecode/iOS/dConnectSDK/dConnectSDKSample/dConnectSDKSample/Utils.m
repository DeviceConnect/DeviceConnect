//
//  Utils.m
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/09/09.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "Utils.h"

@interface Utils()

+ (NSArray *) scopes;

@end

@implementation Utils

+ (NSArray *) scopes
{
    
    return @[
             DConnectNetworkServiceDiscoveryProfileName,
             DConnectSystemProfileName,
             DConnectBatteryProfileName,
             DConnectConnectProfileName,
             DConnectDeviceOrientationProfileName,
             DConnectFileDescriptorProfileName,
             DConnectFileProfileName,
             DConnectMediaStreamRecordingProfileName,
             DConnectMediaPlayerProfileName,
             DConnectNotificationProfileName,
             DConnectPhoneProfileName,
             DConnectProximityProfileName,
             DConnectSettingsProfileName,
             DConnectVibrationProfileName,
             @"remote_controller",
             @"drive_controller",
             @"light",
             @"dice",
             @"sphero",
             ];
    
}



+ (void) authorizeWithCompletion:(DConnectAuthorizationSuccessBlock)success error:(DConnectAuthorizationFailBlock)error
{
    [DConnectUtil asyncAuthorizeWithPackageName:@"com.nttdocomo.ios.dconnect.dConnectSDKSample"
                                        appName:@"SDKサンプル"
                                         scopes:[self scopes]
                                        success:^(NSString *clientId, NSString *clientSecret, NSString *accessToken)
     {
         NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
         @synchronized (ud) {
             [ud setObject:clientId forKey:UD_KEY_CLIENT_ID];
             [ud setObject:clientSecret forKey:UD_KEY_CLIENT_SECRET];
             [ud setObject:accessToken forKey:UD_KEY_ACCESS_TOKEN];
             [ud synchronize];
         }

         success(clientId, clientSecret, accessToken);
     }
                                          error:error];
    
}


+ (void) refreshTokenWithCompletion:(DConnectAuthorizationSuccessBlock)success
                              error:(DConnectAuthorizationFailBlock)error
{
    
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        
        NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
        NSString *clientId = [ud stringForKey:UD_KEY_CLIENT_ID];
        NSString *clientSecret = [ud stringForKey:UD_KEY_CLIENT_SECRET];
        
        [DConnectUtil refreshAccessTokenWithClientId:clientId
                                        clientSecret:clientSecret
                                             appName:@"SDKサンプル"
                                              scopes:[self scopes]
                                             success:^(NSString *clientId, NSString *clientSecret, NSString *accessToken)
         {
             NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
             @synchronized (ud) {
                 [ud setObject:clientId forKey:UD_KEY_CLIENT_ID];
                 [ud setObject:clientSecret forKey:UD_KEY_CLIENT_SECRET];
                 [ud setObject:accessToken forKey:UD_KEY_ACCESS_TOKEN];
                 [ud synchronize];
             }

             success(clientId, clientSecret, accessToken);
         } error:error];
        
    });
    
}

+ (void) authorizeOrRefreshTokenWithForceRefresh:(BOOL)force
                                         success:(DConnectAuthorizationSuccessBlock)success
                                           error:(DConnectAuthorizationFailBlock)error
{
    NSUserDefaults *ud = [NSUserDefaults standardUserDefaults];
    
    @synchronized (ud) {
        NSString *clientId = [ud stringForKey:UD_KEY_CLIENT_ID];
        NSString *clientSecret = [ud stringForKey:UD_KEY_CLIENT_SECRET];
        NSString *accessToken = [ud stringForKey:UD_KEY_ACCESS_TOKEN];
        
        if (!clientId || !clientSecret) {
            [Utils authorizeWithCompletion:success error:error];
        } else if (force || !accessToken) {
            [Utils refreshTokenWithCompletion:success error:error];
        } else {
            success(clientId, clientSecret, accessToken);
        }

    }
    
}

@end
