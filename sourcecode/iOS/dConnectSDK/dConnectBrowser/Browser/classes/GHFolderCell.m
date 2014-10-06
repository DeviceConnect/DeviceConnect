//
//  GHFolderCell.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHFolderCell.h"

@implementation GHFolderCell

- (void)layoutSubviews
{
    // Call super
    [super layoutSubviews];
    
    // Update the frame of the image view
    self.imageView.frame = CGRectMake(self.imageView.frame.origin.x + (self.indentationLevel * self.indentationWidth), self.imageView.frame.origin.y, self.imageView.frame.size.width, self.imageView.frame.size.height);

}

@end
