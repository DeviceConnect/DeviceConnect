//
//  HueProfile.m
//  dConnectDeviceHue
//
//  Created by 星　貴之 on 2014/07/08.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPHueLightProfile.h"
#import <HueSDK_iOS/HueSDK.h>
#import "DCLogger.h"

@interface DPHueLightProfile()


@end
@implementation DPHueLightProfile

const BOOL SYNC_RESPOSE = YES;
const BOOL ASYNC_RESPOSE = NO;

PHHueSDK *phHueSDK;

DConnectResponseMessage *resHue;
PHNotificationManager *notificationManager;
PHBridgeSearching *bridgeSearching;

DCLogger *mlog;
BridgeConnectState mBridgeConnectState;

//======================================================================

- (id)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
    }
    
    mlog = [[DCLogger alloc]initWithSourceClass:self];
    return self;
    
}

//======================================================================

#pragma light
//Light GET ライトのリスト取得
- (BOOL) profile:(DCMLightProfile *)profile didReceiveGetLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    [mlog entering:@"didReceiveGetLightRequest" param:deviceId];

    //Hue初期化
    [self initHueSdk:deviceId];

    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }
    
    //キャッシュにあるライトの一覧からライトを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    DConnectArray *lights = [DConnectArray array];
    
    for (PHLight *light in cache.lights.allValues) {
        
        //ライトの状態をメッセージにセットする（LightID,名前,点灯状態）
        DConnectMessage *led = [DConnectMessage new];
        [led setString:light.identifier forKey:DCMLightProfileParamLightId];
        [led setString:light.name forKey:DCMLightProfileParamName];
        
        [led setBool:[light.lightState.on boolValue] forKey:DCMLightProfileParamOn];
        //        [led setString:@"" forKey:LightProfileParamConfig];
        [lights addMessage:led];
    }
    
    //OKを返す
    [response setResult:DConnectMessageResultTypeOk];
    [response setArray:lights forKey:DCMLightProfileParamLights];

    //HueSDKの開放
    [self disableHeartBeat];

    [mlog exiting:@"didReceiveGetLightRequest" param:deviceId];

    return SYNC_RESPOSE;
}

//======================================================================
//Light Post 点灯
- (BOOL) profile:(DCMLightProfile *)profile
    didReceivePostLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
      brightness:(double)brightness
           color:(NSString*) color
        flashing:(NSArray*) flashing
{
 
    [mlog entering:@"didReceivePostLightRequest" param:lightId];

    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }

    if (![self checkParamLightId:response lightId:lightId]) {
        return SYNC_RESPOSE;
    }
    
    //lightState取得
    PHLightState *lightState = [self getLightState:response isOn:YES brightness:brightness color:color];
    
    //lightStateがnilならエラーなので終了
    if (lightState == nil) {
        return SYNC_RESPOSE;
    }

    //Lightのステータスチェンジ
    BOOL res = [self changeLightStatus:response lightId:lightId lightState:lightState];
    
    [mlog exiting:@"didReceivePostLightRequest" param:lightId];

    return res;
    
}

//======================================================================
//Light Delete 消灯
- (BOOL) profile:(DCMLightProfile *)profile
didReceiveDeleteLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
{
    
    [mlog entering:@"didReceiveDeleteLightRequest" param:lightId];
    
    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }
    
    //LightIdチェック
    if (![self checkParamLightId:response lightId:lightId]) {
        return SYNC_RESPOSE;
    }
    
    //lightState取得
    PHLightState *lightState = [self getLightState:response isOn:NO brightness:0 color:nil];

    //lightStateがnilならエラーなので終了
    if (lightState == nil) {
        return SYNC_RESPOSE;
    }
    
    //Lightのステータスチェンジ
    BOOL res = [self changeLightStatus:response lightId:lightId lightState:lightState];
    
    [mlog exiting:@"didReceiveDeleteLightRequest" param:lightId];

    return res;
    
}

