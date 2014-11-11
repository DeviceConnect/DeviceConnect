//
//  DPChromecastGuideViewController.m
//  dConnectChromecast
//
//  Created by Takashi Tsuchiya on 2014/09/19.
//  Copyright (c) 2014年 NTT DOCOMO, INC. All rights reserved.
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
