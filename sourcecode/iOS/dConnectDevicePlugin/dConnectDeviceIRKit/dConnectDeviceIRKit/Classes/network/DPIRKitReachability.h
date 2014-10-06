//
//  DPIRKitReachability.h
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/09/20.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <SystemConfiguration/SystemConfiguration.h>
#import <netinet/in.h>


typedef enum : NSInteger {
    DPIRKitNotReachable = 0,
    DPIRKitReachableViaWiFi,
    DPIRKitReachableViaWWAN
} DPIRKitNetworkStatus;


extern NSString *DPIRKitReachabilityChangedNotification;


@interface DPIRKitReachability : NSObject

/*!
 * Use to check the reachability of a given host name.
 */
+ (instancetype)reachabilityWithHostName:(NSString *)hostName;

/*!
 * Use to check the reachability of a given IP address.
 */
+ (instancetype)reachabilityWithAddress:(const struct sockaddr_in *)hostAddress;

/*!
 * Checks whether the default route is available. Should be used by applications that do not connect to a particular host.
 */
+ (instancetype)reachabilityForInternetConnection;

/*!
 * Checks whether a local WiFi connection is available.
 */
+ (instancetype)reachabilityForLocalWiFi;

/*!
 * Start listening for reachability notifications on the current run loop.
 */
- (BOOL)startNotifier;
- (void)stopNotifier;

- (DPIRKitNetworkStatus) currentReachabilityStatus;

/*!
 * WWAN may be available, but not active until a connection has been established. WiFi may require a connection for VPN on Demand.
 */
- (BOOL)connectionRequired;

@end