//======================================================================
//Light Put 名前変更
- (BOOL) profile:(DCMLightProfile *)profile didReceivePutLightRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         lightId:(NSString*) lightId
            name:(NSString *)name
      brightness:(double)brightness
           color:(NSString*)
color flashing:(NSArray*) flashing
{
    
    [mlog entering:@"didReceivePutLightRequest" param:lightId];

    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }
    
    //nameが指定されてない場合はエラーで返す
    if (![self checkParamRequiredStringItem:response value:name errMsg:@"変更後のnameが指定されていません"]) {
        return SYNC_RESPOSE;
    }

    //LightIdチェック
    if (![self checkParamLightId:response lightId:lightId]) {
        return SYNC_RESPOSE;
    }

    //Lightのステータスチェンジ
    BOOL res = [self changeLightName:response lightId:lightId name:name];
    
    [mlog exiting:@"didReceivePutLightRequest" param:lightId];

    return res;
    
}

//======================================================================

#pragma light group
//Light Group GET グループ一覧取得
- (BOOL) profile:(DCMLightProfile *)profile didReceiveGetLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
{
    
    [mlog entering:@"didReceiveGetLightGroupRequest" param:deviceId];

    
    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }
    
    //キャッシュにあるグループの一覧からグループを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    DConnectArray *groups = [DConnectArray array];
    
    for (PHGroup *group in cache.groups.allValues) {
        
        DConnectMessage *g = [DConnectMessage new];
//        [mlog fine:@"didReceiveGetLightGroupRequest" paramName:@"groupId" paramString:group.identifier];
        
        [g setString:group.identifier forKey:DCMLightProfileParamGroupId];
        [g setString:group.name forKey:DCMLightProfileParamName];
        
        //キャッシュにあるライトの一覧からライトを取り出す
        NSArray *lightIds = group.lightIdentifiers;
        DConnectArray *lights = [DConnectArray array];
        
        for (PHLight *light in cache.lights.allValues) {
            for (NSString *lightId in lightIds) {
                
                if ([lightId isEqualToString:light.identifier]) {
                    
                    DConnectMessage *led = [DConnectMessage new];
                    [led setString:light.identifier forKey:DCMLightProfileParamLightId];
                    [led setString:light.name forKey:DCMLightProfileParamName];
                    
                    [led setBool:[light.lightState.on boolValue] forKey:DCMLightProfileParamOn];
                    //        [led setString:@"" forKey:LightProfileParamConfig];
                    [lights addMessage:led];
                    
                }
            }
        }
        
        [g setArray:lights forKey:DCMLightProfileParamLights];
        //[g setString:@"" forKey:LightProfileParamConfig];
        [groups addMessage:g];
    }
    
//    [mlog fine:@"didReceiveGetLightGroupRequest" paramName:@"groupCount" paramInt:[groups count]];

    //OKを返す
    [response setResult:DConnectMessageResultTypeOk];
    [response setArray:groups forKey:DCMLightProfileParamLightGroups];
    
    [mlog exiting:@"didReceiveGetLightGroupRequest" param:deviceId];

    return SYNC_RESPOSE;
}

//======================================================================
//Light Group Post ライトグループ点灯
- (BOOL) profile:(DCMLightProfile *)profile didReceivePostLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*) groupId
      brightness:(double)brightness
           color:(NSString*) color
        flashing:(NSArray*) flashing
{
    
    [mlog entering:@"didReceivePostLightGroupRequest" param:groupId];

    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }

    //groupIdチェック
    if (![self checkParamGroupId:response groupId:groupId]) {
        return SYNC_RESPOSE;
    }

    //lightState取得
    PHLightState *lightState = [self getLightState:response isOn:YES brightness:brightness color:color];
    
    //lightStateがnilならエラーなので終了
    if (lightState == nil) {
        return SYNC_RESPOSE;
    }

    //LightGroupのステータスチェンジ
    BOOL res = [self changeGroupStatus:response groupId:groupId lightState:lightState];
    
    [mlog exiting:@"didReceivePostLightGroupRequest" param:groupId];

    return res;
    
}

