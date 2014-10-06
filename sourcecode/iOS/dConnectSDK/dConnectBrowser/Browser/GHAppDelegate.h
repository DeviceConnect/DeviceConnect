//
//  GHAppDelegate.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <UIKit/UIKit.h>

@interface GHAppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property NSURL *redirectURL;
@property (copy) void(^URLLoadingCallback)(NSURL *);

@end
