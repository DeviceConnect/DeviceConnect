//
//  DPHostNotificationProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHostDevicePlugin.h"
#import "DPHostNotificationProfile.h"
#import "DPHostNetworkServiceDiscoveryProfile.h"
#import "DPHostUtils.h"

/*!
 通知情報を保持するNSArrayの各種情報へのインデックス
 */
typedef NS_ENUM(NSUInteger, NotificationIndex) {
    NotificationIndexType,      ///< type: 通知のタイプ
    NotificationIndexDir,       ///< dir: メッセージの文字の向き
    NotificationIndexLang,      ///< lang: メッセージの言語
    NotificationIndexBody,      ///< body: 通知メッセージ
    NotificationIndexTag,       ///< tag: 任意タグ文字列（カンマ区切りで任意個数指定）
    NotificationIndexIcon,      ///< icon: 画像データ
    NotificationIndexNotifiation, ///< Notification
    NotificationIndexDeviceId,  ///< deviceId
};

@interface DPHostNotificationProfile () {
    NSUInteger NotificationIdLength;
}

/// @brief イベントマネージャ
@property DConnectEventManager *eventMgr;

/// 通知に関する情報を管理するオブジェクト
@property NSMutableDictionary *notificationInfoDict;

/*!
 OnClickイベントメッセージを送信する
 @param notificationId イベントが発生した通知の通知ID
 @param deviceId デバイスID
 */
- (void) sendOnClickEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId;
/*!
 OnShowイベントメッセージを送信する
 @param notificationId イベントが発生した通知の通知ID
 @param deviceId デバイスID
 */
- (void) sendOnShowEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId;
/*!
 OnCloseイベントメッセージを送信する
 @param notificationId イベントが発生した通知の通知ID
 @param deviceId デバイスID
 */
- (void) sendOnCloseEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId;

@end

@implementation DPHostNotificationProfile

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.delegate = self;
        
        // イベントマネージャを取得
        self.eventMgr = [DConnectEventManager sharedManagerForClass:[DPHostDevicePlugin class]];
        
        _notificationInfoDict = @{}.mutableCopy;
        
        NotificationIdLength = 3;
        
        __weak typeof(self) _self = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            [[NSNotificationCenter defaultCenter] addObserver:_self
                                                     selector:@selector(didReceiveLocalNotification:)
                                                         name:@"UIApplicationDidReceiveLocalNotification"
                                                       object:nil];
        });

    }
    return self;
}


-(void)didReceiveLocalNotification:(NSNotification*)notification {
    NSDictionary *userInfo = [notification userInfo];
    if (userInfo) {
        [self sendOnClickEventWithNotificaitonId:userInfo[@"id"] deviceId:userInfo[@"deviceId"]];
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"UIApplicationDidReceiveLocalNotification" object:nil];
}



