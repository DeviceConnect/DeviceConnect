//
//  HueProfile.h
//  dConnectDeviceHue
//
//  Created by 星　貴之 on 2014/07/08.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <DCMDevicePluginSDK/DCMLightProfile.h>

enum {
    HueStatusUpdate,
    HueCreateGroup,
    HueClearGroup
};


typedef enum BridgeConnectState : NSInteger {

    STATE_INIT,
    STATE_CONNECT,
    STATE_NON_CONNECT,
    STATE_NOT_AUTHENTICATED
    
} BridgeConnectState;

@class PHHueSDK;

@interface DPHueLightProfile : DCMLightProfile<DCMLightProfileDelegate>


@end
