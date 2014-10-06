//
//  UZMultipleLayeredContentViewController.h
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/01/02.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "UZMultipleLayeredPopoverBaseView.h"

@class UZMultipleLayeredPopoverController;
@class UZMultipleLayeredPopoverBaseView;
@class UZMultipleLayeredContentBackView;

CGSize UZMultipleLayeredPopoverSizeFromContentSize(CGSize contentSize);

/**
 * @class UZMultipleLayeredContentViewController
 * @author sonson
 * @date 2/1/14
 *
 * @version 1.0
 *
 * @discussion To be written.
 **/
@interface UZMultipleLayeredContentViewController : UIViewController {
	UZMultipleLayeredPopoverController	*_parentPopoverController;
	UZMultipleLayeredPopoverBaseView	*_baseView;
	UIViewController					*_contentViewController;
	CGSize								_contentSize;
	CGSize								_popoverSize;
	UZMultipleLayeredPopoverDirection	_direction;
}

@property (nonatomic, readonly) UZMultipleLayeredPopoverBaseView *baseView;
@property (nonatomic, readonly) UZMultipleLayeredContentBackView *backView;
@property (nonatomic, readonly) CGSize popoverSize;

@property (nonatomic, assign) CGSize contentSize;								/**< The size of the content view. The size of the view controller whose content should be displayed by the popover. */
@property (nonatomic, assign) UZMultipleLayeredPopoverDirection direction;		/**< The direction of the popover’s arrow. The new arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle. */

+ (UIEdgeInsets)contentEdgeInsets;
- (id)initWithContentViewController:(UIViewController*)contentViewController contentSize:(CGSize)contentSize;
- (void)updateSubviews;
- (CGRect)contentFrame;

@end