//======================================================================
//Light Group Delete ライトグループ消灯
- (BOOL) profile:(DCMLightProfile *)profile didReceiveDeleteLightGroupRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*)groupId
{
    
    [mlog entering:@"didReceiveDeleteLightGroupRequest" param:groupId];

    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }

    //groupIdチェック
    if (![self checkParamGroupId:response groupId:groupId]) {
        return SYNC_RESPOSE;
    }
    
    //lightState取得
    PHLightState *lightState = [self getLightState:response isOn:NO brightness:0 color:nil];
    
    //lightStateがnilならエラーなので終了
    if (lightState == nil) {
        return SYNC_RESPOSE;
    }
    
    //LightGroupのステータスチェンジ
    BOOL res = [self changeGroupStatus:response groupId:groupId lightState:lightState];
    
    [mlog exiting:@"didReceiveDeleteLightGroupRequest" param:groupId];

    return res;
}

//======================================================================
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
    
    [mlog entering:@"didReceivePutLightGroupRequest" param:groupId];

    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }
    
    //nameが指定されてない場合はエラーで返す
    if (![self checkParamRequiredStringItem:response
                                      value:name
                                     errMsg:@"変更後のnameが指定されていません"])
    {
        return SYNC_RESPOSE;
    }

    //groupIdチェック
    if (![self checkParamGroupId:response groupId:groupId]) {
        return SYNC_RESPOSE;
    }
    
    //groupのステータスチェンジ
    BOOL res = [self changeGroupName:response groupId:groupId name:name];
    
    [mlog exiting:@"didReceivePutLightGroupRequest" param:groupId];

    return res;
    
}

//======================================================================
//Light Group Post ライトグループ作成
- (BOOL) profile:(DCMLightProfile *)profile didReceivePostLightGroupCreateRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
        lightIds:(NSArray*)lightIds
       groupName:(NSString*)groupName
{
    
    [mlog entering:@"didReceivePostLightGroupCreateRequest" param:groupName];

    if (lightIds.count <= 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"lightIdsが指定されていません"];
        
        return SYNC_RESPOSE;
    }
    
    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }

    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];

    //lightIdでArrayを作る
    NSMutableDictionary *lightArray = [NSMutableDictionary dictionary];
    
    for (PHLight *light in cache.lights.allValues) {
        for (NSString *lightId in lightIds) {
            if ([lightId isEqualToString:light.identifier]) {
                [lightArray setObject:light forKey:light.identifier];
            }
        }
    }

    //ID Listエラーチェック
    if (lightArray.count <= 0) {
        [response setErrorToInvalidRequestParameterWithMessage:@"lightIdsが正しくありません"];
        return SYNC_RESPOSE;
    }

    //限界チェック
    if (cache.groups.count >= 16) {
        [response setErrorToUnknownAttributeWithMessage:@"グループが作成できる上限に達しています"];
        return SYNC_RESPOSE;
    }

    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        [bridgeSendAPI createGroupWithName:groupName lightIds:[lightArray allKeys] completionHandler:^(NSString *groupIdentifier, NSArray *errors) {
            
            //メインスレッドで動かすことでエラーは拾えるが詳しくは返さない
            if (errors != nil) {
                
                [response setErrorToUnknownWithMessage:@"グループの作成に失敗しました"];
                
            }else{
                
                //groupIdを返す
                [response setString:groupIdentifier forKey:DCMLightProfileParamGroupId];

                //OKを返す
                [response setResult:DConnectMessageResultTypeOk];
                
            }
            
            // レスポンスを返却
            [[DConnectManager sharedManager] sendResponse:response];

            
        }];
    });
    
    [mlog exiting:@"didReceivePostLightGroupCreateRequest" param:groupName];

    return ASYNC_RESPOSE;
    
}