- (void) sendOnClickEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectNotificationProfileName
                                          attribute:DConnectNotificationProfileAttrOnClick];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        [DConnectNotificationProfile setNotificationId:notificationId target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (void) sendOnShowEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectNotificationProfileName
                                          attribute:DConnectNotificationProfileAttrOnShow];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        [DConnectNotificationProfile setNotificationId:notificationId target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

- (void) sendOnCloseEventWithNotificaitonId:(NSString *)notificationId deviceId:(NSString *)deviceId
{
    // イベントの取得
    NSArray *evts = [_eventMgr eventListForDeviceId:NetworkDiscoveryDeviceId
                                            profile:DConnectNotificationProfileName
                                          attribute:DConnectNotificationProfileAttrOnClose];
    // イベント送信
    for (DConnectEvent *evt in evts) {
        DConnectMessage *eventMsg = [DConnectEventManager createEventMessageWithEvent:evt];
        [DConnectNotificationProfile setNotificationId:notificationId target:eventMsg];
        
        [SELF_PLUGIN sendEvent:eventMsg];
    }
}

#pragma mark - Post Methods

- (BOOL)            profile:(DConnectNotificationProfile *)profile
didReceivePostNotifyRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                       type:(NSNumber *)type
                        dir:(NSString *)dir
                       lang:(NSString *)lang
                       body:(NSString *)body
                        tag:(NSString *)tag
                       icon:(NSData *)icon
{
    if (!type) {
        [response setErrorToInvalidRequestParameterWithMessage:@"type must be specified."];
        return YES;
    } else if (type.integerValue == DConnectNotificationProfileNotificationTypeUnknown) {
        [response setErrorToInvalidRequestParameterWithMessage:@"Unknown type was specified."];
        return YES;
    }
    NSString *notificationId = [DPHostUtils randomStringWithLength:NotificationIdLength];
    do {
        notificationId = [DPHostUtils randomStringWithLength:NotificationIdLength];
    } while (_notificationInfoDict[notificationId]);
    UILocalNotification *notification;
    NSString *status = @"EVENT \n";
    switch ([type intValue]) {
        case DConnectNotificationProfileNotificationTypePhone:
            status = @"PHONE \n";
            break;
        case DConnectNotificationProfileNotificationTypeMail:
            status = @"MAIL \n";
            break;
        case DConnectNotificationProfileNotificationTypeSMS:
            status = @"SMS \n";
            break;
        default:
            [response setError:100 message:@"Not support type"];
            return YES;
    }
    notification = [self scheduleWithAlertBody:[status stringByAppendingString:body] userInfo:@{@"id":notificationId,@"deviceId":deviceId}];
    
    // 通知情報を生成し、notificationInfoDictにて管理
    NSMutableArray *notificationInfo =
    @[type,
      dir  ? dir  : [NSNull null],
      lang ? lang : [NSNull null],
      body ? body : [NSNull null],
      tag  ? tag  : [NSNull null],
      icon ? icon : [NSNull null],
      notification,
      deviceId].mutableCopy;
    _notificationInfoDict[notificationId] = notificationInfo;
    [self sendOnShowEventWithNotificaitonId:notificationId
                                   deviceId:_notificationInfoDict[notificationId][NotificationIndexDeviceId]];
    [response setString:notificationId forKey:DConnectNotificationProfileParamNotificationId];
    [response setResult:DConnectMessageResultTypeOk];

    return YES;
}

#pragma mark - Put Methods
#pragma mark Event Registration

- (BOOL)            profile:(DConnectNotificationProfile *)profile
didReceivePutOnClickRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                 sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

- (BOOL)           profile:(DConnectNotificationProfile *)profile
didReceivePutOnShowRequest:(DConnectRequestMessage *)request
                  response:(DConnectResponseMessage *)response
                  deviceId:(NSString *)deviceId
                sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

- (BOOL)            profile:(DConnectNotificationProfile *)profile
didReceivePutOnCloseRequest:(DConnectRequestMessage *)request
                   response:(DConnectResponseMessage *)response
                   deviceId:(NSString *)deviceId
                 sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr addEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - Delete Methods

- (BOOL)              profile:(DConnectNotificationProfile *)profile
didReceiveDeleteNotifyRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
               notificationId:(NSString *)notificationId
{
    if (!notificationId) {
        [response setErrorToInvalidRequestParameterWithMessage:@"notificationId must be specified."];
        return YES;
    }
    if (!_notificationInfoDict[notificationId]) {
        [response setErrorToInvalidRequestParameterWithMessage:@"Specified notificationId does not exist."];
        return YES;
    }
    

    UILocalNotification *notification =  _notificationInfoDict[notificationId][NotificationIndexNotifiation];
    [[UIApplication sharedApplication] cancelLocalNotification:notification];
    [self sendOnCloseEventWithNotificaitonId:notificationId deviceId:deviceId];
    [_notificationInfoDict removeObjectForKey:notificationId];
    [response setResult:DConnectMessageResultTypeOk];
    
    return YES;
}

#pragma mark Event Unregistration

- (BOOL)               profile:(DConnectNotificationProfile *)profile
didReceiveDeleteOnClickRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response deviceId:(NSString *)deviceId
                    sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

- (BOOL)              profile:(DConnectNotificationProfile *)profile
didReceiveDeleteOnShowRequest:(DConnectRequestMessage *)request
                     response:(DConnectResponseMessage *)response
                     deviceId:(NSString *)deviceId
                   sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

- (BOOL)               profile:(DConnectNotificationProfile *)profile
didReceiveDeleteOnCloseRequest:(DConnectRequestMessage *)request
                      response:(DConnectResponseMessage *)response
                      deviceId:(NSString *)deviceId
                    sessionKey:(NSString *)sessionKey
{
    switch ([_eventMgr removeEventForRequest:request]) {
        case DConnectEventErrorNone:             // エラー無し.
            [response setResult:DConnectMessageResultTypeOk];
            break;
        case DConnectEventErrorInvalidParameter: // 不正なパラメータ.
            [response setErrorToInvalidRequestParameter];
            break;
        case DConnectEventErrorNotFound:         // マッチするイベント無し.
        case DConnectEventErrorFailed:           // 処理失敗.
            [response setErrorToUnknown];
            break;
    }
    
    return YES;
}

#pragma mark - private method


// ローカル通知を作成する
- (UILocalNotification *)scheduleWithAlertBody:(NSString *)alertBody userInfo:(NSDictionary *) userInfo {
    UILocalNotification *notification = [[UILocalNotification alloc] init];
    [notification setUserInfo:userInfo];
    [notification setTimeZone:[NSTimeZone systemTimeZone]];
    [notification setAlertBody:alertBody];
    [notification setUserInfo:userInfo];
    [notification setAlertAction:@"Open"];
    [[UIApplication sharedApplication] scheduleLocalNotification:notification];
    return notification;
}

@end
