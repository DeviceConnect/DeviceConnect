//
//  DConnectMediaStreamRecordingProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DConnectMediaStreamRecordingProfile.h"

NSString *const DConnectMediaStreamRecordingProfileName = @"mediastream_recording";
NSString *const DConnectMediaStreamRecordingProfileAttrMediaRecorder = @"mediarecorder";
NSString *const DConnectMediaStreamRecordingProfileAttrTakePhoto = @"takephoto";
NSString *const DConnectMediaStreamRecordingProfileAttrRecord = @"record";
NSString *const DConnectMediaStreamRecordingProfileAttrPause = @"pause";
NSString *const DConnectMediaStreamRecordingProfileAttrResume = @"resume";
NSString *const DConnectMediaStreamRecordingProfileAttrStop = @"stop";
NSString *const DConnectMediaStreamRecordingProfileAttrMuteTrack = @"mutetrack";
NSString *const DConnectMediaStreamRecordingProfileAttrUnmuteTrack = @"unmutetrack";
NSString *const DConnectMediaStreamRecordingProfileAttrOptions = @"options";
NSString *const DConnectMediaStreamRecordingProfileAttrOnPhoto = @"onphoto";
NSString *const DConnectMediaStreamRecordingProfileAttrOnRecordingChange = @"onrecordingchange";
NSString *const DConnectMediaStreamRecordingProfileAttrOnDataAvailable = @"ondataavailable";
NSString *const DConnectMediaStreamRecordingProfileParamRecorders = @"recorders";
NSString *const DConnectMediaStreamRecordingProfileParamId = @"id";
NSString *const DConnectMediaStreamRecordingProfileParamName = @"name";
NSString *const DConnectMediaStreamRecordingProfileParamState = @"state";
NSString *const DConnectMediaStreamRecordingProfileParamImageWidth = @"imageWidth";
NSString *const DConnectMediaStreamRecordingProfileParamImageHeight = @"imageHeight";
NSString *const DConnectMediaStreamRecordingProfileParamMin = @"min";
NSString *const DConnectMediaStreamRecordingProfileParamMax = @"max";
NSString *const DConnectMediaStreamRecordingProfileParamMIMEType = @"mimeType";
NSString *const DConnectMediaStreamRecordingProfileParamConfig = @"config";
NSString *const DConnectMediaStreamRecordingProfileParamTarget = @"target";
NSString *const DConnectMediaStreamRecordingProfileParamMediaId = @"mediaId";
NSString *const DConnectMediaStreamRecordingProfileParamTimeSlice = @"timeslice";
NSString *const DConnectMediaStreamRecordingProfileParamSettings = @"settings";
NSString *const DConnectMediaStreamRecordingProfileParamPhoto = @"photo";
NSString *const DConnectMediaStreamRecordingProfileParamMedia = @"media";
NSString *const DConnectMediaStreamRecordingProfileParamUri = @"uri";
NSString *const DConnectMediaStreamRecordingProfileParamStatus = @"status";
NSString *const DConnectMediaStreamRecordingProfileParamErrorMessage = @"errorMessage";
NSString *const DConnectMediaStreamRecordingProfileParamPath = @"path";

NSString *const DConnectMediaStreamRecordingProfileRecorderStateUnknown = @"Unknown";
NSString *const DConnectMediaStreamRecordingProfileRecorderStateInactive = @"inactive";
NSString *const DConnectMediaStreamRecordingProfileRecorderStateRecording = @"recording";
NSString *const DConnectMediaStreamRecordingProfileRecorderStatePaused = @"paused";

NSString *const DConnectMediaStreamRecordingProfileRecordingStateUnknown = @"Unknown";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateRecording = @"recording";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateStop = @"stop";
NSString *const DConnectMediaStreamRecordingProfileRecordingStatePause = @"pause";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateResume = @"resume";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateMutetrack = @"mutetrack";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateUnmutetrack = @"unmutetrack";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateError = @"error";
NSString *const DConnectMediaStreamRecordingProfileRecordingStateWarning = @"warning";

@interface DConnectMediaStreamRecordingProfile()

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response;

@end

@implementation DConnectMediaStreamRecordingProfile

#pragma mark - DConnectProfile Methods -

- (NSString *) profileName {
    return DConnectMediaStreamRecordingProfileName;
}

