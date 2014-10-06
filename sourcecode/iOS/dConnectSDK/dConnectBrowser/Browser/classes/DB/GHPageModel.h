//
//  GHPageModel.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface GHPageModel : NSObject

@property (nonatomic, strong) NSString * title;
@property (nonatomic, strong) NSString * url;
@property (nonatomic, strong) NSString * category;
@property (nonatomic, strong) NSString * type;
@property (nonatomic, strong) NSNumber * priority;
@property (nonatomic, strong) NSString * identifier;
@property (nonatomic, strong) NSArray  * children;

@end
