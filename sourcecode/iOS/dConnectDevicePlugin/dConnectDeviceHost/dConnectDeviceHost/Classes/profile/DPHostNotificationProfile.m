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
    NotificationIndexAlertView, ///< UIAlertView
    NotificationIndexDeviceId,  ///< deviceId
};

@interface DPHostNotificationProfile () <UIAlertViewDelegate> {
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
/*!
 指定されたUIAlertViewに対応する通知IDを返す。
 @param[in] alertView UIAlertView
 @return <code>alertView</code>の通知ID
 */
- (NSString *) notificationIdForUIAlertView:(UIAlertView *)alertView;

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
    }
    return self;
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

- (NSString *) notificationIdForUIAlertView:(UIAlertView *)alertView
{
    for (NSString *notificationId in _notificationInfoDict) {
        if (_notificationInfoDict[notificationId][NotificationIndexAlertView] == alertView) {
            return notificationId;
        }
    }
    return nil;
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
    
    // 通知情報を生成し、notificationInfoDictにて管理
    UIAlertView *alertView = [UIAlertView new];
    NSMutableArray *notificationInfo =
    @[type,
      dir  ? dir  : [NSNull null],
      lang ? lang : [NSNull null],
      body ? body : [NSNull null],
      tag  ? tag  : [NSNull null],
      icon ? icon : [NSNull null],
      alertView,
      deviceId].mutableCopy;
    
    NSString *notificationId = [DPHostUtils randomStringWithLength:NotificationIdLength];
    do {
        notificationId = [DPHostUtils randomStringWithLength:NotificationIdLength];
    } while (_notificationInfoDict[notificationId]);
    _notificationInfoDict[notificationId] = notificationInfo;
    
    alertView.delegate = self;
    alertView.title = @"Notification";
    alertView.message = body;
    [alertView addButtonWithTitle:@"OK"];
    dispatch_async(dispatch_get_main_queue(), ^{
        [alertView show];
    });
    
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
    
    // UIAlertViewが表示されている場合は閉じる。
    UIAlertView *alertView = _notificationInfoDict[notificationId][NotificationIndexAlertView];
    if (alertView.visible) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [alertView dismissWithClickedButtonIndex:-1 animated:YES];
        });
    }
//    [_notificationInfoDict removeObjectForKey:notificationId];
    
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

#pragma mark - UIAlertViewDelegate

// Responding to Actions

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    // OnClickイベント送信
    NSString *notificationId = [self notificationIdForUIAlertView:alertView];
    if (notificationId) {
        [self sendOnClickEventWithNotificaitonId:notificationId
                                        deviceId:_notificationInfoDict[notificationId][NotificationIndexDeviceId]];
    }
    
    // ボタンの如何に関わらず閉じる。
    dispatch_async(dispatch_get_main_queue(), ^{
        [alertView dismissWithClickedButtonIndex:-1 animated:YES];
    });
}

// Customizing Behavior

//- (BOOL)alertViewShouldEnableFirstOtherButton:(UIAlertView *)alertView
//{
//
//}

//– willPresentAlertView:

- (void)didPresentAlertView:(UIAlertView *)alertView
{
    // OnShowイベント送信
    NSString *notificationId = [self notificationIdForUIAlertView:alertView];
    if (notificationId) {
        [self sendOnShowEventWithNotificaitonId:notificationId
                                       deviceId:_notificationInfoDict[notificationId][NotificationIndexDeviceId]];
    }
}

//– alertView:willDismissWithButtonIndex:

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex
{
    // OnCloseイベント送信
    NSString *notificationId = [self notificationIdForUIAlertView:alertView];
    if (notificationId) {
        [self sendOnCloseEventWithNotificaitonId:notificationId
                                        deviceId:_notificationInfoDict[notificationId][NotificationIndexDeviceId]];
        
        // 管理している通知情報から閉じられた通知のエントリを削除
        [_notificationInfoDict removeObjectForKey:notificationId];
    }
}

// Canceling

- (void)alertViewCancel:(UIAlertView *)alertView
{
    NSString *notificationId = [self notificationIdForUIAlertView:alertView];
    if (notificationId) {
        [self sendOnCloseEventWithNotificaitonId:notificationId
                                        deviceId:_notificationInfoDict[notificationId][NotificationIndexDeviceId]];
        
        // 管理している通知情報から閉じられた通知のエントリを削除
        [_notificationInfoDict removeObjectForKey:notificationId];
    }
}

@end
