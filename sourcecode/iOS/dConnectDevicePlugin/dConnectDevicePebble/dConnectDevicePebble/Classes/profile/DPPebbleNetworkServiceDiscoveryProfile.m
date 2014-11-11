//
//  DPPebbleNetworkServiceDiscoveryProfile.m
//  dConnectDevicePebble
//
//  Created by 小林伸郎 on 2014/08/24.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPPebbleNetworkServiceDiscoveryProfile.h"
#import "DPPebbleManager.h"

#import <CoreBluetooth/CoreBluetooth.h>
@interface DPPebbleNetworkServiceDiscoveryProfile ()
/*!
 @brief Pebble管理クラス。
 */
@property (nonatomic) DPPebbleManager *mgr;


@property (strong, nonatomic) CBCentralManager *centralManager;
@property (strong, nonatomic) CBPeripheral *discoveredPeripheral;
@property (strong, nonatomic) NSMutableData *data;
@end


@implementation DPPebbleNetworkServiceDiscoveryProfile

- (id) initWithPebbleManager:(DPPebbleManager *)mgr {
    self = [super init];
    if (self) {
        self.mgr = mgr;
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    
    NSArray* withList = [self.mgr getWatchesList];
    
    
    DConnectArray *services = [DConnectArray array];
    for (PBWatch *with in withList) {
        DConnectMessage *service = [DConnectMessage message];
        
        //DeviceIdの取得　　URL対応のため空白と：を除去
        [DConnectNetworkServiceDiscoveryProfile setId:[self getWatcheName:with]
         
                                               target:service];
        
        [DConnectNetworkServiceDiscoveryProfile setName:with.name target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeBluetooth
                                                 target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
        [services addMessage:service];
    }
    
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    [response setResult:DConnectMessageResultTypeOk];
    return YES;
}
-(NSString*)getWatcheName:(PBWatch*)Watche{
    return  [[Watche.name
              stringByReplacingOccurrencesOfString:@" " withString:@""]
             stringByReplacingOccurrencesOfString:@":" withString:@""];
}
@end
