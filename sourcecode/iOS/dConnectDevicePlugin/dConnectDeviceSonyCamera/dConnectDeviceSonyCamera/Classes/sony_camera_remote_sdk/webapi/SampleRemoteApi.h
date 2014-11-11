//
//  SampleRemoteApi.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>
#import "HttpAsynchronousRequest.h"
#import "HttpSynchronousRequest.h"
#import "RemoteApiList.h"

@interface SampleRemoteApi : NSObject

+ (NSData*) getAvailableApiList:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate isSync:(BOOL)isSync;

+ (NSData*) getApplicationInfo:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate isSync:(BOOL)isSync;

+ (void) getShootMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (NSInteger) setShootMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate shootMode:(NSString*)shootMode;

+ (void) getAvailableShootMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) getSupportedShootMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) startLiveview:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) stopLiveview:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (NSData*) startRecMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate isSync:(BOOL)isSync;

+ (void) stopRecMode:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (NSInteger) actTakePicture:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) startMovieRec:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) stopMovieRec:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) actZoom:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate direction:(NSString*)direction movement:(NSString*)movement;

+ (void) getEvent:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate longPollingFlag:(BOOL) longPollingFlag;

+ (void) getStillSize:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate;

+ (void) setCurrentTime:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate dateTime:(NSString*)dateTime timeZoneOffsetMinute:(int)timeZoneOffsetMinute dstOffsetMinute:(int) dstOffsetMinute;
@end
