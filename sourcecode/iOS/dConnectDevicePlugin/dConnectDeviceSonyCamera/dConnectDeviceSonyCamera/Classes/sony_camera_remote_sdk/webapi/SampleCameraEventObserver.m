//
//  SampleCameraEventObserver.m
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import "SampleCameraEventObserver.h"
#import "SampleRemoteApi.h"

static SampleCameraEventObserver *_instance;

@implementation SampleCameraEventObserver
{
    BOOL _isStarted;
    BOOL _isFirstCall;
    id<SampleEventObserverDelegate> _eventDelegate;
}

+(SampleCameraEventObserver*) getInstance
{
    if (!_instance)
    {
        _instance = [[SampleCameraEventObserver alloc]init];
    }
    return _instance;
}

-(void) start:(id<SampleEventObserverDelegate>)eventDelegate
{
    if(!_isStarted)
    {
        _isStarted = YES;
        _isFirstCall = YES;
        _eventDelegate = eventDelegate;
        [self call];
    }
}

-(void) call
{
    if(_isStarted)
    {
        [SampleRemoteApi getEvent:self longPollingFlag:!_isFirstCall];
        _isFirstCall = NO;
    }
}

-(void) stop
{
    if(_isStarted)
    {
        _isStarted = NO;
        _isFirstCall = NO;
    }
}

-(void) destroy
{
    [self stop];
    _instance = nil;
}

-(BOOL) isStarted
{
    return _isStarted;
}

- (void) parseMessage:(NSData*)response apiName:(NSString*)apiName
{
//    NSString *responseText = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
//    NSLog(@"SampleCameraEventObserver parseMessage = %@, apiname=%@", responseText, apiName);
    if(_isStarted)
    {
        NSError *e = nil;
        NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:response options:NSJSONReadingMutableContainers error:&e];
        if(!e)
        {
            if([jsonDict objectForKey:@"error"])
            {
                // DEVELOPER : check for error codes and restart event
            }
            if([jsonDict objectForKey:@"result"])
            {
                NSArray * result = [jsonDict objectForKey:@"result"];
                // check for all event callbacks required by the application.
                [self findAvailableApiList:result];
                [self findCameraStatus:result];
                [self findLiveviewStatus:result];
                [self findZoomInformation:result];
                [self findShootMode:result];
                [self findTakePicture:result];
            }
            [self call];
        }
    }
}

// Finds and extracts a list of available APIs from reply JSON data.
// As for getEvent v1.0, results[0] => "availableApiList"
- (void) findAvailableApiList:(NSArray*) response
{
    NSArray* availableApiList = NULL;
    int indexOfAvailableApiList = 0;
    if([response objectAtIndex:indexOfAvailableApiList])
    {
        NSDictionary* typeObj = [response objectAtIndex:indexOfAvailableApiList];
        if(![typeObj isEqual:[NSNull null]])
        {
            NSString* type = [typeObj objectForKey:@"type"];
            if(type!=nil && [type isEqualToString: @"availableApiList"])
            {
                availableApiList = [typeObj objectForKey:@"names"];
                if(availableApiList!=NULL && availableApiList.count > 0)
                {
                    [_eventDelegate didApiListModified:availableApiList];
                }
            }
        }
    }
}

// Finds and extracts a value of Camera Status from reply JSON data.
// As for getEvent v1.0, results[1] => "cameraStatus"
- (void) findCameraStatus:(NSArray*) response
{
    NSString* cameraStatus = NULL;
    int indexOfCameraStatus = 1;
    if([response objectAtIndex:indexOfCameraStatus]!=NULL)
    {
        NSDictionary* typeObj = [response objectAtIndex:indexOfCameraStatus];
        if(![typeObj isEqual:[NSNull null]])
        {
            NSString* type = [typeObj objectForKey:@"type"];
            if(type!=nil && [type isEqualToString:@"cameraStatus"])
            {
                cameraStatus = [typeObj objectForKey:@"cameraStatus"];
                if(cameraStatus)
                {
                    [_eventDelegate didCameraStatusChanged:cameraStatus];
                }
            }
        }
    }
}

// Finds and extracts a value of Liveview Status from reply JSON data.
// As for getEvent v1.0, results[3] => "liveviewStatus"
- (void) findLiveviewStatus:(NSArray*) response
{
    BOOL liveviewStatus = NO;
    int indexOfLiveviewStatus = 3;
    if([response objectAtIndex:indexOfLiveviewStatus])
    {
        NSDictionary* typeObj = [response objectAtIndex:indexOfLiveviewStatus];
        if(![typeObj isEqual:[NSNull null]])
        {
            NSString* type = [typeObj objectForKey:@"type"];
            if(type!=nil && [type isEqualToString:@"liveviewStatus"])
            {
                liveviewStatus = (BOOL)[typeObj objectForKey:@"liveviewStatus"];
                [_eventDelegate didLiveviewStatusChanged:liveviewStatus];
            }
        }
    }
}

// Finds and extracts a value of Zoom Information from reply JSON data.
// As for getEvent v1.0, results[2] => "zoomInformation"
- (void) findZoomInformation:(NSArray*) response
{
    int indexOfZoomInformation = 2;
    if([response objectAtIndex:indexOfZoomInformation])
    {
        NSDictionary* typeObj = [response objectAtIndex:indexOfZoomInformation];
        if(![typeObj isEqual:[NSNull null]])
        {
            NSString* type = [typeObj objectForKey:@"type"];
            if(type!=nil && [type isEqualToString:@"zoomInformation"])
            {
                NSNumber *zoomPosition = (NSNumber*)[typeObj objectForKey:@"zoomPosition"];
                [_eventDelegate didZoomPositionChanged:[zoomPosition intValue]];
            }
        }
    }
}

// Finds and extracts a value of Camera Status from reply JSON data.
// As for getEvent v1.0, results[21] => "cameraStatus"
- (void) findShootMode:(NSArray*) response
{
    NSString* shootMode = nil;
    int indexOfShootMode = 21;
    if([response objectAtIndex:indexOfShootMode])
    {
        NSDictionary* typeObj = [response objectAtIndex:indexOfShootMode];
        if(![typeObj isEqual:[NSNull null]])
        {
            NSString* type = [typeObj objectForKey:@"type"];
            if(type!=nil && [type isEqualToString:@"shootMode"])
            {
                shootMode = [typeObj objectForKey:@"currentShootMode"];
                if(shootMode)
                {
                    [_eventDelegate didShootModeChanged:shootMode];
                }
            }
        }
    }
}

- (void) findTakePicture:(NSArray *)response
{
    NSString *postImageUrl = nil;
    int indexOfTakePicture = 5;
    NSArray *takePictures = [response objectAtIndex:indexOfTakePicture];
    if (takePictures) {
        for (int i = 0; i < takePictures.count; i++) {
            NSDictionary *takeObj = [takePictures objectAtIndex:i];
            NSString *type = (NSString *) [takeObj objectForKey:@"type"];
            if ([type isEqualToString:@"takePicture"]) {
                NSArray *imageUrlObj = [takeObj objectForKey:@"takePictureUrl"];
                if (1 <= imageUrlObj.count) {
                    postImageUrl = (NSString *)[imageUrlObj objectAtIndex:0];
                }
            }
        }
    }
    if (postImageUrl) {
        [_eventDelegate didTakePicture:postImageUrl];
    }
}

@end
