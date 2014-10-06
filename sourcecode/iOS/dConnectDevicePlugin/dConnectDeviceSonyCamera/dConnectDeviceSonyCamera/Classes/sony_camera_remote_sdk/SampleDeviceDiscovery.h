//
//  SampleDeviceDiscovery.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>
#import "UdpRequest.h"

@protocol SampleDiscoveryDelegate <NSObject>
- (void) didReceiveDeviceList:(BOOL) discovery;
@end

@interface SampleDeviceDiscovery : NSObject<UdpRequestDelegate, NSXMLParserDelegate>

-(void) discover:(id<SampleDiscoveryDelegate>)delegate;

@end
