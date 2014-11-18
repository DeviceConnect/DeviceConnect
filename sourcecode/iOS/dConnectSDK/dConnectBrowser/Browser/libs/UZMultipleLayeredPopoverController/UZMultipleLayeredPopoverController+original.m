//
//  UZMultipleLayeredPopoverController+original.m
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/04/25.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import "UZMultipleLayeredPopoverController+original.h"

#import "UZMultipleLayeredContentViewController.h"
#import "UZMultipleLayeredPopoverBackView.h"
#import "UZMultipleLayeredContentBackView.h"

#define CGRectGetCenter(p)	CGPointMake(CGRectGetMidX(p), CGRectGetMidY(p))
#define CGSizeGetArea(p) p.width * p.height
#define CGRectFloor(p) CGRectMake(floorf(p.origin.x), floorf(p.origin.y), floorf(p.size.width), floorf(p.size.height))
#define CGSizeFloor(p) CGSizeMake(floorf(p.width), floorf(p.height))

@interface UZMultipleLayeredPopoverController() {
	UIViewController *_inViewController;
	NSMutableArray *_layeredControllers;
	UZMultipleLayeredPopoverBackView *_backView;
}
- (id)initWithRootViewController:(UIViewController*)rootViewController contentSize:(CGSize)contentSize passThroughViews:(NSArray*)passThroughViews;
- (void)presentFromRect:(CGRect)fromRect inViewController:(UIViewController*)inViewController direction:(UZMultipleLayeredPopoverDirection)direction passThroughViews:(NSArray*)passThroughViews;
- (void)presentViewController:(UIViewController *)viewControllerToPresent fromRect:(CGRect)fromRect inView:(UIView*)inView contentSize:(CGSize)contentSize direction:(UZMultipleLayeredPopoverDirection)direction passThroughViews:(NSArray*)passThroughViews;
- (void)dismiss;
- (void)dismissTopViewController;
@end

@implementation UZMultipleLayeredPopoverController

- (void)dealloc {
}

#pragma mark - Override

/**
 * Dismiss the view controllers that place betweet the top and the view controller which is tapped by user.
 *
 * @param touches A set of UITouch instances in the event represented by event that represent the touches in the UITouchPhaseBegan phase.
 * @param event A UIEvent object representing the event to which the touches belong.
 **/
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	UITouch *touch = [touches anyObject];
	CGPoint touchPoint = [touch locationInView:self.view];
	
	UZMultipleLayeredContentViewController *cvc = nil;
	for (UZMultipleLayeredContentViewController *vc in [_layeredControllers reverseObjectEnumerator]) {
		if (CGRectContainsPoint(vc.contentFrame, touchPoint)) {
			cvc = vc;
			break;
		}
	}
	if (cvc)
		[self removeChildViewControllersToPopoverContentViewController:cvc];
	else
		[self dismiss];
}

#pragma mark - Dismiss

/**
 * Dismiss UZMultipleLayeredPopoverController object at all.
 **/
- (void)dismiss {
	[self.view removeFromSuperview];
	for (UIViewController *vc in _layeredControllers) {
		[vc removeFromParentViewController];
		[vc.view removeFromSuperview];
	}
	[_layeredControllers removeAllObjects];
	[self removeFromParentViewController];
	
	// Post notification when UZMultipleLayeredPopoverController is dismissed.
	[[NSNotificationCenter defaultCenter] postNotificationName:UZMultipleLayeredPopoverDidDismissNotification object:self userInfo:@{}];
}

/**
 * Dismiss the top of layered popover controllers on UZMultipleLayeredPopoverController.
 **/
- (void)dismissTopViewController {
	if ([_layeredControllers count] == 1) {
		[self dismiss];
	}
	else {
		UIViewController *vc = [_layeredControllers lastObject];
		[vc removeFromParentViewController];
		[vc.view removeFromSuperview];
		[_layeredControllers removeObject:vc];
		UZMultipleLayeredContentViewController *contentViewController = [_layeredControllers lastObject];
		contentViewController.backView.passThroughViews = nil;
		contentViewController.backView.active = YES;
	}
}

/**
 * Dismiss the layered popovers that are placed between top and sepecified one on UZMultipleLayeredPopoverController.
 * @param contentViewController The view controller to be removed.
 **/
- (void)removeChildViewControllersToPopoverContentViewController:(UZMultipleLayeredContentViewController*)contentViewController {
	for (UIViewController *vc in [_layeredControllers reverseObjectEnumerator]) {
		if (vc == contentViewController)
			break;
		[vc removeFromParentViewController];
		[vc.view removeFromSuperview];
		[_layeredControllers removeObject:vc];
	}
	{
		UZMultipleLayeredContentViewController *contentViewController = [_layeredControllers lastObject];
		contentViewController.backView.passThroughViews = nil;
		contentViewController.backView.active = YES;
	}
}

