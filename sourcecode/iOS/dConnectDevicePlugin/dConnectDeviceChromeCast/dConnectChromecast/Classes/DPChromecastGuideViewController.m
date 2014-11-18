//
//  DPChromecastGuideViewController.m
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO, INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "DPChromecastGuideViewController.h"
#import "DPChromecastManager.h"

@interface DPChromecastGuideViewController () {
    DPChromecastManager *_manager;
}
@end

@implementation DPChromecastGuideViewController

- (IBAction)rulButtonPressed:(id)sender
{
    NSURL *url = [NSURL URLWithString: @"http://www.google.com/chromecast/setup"];
    [[UIApplication sharedApplication] openURL:url];
}

@end
