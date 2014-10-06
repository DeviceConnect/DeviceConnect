//
//  UZMultipleLayeredContentBackView.h
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/03/24.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 * @class UZMultipleLayeredContentBackView
 * @author sonson
 * @date 24/3/14
 *
 * @version 1.0
 *
 * @discussion This class is designed for ignoring the events which are occured outside pass through views in order to send the events to UZMultipleLayeredPopoverController object.
 * passthroughViews object is vacant when this view is attached to the top of UZMultipleLayeredContentViewController objects.
 **/
@interface UZMultipleLayeredContentBackView : UIView
@property (nonatomic, copy) NSArray *passThroughViews;
@property (nonatomic, assign) BOOL active;
@end