#pragma mark - Initialize

/**
 * Returns an initialized UZMultipleLayeredPopoverController object.
 * @param rootViewController The view controller for managing the bottom of popover’s content.
 * @param contentSize The size to apply to the content view.
 * @return An initialized popover controller object.
 **/
- (id)initWithRootViewController:(UIViewController*)rootViewController contentSize:(CGSize)contentSize {
	return [self initWithRootViewController:rootViewController contentSize:contentSize passThroughViews:nil];
}

/**
 * Returns an initialized UZMultipleLayeredPopoverController object.
 * @param rootViewController The view controller for managing the bottom of popover’s content.
 * @param contentSize The size to apply to the content view.
 * @param passthroughViews An array of views in "inView" argument that the user can interact with while the popover is visible.
 * @return An initialized popover controller object.
 **/
- (id)initWithRootViewController:(UIViewController*)rootViewController contentSize:(CGSize)contentSize passThroughViews:(NSArray*)passthroughViews {
	if ([rootViewController isKindOfClass:[UZMultipleLayeredPopoverController class]]) {
		NSLog(@"You can not set a UZMultipleLayeredPopoverController object as the view controller on UZMultipleLayeredPopoverController objects.");
		return nil;
	}
	if ([rootViewController isKindOfClass:[UZMultipleLayeredContentViewController class]]) {
		NSLog(@"You can not set a UZMultipleLayeredContentViewController object as the view controller on UZMultipleLayeredPopoverController objects.");
		return nil;
	}
	if (!rootViewController) {
		NSLog(@"You have to set any object as the view controller on UZMultipleLayeredPopoverController objects.");
		return nil;
	}
	self = [super init];
	if (self) {
		_backView = [[UZMultipleLayeredPopoverBackView alloc] initWithFrame:CGRectZero];
		_backView.passThroughViews = passthroughViews;
		self.view = _backView;
		_layeredControllers = [NSMutableArray array];
		UZMultipleLayeredContentViewController *vc = [[UZMultipleLayeredContentViewController alloc] initWithContentViewController:rootViewController contentSize:contentSize];
		vc.backView.active = YES;
		[_layeredControllers addObject:vc];
		
		self.view.backgroundColor = [UIColor clearColor];
	}
	return self;
}

#pragma mark - Present

/**
 * Calculate popover rect according to the some parameters in order to adjust the offset betweeen popover and specified rectangle area.
 * Results are copied into p1 and p2 and p3 arguments.
 *
 * @param specifiedContentSize The size to apply to the content view.
 * @param fromRectInPopover The rectangle in view at which to anchor the popover.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 * @param p1 Upon return, contains the rectangle area in which the popover located.
 * @param p2 Upon return, contains the intrinsic size of view controller in the popover.
 * @param p3 Upon return, contains the offset between popover and specified rectangle area.
 **/