- (BOOL) didReceiveGetRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    
    if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrMediaRecorder]) {
        if ([self hasMethod:@selector(profile:didReceiveGetMediaRecorderRequest:response:deviceId:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveGetMediaRecorderRequest:request
                             response:response deviceId:deviceId];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOptions]) {
        if ([self hasMethod:@selector(profile:didReceiveGetOptionsRequest:response:deviceId:target:)
                   response:response])
        {
            NSString *target = [DConnectMediaStreamRecordingProfile targetFromRequest:request];
            send = [_delegate profile:self didReceiveGetOptionsRequest:request response:response
                             deviceId:deviceId target:target];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePostRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    NSString *target = [DConnectMediaStreamRecordingProfile targetFromRequest:request];
    
    if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrTakePhoto]) {
        if ([self hasMethod:@selector(profile:didReceivePostTakePhotoRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePostTakePhotoRequest:request
                             response:response deviceId:deviceId target:target];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrRecord]) {
        if ([self hasMethod:@selector(profile:didReceivePostRecordRequest:response:deviceId:target:timeslice:)
                   response:response])
        {
            NSNumber *timeslice = [DConnectMediaStreamRecordingProfile timesliceFromRequest:request];
            send = [_delegate profile:self didReceivePostRecordRequest:request response:response deviceId:deviceId
                               target:target timeslice:timeslice];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceivePutRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    
    if (!_delegate) {
        [response setErrorToNotSupportAction];
        return send;
    }
    
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    NSString *target = [DConnectMediaStreamRecordingProfile targetFromRequest:request];
    NSString *sessionKey = [request sessionKey];
    
    if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrPause]) {
        if ([self hasMethod:@selector(profile:didReceivePutPauseRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutPauseRequest:request
                             response:response deviceId:deviceId target:target];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrResume]) {
        if ([self hasMethod:@selector(profile:didReceivePutResumeRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutResumeRequest:request
                             response:response deviceId:deviceId target:target];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrStop]) {
        if ([self hasMethod:@selector(profile:didReceivePutStopRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutStopRequest:request
                             response:response deviceId:deviceId target:target];
        }
        
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrMuteTrack]) {
        if ([self hasMethod:@selector(profile:didReceivePutMuteTrackRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutMuteTrackRequest:request
                             response:response deviceId:deviceId target:target];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrUnmuteTrack]) {
        if ([self hasMethod:@selector(profile:didReceivePutUnmuteTrackRequest:response:deviceId:target:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutUnmuteTrackRequest:request
                             response:response deviceId:deviceId target:target];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOptions]) {
        if ([self hasMethod:
             @selector(profile:didReceivePutOptionsRequest:response:deviceId:target:imageWidth:imageHeight:mimeType:)
                   response:response])
        {
            NSString *target = [DConnectMediaStreamRecordingProfile targetFromRequest:request];
            NSNumber *imageWidth = [DConnectMediaStreamRecordingProfile imageWidthFromRequest:request];
            NSNumber *imageHeight = [DConnectMediaStreamRecordingProfile imageHeightFromRequest:request];
            NSString *mimeType = [DConnectMediaStreamRecordingProfile mimeTypeFromRequest:request];
            send = [_delegate profile:self didReceivePutOptionsRequest:request response:response
                             deviceId:deviceId target:target
                           imageWidth:imageWidth imageHeight:imageHeight
                             mimeType:mimeType];
            
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnPhoto]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnPhotoRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnPhotoRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnRecordingChange]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnRecordingChangeRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnRecordingChangeRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnDataAvailable]) {
        if ([self hasMethod:@selector(profile:didReceivePutOnDataAvailableRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceivePutOnDataAvailableRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

- (BOOL) didReceiveDeleteRequest:(DConnectRequestMessage *)request response:(DConnectResponseMessage *)response {
    BOOL send = YES;
    NSString *attribute = [request attribute];
    NSString *deviceId = [request deviceId];
    NSString *sessionKey = [request sessionKey];
    
    if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnPhoto]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnPhotoRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnPhotoRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }

    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnRecordingChange]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnRecordingChangeRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnRecordingChangeRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else if ([attribute isEqualToString:DConnectMediaStreamRecordingProfileAttrOnDataAvailable]) {
        if ([self hasMethod:@selector(profile:didReceiveDeleteOnDataAvailableRequest:response:deviceId:sessionKey:)
                   response:response])
        {
            send = [_delegate profile:self didReceiveDeleteOnDataAvailableRequest:request response:response
                             deviceId:deviceId sessionKey:sessionKey];
        }
    } else {
        [response setErrorToUnknownAttribute];
    }
    
    return send;
}

#pragma mark - Setter

+ (void) setRecorderId:(NSString *)cameraId target:(DConnectMessage *)message {
    [message setString:cameraId forKey:DConnectMediaStreamRecordingProfileParamId];
}

+ (void) setRecorderName:(NSString *)name target:(DConnectMessage *)message {
    [message setString:name forKey:DConnectMediaStreamRecordingProfileParamName];
}

+ (void) setRecorderState:(NSString *)state target:(DConnectMessage *)message {
    [message setString:state forKey:DConnectMediaStreamRecordingProfileParamState];
}

+ (void) setRecorderImageWidth:(int)imageWidth target:(DConnectMessage *)message {
    [message setInteger:imageWidth forKey:DConnectMediaStreamRecordingProfileParamImageWidth];
}

+ (void) setRecorderImageHeight:(int)imageHeight target:(DConnectMessage *)message {
    [message setInteger:imageHeight forKey:DConnectMediaStreamRecordingProfileParamImageHeight];
}

+ (void) setRecorderMIMEType:(NSString *)mimeType target:(DConnectMessage *)message {
    [message setString:mimeType forKey:DConnectMediaStreamRecordingProfileParamMIMEType];
}

+ (void) setRecorderConfig:(NSString *)config target:(DConnectMessage *)message {
    [message setString:config forKey:DConnectMediaStreamRecordingProfileParamConfig];
}

+ (void) setImageHeight:(DConnectMessage *)imageHeight target:(DConnectMessage *)message {
    [message setMessage:imageHeight forKey:DConnectMediaStreamRecordingProfileParamImageHeight];
}

+ (void) setImageWidth:(DConnectMessage *)imageWidth target:(DConnectMessage *)message {
    [message setMessage:imageWidth forKey:DConnectMediaStreamRecordingProfileParamImageWidth];
}

+ (void) setMin:(int)min target:(DConnectMessage *)message {
    [message setInteger:min forKey:DConnectMediaStreamRecordingProfileParamMin];
}

+ (void) setMax:(int)max target:(DConnectMessage *)message {
    [message setInteger:max forKey:DConnectMediaStreamRecordingProfileParamMax];
}

+ (void) setPath:(NSString *)path target:(DConnectMessage *)message {
    [message setString:path forKey:DConnectMediaStreamRecordingProfileParamPath];
}

+ (void) setPhoto:(DConnectMessage *)photo target:(DConnectMessage *)message {
    [message setMessage:photo forKey:DConnectMediaStreamRecordingProfileParamPhoto];
}

+ (void) setMedia:(DConnectMessage *)media target:(DConnectMessage *)message {
    [message setMessage:media forKey:DConnectMediaStreamRecordingProfileParamMedia];
}

+ (void) setUri:(NSString *)uri target:(DConnectMessage *)message {
    [message setString:uri forKey:DConnectMediaStreamRecordingProfileParamUri];
}

+ (void) setErrorMessage:(NSString *)errorMessage target:(DConnectMessage *)message {
    [message setString:errorMessage forKey:DConnectMediaStreamRecordingProfileParamErrorMessage];
}

+ (void) setStatus:(NSString *)status target:(DConnectMessage *)message {
    [message setString:status forKey:DConnectMediaStreamRecordingProfileParamStatus];
}

+ (void) setMIMEType:(NSString *)mimeType target:(DConnectMessage *)message {
    [message setString:mimeType forKey:DConnectMediaStreamRecordingProfileParamMIMEType];
}

+ (void) setMIMETypes:(DConnectArray *)mimeTypes target:(DConnectMessage *)message {
    [message setArray:mimeTypes forKey:DConnectMediaStreamRecordingProfileParamMIMEType];
}

+ (void) setRecorders:(DConnectArray *)recorders target:(DConnectMessage *)message {
    [message setArray:recorders forKey:DConnectMediaStreamRecordingProfileParamRecorders];
}

#pragma mark - Getter

+ (NSString *) targetFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectMediaStreamRecordingProfileParamTarget];
}

+ (NSNumber *) timesliceFromRequest:(DConnectMessage *)request {
	return [request numberForKey:DConnectMediaStreamRecordingProfileParamTimeSlice];
}

+ (NSNumber *) imageWidthFromRequest:(DConnectMessage *)request {
	return [request numberForKey:DConnectMediaStreamRecordingProfileParamImageWidth];
}

+ (NSNumber *) imageHeightFromRequest:(DConnectMessage *)request {
	return [request numberForKey:DConnectMediaStreamRecordingProfileParamImageHeight];
}

+ (NSString *) mimeTypeFromRequest:(DConnectMessage *)request {
    return [request stringForKey:DConnectMediaStreamRecordingProfileParamMIMEType];
}

#pragma mark - Private Methods

- (BOOL) hasMethod:(SEL)method response:(DConnectResponseMessage *)response {
    BOOL result = [_delegate respondsToSelector:method];
    if (!result) {
        [response setErrorToNotSupportAttribute];
    }
    return result;
}

@end