//completionHandlerの共通処理
- (void) setCompletionResponse:(DConnectResponseMessage *)response
                        errors:(NSArray*)errors
                        errMsg:(NSString*)errMsg
{
    if (errors != nil) {
        
        [response setErrorToUnknownWithMessage:errMsg];
        
    }else{
        
        //OKを返す
        [response setResult:DConnectMessageResultTypeOk];
        
    }

    // レスポンスを返却
    [[DConnectManager sharedManager] sendResponse:response];

    //HueSDKの開放
    [self disableHeartBeat];

}

//======================================================================
//Light Group Delete ライトグループ削除
- (BOOL) profile:(DCMLightProfile *)profile didReceiveDeleteLightGroupClearRequest:(DConnectRequestMessage *)request
        response:(DConnectResponseMessage *)response
        deviceId:(NSString *)deviceId
         groupId:(NSString*)groupId
{
    
    [mlog entering:@"didReceiveDeleteLightGroupClearRequest" param:groupId];
    
    //Hue初期化
    [self initHueSdk:deviceId];
    
    //Hue初期化でエラーに成った場合の処理
    if (mBridgeConnectState != STATE_CONNECT) {
        
        [self setErrRespose:response];
        
        return SYNC_RESPOSE;
    }

    //groupIdチェック
    if (![self checkParamGroupId:response groupId:groupId]) {
        return SYNC_RESPOSE;
    }

    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    dispatch_sync(dispatch_get_main_queue(), ^{

        [bridgeSendAPI removeGroupWithId:groupId completionHandler:^(NSArray *errors) {
            
            [self setCompletionResponse:response errors:errors
                                 errMsg:@"グループの削除に失敗しました"];
            
        }];
    });
    
    [mlog exiting:@"didReceiveDeleteLightGroupClearRequest" param:groupId];

    return ASYNC_RESPOSE;
}

//======================================================================

#pragma common method

//======================================================================
//trim
- (NSString*)trimString:(NSString*)orgString
{
    if (orgString == nil) {
        return nil;
    }
    
    return [orgString stringByTrimmingCharactersInSet:[NSCharacterSet newlineCharacterSet]];
}

//======================================================================
//パラメータチェック LightId
- (BOOL)checkParamLightId:(DConnectResponseMessage *)response lightId:(NSString*) lightId
{
    
    NSString *myLightId = [self trimString:lightId];
    
    //LightIdが指定されてない場合はエラーで返す
    if (!myLightId) {
        [response setErrorToInvalidRequestParameterWithMessage:@"lightIdが指定されていません"];//"lightId must be specified."
        
        //HueSDKの開放
        [self disableHeartBeat];

        return NO;
    }
    
    //キャッシュにあるライトの一覧からライトを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    
    [mlog fine:@"checkParamLightId" paramName:@"myLightId" paramString:myLightId];
    
    for (PHLight *light in cache.lights.allValues) {
        
        [mlog fine:@"checkParamLightId" paramName:@"light.identifier" paramString:[self trimString:light.identifier]];
        
        if ([myLightId isEqualToString:light.identifier]) {
            return YES;
        }
    }
    
    [response setErrorToInvalidRequestParameterWithMessage:@"ライトが見つかりません"];

    //HueSDKの開放
    [self disableHeartBeat];

    return NO;
}

