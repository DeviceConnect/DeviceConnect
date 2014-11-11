//
//  DPIRKitManager.h
//  dConnectDeviceIRKit
//
//  Created by 安部 将史 on 2014/08/19.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>

@class DPIRKitDevice;
@class DPIRKitManager;

typedef NS_ENUM(NSUInteger, DPIRKitWiFiSecurityType) {
    DPIRKitWiFiSecurityTypeNone = 0,
    DPIRKitWiFiSecurityTypeWEP = 2,
    DPIRKitWiFiSecurityTypeWPA2 = 8,
};

typedef NS_ENUM(NSUInteger, DPIRKitConnectionErrorCode) {
    DPIRKitConnectionErrorCodeNone = 0,
    DPIRKitConnectionErrorCodeNotIRKitDevice,
    DPIRKitConnectionErrorCodeFailed,
    DPIRKitConnectionErrorCodeServerNotReachable,
    DPIRKitConnectionErrorCodeDeviceNotReachable,
};

/**
 デバイスの検出、消失の通知を受けるデリゲート。
 */
@protocol DPIRKitManagerDetectionDelegate <NSObject>

- (void) manager:(DPIRKitManager *)manager didFindDevice:(DPIRKitDevice *)device;
- (void) manager:(DPIRKitManager *)manager didLoseDevice:(DPIRKitDevice *)device;

@end

/**
 Managerは基本的にスレッドセーフではないので呼び出しもとで管理すること。
 */
@interface DPIRKitManager : NSObject

@property (nonatomic, copy) NSString *apiKey;
@property (nonatomic, weak) id<DPIRKitManagerDetectionDelegate> detectionDelegate;

#pragma mark - Instance Methods

@property (nonatomic, readonly) BOOL isDetecting;

/**
 検知の開始、停止。
 */
- (void) startDetection;
- (void) stopDetection;

- (void) fetchClientKeyWithCompletion:(void (^)(NSString *clientKey, DPIRKitConnectionErrorCode errorCode))completion;

- (void) createNewDeviceWithClientKey:(NSString *)clientKey
                           completion:(void (^)(NSString *deviceId, NSString *deviceKey,
                                                DPIRKitConnectionErrorCode errorCode))completion;

- (void) checkIfCurrentSSIDIsIRKitWithCompletion:(void (^)(BOOL isIRKit, NSError *error)) callback;

- (void) connectIRKitToWiFiWithSSID:(NSString *)ssid
                           password:(NSString *)password
                       securityType:(DPIRKitWiFiSecurityType)type
                          deviceKey:(NSString *)deviceKey
                         completion:(void (^)(BOOL success, DPIRKitConnectionErrorCode errorCode))completion;

/**
 Get /1/messagesを利用してネットにIRKitがつながっているかをチェックする。
 */
- (void) checkIfIRKitIsConnectedToInternetWithClientKey:(NSString *)clientKey
                                            completion:(void (^)(BOOL isConnected))completion;

/**
 WiFi認証が完了し、IRKitがインターネットにつながったかをチェックする。
 */
- (void) checkIfIRKitIsConnectedToInternetWithClientKey:(NSString *)clientKey
                                               deviceId:(NSString *)deviceId
                                             completion:(void (^)(BOOL isConnected))completion;

- (void) fetchDeviceInfoWithDeviceHost:(NSString *)host
                        withCompletion:(void (^)(NSString *deviceId, NSString *clientKey))completion;

/**
 赤外線データを取得する。
 処理は非同期で行われ、結果はブロックに渡される。
 データがとれない場合はブロックにnilが渡される。
 */
- (void) fetchMessageWithHostName:(NSString *)hostName completion:(void (^)(NSString *message))completion;

- (void) sendMessage:(NSString *)message
        withHostName:(NSString *)hostName
          completion:(void (^)(BOOL success))completion;

#pragma mark - Static Methods

+ (DPIRKitManager *) sharedInstance;

@end
