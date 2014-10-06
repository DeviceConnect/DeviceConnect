//
//  GHConnectionManager.m
//  Browser
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import "GHConnectionManager.h"

@implementation GHConnectionManager


//--------------------------------------------------------------//
#pragma mark - 初期化
//--------------------------------------------------------------//
- (id)init
{
    self = [super init];
    if (self) {
        
    }
    return self;
}

- (void)dealloc
{
    self.requestUrl = nil;
    self.myConnection = nil;
}


- (void)startConnection:(NSURLRequest *)req
{
    self.requestUrl = [[req URL]absoluteString];
    
    self.myConnection = nil;
    self.myConnection = [[NSURLConnection alloc] initWithRequest:req delegate:self];
    [self.myConnection start];
}

//--------------------------------------------------------------//
#pragma mark - NSURLConnectionDelegate
//--------------------------------------------------------------//
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    self.myConnection = nil;
    if (self.delegate) {
        [self.delegate didFailWithError:error];
    }
}

//Basic認証があるかチェック
- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    //リトライカウントが1回以上だとBasic認証ダイアログを出す。
    if ([challenge previousFailureCount] == 0){
        NSURLCredential *credential = [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust];
        [challenge.sender useCredential:credential forAuthenticationChallenge:challenge];
        
    }else{
        self.myChallenge = challenge;
        [self showLogin];
    }
}

//基本呼ばれる事はない
- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    self.myConnection = nil;
}


///コネクションに成功した場合は、connectionをキャンセルして、webviewにリクエストを投げる
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response;
{
    
    if (self.delegate) {
        [self.delegate didReceiveResponse:[response.URL absoluteString]];
    }
    
    [connection cancel];
    self.myConnection = nil;
}


/**
 * Basic認証でuseridとpasswordを投げる
 * @param url 表示するurl
 */
- (void)createCredential:(NSString*)userid password:(NSString*)password
{
    NSURLCredential* credential = [NSURLCredential credentialWithUser:userid
                                                             password:password
                                                          persistence:NSURLCredentialPersistencePermanent];
    [[self.myChallenge sender] useCredential:credential forAuthenticationChallenge:self.myChallenge];
}


//--------------------------------------------------------------//
#pragma mark - ID & PW入力
//--------------------------------------------------------------//

///ログインダイアログを出す
- (void)showLogin
{
    UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"認証が必要です"
                                                   message:[NSString stringWithFormat:@"%@", self.requestUrl]
                                                  delegate:self
                                         cancelButtonTitle:@"キャンセル"
                                         otherButtonTitles:@"OK", nil];
    [alert setAlertViewStyle:UIAlertViewStyleLoginAndPasswordInput];
    [alert show];
}

///ログインダイアログにID&PWが入力されたらOKボタンを有効にする
- (BOOL)alertViewShouldEnableFirstOtherButton:(UIAlertView *)alertView
{
    NSString *inputText = [[alertView textFieldAtIndex:0] text];
    NSString *inputText2 = [[alertView textFieldAtIndex:1] text];
    if( [inputText length] >= 1 && [inputText2 length] >= 1)
    {
        return YES;
    }
    else
    {
        return NO;
    }
}


- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (alertView.cancelButtonIndex != buttonIndex) {
        //Basic認証チェック
        NSString* userid   = [[alertView textFieldAtIndex:0] text];
        NSString* password = [[alertView textFieldAtIndex:1] text];
        [self createCredential:userid password:password];
        
    }else{
        //キャンセル
        [[self.myChallenge sender] cancelAuthenticationChallenge:self.myChallenge];
        [self.myConnection cancel];
    }
}





@end
