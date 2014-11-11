//
//  HttpAsynchronousRequest.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>

@protocol HttpAsynchronousRequestParserDelegate <NSObject>

/* 
 * Response callback for HTTP calls with WebAPI name
 */
- (void) parseMessage:(NSData*)response apiName:(NSString*)apiName;

@end

@interface HttpAsynchronousRequest : NSObject

/**
 * Asynchronous HTTP POST call for webAPI
 */
- (void) call:(NSString*)url postParams:(NSString*)params apiName:(NSString*)apiName parserDelegate:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

@end
