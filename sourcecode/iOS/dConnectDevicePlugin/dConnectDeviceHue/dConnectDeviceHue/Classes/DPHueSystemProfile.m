//
//  DicePlusSystemProfile.m
//  dConnectDeviceDicePlus
//
//  Created by 星貴之 on 2014/07/01.
//  Copyright (c) 2014年 Docomo. All rights reserved.
//

#import "DPHueSystemProfile.h"

@implementation DPHueSystemProfile
- (id)init {
    self = [super init];
    if (self) {
        self.dataSource = self;
        self.delegate = self;
    }
    return self;
}

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile {
    return @"1.0";
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender settingPageForRequest:(DConnectRequestMessage *)request {
    
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceHue_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    
    // iphoneとipadでストーリーボードを切り替える
    UIStoryboard *sb;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        sb = [UIStoryboard storyboardWithName:@"HueSetting_iPhone" bundle:bundle];
    } else{
        sb = [UIStoryboard storyboardWithName:@"HueSetting_iPad" bundle:bundle];
    }

    //戻り値を取得
    UINavigationController *nc = [sb instantiateInitialViewController];
    
    return nc;

}

@end
