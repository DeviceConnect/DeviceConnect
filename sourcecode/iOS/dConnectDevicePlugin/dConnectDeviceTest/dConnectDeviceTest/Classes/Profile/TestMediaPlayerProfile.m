//
//  TestMediaPlayerProfile.m
//  dConnectDeviceTest
//
//  Created by 安部 将史 on 2014/08/04.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import "TestMediaPlayerProfile.h"
#import "DeviceTestPlugin.h"

@implementation TestMediaPlayerProfile

- (id) initWithDevicePlugin:(DeviceTestPlugin *)plugin {
    self = [super init];
    
    if (self) {
        self.delegate = self;
        _plugin = plugin;
    }
    
    return self;
}

#pragma mark - Get Methods


- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetPlayStatusRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setStatus:DConnectMediaPlayerProfileStatusPlay target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetMediaRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         mediaId:(NSString *)mediaId
{
    
    CheckDID(response, deviceId)
    if (mediaId == nil || mediaId.length == 0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setMIMEType:@"audio/mp3" target:response];
        [DConnectMediaPlayerProfile setTitle:@"test title" target:response];
        [DConnectMediaPlayerProfile setType:@"test type" target:response];
        [DConnectMediaPlayerProfile setLanguage:@"ja" target:response];
        [DConnectMediaPlayerProfile setDescription:@"test description" target:response];
        [DConnectMediaPlayerProfile setDuration:60000 target:response];
        
        DConnectMessage *creator = [DConnectMessage message];
        [DConnectMediaPlayerProfile setCreator:@"test creator" target:creator];
        [DConnectMediaPlayerProfile setRole:@"test composer" target:creator];
        
        DConnectArray *creators = [DConnectArray array];
        [creators addMessage:creator];
        
        DConnectArray *keywords = [DConnectArray array];
        [keywords addString:@"keyword1"];
        [keywords addString:@"keyword2"];
        
        DConnectArray *genres = [DConnectArray array];
        [genres addString:@"test1"];
        [genres addString:@"test2"];
        
        [DConnectMediaPlayerProfile setCreators:creators target:response];
        [DConnectMediaPlayerProfile setKeywords:keywords target:response];
        [DConnectMediaPlayerProfile setGenres:genres target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetMediaListRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
           query:(NSString *)query
        mimeType:(NSString *)mimeType
           order:(NSArray *)order
          offset:(NSNumber *)offset
           limit:(NSNumber *)limit
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setCount:1 target:response];
        
        DConnectMessage *medium = [DConnectMessage message];
        [DConnectMediaPlayerProfile setMediaId:@"media001" target:medium];
        [DConnectMediaPlayerProfile setMIMEType:@"audio/mp3" target:medium];
        [DConnectMediaPlayerProfile setTitle:@"test title" target:medium];
        [DConnectMediaPlayerProfile setType:@"test type" target:medium];
        [DConnectMediaPlayerProfile setLanguage:@"ja" target:medium];
        [DConnectMediaPlayerProfile setDescription:@"test description" target:medium];
        [DConnectMediaPlayerProfile setDuration:60000 target:medium];
        
        DConnectMessage *creator = [DConnectMessage message];
        [DConnectMediaPlayerProfile setCreator:@"test creator" target:creator];
        [DConnectMediaPlayerProfile setRole:@"test composer" target:creator];
        
        DConnectArray *creators = [DConnectArray array];
        [creators addMessage:creator];
        
        DConnectArray *keywords = [DConnectArray array];
        [keywords addString:@"keyword1"];
        [keywords addString:@"keyword2"];
        
        DConnectArray *genres = [DConnectArray array];
        [genres addString:@"test1"];
        [genres addString:@"test2"];
        
        [DConnectMediaPlayerProfile setCreators:creators target:medium];
        [DConnectMediaPlayerProfile setKeywords:keywords target:medium];
        [DConnectMediaPlayerProfile setGenres:genres target:medium];
        
        DConnectArray *media = [DConnectArray array];
        [media addMessage:medium];
        
        [DConnectMediaPlayerProfile setMedia:media target:response];
    }
    
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetSeekRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setPos:0 target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setVolume:0 target:response];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveGetMuteRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
        [DConnectMediaPlayerProfile setMute:YES target:response];
    }
    
    return YES;
}

#pragma mark - Put Methods


- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutMediaRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         mediaId:(NSString *) mediaId
{
    
    CheckDID(response, deviceId)
    if (mediaId == nil || mediaId.length == 0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutPlayRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutStopRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutPauseRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutResumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}


- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutSeekRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
             pos:(NSNumber *)pos
{
    CheckDID(response, deviceId)
    if (pos == nil || pos < 0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutOnStatusChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionkey
{
    CheckDIDAndSK(response, deviceId, sessionkey) {
        response.result = DConnectMessageResultTypeOk;
        
        DConnectMessage *event = [DConnectMessage message];
        [event setString:sessionkey forKey:DConnectMessageSessionKey];
        [event setString:deviceId forKey:DConnectMessageDeviceId];
        [event setString:self.profileName forKey:DConnectMessageProfile];
        [event setString:DConnectMediaPlayerProfileAttrOnStatusChange forKey:DConnectMessageAttribute];
        
        DConnectMessage *mediaPlayer = [DConnectMessage message];
        [DConnectMediaPlayerProfile setStatus:DConnectMediaPlayerProfileStatusPlay target:mediaPlayer];
        [DConnectMediaPlayerProfile setMediaId:@"test.mp4" target:mediaPlayer];
        [DConnectMediaPlayerProfile setMIMEType:@"video/mp4" target:mediaPlayer];
        [DConnectMediaPlayerProfile setPos:0 target:mediaPlayer];
        [DConnectMediaPlayerProfile setVolume:0.5 target:mediaPlayer];
        
        [DConnectMediaPlayerProfile setMediaPlayer:mediaPlayer target:event];
        [_plugin asyncSendEvent:event];
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutVolumeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
          volume:(NSNumber *)volume
{
    CheckDID(response, deviceId)
    if (volume == nil || [volume doubleValue] < 0.0 || [volume doubleValue] > 1.0) {
        [response setErrorToInvalidRequestParameter];
    } else {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceivePutMuteRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveDeleteMuteRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    CheckDID(response, deviceId) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}

- (BOOL) profile:(DConnectMediaPlayerProfile *)profile didReceiveDeleteOnStatusChangeRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
      sessionKey:(NSString *)sessionkey
{
    CheckDIDAndSK(response, deviceId, sessionkey) {
        response.result = DConnectMessageResultTypeOk;
    }
    
    return YES;
}


@end
