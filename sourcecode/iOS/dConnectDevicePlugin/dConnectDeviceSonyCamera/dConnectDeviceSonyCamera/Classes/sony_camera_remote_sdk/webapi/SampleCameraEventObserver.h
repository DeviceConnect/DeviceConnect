//
//  SampleCameraEventObserver.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>
#import "HttpAsynchronousRequest.h"

@protocol SampleEventObserverDelegate <NSObject>

// It is a singleton class.

/**
 * Delegate function called when available API lists changes.
 */
- (void) didApiListModified:(NSArray*) api_list;

/**
 * Delegate function called when camera status is changed.
 */
- (void) didCameraStatusChanged:(NSString*) status;

/**
 * Delegate function called when liveview status is changed.
 */
- (void) didLiveviewStatusChanged:(BOOL) status;

/**
 * Delegate function called when shoot mode is chnaged.
 */
- (void) didShootModeChanged:(NSString*) shootMode;

/**
 * Delegate function called when zoom position is changed.
 */
- (void) didZoomPositionChanged:(int) zoomPosition;

/*!
 @brief 追加: 写真撮影されたイベント.
 
 */
- (void) didTakePicture:(NSString *)imageUri;

@end

@interface SampleCameraEventObserver : NSObject<HttpAsynchronousRequestParserDelegate>

/**
 * get the instance of Event observer
 */
+(SampleCameraEventObserver*) getInstance;

/**
 * Start the getEvent API. The API continues using long polling.
 */
-(void) start:(id<SampleEventObserverDelegate>)eventDelegate;

/**
 * Stop the polling of getEvent API
 */
-(void) stop;

/**
 * get status of event observer
 */
-(BOOL) isStarted;

/**
 * To destroy the instance of this class.
 */
-(void) destroy;

@end