- (void)getPopoverRectWithsSecifiedContentSize:(CGSize)specifiedContentSize
							 fromRectInPopover:(CGRect)fromRectInPopover
									 direction:(UZMultipleLayeredPopoverDirection)direction
								   popoverRect:(CGRect*)p1
								   contentSize:(CGSize*)p2
										offset:(CGFloat*)p3 {
	CGFloat popoverOffset = 0;
	CGRect popoverRect = CGRectZero;
	popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(specifiedContentSize);
	CGSize contentSize = specifiedContentSize;
	
	// On the assumption that popover covers inViewController's view.
	if (direction == UZMultipleLayeredPopoverTopDirection) {
		popoverRect.origin.x = CGRectGetMidX(fromRectInPopover) - popoverRect.size.width/2;
		popoverRect.origin.y = fromRectInPopover.origin.y + fromRectInPopover.size.height - UZMultipleLayeredPopoverArrowSize;
	}
	else if (direction == UZMultipleLayeredPopoverBottomDirection) {
		popoverRect.origin.x = CGRectGetMidX(fromRectInPopover) - popoverRect.size.width/2;
		popoverRect.origin.y = fromRectInPopover.origin.y - popoverRect.size.height + UZMultipleLayeredPopoverArrowSize;
	}
	else if (direction == UZMultipleLayeredPopoverRightDirection) {
		popoverRect.origin.x = fromRectInPopover.origin.x + fromRectInPopover.size.width  - UZMultipleLayeredPopoverArrowSize;
		popoverRect.origin.y = CGRectGetMidY(fromRectInPopover) - popoverRect.size.height/2;
	}
	else if (direction == UZMultipleLayeredPopoverLeftDirection) {
		popoverRect.origin.x = fromRectInPopover.origin.x - popoverRect.size.width + UZMultipleLayeredPopoverArrowSize;
		popoverRect.origin.y = CGRectGetMidY(fromRectInPopover) - popoverRect.size.height/2;
	}
	
	// Adjust the position of baloon's arrow
	if (direction == UZMultipleLayeredPopoverTopDirection || direction == UZMultipleLayeredPopoverBottomDirection) {
		if (popoverRect.origin.x < 0) {
			popoverOffset = - (CGRectGetMidX(fromRectInPopover) - popoverRect.size.width/2);
			popoverRect.origin.x = 0;
			
			// Adjust width when right edge goes over the parent view.
			if (popoverRect.origin.x + popoverRect.size.width > self.view.frame.size.width) {
				contentSize.width -= fabsf(popoverRect.origin.x + popoverRect.size.width - self.view.frame.size.width);
				popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
				popoverOffset = - (CGRectGetMidX(fromRectInPopover) - popoverRect.size.width/2);
			}
		}
		else if (popoverRect.origin.x + popoverRect.size.width > self.view.frame.size.width) {
			popoverOffset = (self.view.frame.size.width - (CGRectGetMidX(fromRectInPopover) + popoverRect.size.width/2));
			popoverRect.origin.x = (self.view.frame.size.width - popoverRect.size.width);
			
			// Adjust width when right edge goes over the parent view.
			if (popoverRect.origin.x < 0) {
				contentSize.width -= fabsf(popoverRect.origin.x);
				popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
				popoverOffset = - (CGRectGetMidX(fromRectInPopover) - popoverRect.size.width/2);
				popoverRect.origin.x = 0;
			}
		}
	}
	else if (direction == UZMultipleLayeredPopoverRightDirection || direction == UZMultipleLayeredPopoverLeftDirection) {
		if (popoverRect.origin.y < 0) {
			popoverOffset = - (CGRectGetMidY(fromRectInPopover) - popoverRect.size.height/2);
			popoverRect.origin.y = 0;
			
			// Adjust width when top edge goes over the parent view.
			if (popoverRect.origin.y + popoverRect.size.height > self.view.frame.size.height) {
				contentSize.height -= fabsf(popoverRect.origin.y + popoverRect.size.height - self.view.frame.size.height);
				popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
				popoverOffset = - (CGRectGetMidY(fromRectInPopover) - popoverRect.size.height/2);
			}
		}
		else if (popoverRect.origin.y + popoverRect.size.height > self.view.frame.size.height) {
			popoverOffset = (self.view.frame.size.height - (CGRectGetMidY(fromRectInPopover) + popoverRect.size.height/2));
			popoverRect.origin.y = (self.view.frame.size.height - popoverRect.size.height);
			
			// Adjust width when right edge goes over the parent view.
			if (popoverRect.origin.y < 0) {
				contentSize.height -= fabsf(popoverRect.origin.y);
				popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
				popoverOffset = - (CGRectGetMidY(fromRectInPopover) - popoverRect.size.height/2);
				popoverRect.origin.y = 0;
			}
		}
	}
	
	if (direction == UZMultipleLayeredPopoverTopDirection) {
		// Adjust height when bottom edge goes over the parent view.
		if (popoverRect.origin.y + popoverRect.size.height > self.view.frame.size.height) {
			contentSize.height -= fabsf(popoverRect.origin.y + popoverRect.size.height - self.view.frame.size.height);
			popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
		}
	}
	else if (direction == UZMultipleLayeredPopoverBottomDirection) {
		// Adjust height when bottom edge goes over the parent view.
		if (popoverRect.origin.y < 0) {
			contentSize.height -= fabsf(popoverRect.origin.y);
			popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
			popoverRect.origin.y = 0;
		}
	}
	else if (direction == UZMultipleLayeredPopoverRightDirection) {
		// Adjust height when bottom edge goes over the parent view.
		if (popoverRect.origin.x + popoverRect.size.width > self.view.frame.size.width) {
			contentSize.width -= fabsf(popoverRect.origin.x + popoverRect.size.width - self.view.frame.size.width);
			popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
		}
	}
	else if (direction == UZMultipleLayeredPopoverLeftDirection) {
		// Adjust height when bottom edge goes over the parent view.
		if (popoverRect.origin.x < 0) {
			contentSize.width -= fabsf(popoverRect.origin.x);
			popoverRect.size = UZMultipleLayeredPopoverSizeFromContentSize(contentSize);
			popoverRect.origin.x = 0;
		}
	}
	*p1 = CGRectFloor(popoverRect);
	*p2 = CGSizeFloor(contentSize);
	*p3 = floorf(popoverOffset);
}

