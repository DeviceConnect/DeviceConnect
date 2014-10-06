//
//  GHPrintActivity.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHPrintActivity.h"

@implementation GHPrintActivity

- (NSString *)activityType
{
	return NSStringFromClass([self class]);
}

- (UIImage *)activityImage
{
	return [UIImage imageNamed:@"print"];
}

- (NSString *)activityTitle
{
	return NSLocalizedString(@"プリント", @"Add Bookmark");
}

- (BOOL)canPerformWithActivityItems:(NSArray *)activityItems
{
	return YES;
}

- (void)prepareWithActivityItems:(NSArray *)activityItems
{
    
}

- (void)performActivity
{
    bool completed = NO;
    [GHUtils postNotification:nil withKey:SHOW_PRINT];
    [self activityDidFinish:completed];
}



@end
