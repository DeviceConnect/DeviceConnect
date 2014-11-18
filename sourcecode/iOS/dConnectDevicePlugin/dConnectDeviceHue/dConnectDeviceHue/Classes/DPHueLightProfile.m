//
//  DPHueLightProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueLightProfile.h"
#import <HueSDK_iOS/HueSDK.h>

@interface DPHueLightProfile()
@property (nonatomic) id hueStatusBlock;

@end
@implementation DPHueLightProfile



- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    return self;
    
}

- (void)dealloc {
    [[DPHueManager sharedManager] deallocPHNotificationManagerWithReceiver:self];
}
#pragma mark - light
//Light GET ライトのリスト取得
- (BOOL) profile:(DCMLightProfile *)profile didReceiveGetLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    //Bridge Status Update
    [self initHueSdk:deviceId];
    __weak typeof(self) _self = self;
    
    _hueStatusBlock = ^(BridgeConnectState state){
        if (state != STATE_CONNECT) {
            [_self setErrRespose:response];
            [[DConnectManager sharedManager] sendResponse:response];
            return;
        }
        NSDictionary *lightList = [[DPHueManager sharedManager] getLightStatus];
        DConnectArray *lights = [DConnectArray array];
        
        for (PHLight *light in lightList.allValues) {
            
            //ライトの状態をメッセージにセットする（LightID,名前,点灯状態）
            DConnectMessage *led = [DConnectMessage new];
            [led setString:light.identifier forKey:DCMLightProfileParamLightId];
            [led setString:light.name forKey:DCMLightProfileParamName];
            
            [led setBool:[light.lightState.on boolValue] forKey:DCMLightProfileParamOn];
            [lights addMessage:led];
        }
        [response setResult:DConnectMessageResultTypeOk];
        [response setArray:lights forKey:DCMLightProfileParamLights];
        [[DPHueManager sharedManager] deallocPHNotificationManagerWithReceiver:_self];
        [[DPHueManager sharedManager] deallocHueSDK];
        [[DConnectManager sharedManager] sendResponse:response];
    };
    return NO;
}

//Light Post 点灯
- (BOOL) profile:(DCMLightProfile *)profile
    didReceivePostLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
      brightness:(double)brightness
           color:(NSString*) color
        flashing:(NSArray*) flashing
{
    NSString* brightnessString = [request stringForKey:DCMLightProfileParamBrightness];
    if (brightnessString) {
        if (![[DPHueManager sharedManager] isDigitWithString:brightnessString]) {
            [self setErrRespose:response];
            return YES;
        }
    }
    return [self turnOnOffHueLightWithResponse:response lightId:lightId isOn:YES brightness:brightness color:color];
}

//Light Delete 消灯
- (BOOL) profile:(DCMLightProfile *)profile
didReceiveDeleteLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
{
    return [self turnOnOffHueLightWithResponse:response lightId:lightId isOn:NO brightness:0 color:nil];
}





//Light Put 名前変更
- (BOOL) profile:(DCMLightProfile *)profile didReceivePutLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
            name:(NSString *)name
      brightness:(double)brightness
           color:(NSString*)color
        flashing:(NSArray*) flashing
{
    //nameが指定されてない場合はエラーで返す
    if (![[DPHueManager sharedManager] checkParamRequiredStringItemWithParam:name errorState:STATE_ERROR_NO_NAME]) {
        [self setErrRespose:response];
        return YES;
    }

    if (![[DPHueManager sharedManager] checkParamLightId:lightId]) {
        [self setErrRespose:response];
        return YES;
    }
    return [[DPHueManager sharedManager] changeLightNameWithLightId:lightId
                                                                  name:name
                                                         completion:^{
                                                             [self setErrRespose:response];
                                                             [[DConnectManager sharedManager] sendResponse:response];
                                                         }];
    
}


