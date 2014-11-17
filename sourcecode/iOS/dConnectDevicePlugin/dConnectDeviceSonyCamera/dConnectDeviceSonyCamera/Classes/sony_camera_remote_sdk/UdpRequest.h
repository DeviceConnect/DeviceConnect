//
//  UdpRequest.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>
#import <CoreFoundation/CoreFoundation.h>
#import <sys/socket.h>
#import <netinet/in.h>
#import <ifaddrs.h>
#import <sys/ioctl.h>
#import <net/if.h>
#import <sys/socket.h>
#import <arpa/inet.h>
#include <netdb.h>

@protocol UdpRequestDelegate <NSObject>
- (void) didReceiveDdUrl:(NSString*) ddUrl;
@end

@interface UdpRequest : NSObject

- (void) search:(id<UdpRequestDelegate>)delegate;

@end
