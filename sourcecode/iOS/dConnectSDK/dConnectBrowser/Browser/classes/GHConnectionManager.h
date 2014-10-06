//
//  GHConnectionManager.h
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@protocol GHConnectionManagerDelegate <NSObject>

- (void)didReceiveResponse:(NSString*)url;
- (void)didFailWithError:(NSError *)error;

@end

@interface GHConnectionManager : NSObject<NSURLConnectionDelegate,UIAlertViewDelegate>

@property (nonatomic, strong) NSString *requestUrl;
@property (nonatomic, weak)   NSURLAuthenticationChallenge* myChallenge;
@property (nonatomic, strong) NSURLConnection *myConnection;
@property (nonatomic, weak)   id<GHConnectionManagerDelegate>delegate;

- (void)startConnection:(NSURLRequest *)req;

@end
