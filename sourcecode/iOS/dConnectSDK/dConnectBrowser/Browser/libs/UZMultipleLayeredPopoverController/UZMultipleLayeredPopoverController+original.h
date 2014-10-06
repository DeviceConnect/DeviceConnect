//
//  UZMultipleLayeredPopoverController+original.h
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/04/25.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "UZMultipleLayeredPopoverController.h"

/**
 * @class UZMultipleLayeredPopoverController
 * @author sonson
 * @date 2/1/14
 *
 * @version 1.0
 *
 * @discussion To be written.
 **/
@interface UZMultipleLayeredPopoverController : UIViewController

/**
 * Dismiss UZMultipleLayeredPopoverController object at all.
 **/
- (void)dismiss;

/**
 * Dismiss the top of layered popover controllers on UZMultipleLayeredPopoverController.
 **/
- (void)dismissTopViewController;

/**
 * Displays the popover and anchors it to the specified location in the view.
 *
 * @param viewControllerToPresent The view controller for managing the bottom of popover’s content.
 * @param fromRect The rectangle in view at which to anchor the popover.
 * @param inView The view containing the anchor rectangle for the popover.
 * @param contentSize The new size to apply to the content view.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 * @param passThroughViews An array of views in "inView" argument that the user can interact with while the popover is visible.
 **/
- (void)presentViewController:(UIViewController *)viewControllerToPresent
					 fromRect:(CGRect)fromRect
					   inView:(UIView*)inView
				  contentSize:(CGSize)contentSize
					direction:(UZMultipleLayeredPopoverDirection)direction
			 passThroughViews:(NSArray*)passThroughViews;

/**
 * Returns an initialized UZMultipleLayeredPopoverController object.
 * @param rootViewController The view controller for managing the bottom of popover’s content.
 * @param contentSize The size to apply to the content view.
 * @param passthroughViews An array of views in "inView" argument that the user can interact with while the popover is visible.
 * @return An initialized popover controller object.
 **/
- (id)initWithRootViewController:(UIViewController*)rootViewController contentSize:(CGSize)contentSize passThroughViews:(NSArray*)passthroughViews;

/**
 * Displays the popover's one self on the specified view controller as its child view controller.
 *
 * @param fromRect The rectangle in view at which to anchor the popover.
 * @param inViewController The view controller to which the this popover is added.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 * @param passthroughViews An array of views in "inViewController"'s view that the user can interact with while the popover is visible.
 **/
- (void)presentFromRect:(CGRect)fromRect
	   inViewController:(UIViewController*)inViewController
			  direction:(UZMultipleLayeredPopoverDirection)direction
	   passThroughViews:(NSArray*)passthroughViews;

@end
