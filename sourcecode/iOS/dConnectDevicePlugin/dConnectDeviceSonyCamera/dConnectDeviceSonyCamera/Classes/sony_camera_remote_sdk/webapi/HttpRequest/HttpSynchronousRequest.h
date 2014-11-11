//
//  HttpSynchronousRequest.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>

@interface HttpSynchronousRequest : NSObject

/**
 * Synchronous HTTP POST call for webAPI
 */
- (NSData*) call:(NSString*)url postParams:(NSString*)params;

@end
