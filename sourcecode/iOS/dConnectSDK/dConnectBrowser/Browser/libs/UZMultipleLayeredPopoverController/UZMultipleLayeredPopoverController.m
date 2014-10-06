//
//  UZMultipleLayeredPopoverController.m
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/01/02.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import "UZMultipleLayeredPopoverController.h"

#import "UZMultipleLayeredPopoverController+original.h"

#import <QuartzCore/QuartzCore.h>

NSString *const UZMultipleLayeredPopoverDidDismissNotification = @"UZMultipleLayeredPopoverDidDismissNotification";

@implementation UIViewController (UZMultipleLayeredPopoverController)

/**
 * Returns the bottom of view controller's hierarchy parsing each view controllers' parents.
 * Typically, this method returns the object is as same as one UIWindow's keyWindow's rootViewController method returns.
 * @return UIViewController object which is the bottom of view controller's hierarchy.
 **/
- (UIViewController*)rootViewController {
	UIViewController *current = self;
	while (1) {
		if (current.parentViewController == nil)
			return current;
		current = current.parentViewController;
	}
	return nil;
}

/**
 * Returns the view controller object which has to be attached a new view controller as popover.
 * If UZMultipleLayeredPopoverController object is not attached, typically returns the object is as same as one UIWindow's keyWindow's rootViewController method.
 * If more than one view controllers are presented, returns UZMultipleLayeredPopoverController object.
 * @return UIViewController object which is the bottom of view controller's hierarchy.
 **/
- (UIViewController*)targetViewController {
	UIViewController *rootViewController = [self rootViewController];
	for (id vc in [rootViewController childViewControllers]) {
		if ([vc isKindOfClass:[UZMultipleLayeredPopoverController class]])
			return (UZMultipleLayeredPopoverController*)vc;
	}
	return rootViewController;
}

/**
 * Dismiss view controller on the top of popover controllers on UZMultipleLayeredPopoverController object.
 **/
- (void)dismissCurrentPopoverController {
	UIViewController *con = [self targetViewController];
	if ([con isKindOfClass:[UZMultipleLayeredPopoverController class]])
		[(UZMultipleLayeredPopoverController*)con dismissTopViewController];
}

/**
 * Dismiss all popover controllers on UZMultipleLayeredPopoverController object.
 **/
- (void)dismissMultipleLayeredPopoverController {
	UIViewController *con = [self targetViewController];
	if ([con isKindOfClass:[UZMultipleLayeredPopoverController class]])
		[(UZMultipleLayeredPopoverController*)con dismiss];
}

/**
 * Present the specified view controller as popover.
 * The popover is always displayed on the unique UZMultipleLayeredPopoverController in the application.
 *
 * @param viewController The view controller for managing the popover’s content.
 * @param contentSize The new size to apply to the content view.
 * @param fromRect The rectangle in view at which to anchor the popover.
 * @param inView The view containing the anchor rectangle for the popover.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 **/
- (void)presentMultipleLayeredPopoverWithViewController:(UIViewController*)viewController contentSize:(CGSize)contentSize fromRect:(CGRect)fromRect inView:(UIView*)inView direction:(UZMultipleLayeredPopoverDirection)direction {
	[self presentMultipleLayeredPopoverWithViewController:viewController contentSize:contentSize fromRect:fromRect inView:inView direction:direction passThroughViews:nil];
}

/**
 * Present the specified view controller as popover.
 * The popover is always displayed on the unique UZMultipleLayeredPopoverController in the application.
 *
 * @param viewController The view controller for managing the popover’s content.
 * @param contentSize The new size to apply to the content view.
 * @param fromRect The rectangle in view at which to anchor the popover.
 * @param inView The view containing the anchor rectangle for the popover.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 * @param passthroughViews An array of views in "inView" argument that the user can interact with while the popover is visible.
 **/
- (void)presentMultipleLayeredPopoverWithViewController:(UIViewController*)viewController contentSize:(CGSize)contentSize fromRect:(CGRect)fromRect inView:(UIView*)inView direction:(UZMultipleLayeredPopoverDirection)direction passThroughViews:(NSArray*)passthroughViews {
	UIViewController *con = [self targetViewController];
	CGRect frame = [con.view convertRect:fromRect fromView:inView];
	if ([con isKindOfClass:[UZMultipleLayeredPopoverController class]]) {
		[(UZMultipleLayeredPopoverController*)con presentViewController:viewController fromRect:frame inView:con.view contentSize:contentSize direction:direction passThroughViews:passthroughViews];
	}
	else {
		UZMultipleLayeredPopoverController *popoverController = [[UZMultipleLayeredPopoverController alloc] initWithRootViewController:viewController contentSize:contentSize passThroughViews:passthroughViews];
		[popoverController presentFromRect:frame inViewController:con direction:direction passThroughViews:passthroughViews];
	}
}

@end

