//
//  DPHueSystemProfile.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPHueSystemProfile.h"

@implementation DPHueSystemProfile
- (id)init
{
    self = [super init];
    if (self) {
        self.dataSource = self;
        self.delegate = self;
    }
    return self;
}

- (NSString *) versionOfSystemProfile:(DConnectSystemProfile *)profile
{
    return @"1.0";
}

- (UIViewController *) profile:(DConnectSystemProfile *)sender
         settingPageForRequest:(DConnectRequestMessage *)request
{
    
    NSString *bundlePath = [[NSBundle mainBundle] pathForResource:@"dConnectDeviceHue_resources" ofType:@"bundle"];
    NSBundle *bundle = [NSBundle bundleWithPath:bundlePath];
    
    UIStoryboard *sb;
    if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone) {
        sb = [UIStoryboard storyboardWithName:@"HueSetting_iPhone" bundle:bundle];
    } else{
        sb = [UIStoryboard storyboardWithName:@"HueSetting_iPad" bundle:bundle];
    }
    UINavigationController *nc = [sb instantiateInitialViewController];
    
    return nc;

}

@end