//======================================================================
//パラメータチェック groupId
- (BOOL)checkParamGroupId:(DConnectResponseMessage *)response groupId:(NSString*) groupId
{
    
    NSString *myGroupId = [self trimString:groupId];
    
    //groupIdが指定されてない場合はエラーで返す
    if (!myGroupId) {
        [response setErrorToInvalidRequestParameterWithMessage:@"groupIdが指定されていません"];

        //HueSDKの開放
        [self disableHeartBeat];

        return NO;
    }
    
    [mlog fine:@"checkParamGroupId" paramName:@"myGroupId" paramString:myGroupId];

    //グループ０は有効
    if ([myGroupId isEqualToString:@"0"]) {
        return YES;
    }
    
    //キャッシュにあるグループの一覧からグループを取り出す
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
//    DConnectArray *groups = [DConnectArray array];
    
    for (PHGroup *group in cache.groups.allValues) {
        
        [mlog fine:@"checkParamGroupId" paramName:@"group.identifier" paramString:[self trimString:group.identifier]];
        
        if ([myGroupId isEqualToString:group.identifier]) {
            return YES;
        }
    }
    
    [response setErrorToInvalidRequestParameterWithMessage:@"グループが見つかりません"];
    
    //HueSDKの開放
    [self disableHeartBeat];

    return NO;
}

//======================================================================
//文字列がnil、trim結果が空文字の場合Trueを返す
- (BOOL)isZeroLengthString:(NSString*)value
{
    if (value == nil) {
        value = @"";
    }
    
    NSString *myValue = [self trimString:value];
    
    //valueが指定されてない場合はエラーで返す
    if (myValue.length == 0) {
        
        return YES;
    }

    return NO;
}

//======================================================================
//パラメータチェック 必須文字チェック
- (BOOL)checkParamRequiredStringItem:(DConnectResponseMessage *)response
                   value:(NSString*)value
                  errMsg:(NSString*)errMsg
{
    
    //valueが指定されてない場合はエラーで返す
    if ([self isZeroLengthString:value]) {
        [response setErrorToInvalidRequestParameterWithMessage:errMsg];
        
        //HueSDKの開放
        [self disableHeartBeat];
        
        return NO;
    }
    
    return YES;
}

//======================================================================
//エラーの場合、エラー情報をresponseに設定しnilをreturn
- (PHLightState*) getLightState:(DConnectResponseMessage *)response
                           isOn:(BOOL)isOn
                     brightness:(double)brightness
                          color:(NSString *)color
{
    PHLightState *lightState = [[PHLightState alloc] init];
    
    [lightState setOnBool:isOn];
    
    if (isOn) {
        NSString *myColor = [self trimString:color];
        
        if (myColor) {
            
            [mlog fine:@"getLightState" paramName:@"color" paramString:color];
            
            if (color.length != 6) {
                [response setErrorToInvalidRequestParameterWithMessage:@"colorが正しくありません"];
                return nil;
            }
            
            @try {
                
                CGPoint xyPoint = [DPHueLightProfile convRgbToXy:color];
                
                [lightState setX:[NSNumber numberWithFloat:xyPoint.x]];
                [lightState setY:[NSNumber numberWithFloat:xyPoint.y]];
                
            }
            @catch(NSException *exception) {
                [response setErrorToInvalidRequestParameterWithMessage:@"colorが正しくありません"];
                return nil;
            }
            
        }else{
            
            CGPoint xyPoint = [DPHueLightProfile convRgbToXy:@"FFFFFF"];
            
            [lightState setX:[NSNumber numberWithFloat:xyPoint.x]];
            [lightState setY:[NSNumber numberWithFloat:xyPoint.y]];
            
        }
        
        if (brightness != DBL_MIN) {
            int myBlightness = brightness * 255;
            
            if (myBlightness < 0) {
                myBlightness = 0;
            }
            if (myBlightness > 254) {
                myBlightness = 254;
            }
            
            [mlog fine:@"getLightState" paramName:@"brightness" paramDouble:brightness];
            
            [lightState setBrightness:[NSNumber numberWithInt:(int)myBlightness]];
        }
    }
    
    return lightState;
}

