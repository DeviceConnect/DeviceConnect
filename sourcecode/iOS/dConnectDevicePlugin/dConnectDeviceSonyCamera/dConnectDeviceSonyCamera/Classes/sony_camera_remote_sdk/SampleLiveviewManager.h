//
//  SampleLiveviewManager.h
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import <Foundation/Foundation.h>

@protocol SampleLiveviewDelegate <NSObject>

- (void) didReceivedData:(NSData *)imageData;

- (void) didReceivedError;

@end

@interface SampleLiveviewManager : NSObject<NSStreamDelegate>

-(void) start:(NSString*)liveviewUrl delegate:(id<SampleLiveviewDelegate>)viewDelegate;

-(void) stop;

@property (nonatomic) BOOL isStarted;

@end
