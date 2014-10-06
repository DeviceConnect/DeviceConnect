//
//  RequestViewController.h
//  dConnectSDKSample
//
//  Created by 安部 将史 on 2014/08/27.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <DConnectSDK/DConnectSDK.h>

@interface RequestViewController : UIViewController

@property (nonatomic, copy) DConnectMessage *service;

@end