//======================================================================
/*!
 Lightのステータスチェンジ
*/
- (BOOL) changeLightStatus:(DConnectResponseMessage *)response
                   lightId:(NSString *)lightId
                lightState:(PHLightState*)lightState
{
    
    [mlog entering:@"changeLightStatus" param:lightId];

    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        // Send lightstate to light
        
        [bridgeSendAPI updateLightStateForId:lightId withLightState:lightState completionHandler:^(NSArray *errors) {
        
            [self setCompletionResponse:response
                                 errors:errors
                                 errMsg:@"ライトの状態更新に失敗しました"];

        }];
    });
    
    return ASYNC_RESPOSE;
}

//======================================================================
- (BOOL) changeLightName:(DConnectResponseMessage *)response lightId:(NSString *)lightId name:(NSString *)name {
    
    [mlog entering:@"changeLightName" param:lightId];
    
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];

    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        for (PHLight *light in cache.lights.allValues) {
            
            [mlog fine:@"changeLightName" paramName:@"changeLightName" paramString:light.identifier];
            
            if ([light.identifier isEqualToString:lightId]) {
                
                [light setName:name];
                
                [bridgeSendAPI updateLightWithLight:light completionHandler:^(NSArray *errors) {
                    
                    [self setCompletionResponse:response
                                         errors:errors
                                         errMsg:@"ライトの名称変更に失敗しました"];
  
                }];
                
                break;
            }
        }
    });
    
    [mlog exiting:@"changeLightName" param:lightId];
    
    return ASYNC_RESPOSE;
}

//======================================================================
/*!
 LightGroupのステータスチェンジ
 */
- (BOOL) changeGroupStatus:(DConnectResponseMessage *)response
                   groupId:(NSString *)groupId
                lightState:(PHLightState*)lightState
{
    
    [mlog entering:@"statusChangeGroup" param:groupId];

    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        // Send lightstate to light
        [bridgeSendAPI setLightStateForGroupWithId:groupId lightState:lightState completionHandler:^(NSArray *errors) {
  
            [self setCompletionResponse:response
                                 errors:errors
                                 errMsg:@"ライトの状態更新に失敗しました"];
            
        }];
    });

    return ASYNC_RESPOSE;
}

/*!
 LightGroupのnameチェンジ
 */
- (BOOL) changeGroupName:(DConnectResponseMessage *)response
                 groupId:(NSString *)groupId
                    name:(NSString *)name {
    
    [mlog entering:@"changeGroupName" param:groupId];
    
    PHBridgeSendAPI *bridgeSendAPI = [[PHBridgeSendAPI alloc] init];
    
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        for (PHGroup *group in cache.groups.allValues) {
            
            [mlog fine:@"changeGroupName" paramName:@"group.identifier"
                paramString:group.identifier];

            if ([group.identifier isEqualToString:groupId]) {
                
                [group setName:name];
                
                [bridgeSendAPI updateGroupWithGroup:group completionHandler:^(NSArray *errors) {
                
                    [self setCompletionResponse:response
                                         errors:errors
                                         errMsg:@"グループの名称変更に失敗しました"];
                    
                }];
                
                break;
                
            }
        }
        
        //HueSDKの開放
        [self disableHeartBeat];
    
    });
    
    [mlog exiting:@"changeGroupName" param:groupId];
    
    return ASYNC_RESPOSE;

}

//======================================================================

+ (CGPoint) convRgbToXy:(NSString *)color {
    
    NSString *r = [color substringWithRange:NSMakeRange(0, 2)];
    NSString *g = [color substringWithRange:NSMakeRange(2, 2)];
    NSString *b = [color substringWithRange:NSMakeRange(4, 2)];
    
    NSScanner *scan = [NSScanner scannerWithString:r];
    
    unsigned int rr, gg, bb;
    
    [scan scanHexInt:&rr];
    scan = [NSScanner scannerWithString:g];
    [scan scanHexInt:&gg];
    scan = [NSScanner scannerWithString:b];
    [scan scanHexInt:&bb];
    
    [mlog fine:@"convRgbToXy" param:[NSString stringWithFormat:@"%ud,%ud,%ud",rr,gg,bb]];
    
    float fRR = (float)(rr/255.0);
    float fGG = (float)(gg/255.0);
    float fBB = (float)(bb/255.0);

    [mlog fine:@"convRgbToXy" param:[NSString stringWithFormat:@"%f,%f,%f",fRR,fGG,fBB]];

    UIColor *uicolor = [UIColor colorWithRed:fRR green:fGG blue:fBB alpha:1.0f];
    
    CGPoint xyPoint = [PHUtilities calculateXY:uicolor forModel:@""];
    
    return xyPoint;
}

