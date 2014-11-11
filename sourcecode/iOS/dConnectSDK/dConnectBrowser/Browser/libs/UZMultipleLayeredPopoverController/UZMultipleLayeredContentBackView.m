//
//  UZMultipleLayeredContentBackView.m
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/03/24.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import "UZMultipleLayeredContentBackView.h"

@implementation UZMultipleLayeredContentBackView

/**
 * Ignore the events which are occured outside pass through views in order to send the events to UZMultipleLayeredPopoverController object.
 * passthroughViews object is vacant when this view is attached to the top of UZMultipleLayeredContentViewController objects.
 *
 * @param point A point specified in the receiver’s local coordinate system (bounds).
 * @param event The event that warranted a call to this method. If you are calling this method from outside your event-handling code, you may specify nil.
 * @return The view object that is the farthest descendent the current view and contains point. Returns nil if the point lies completely outside the receiver’s view hierarchy.
 **/
- (UIView *)hitTest:(CGPoint)point withEvent:(UIEvent *)event {
	if (self.active) {
		return [super hitTest:point withEvent:event];
	}
	else {
		// This view is not active.
		for (UIView *passthroughView in self.passThroughViews) {
			CGRect r = [self convertRect:passthroughView.bounds fromView:passthroughView];
			if (CGRectContainsPoint(r, point))
				return [super hitTest:point withEvent:event];
		}
		return nil;
	}
}

@end
