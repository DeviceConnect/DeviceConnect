//
//  DConnectEvent.h
//  DConnectSDK
//
//  Created by 安部 将史 on 2014/07/07.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <Foundation/Foundation.h>


#pragma mark - Constants

/*!
 * @brief Eventのエラー定義.
 */
typedef NS_ENUM(NSUInteger, DConnectEventError) {
    DConnectEventErrorNone,                 /*!< エラー無し. */
    DConnectEventErrorInvalidParameter,		/*!< 不正なパラメータ. */
    DConnectEventErrorNotFound,       		/*!< マッチするイベント無し. */
    DConnectEventErrorFailed,            	/*!< 処理失敗. */
};

#pragma mark - DConnectEvent
@interface DConnectEvent : NSObject<NSCoding, NSSecureCoding>

@property (nonatomic, strong) NSString *profile;
@property (nonatomic, strong) NSString *interface;
@property (nonatomic, strong) NSString *attribute;
@property (nonatomic, strong) NSString *deviceId;
@property (nonatomic, strong) NSString *accessToken;
@property (nonatomic, strong) NSString *sessionKey;
@property (nonatomic, strong) NSDate *createDate;
@property (nonatomic, strong) NSDate *updateDate;

@end