//======================================================================

-(PHLightState*)convertHSVtoRGB:(PHLightState*)light r:(double)r g:(double)g b:(double)b{
    
    [mlog entering:@"convertHSVtoRGB" param:nil];

    double rr = r / 255.0f;
    double gg = g / 255.0f;
    double bb = b / 255.0f;
    
    if (rr > 1.0f) rr = 1.0f;
    if (rr < 0.0f) rr = 0;
    if (gg > 1.0f) gg = 1.0f;
    if (gg < 0.0f) gg = 0;
    if (bb > 1.0f) bb = 1.0f;
    if (bb < 0.0f) bb = 0.0;
    
    double max = MAX(MAX(rr, gg), bb);
    double min = MIN(MIN(rr, gg), bb);
    double sub = max - min;
    
    double h = 0, s = 0, v = 0;
    if (sub == 0) {
        h = 0;
    } else {
        if (max == rr) {
            h = ((gg - bb) / (float) sub) * 60 + 0;
        } else if (max == gg) {
            h = ((bb - rr) / (float) sub) * 60 + 120;
        } else if (max == bb) {
            h = ((rr - gg) / (float) sub) * 60 + 240;
        }
        
        if (h < 0) {
            h += 360;
        }
        h /= 360;
    }
    if (max > 0) {
        s = sub / (float) max;
    }
    v = max;
    NSLog(@"%f, %f, %f", (h * 65535), s, v);
    [light setSaturation:[NSNumber numberWithDouble:s * 254]];
    [light setHue:[NSNumber numberWithDouble:h * 65535]];
    [light setBrightness:[NSNumber numberWithDouble:v * 254]];
    return light;
}

//======================================================================
//Hue SDKの初期化
- (void)initHueSdk:(NSString*)deviceId
{
    [mlog entering:@"initHueSdk" param:deviceId];

    //ブリッジとの接続などを一度切る
    [self disableHeartBeat];
    
    phHueSDK = [[PHHueSDK alloc] init];
    [phHueSDK startUpSDK];
    
    //メインスレッドで動作させる
    dispatch_sync(dispatch_get_main_queue(), ^{

        [phHueSDK enableLogging:YES];

        NSArray *arr = [deviceId componentsSeparatedByString:@"_"];
        
        if(arr.count > 1) {

            NSString * ipAdr =  [arr objectAtIndex:0];

            NSString * macAdr = [arr objectAtIndex:1];

            [phHueSDK setBridgeToUseWithIpAddress:ipAdr macAddress:macAdr];


        }
        
        mBridgeConnectState = STATE_INIT;
        

        notificationManager = [PHNotificationManager defaultManager];
        
        [notificationManager registerObject:self
                                    withSelector:@selector(localConnection)
                                 forNotification:LOCAL_CONNECTION_NOTIFICATION];
        
        [notificationManager registerObject:self
                                    withSelector:@selector(noLocalConnection)
                                 forNotification:NO_LOCAL_CONNECTION_NOTIFICATION];
        
        [notificationManager registerObject:self
                                    withSelector:@selector(notAuthenticated)
                                 forNotification:NO_LOCAL_AUTHENTICATION_NOTIFICATION];


        bridgeSearching = [[PHBridgeSearching alloc] initWithUpnpSearch:YES andPortalSearch:YES andIpAdressSearch:NO];

    
        [self enableLocalHeartbeat];

    });
    
    //ハートビートの戻りを待つ
    [self waitHeartBeat];
    
    [mlog exiting:@"initHueSdk" param:deviceId];

}