#pragma mark - light group
//Light Group GET グループ一覧取得
- (BOOL)                   profile:(DCMLightProfile *)profile
    didReceiveGetLightGroupRequest:(DConnectRequestMessage *)request
                          response:(DConnectResponseMessage *)response
                          deviceId:(NSString *)deviceId
{
    [self initHueSdk:deviceId];
    __weak typeof(self) _self = self;
    _hueStatusBlock = ^(BridgeConnectState state){
        if (state != STATE_CONNECT) {
            [_self setErrRespose:response];
            [[DConnectManager sharedManager] sendResponse:response];
            return;
        }
        NSDictionary* groupList = [[DPHueManager sharedManager] getLightGroupStatus];
        NSDictionary* lightList = [[DPHueManager sharedManager] getLightStatus];

        DConnectArray *groups = [DConnectArray array];
        
        for (PHGroup *group in groupList.allValues) {
            
            DConnectMessage *g = [DConnectMessage new];
            [g setString:group.identifier forKey:DCMLightProfileParamGroupId];
            if (group.name) {
                [g setString:group.name forKey:DCMLightProfileParamName];
            } else {
                [g setString:@"" forKey:DCMLightProfileParamName];
            }
            //キャッシュにあるライトの一覧からライトを取り出す
            NSArray *lightIds = group.lightIdentifiers;
            DConnectArray *lights = [DConnectArray array];
            for (PHLight *light in lightList.allValues) {
                for (NSString *lightId in lightIds) {
                    
                    if ([lightId isEqualToString:light.identifier]) {
                        
                        DConnectMessage *led = [DConnectMessage new];
                        [led setString:light.identifier forKey:DCMLightProfileParamLightId];
                        [led setString:light.name forKey:DCMLightProfileParamName];
                        
                        [led setBool:[light.lightState.on boolValue] forKey:DCMLightProfileParamOn];
                        [lights addMessage:led];
                        
                    }
                }
            }
            
            [g setArray:lights forKey:DCMLightProfileParamLights];
            [groups addMessage:g];
        }
        [response setResult:DConnectMessageResultTypeOk];
        [response setArray:groups forKey:DCMLightProfileParamLightGroups];
        [[DPHueManager sharedManager] deallocPHNotificationManagerWithReceiver:_self];
        [[DPHueManager sharedManager] deallocHueSDK];
        [[DConnectManager sharedManager] sendResponse:response];
    };
    return NO;
}

//Light Group Post ライトグループ点灯
- (BOOL) profile:(DCMLightProfile *)profile didReceivePostLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*)groupId
      brightness:(double)brightness
           color:(NSString*)color
        flashing:(NSArray*)flashing
{
    NSString* brightnessString = [request stringForKey:DCMLightProfileParamBrightness];
    if (brightnessString) {
        if (![[DPHueManager sharedManager] isDigitWithString:brightnessString]) {
            [self setErrRespose:response];
            return YES;
        }
    }

    return [self turnOnOffHueLightGroupWithResponse:response groupId:groupId isOn:YES brightness:brightness color:color];
}


//Light Group Delete ライトグループ消灯
- (BOOL) profile:(DCMLightProfile *)profile didReceiveDeleteLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*)groupId
{
    return [self turnOnOffHueLightGroupWithResponse:response groupId:groupId isOn:NO brightness:0 color:nil];
}

//Light Group Put ライトグループ名称変更
- (BOOL) profile:(DCMLightProfile *)profile didReceivePutLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*) groupId
            name:(NSString *)name
      brightness:(double)brightness
           color:(NSString*)color
        flashing:(NSArray*)flashing
{
    
    //nameが指定されてない場合はエラーで返す
    if (![[DPHueManager sharedManager] checkParamRequiredStringItemWithParam:name errorState:STATE_ERROR_NO_NAME]) {
        [self setErrRespose:response];
        return YES;
    }
    
    //groupIdチェック
    if (![[DPHueManager sharedManager] checkParamGroupId:groupId]) {
        [self setErrRespose:response];
        return YES;
    }
    
    return [[DPHueManager sharedManager] changeGroupNameWithGroupId:groupId name:name completion:^{
        [self setErrRespose:response];
        [[DConnectManager sharedManager] sendResponse:response];
    }];
    
}

