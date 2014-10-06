//
//  GHBookmarkTitleCell.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHBookmarkTitleCell.h"

@implementation GHBookmarkTitleCell

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}


- (void)setItem:(GHPageModel*)page
{
    self.titleField.text = page.title;
    self.urlLabel.text = page.url;
    
    /* Favicon取得は外す
    // google.comのFaviconをロードしてくる。
    NSURL *url = [[NSURL alloc] initWithString:@"http://www.google.com/images/google_favicon_128.png"];
    NSError *error = nil;
    NSData *data = [[NSData alloc] initWithContentsOfURL:url options:NSDataReadingUncached error:&error];
    if (error) {
        // favicon取得処理でエラーが発生した場合には、処理を中止する。
        NSLog(@"/favicon.ico load failed. reason = %@", error);
        return;
    }
    
    // ico -> png -> base64に変換する。
    UIImage *icon = [UIImage imageWithData:data];
    self.favicon.image = icon;
     */
}
@end
