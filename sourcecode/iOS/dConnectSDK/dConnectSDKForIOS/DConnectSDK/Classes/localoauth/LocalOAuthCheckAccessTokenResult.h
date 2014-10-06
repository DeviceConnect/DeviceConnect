//
//  LocalOAuthCheckAccessTokenResult.h
//  DConnectSDK
//
//  Copyright (c) 2014 NTT DOCOMO,INC.
//  Released under the MIT license
//  http://opensource.org/licenses/mit-license.php
//

#import <Foundation/Foundation.h>

@interface LocalOAuthCheckAccessTokenResult : NSObject

@property BOOL _isExistClientId;
@property BOOL _isExistAccessToken;
@property BOOL _isExistScope;
@property BOOL _isNotExpired;

+ (LocalOAuthCheckAccessTokenResult *) checkAccessTokenResultWithFlags: (BOOL)isExistClientId
                        isExistAccessToken:(BOOL)isExistAccessToken
                              isExistScope:(BOOL)isExistScope_ isNotExpired:(BOOL)isNotExpired;

/*!
    アクセストークンが有効か判定結果を返す.

    @return YES: アクセストークンは有効 / NO: アクセストークンは無効
 */
- (BOOL) checkResult;

/*!
    判定結果(アクセストークンを発行したクライアントIDが存在するか)を返す.

    @return YES: アクセストークンを発行したクライアントIDあり / NO: アクセストークンを発行したクライアントIDなし
 */
- (BOOL) isExistClientId;

/*!
    判定結果(アクセストークンが存在するか)を返す.

    @return YES: アクセストークンあり / NO: アクセストークンなし
 */
- (BOOL) isExistAccessToken;

/*!
    判定結果(アクセストークンにスコープが登録されているか)を返す.

    @return YES: スコープあり / NO: スコープなし.
 */
- (BOOL) isExistScope;

/*!
    判定結果(アクセストークンの有効期限内か)を返す.

    @return YES: 有効期限は切れていない / NO: 有効期限は切れている.
 */
- (BOOL) isExistNotExpired;


@end