//Light Group Post ライトグループ作成
- (BOOL) profile:(DCMLightProfile *)profile didReceivePostLightGroupCreateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
        lightIds:(NSArray*)lightIds
       groupName:(NSString*)groupName {
    BOOL result = [[DPHueManager sharedManager] createLightGroupWithLightIds:lightIds groupName:groupName completion:^(NSString* groupId) {
        if (groupId) {
            [response setString:groupId forKey:DCMLightProfileParamGroupId];
            [DPHueManager sharedManager].bridgeConnectState = STATE_CONNECT;
        } else {
            [DPHueManager sharedManager].bridgeConnectState = STATE_ERROR_CREATE_FAIL_GROUP;
        }
        [self setErrRespose:response];
        [[DConnectManager sharedManager] sendResponse:response];

    }];
    [response setResult:DConnectMessageResultTypeError];
    [self setErrRespose:response];
    return result;
}



//Light Group Delete ライトグループ削除
- (BOOL) profile:(DCMLightProfile *)profile didReceiveDeleteLightGroupClearRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*)groupId
{
    BOOL result = [[DPHueManager sharedManager] removeLightGroupWithWithGroupId:groupId completion:^{
        [self setErrRespose:response];
        [[DConnectManager sharedManager] sendResponse:response];
    }];
    [response setResult:DConnectMessageResultTypeError];
    [self setErrRespose:response];
    return result;    
}

#pragma mark - private method

//Hue SDKの初期化
- (void)initHueSdk:(NSString*)deviceId
{
    dispatch_sync(dispatch_get_main_queue(), ^{
        NSArray *arr = [deviceId componentsSeparatedByString:@"_"];
        if(arr.count > 1) {
            NSString * ipAdr =  [arr objectAtIndex:0];
            NSString * macAdr = [arr objectAtIndex:1];
            [[DPHueManager sharedManager] initHue];
            [[DPHueManager sharedManager] startAuthenticateBridgeWithIpAddress:ipAdr
                                                                    macAddress:macAdr
                                                                      receiver:self
                                                localConnectionSuccessSelector:@selector(willLocalConnectionSuccess)
                                                             noLocalConnection:@selector(willNoLocalConnection)
                                                              notAuthenticated:@selector(willNotAuthenticated)];
        }
        [DPHueManager sharedManager].bridgeConnectState = STATE_INIT;
    });
}
//接続した時のイベント
- (void)willLocalConnectionSuccess {
    [DPHueManager sharedManager].bridgeConnectState = STATE_CONNECT;

    DPHueLightStatusBlock block = _hueStatusBlock;
    if (block) {
        block(STATE_CONNECT);
        _hueStatusBlock = nil;
    }
}

//接続が切れた場合のイベント
- (void)willNoLocalConnection {
    [DPHueManager sharedManager].bridgeConnectState = STATE_NON_CONNECT;
    DPHueLightStatusBlock block = _hueStatusBlock;
    if (block) {
        block(STATE_NON_CONNECT);
        _hueStatusBlock = nil;
    }
}

//アプリ登録がHueのブリッジに行われていない場合のイベント
- (void)willNotAuthenticated {
    [DPHueManager sharedManager].bridgeConnectState = STATE_NOT_AUTHENTICATED;
    DPHueLightStatusBlock block = _hueStatusBlock;
    if (block) {
        block(STATE_NOT_AUTHENTICATED);
        _hueStatusBlock = nil;
    }
}

//ライトのON/OFF
- (BOOL)turnOnOffHueLightWithResponse:(DConnectResponseMessage*)response
                              lightId:(NSString*)lightId
                                isOn:(BOOL)isOn
                          brightness:(double)brightness
                               color:(NSString*)color
{
    if (![[DPHueManager sharedManager] checkParamLightId:lightId]) {
        [self setErrRespose:response];
        return YES;
    }
    if (brightness < 0 || brightness > 1) {
        [response setErrorToInvalidRequestParameterWithMessage:@"invalid brightness value."];
        return YES;
    }
    PHLightState *lightState = [[DPHueManager sharedManager] getLightStateIsOn:isOn brightness:brightness color:color];
    if (lightState == nil) {
        [self setErrRespose:response];
        return YES;
    }
    return [[DPHueManager sharedManager] changeLightStatusWithLightId:lightId
                                                           lightState:lightState
                                                           completion:^ {
                                                               [self setErrRespose:response];
                                                               [[DConnectManager sharedManager] sendResponse:response];
                                                           }];
}

