//
//  HttpAsynchronousRequest.m
//  CameraRemoteSampleApp
//  Copyright 2014 Sony Corporation
//

#import "HttpAsynchronousRequest.h"

@implementation HttpAsynchronousRequest
{
    id<HttpAsynchronousRequestParserDelegate> _parserDelegate;
    NSMutableData *_receiveData;
    NSString* _apiName;
}

- (void) call:(NSString*)url postParams:(NSString*)params apiName:(NSString*)apiName parserDelegate:(id<HttpAsynchronousRequestParserDelegate>)parserDelegate
{
    _parserDelegate = parserDelegate;
    _apiName = apiName;
    _receiveData = [NSMutableData data];
    NSURL *aUrl = [NSURL URLWithString:url];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:aUrl
                                                           cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                       timeoutInterval:60.0];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:[params dataUsingEncoding:NSUTF8StringEncoding]];
    NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request
																  delegate:self
														  startImmediately:NO];
	if (connection) {
		[connection scheduleInRunLoop:[NSRunLoop mainRunLoop]
							  forMode:NSDefaultRunLoopMode];
		[connection start];
	} else {
		NSLog(@"NSURLConnection Error.");
	}
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [_receiveData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [_receiveData appendData:data];
}

-(void) connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    NSLog(@"HttpRequest didFailWithError = %@", error);
    NSString* errorResponse =@"{ \"id\"= 0 , \"error\"=[16,\"Transport Error\"]}";
    [_parserDelegate parseMessage:[errorResponse dataUsingEncoding:NSUTF8StringEncoding] apiName:_apiName];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [_parserDelegate parseMessage:_receiveData apiName:_apiName];
}

@end
