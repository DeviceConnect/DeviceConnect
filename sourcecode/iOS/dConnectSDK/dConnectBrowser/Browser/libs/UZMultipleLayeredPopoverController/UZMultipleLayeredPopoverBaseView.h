//
//  UZMultipleLayeredPopoverBaseView.h
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/01/02.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "UZMultipleLayeredPopoverBaseView.h"
#import "UZMultipleLayeredPopoverController.h"

#define UZMultipleLayeredPopoverCornerRadious	10
#define UZMultipleLayeredPopoverContentMargin	20
#define UZMultipleLayeredPopoverArrowSize		10

/**
 * @class UZMultipleLayeredPopoverBaseView
 * @author sonson
 * @date 2/1/14
 *
 * @version 1.0
 *
 * @discussion This class is designed to draw the background image for popover.
 **/
@interface UZMultipleLayeredPopoverBaseView : UIView

@property (nonatomic, assign) UZMultipleLayeredPopoverDirection direction;		/**< The direction of the popover’s arrow. The new arrow directions the popover is permitted to use. You can use this value to force the popover to be positioned on a specific side of the rectangle. */
@property (nonatomic, assign) CGFloat popoverArrowOffset;							/**< The vertical or horizontal offset between the center of rectangle and the position to be rendered an arrow. */

@end
