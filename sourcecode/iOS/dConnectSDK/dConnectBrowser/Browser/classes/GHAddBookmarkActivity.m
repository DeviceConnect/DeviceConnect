//
//  GHAddBookmarkActivity.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHAddBookmarkActivity.h"

@implementation GHAddBookmarkActivity

- (NSString *)activityType
{
	return NSStringFromClass([self class]);
}

- (UIImage *)activityImage
{
	return [UIImage imageNamed:@"addBookmark"];
}

- (NSString *)activityTitle
{
	return NSLocalizedString(@"ブックマーク", @"Add Bookmark");
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
    [GHUtils postNotification:nil withKey:ADD_BOOKMARK];
    [self activityDidFinish:completed];
}


@end
