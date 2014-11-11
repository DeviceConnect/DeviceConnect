//
//  DPDicePlusNetworkServiceDiscoveryProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPDicePlusNetworkServiceDiscoveryProfile.h"
#import "DPDicePlusManager.h"

@implementation DPDicePlusNetworkServiceDiscoveryProfile

- (id) init {
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
}

#pragma mark - DConnectNetworkServiceDiscoveryProfileDelegate delegate
#pragma mark - Get Methods

- (BOOL)                       profile:(DConnectNetworkServiceDiscoveryProfile *)profile
didReceiveGetGetNetworkServicesRequest:(DConnectRequestMessage *)request
                              response:(DConnectResponseMessage *)response
{
    DConnectArray *services = [DConnectArray array];
    
    NSArray *diceList = [DPDicePlusManager sharedManager].diceList;
    for (int i = 0; i < diceList.count; i++) {
        DPDie *dice = [diceList objectAtIndex:i];
        
        DConnectMessage *service = [DConnectMessage new];
        NSString *uid = [NSString stringWithFormat:@"%@", dice.UUID];

        NSString *diceName = [NSString stringWithFormat:@"Dice+ %@", [uid substringToIndex:8]];
        [DConnectNetworkServiceDiscoveryProfile setId:dice.UID target:service];
        [DConnectNetworkServiceDiscoveryProfile setName:diceName target:service];
        [DConnectNetworkServiceDiscoveryProfile setType:DConnectNetworkServiceDiscoveryProfileNetworkTypeBluetooth
                                                 target:service];
        [DConnectNetworkServiceDiscoveryProfile setOnline:YES target:service];
        [services addMessage:service];
    }
    [response setResult:DConnectMessageResultTypeOk];
    [DConnectNetworkServiceDiscoveryProfile setServices:services target:response];
    return YES;
}

@end