/**
 * Updates the location of the last view controller which is stacked in _layeredControllers buffer.
 *
 * @param fromRect The size to apply to the content view.
 * @param inView The rectangle in view at which to anchor the popover.
 * @param direction The arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle.
 **/
- (void)updateLastLayeredViewControllerFromRect:(CGRect)fromRect
										 inView:(UIView*)inView
									  direction:(UZMultipleLayeredPopoverDirection)direction {
	UZMultipleLayeredContentViewController *contentViewController = [_layeredControllers lastObject];
	CGRect fromRectInPopover = [self.view convertRect:fromRect fromView:inView];
	CGSize contentSize = CGSizeZero;
	CGRect popoverRect = CGRectZero;
	CGFloat popoverArrowOffset = 0;
	
	if (!(direction & UZMultipleLayeredPopoverAnyDirection))
		direction = UZMultipleLayeredPopoverAnyDirection;
	
	if (direction != UZMultipleLayeredPopoverAnyDirection && direction != UZMultipleLayeredPopoverVerticalDirection && direction != UZMultipleLayeredPopoverHorizontalDirection) {
		[self getPopoverRectWithsSecifiedContentSize:contentViewController.contentSize
								   fromRectInPopover:fromRectInPopover
										   direction:direction
										 popoverRect:&popoverRect
										 contentSize:&contentSize
											  offset:&popoverArrowOffset];
		contentViewController.direction = direction;
		contentViewController.contentSize = contentSize;
		contentViewController.baseView.popoverArrowOffset = popoverArrowOffset;
	}
	else {
		// calculate the frames for all directions
		// select the direction whose frame is the largest square in them.
		CGRect popoverRects[4];
		CGSize contentSizes[4];
		CGFloat popoverArrowOffsets[4];
		UZMultipleLayeredPopoverDirection directions[] = {
			UZMultipleLayeredPopoverBottomDirection,
			UZMultipleLayeredPopoverTopDirection,
			UZMultipleLayeredPopoverRightDirection,
			UZMultipleLayeredPopoverLeftDirection
		};
		for (NSInteger i = 0; i < 4; i++) {
			if (directions[i] & direction) {
				[self getPopoverRectWithsSecifiedContentSize:contentViewController.contentSize
										   fromRectInPopover:fromRectInPopover
												   direction:directions[i]
												 popoverRect:&popoverRects[i]
												 contentSize:&contentSizes[i]
													  offset:&popoverArrowOffsets[i]];
			}
			else {
				popoverArrowOffsets[i] = 0;
				popoverRects[i] = CGRectZero;
				contentSizes[i] = CGSizeZero;
			}
		}
		NSInteger saved = 0;
		CGFloat maxArea = CGSizeGetArea(contentSizes[0]);
		for (NSInteger i = 1; i < 4; i++) {
			CGFloat area = CGSizeGetArea(contentSizes[i]);
			if (maxArea < area) {
				maxArea = area;
				saved = i;
			}
		}
		contentViewController.direction = directions[saved];
		contentViewController.contentSize = contentSizes[saved];
		contentViewController.baseView.popoverArrowOffset = popoverArrowOffsets[saved];
		popoverRect = popoverRects[saved];
	}
	
	for (UZMultipleLayeredContentViewController *vc in _layeredControllers) {
		vc.backView.active = NO;
	}
	
	// add the new top level view controller
	[self addChildViewController:contentViewController];
	[self.view addSubview:contentViewController.view];
	contentViewController.backView.active = YES;
	contentViewController.view.frame = popoverRect;
	[contentViewController updateSubviews];
}

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
			 passThroughViews:(NSArray*)passThroughViews {
	UZMultipleLayeredContentViewController *viewController = [[UZMultipleLayeredContentViewController alloc] initWithContentViewController:viewControllerToPresent contentSize:contentSize];
	UZMultipleLayeredContentViewController *previousViewController = [_layeredControllers lastObject];
	if (previousViewController) {
		previousViewController.backView.passThroughViews = passThroughViews;
		[previousViewController.backView setNeedsDisplay];
	}
	[_layeredControllers addObject:viewController];
	[self updateLastLayeredViewControllerFromRect:fromRect inView:inView direction:direction];
}

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
	   passThroughViews:(NSArray*)passthroughViews {
	_inViewController = inViewController;
	[_inViewController addChildViewController:self];
	self.view.frame = _inViewController.view.bounds;
	[_inViewController.view addSubview:self.view];
	
	[self updateLastLayeredViewControllerFromRect:fromRect inView:inViewController.view direction:direction];
}

@end