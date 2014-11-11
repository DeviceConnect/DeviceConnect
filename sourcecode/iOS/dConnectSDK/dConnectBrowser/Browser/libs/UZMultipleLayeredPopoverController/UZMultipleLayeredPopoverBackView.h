//
//  UZMultipleLayeredPopoverBackView.h
//  UZMultipleLayeredPopoverController
//
//  Created by sonson on 2014/03/23.
//  Copyright (c) 2014年 sonson. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
 * @class UZMultipleLayeredPopoverBackView
 * @author sonson
 * @date 23/3/14
 *
 * @version 1.0
 *
 * @discussion This class is designed to ignore the events which are occured in pass through views in order to dismiss UZMultipleLayeredPopoverController object.
 **/
@interface UZMultipleLayeredPopoverBackView : UIView
@property (nonatomic, copy) NSArray *passThroughViews;
@end