//======================================================================
- (void)waitHeartBeat {
    
    //最大５秒待つ
    for (int i = 1; i < 6; i++) {
        if (mBridgeConnectState != STATE_INIT) {
            break;
        }
        
        sleep(1);
        [mlog fine:@"waitHeartBeat" paramName:@"waitCount" paramInt:i];
        
    }
}

//======================================================================
/**
 Starts the local heartbeat with a 10 second interval
 */
- (void)enableLocalHeartbeat {
    /***************************************************
     The heartbeat processing collects data from the bridge
     so now try to see if we have a bridge already connected
     *****************************************************/
    
    PHBridgeResourcesCache *cache = [PHBridgeResourcesReader readBridgeResourcesCache];
    if (cache != nil && cache.bridgeConfiguration != nil && cache.bridgeConfiguration.ipaddress != nil) {
        // Some bridge is known
        [phHueSDK enableLocalConnection];
    }
    else {
        /***************************************************
         No bridge connected so start the bridge search process
         *****************************************************/
        
        // No bridge known
        [self searchForBridgeLocal];
    }
}

//======================================================================
/**
 Search for bridges using UPnP and portal discovery, shows results to user or gives error when none found.
 */
- (void)searchForBridgeLocal {
    
//    Stops the local heartbeat
    [phHueSDK disableLocalConnection];
    
    // Show search screen
    /***************************************************
     A bridge search is started using UPnP to find local bridges
     *****************************************************/
    
    // Start search
    [bridgeSearching startSearchWithCompletionHandler:^(NSDictionary *bridgesFound) {
        
    }];
}

//======================================================================
//HueSDKの開放
- (void)disableHeartBeat {
    
    [mlog entering:@"disableHeartBeat" param:nil];
    
    if (phHueSDK != nil) {
        [phHueSDK disableLocalConnection];
        [phHueSDK stopSDK];
    }
    
    if (notificationManager != nil) {
        [notificationManager deregisterObjectForAllNotifications:self];
        notificationManager = nil;
    }
    
    [mlog exiting:@"disableHeartBeat" param:nil];
    
}

//======================================================================
//接続した時のイベント
- (void)localConnection {
    
    [mlog entering:@"localConnection" param:nil];

    mBridgeConnectState = STATE_CONNECT;

}

//======================================================================
//接続が切れた場合のイベント
- (void)noLocalConnection {
    
    [mlog entering:@"noLocalConnection" param:nil];
    
    //HueSDK開放
    [self disableHeartBeat];

    mBridgeConnectState = STATE_NON_CONNECT;

}

//======================================================================
//アプリ登録されていない場合のイベント
- (void)notAuthenticated {
    
    [mlog entering:@"notAuthenticated" param:nil];
    
    //HueSDK開放
    [self disableHeartBeat];
    
    mBridgeConnectState = STATE_NOT_AUTHENTICATED;

}

//======================================================================
- (void) setErrRespose:(DConnectResponseMessage *)response {
    
    switch (mBridgeConnectState) {
        case STATE_INIT:
            [response setError:104 message:@"hueからのレスポンスありませんでした"];
            [response setResult:DConnectMessageResultTypeError];
            break;
            
        case STATE_NON_CONNECT:
            [response setError:201 message:@"ブリッジが見つかりません"];
            [response setResult:DConnectMessageResultTypeError];
            break;
            
        case STATE_NOT_AUTHENTICATED:
            [response setError:401 message:@"アプリ登録されていません、設定画面からアプリ登録をしてください"];
            break;
            
        default:
            [response setResult:DConnectMessageResultTypeError];
            break;
    }
    
    //HueSDKの開放
    [self disableHeartBeat];

}

@end