//ライトグループのON/OFF
-(BOOL)turnOnOffHueLightGroupWithResponse:(DConnectResponseMessage*)response
                                  groupId:(NSString*)groupId
                                     isOn:(BOOL)isOn
                               brightness:(double)brightness
                                    color:(NSString*)color
{
    //groupIdチェック
    if (![[DPHueManager sharedManager] checkParamGroupId:groupId]) {
        [self setErrRespose:response];
        return YES;
    }
    PHLightState *lightState = [[DPHueManager sharedManager] getLightStateIsOn:isOn brightness:brightness color:color];
    if (lightState == nil) {
        [self setErrRespose:response];
        return YES;
    }
    return [[DPHueManager sharedManager] changeGroupStatusWithGroupId:groupId lightState:lightState completion:^{
        [self setErrRespose:response];
        [[DConnectManager sharedManager] sendResponse:response];
        
    }];
}

//エラーの振り分け
- (void) setErrRespose:(DConnectResponseMessage *)response {
    
    switch ([DPHueManager sharedManager].bridgeConnectState) {
        case STATE_INIT:
            [response setErrorToNotFoundDeviceWithMessage:@"Not the response from the hue"];
            break;
        case STATE_NON_CONNECT:
            [response setErrorToNotFoundDeviceWithMessage:@"Bridge not found"];
            break;
        case STATE_NOT_AUTHENTICATED:
            [response setErrorToNotFoundDeviceWithMessage:@"It is not application registration, please register from the app settings screen"];
            break;
        case STATE_ERROR_NO_NAME:
            [response setErrorToInvalidRequestParameterWithMessage:@"Name after the change has not been specified"];
            break;
        case STATE_ERROR_NO_LIGHTID:
             [response setErrorToInvalidRequestParameterWithMessage:@"lightIds must be specified"];
            break;
        case STATE_ERROR_INVALID_LIGHTID:
            [response setErrorToInvalidRequestParameterWithMessage:@"lightIds is invalid"];
            break;
        case STATE_ERROR_INVALID_BRIGHTNESS:
            [response setErrorToInvalidRequestParameterWithMessage:@"brightness is invalid"];
            break;
        case STATE_ERROR_LIMIT_GROUP:
            [response setErrorToUnknownAttributeWithMessage:@"Hue has reached the upper limit to which the group can create"];
            break;
        case STATE_ERROR_CREATE_FAIL_GROUP:
            [response setErrorToUnknownWithMessage:@"Failed to create a group"];
            break;
        case STATE_ERROR_DELETE_FAIL_GROUP:
            [response setErrorToUnknownWithMessage:@"Failed to delete the group"];
            break;
        case STATE_ERROR_NOT_FOUND_LIGHT:
            [response setErrorToInvalidRequestParameterWithMessage:@"light not found"];
            break;
        case STATE_ERROR_NO_GROUPID:
            [response setErrorToInvalidRequestParameterWithMessage:@"groupId must be specified"];
            break;
        case STATE_ERROR_NOT_FOUND_GROUP:
            [response setErrorToInvalidRequestParameterWithMessage:@"group not found"];
            break;
        case STATE_ERROR_INVALID_COLOR:
            [response setErrorToInvalidRequestParameterWithMessage:@"color is invalid"];
            break;
        case STATE_ERROR_UPDATE_FAIL_LIGHT_STATE:
             [response setErrorToUnknownWithMessage:@"Failed to update the state of the light"];
            break;
        case STATE_ERROR_CHANGE_FAIL_LIGHT_NAME:
            [response setErrorToUnknownWithMessage:@"Failed to change the name of the light"];
            break;
        case STATE_ERROR_UPDATE_FAIL_GROUP_STATE:
            [response setErrorToUnknownWithMessage:@"Failed to update the state of the light group"];
            break;
        case STATE_ERROR_CHANGE_FAIL_GROUP_NAME:
            [response setErrorToUnknownWithMessage:@"Failed to change the name of the light group"];
            break;
        case STATE_CONNECT:
            [response setResult:DConnectMessageResultTypeOk];
            break;
        default:
            [response setResult:DConnectMessageResultTypeError];
            break;
    }
}

@end
